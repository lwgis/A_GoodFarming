package com.zhonghaodi.goodfarming;

import javax.sdp.Info;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.Holder1;
import com.zhonghaodi.customui.Holder2;
import com.zhonghaodi.customui.Holder3;
import com.zhonghaodi.customui.HolderResponse;
import com.zhonghaodi.customui.UrlTextView.UrlOnClick;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFString;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class QuestionActivity extends Activity implements UrlOnClick,
		HandMessage,OnClickListener {
	private PullToRefreshListView pullToRefreshListView;
	private Question question;
	private int questionId;
	private GFHandler<QuestionActivity> handler = new GFHandler<QuestionActivity>(
			this);
	private ResponseAdapter adapter;
	private Response selectResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_question);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button sendBtn = (Button) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (questionId == 0) {
					return;
				}
				if (GFUserDictionary.getUserId() != null) {
					Intent it = new Intent(QuestionActivity.this,
							CreateResponseActivity.class);
					it.putExtra("questionId", questionId);
					QuestionActivity.this.startActivityForResult(it, 2);
				} else {
					Intent it = new Intent(QuestionActivity.this,
							LoginActivity.class);
					QuestionActivity.this.startActivity(it);
				}
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		questionId = getIntent().getIntExtra("questionId", 0);
		loadData();
		adapter = new ResponseAdapter();
		pullToRefreshListView.setAdapter(adapter);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
					}
				});
		registerForContextMenu(pullToRefreshListView.getRefreshableView());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(info.position>1){
			String uid = GFUserDictionary.getUserId();
			if(uid!=null){
				
				Response response = question.getResponses().get(info.position-2);
				menu.add(0, 0, 0, "回复");
				if(response.getWriter().getId().equals(uid)){
					menu.add(0, 1, 1, "删除");
				} 
			}
		}
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo();
		selectResponse = question.getResponses().get(info.position-2);
		if(item.getItemId()==0){
			String wname = "回复 "+question.getResponses().get(info.position-2).getWriter().getAlias()+"：";
			Intent it = new Intent(QuestionActivity.this,
					CreateResponseActivity.class);
			it.putExtra("questionId", questionId);
			it.putExtra("wname", wname);
			QuestionActivity.this.startActivityForResult(it, 2);
		}
		else{
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
	        //设置它的ContentView
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout = inflater.inflate(R.layout.dialog, null);
	        dialog.setContentView(layout);
	        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
	        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
	        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
	        okBtn.setText("确定");
	        okBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					
					delete(question.getId(),selectResponse.getId());
				}
			});
	        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
	        cancelButton.setText("取消");
	        cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
	        titleView.setText("提示");
	        contentView.setText("确定要删除选中的评论吗？");
	        dialog.show();
		}
		 
		return super.onContextItemSelected(item);
	}
	
	private void delete(final int qid,final int rid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deleteResponse(qid,rid);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == 2) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					loadData();
				}
			}, 1000);
		}
	}

	private void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jsonString = HttpUtil.getSingleQuestion(questionId);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();

	}

	class ResponseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (question == null) {
				return 0;
			}
			return question.getResponses().size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				if (question.getAttachments().size() == 0) {
					return 0;
				}
				if (question.getAttachments().size() > 0
						&& question.getAttachments().size() < 4) {
					return 1;
				}
				if (question.getAttachments().size() > 3) {
					return 2;
				}
			}
			return 3;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			Holder1 holder1 = null;
			Holder2 holder2 = null;
			Holder3 holder3 = null;
			HolderResponse holderResponse = null;
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = LayoutInflater.from(QuestionActivity.this)
							.inflate(R.layout.cell_question, parent, false);
					holder1 = new Holder1(convertView);
					convertView.setTag(holder1);
					break;
				case 1:
					convertView = LayoutInflater.from(QuestionActivity.this)
							.inflate(R.layout.cell_question_3_image, parent,
									false);
					holder2 = new Holder2(convertView);
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = LayoutInflater.from(QuestionActivity.this)
							.inflate(R.layout.cell_question_6_image, parent,
									false);
					holder3 = new Holder3(convertView);
					convertView.setTag(holder3);
					break;
				case 3:
					convertView = LayoutInflater.from(QuestionActivity.this)
							.inflate(R.layout.cell_response, parent, false);
					holderResponse = new HolderResponse(convertView);
					convertView.setTag(holderResponse);
					break;
				default:
					break;
				}
			}
			
			String content = PublicHelper.TrimRight(question.getContent());
			switch (type) {
			case 0:
				holder1 = (Holder1) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder1.headIv, ImageOptions.options);
				holder1.nameTv.setText(question.getWriter().getAlias());
				holder1.timeTv.setText(question.getTime());
				holder1.contentTv.setText(content);
				holder1.countTv.setText("已有" + question.getResponsecount()
						+ "个答案");
				holder1.cropTv.setText(question.getCrop().getName());
				holder1.headIv.setTag(question.getWriter());
				holder1.headIv.setOnClickListener(QuestionActivity.this);
				break;
			case 1:
				holder2 = (Holder2) convertView.getTag();
				holder2.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder2.headIv, ImageOptions.options);
				holder2.nameTv.setText(question.getWriter().getAlias());
				holder2.contentTv.setText(content);
				holder2.timeTv.setText(question.getTime());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(0).getUrl(),
						holder2.imageView1, ImageOptions.options);
				holder2.imageView1.setVisibility(View.VISIBLE);
				holder2.imageView1.setIndex(0);
				holder2.imageView1.setImages(question.getAttachments(),
						"questions");
				if (question.getAttachments().size() > 1) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(1)
													.getUrl(),
									holder2.imageView2, ImageOptions.options);
					holder2.imageView2.setVisibility(View.VISIBLE);
					holder2.imageView2.setIndex(1);
					holder2.imageView2.setImages(question.getAttachments(),
							"questions");
				}
				if (question.getAttachments().size() > 2) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(2)
													.getUrl(),
									holder2.imageView3, ImageOptions.options);
					holder2.imageView3.setVisibility(View.VISIBLE);
					holder2.imageView3.setIndex(2);
					holder2.imageView3.setImages(question.getAttachments(),
							"questions");
				}
				holder2.countTv.setText("已有" + question.getResponsecount()
						+ "个答案");
				holder2.cropTv.setText(question.getCrop().getName());
				holder2.headIv.setTag(question.getWriter());
				holder2.headIv.setOnClickListener(QuestionActivity.this);
				break;
			case 2:
				holder3 = (Holder3) convertView.getTag();
				holder3.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder3.headIv, ImageOptions.options);
				holder3.nameTv.setText(question.getWriter().getAlias());
				holder3.timeTv.setText(question.getTime());
				holder3.contentTv.setText(content);
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder3.headIv, ImageOptions.options);
				holder3.nameTv.setText(question.getWriter().getAlias());
				holder3.timeTv.setText(question.getTime());
				holder3.contentTv.setText(question.getContent());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(0).getUrl(),
						holder3.imageView1, ImageOptions.options);
				holder3.imageView1.setVisibility(View.VISIBLE);
				holder3.imageView1.setIndex(0);
				holder3.imageView1.setImages(question.getAttachments(),
						"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(1).getUrl(),
						holder3.imageView2, ImageOptions.options);
				holder3.imageView2.setVisibility(View.VISIBLE);
				holder3.imageView2.setIndex(1);
				holder3.imageView2.setImages(question.getAttachments(),
						"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(2).getUrl(),
						holder3.imageView3, ImageOptions.options);
				holder3.imageView3.setVisibility(View.VISIBLE);
				holder3.imageView3.setIndex(2);
				holder3.imageView3.setImages(question.getAttachments(),
						"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(3).getUrl(),
						holder3.imageView4, ImageOptions.options);
				holder3.imageView4.setVisibility(View.VISIBLE);
				holder3.imageView4.setIndex(3);
				holder3.imageView4.setImages(question.getAttachments(),
						"questions");
				if (question.getAttachments().size() > 4) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(4)
													.getUrl(),
									holder3.imageView5, ImageOptions.options);
					holder3.imageView5.setVisibility(View.VISIBLE);
					holder3.imageView5.setIndex(4);
					holder3.imageView5.setImages(question.getAttachments(),
							"questions");
				}
				if (question.getAttachments().size() > 5) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(5)
													.getUrl(),
									holder3.imageView6, ImageOptions.options);
					holder3.imageView6.setVisibility(View.VISIBLE);
					holder3.imageView6.setIndex(5);
					holder3.imageView6.setImages(question.getAttachments(),
							"questions");
				}
				holder3.countTv.setText("已有" + question.getResponsecount()
						+ "个答案");
				holder3.cropTv.setText(question.getCrop().getName());
				holder3.headIv.setTag(question.getWriter());
				holder3.headIv.setOnClickListener(QuestionActivity.this);
				break;
			case 3:
				holderResponse = (HolderResponse) convertView.getTag();
				final Response response = question.getResponses().get(
						position - 1);
				final HolderResponse holder = holderResponse;
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ response.getWriter().getThumbnail(),
						holderResponse.headIv, ImageOptions.options);
				holderResponse.nameTv.setText(response.getWriter().getAlias());
				holderResponse.timeTv.setText(response.getTime());
				String rcontent = PublicHelper.TrimRight(response.getContent());
				holderResponse.contentTv.setHtmlText(rcontent);
				holderResponse.contentTv.setUrlOnClick(QuestionActivity.this);
				holderResponse.countTv
						.setText(String.valueOf(response.getZan()));
				if (response.getWriter().getId()
						.equals(GFUserDictionary.getUserId())) {
					holderResponse.zanBtn.setEnabled(false);
				} else {
					final Button zanButton = holderResponse.zanBtn;
					if (response.isHasUser(GFUserDictionary.getUserId())) {
						zanButton.setSelected(true);
					} else {
						zanButton.setSelected(false);
					}
					holderResponse.zanBtn
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (GFUserDictionary.getUserId() == null) {
										Intent it = new Intent(
												QuestionActivity.this,
												LoginActivity.class);
										QuestionActivity.this.startActivity(it);
									} else {
										zanButton.setSelected(!zanButton
												.isSelected());
										if (zanButton.isSelected()) {
											new Thread(new Runnable() {

												@Override
												public void run() {
													HttpUtil.zanResponse(
															question.getId(),
															response.getId());
												}
											}).start();
											response.zan(GFUserDictionary
													.getUserId());
											holder.zan();
											int point = GFPointDictionary.getZanPoint();
											if(point>0){
												GFToast.show("点赞成功，积分+"+point+" ^-^");
											}
										} else {
											new Thread(new Runnable() {

												@Override
												public void run() {
													HttpUtil.cancelZanResponse(
															question.getId(),
															response.getId());
												}
											}).start();
											response.cancelZan(GFUserDictionary
													.getUserId());
											holder.cancelZan();
										}
									}
								}
							});
				}
				holderResponse.headIv.setTag(response.getWriter());
				holderResponse.headIv.setOnClickListener(QuestionActivity.this);
				break;
			default:
				break;
			}
			return convertView;
		}

	}

	@Override
	public void onClick(View view, String urlString) {
		if (!GFString.isNumeric(urlString)) {
			return;
		}
		Intent it = new Intent(this, DiseaseActivity.class);
		it.putExtra("diseaseId", Integer.parseInt(urlString));
		startActivity(it);
	}
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		this.setResult(2);
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterForContextMenu(pullToRefreshListView.getRefreshableView());
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		User user = (User)v.getTag();
		if(user.getLevelID()!=1){
			Intent it = new Intent();
			it.setClass(this, ChatActivity.class);
			it.putExtra("userName", user.getPhone());
			it.putExtra("title", user.getAlias());
			it.putExtra("thumbnail", user.getThumbnail());
			startActivity(it);
		}
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		QuestionActivity activity = (QuestionActivity) object;
		if(msg.what==1){
			Gson gson = new Gson();
			String jsonString = (String) msg.obj;
			activity.question = gson.fromJson(jsonString, Question.class);
			if(question.getStatus()!=1){
				activity.adapter.notifyDataSetChanged();
			}
			else{
				GFToast.show("问题已删除");
			}
			activity.pullToRefreshListView.onRefreshComplete();
		}
		else if(msg.what == 3){
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				GFToast.show(strerr);
			}
			else{
				question.getResponses().remove(selectResponse);
				adapter.notifyDataSetChanged();
			}
		}
	}
}

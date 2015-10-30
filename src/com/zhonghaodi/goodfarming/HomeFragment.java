package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.Holder1;
import com.zhonghaodi.customui.Holder2;
import com.zhonghaodi.customui.Holder3;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.utils.PublicHelper;

import android.R.integer;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
public class HomeFragment extends Fragment implements HandMessage,OnClickListener,OnCreateContextMenuListener {
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private QuestionAdpter adapter;
	private GFHandler<HomeFragment> handler = new GFHandler<HomeFragment>(this);
	private int bAll = 0;
	private Button signinButton;
	private TextView allTextView;
	private TextView ascTextView;
	private TextView myTextView;
	private PopupWindow popupWindow;
	private View popView;
	private Question selectQuestion;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		Button questionButton = (Button) view
				.findViewById(R.id.question_button);
		questionButton.setOnClickListener(this);
		allTextView = (TextView)view.findViewById(R.id.all_text);
		allTextView.setOnClickListener(this);
		ascTextView = (TextView)view.findViewById(R.id.asc_text);
		ascTextView.setOnClickListener(this);
		myTextView = (TextView)view.findViewById(R.id.my_text);
		myTextView.setOnClickListener(this);
		popView = inflater.inflate(R.layout.popupwindow_camera, container,
				false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(
				getActivity(), 180), DpTransform.dip2px(getActivity(), 100));
		signinButton = (Button)view.findViewById(R.id.signin_button);
		signinButton.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if(bAll==0)
							loadNewDate();
						else if(bAll==1){
							loadNewMyDate();
						}
						else if(bAll==2){
							loadNewAscDate();
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuestions.size() == 0) {
							return;
						}
						Question question = allQuestions.get(allQuestions
								.size() - 1);
						if(bAll==0)
							loadMoreData(question.getId());
						else if(bAll==1){
							loadMoreMyData(question.getId());
						}
						else if(bAll==2){
							loadMoreAscData(question.getId());
						}
					}

				});
		allQuestions = new ArrayList<Question>();
		adapter = new HomeFragment.QuestionAdpter();
		HomeFragment.this.pullToRefreshListView.getRefreshableView()
				.setAdapter(adapter);
		loadNewDate();
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(getActivity(),
								QuestionActivity.class);
						it.putExtra("questionId", allQuestions
								.get(position - 1).getId());
						getActivity().startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
		selectTextView(allTextView);
		
		return view;
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId();
		if(uid!=null){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Question question = allQuestions.get(info.position-1);
			if(question.getWriter().getId().equals(uid)){
				menu.add(0, 0, 0, "删除");
			} 
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo(); 
		selectQuestion = allQuestions.get(info.position-1);
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				delete(selectQuestion.getId());
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
        contentView.setText("确定要删除选中的提问吗？");
        dialog.show();
		return super.onContextItemSelected(item);
	}


	private void loadNewDate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuestionsString();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreData(final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuestionsString(qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadNewMyDate() {
		final String  uid= GFUserDictionary.getUserId();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getMyQuestionsString(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreMyData(final int qid) {
		final String  uid= GFUserDictionary.getUserId();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getMyQuestionsString(uid,qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadNewAscDate() {
		final String  uid= GFUserDictionary.getUserId();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAscQuestionsString(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreAscData(final int qid) {
		final String  uid= GFUserDictionary.getUserId();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAscQuestionsString(uid,qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void signin() {
		final String  uid= GFUserDictionary.getUserId();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.signIn(uid);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void delete(final int qid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deleteQuestion(qid);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 弹出设置作物对话框
	 */
	private void popupDialog(){
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setText("现在就去");
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				MainActivity mainActivity = (MainActivity)getActivity();
				mainActivity.seletFragmentIndex(3);
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setText("先不去");
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				allQuestions.clear();
				adapter.notifyDataSetChanged();
			}
		});
        titleView.setText("提示");
        contentView.setText("您还没有设置自己擅长或种植的作物，现在要设置吗？");
        dialog.show();
	}

	class QuestionAdpter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allQuestions.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allQuestions.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			Question question = allQuestions.get(position);
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
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Question question = allQuestions.get(position);
			int type = getItemViewType(position);
			Holder1 holder1 = null;
			Holder2 holder2 = null;
			Holder3 holder3 = null;
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = LayoutInflater.from(
							HomeFragment.this.getActivity()).inflate(
							R.layout.cell_question, parent, false);
					holder1 = new Holder1(convertView);
					convertView.setTag(holder1);
					break;
				case 1:
					convertView = LayoutInflater.from(
							HomeFragment.this.getActivity()).inflate(
							R.layout.cell_question_3_image, parent, false);
					holder2 = new Holder2(convertView);			
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = LayoutInflater.from(
							HomeFragment.this.getActivity()).inflate(
							R.layout.cell_question_6_image, parent, false);
					holder3 = new Holder3(convertView);
					convertView.setTag(holder3);
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
				holder1.headIv.setOnClickListener(HomeFragment.this);
				if(question.getAddress()!=null){
					holder1.addressTextView.setText(question.getAddress());
				}
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
				holder2.cropTv.setText(question.getCrop().getName());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(0).getUrl(),
						holder2.imageView1, ImageOptions.options);
				holder2.imageView1.setVisibility(View.VISIBLE);
				holder2.imageView1.setIndex(0);
				holder2.imageView1.setImages(question.getAttachments(),"questions");
				if (question.getAttachments().size() > 1) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(1)
													.getUrl(),
									holder2.imageView2, ImageOptions.options);
					holder2.imageView2.setVisibility(View.VISIBLE);
					holder2.imageView2.setIndex(1);
					holder2.imageView2.setImages(question.getAttachments(),"questions");
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
					holder2.imageView3.setImages(question.getAttachments(),"questions");
				}
				holder2.countTv.setText("已有" + question.getResponsecount()
						+ "个答案");
				holder2.headIv.setTag(question.getWriter());
				holder2.headIv.setOnClickListener(HomeFragment.this);
				if(question.getAddress()!=null){
					holder2.addressTextView.setText(question.getAddress());
				}
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
				
				holder3.cropTv.setText(question.getCrop().getName());
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
				holder3.imageView1.setImages(question.getAttachments(),"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(1).getUrl(),
						holder3.imageView2, ImageOptions.options);
				holder3.imageView2.setVisibility(View.VISIBLE);
				holder3.imageView2.setIndex(1);
				holder3.imageView2.setImages(question.getAttachments(),"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(2).getUrl(),
						holder3.imageView3, ImageOptions.options);
				holder3.imageView3.setVisibility(View.VISIBLE);
				holder3.imageView3.setIndex(2);
				holder3.imageView3.setImages(question.getAttachments(),"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(3).getUrl(),
						holder3.imageView4, ImageOptions.options);
				holder3.imageView4.setVisibility(View.VISIBLE);
				holder3.imageView4.setIndex(3);
				holder3.imageView4.setImages(question.getAttachments(),"questions");
				if (question.getAttachments().size() > 4) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(4)
													.getUrl(),
									holder3.imageView5, ImageOptions.options);
					holder3.imageView5.setVisibility(View.VISIBLE);
					holder3.imageView5.setIndex(4);
					holder3.imageView5.setImages(question.getAttachments(),"questions");
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
					holder3.imageView6.setImages(question.getAttachments(),"questions");
				}
				holder3.countTv.setText("已有" + question.getResponsecount()
						+ "个答案");
				holder3.headIv.setTag(question.getWriter());
				holder3.headIv.setOnClickListener(HomeFragment.this);
				if(question.getAddress()!=null){
					holder3.addressTextView.setText(question.getAddress());
				}
				break;
			default:
				break;
			}

			return convertView;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.question_button:
			Intent it = new Intent();
			if (GFUserDictionary.getUserId()==null) {
				it.setClass(getActivity(), LoginActivity.class);
			}
			else {
				it.setClass(getActivity(), CreateQuestionActivity.class);
			}
			getActivity().startActivity(it);
			break;
		case R.id.signin_button:
			Intent it1 = new Intent();
			if (GFUserDictionary.getUserId()==null) {
				it1.setClass(getActivity(), LoginActivity.class);
				getActivity().startActivity(it1);
			}
			else {
				signinButton.setEnabled(false);
				signin();
			}
			break;
		case R.id.title_txt:
			
			break;
			
		case R.id.all_text:
			selectTextView(v);
			loadNewDate();
			bAll = 0;
			break;
		case R.id.my_text:
			selectTextView(v);
			String uid = GFUserDictionary.getUserId();
			if(uid==null){
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent);
			}
			else{
				loadNewMyDate();
				
				bAll = 1;
			}
			
			break;
		case R.id.asc_text:
			selectTextView(v);
			String uid1 = GFUserDictionary.getUserId();
			String cropids = GFUserDictionary.getCroids();
			if(uid1==null){
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent);
			}
			else if(cropids==null || cropids.isEmpty()){
				popupDialog();
			}
			else{
				loadNewAscDate();
				bAll = 2;
			}
			break;
		
		default:
			break;
		}
	}
	
	public void selectTextView(View view){
		allTextView.setTextColor(Color.rgb(128, 128, 128));
		allTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		ascTextView.setTextColor(Color.rgb(128, 128, 128));
		ascTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		myTextView.setTextColor(Color.rgb(128, 128, 128));
		myTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
	}
	
	public void hidePopueWindow(){
	}
	

	@Override
	public void handleMessage(Message msg,Object object) {
			final HomeFragment fragment =(HomeFragment)object;
			if(msg.what==0||msg.what==1){
				if (msg.obj != null) {
					Gson gson = new Gson();
					List<Question> questions = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<Question>>() {
							}.getType());
					if (msg.what == 0) {
						fragment.allQuestions.clear();
					}
					if(bAll==1){
						if(questions==null || questions.size()==0){
							GFToast.show("您还没有提问过。");
						}
					}
					for (Question question : questions) {
						fragment.allQuestions.add(question);
					}
					fragment.adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(fragment.getActivity(), "连接服务器失败,请稍候再试!",
							Toast.LENGTH_SHORT).show();
				}
				fragment.pullToRefreshListView.onRefreshComplete();
			}
			else if(msg.what==2){
				signinButton.setEnabled(true);
				if(msg.obj!=null){
					GFToast.show(msg.obj.toString());
				}
				else{
					GFToast.show("连接服务器失败,请稍候再试!");
				}
			}
			else if(msg.what==3){
				
				String str = msg.obj.toString();
				if(!str.isEmpty()){
					GFToast.show(str);
				}
				else{
					allQuestions.remove(selectQuestion);
					fragment.adapter.notifyDataSetChanged();
				}
				
			}
	
	}


}

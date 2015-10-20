package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HoldChatTextMessage;
import com.zhonghaodi.customui.Holder1;
import com.zhonghaodi.customui.HolderCommentQr;
import com.zhonghaodi.customui.HolderCommentTextMessage;
import com.zhonghaodi.customui.HolderResponse;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.UrlTextView.UrlOnClick;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.RComment;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFString;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

public class CommentActivity extends Activity implements UrlOnClick,
									HandMessage,OnClickListener{
	private TextView titleTv;
	private MyEditText chatEv;
	private MyTextButton sendMessageBtn;
	private ListView pullToRefreshList;
	public CommentAdapter adapter;
	private GFHandler<CommentActivity> handler = new GFHandler<CommentActivity>(this);
	private Question question;
	private Response response;
	private List<RComment> comments;
	private String rContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		MyTextButton cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		cancelBtn.setText("<返回");
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleTv = (TextView) findViewById(R.id.title_text);
		chatEv = (MyEditText) findViewById(R.id.chat_edit);
		sendMessageBtn = (MyTextButton) findViewById(R.id.send_meassage_button);
		pullToRefreshList = (ListView) findViewById(R.id.pull_refresh_list);
		comments = new ArrayList<RComment>();
		adapter = new CommentAdapter();
		pullToRefreshList.setAdapter(adapter);
		sendMessageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(chatEv.getText().toString().trim().isEmpty()){
					return;
				}
				sendText(chatEv.getText().toString());
				chatEv.setText("");
			}
		});
		question = (Question)getIntent().getSerializableExtra("question");
		response = (Response)getIntent().getSerializableExtra("response");
		rContent = response.getContent();
		titleTv.setText(question.getWriter().getAlias()+"的提问");
		loadData();
	}
	
	private void loadData(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jsonString = HttpUtil.getSingleResponse(question.getId(),response.getId());
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void sendText(final String content){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String uid = GFUserDictionary.getUserId();
				String jsonString = HttpUtil.commentResponse(question.getId(),response.getId(),uid,content);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	class CommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return comments.size()+2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(position==0){
				return question;
			}
			else if(position==1){
				return response;
			}
			else{
				return comments.get(position-2);
			}
			
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
			if(position==0){
				return 0;
			}
			else if(position ==1){
				return 1;
			}
			else{
				return 2;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			int type = getItemViewType(position);
			HolderCommentQr holder1;
			HolderCommentTextMessage holderCommentTextMessage;
			if (convertView == null) {
				if(type==0 || type==1){
					convertView = getLayoutInflater().inflate(
							R.layout.cell_comment_qr, parent, false);
					holder1 = new HolderCommentQr(convertView);
					convertView.setTag(holder1);
				}
				else{
					convertView = getLayoutInflater().inflate(
							R.layout.cell_comment, parent, false);
					holderCommentTextMessage = new HolderCommentTextMessage(convertView);
					convertView.setTag(holderCommentTextMessage);
				}
			}
			
			switch (getItemViewType(position)) {
			// 文本
			case 0:
				holder1 = (HolderCommentQr) convertView.getTag();
				holder1.contentTv.setText(question.getContent());
				holder1.timeTv.setText("提问于："+question.getTime());
				holder1.nameTv.setText(question.getWriter().getAlias());
				holder1.countTv.setText("已有" + question.getResponsecount()
						+ "个答案");
				holder1.cropTv.setVisibility(View.VISIBLE);
				holder1.cropTv.setText(question.getCrop().getName());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(), holder1.headIv,
						ImageOptions.optionsNoPlaceholder);
				break;
			case 1:
				holder1 = (HolderCommentQr) convertView.getTag();
				holder1.contentTv.setHtmlText(rContent);
				holder1.contentTv.setUrlOnClick(CommentActivity.this);
				holder1.timeTv.setText("回复于："+response.getTime());
				holder1.nameTv.setText(response.getWriter().getAlias());
				holder1.cropTv.setVisibility(View.GONE);
				holder1.countTv.setText("评论：" + response.getCommentCount());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ response.getWriter().getThumbnail(), holder1.headIv,
						ImageOptions.optionsNoPlaceholder);
				break;
			case 2:
				RComment comment = comments.get(position-2);
				holderCommentTextMessage = (HolderCommentTextMessage) convertView.getTag();
				holderCommentTextMessage.contentTv.setHtmlText(comment.getContent());
				holderCommentTextMessage.contentTv.setUrlOnClick(CommentActivity.this);
				holderCommentTextMessage.timeTv.setText(comment.getTime());
				holderCommentTextMessage.nameTv.setText(comment.getWriter().getAlias());
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"users/small/"
										+ comment.getWriter().getThumbnail(),
										holderCommentTextMessage.headIv,
								ImageOptions.optionsNoPlaceholder);
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
		
	}

	@Override
	public void onClick(View view, String urlString) {
		// TODO Auto-generated method stub
		if (!GFString.isNumeric(urlString)) {
			return;
		}
		Intent it = new Intent(this, DiseaseActivity.class);
		it.putExtra("diseaseId", Integer.parseInt(urlString));
		startActivity(it);
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				String jsonString = (String) msg.obj;
				response = gson.fromJson(jsonString, Response.class);
				comments.clear();
				if(response.getComments()!=null&&response.getComments().size()>0){
					for(RComment comment:response.getComments()){
						comments.add(comment);
					}
				}
				adapter.notifyDataSetChanged();
			}
			else{
				GFToast.show("获取评论失败");
			}
			break;
		case 2:
			if(msg.obj==null){
				GFToast.show("操作失败");
			}
			else{
				loadData();
				GFToast.show("评论成功");
			}
			break;
		
		default:
			break;
		}
	}

}

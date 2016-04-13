package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.zhonghaodi.model.NetResponse;
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
	private MyTextButton prescriptionButton;
	private ListView pullToRefreshList;
	public CommentAdapter adapter;
	private GFHandler<CommentActivity> handler = new GFHandler<CommentActivity>(this);
	private Question question;
	private Response response;
	private List<RComment> comments;
	private String rContent;
	private int status;
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
				if(GFUserDictionary.getUserId(getApplicationContext())==null){
					GFToast.show(getApplicationContext(),"请您先登录！");
					return;
				}
				if(chatEv.getText().toString().trim().isEmpty()){
					return;
				}
				sendText(chatEv.getText().toString());
				chatEv.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	 			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
			}
		});
		prescriptionButton = (MyTextButton)findViewById(R.id.prescription_button);
		prescriptionButton.setOnClickListener(this);
		question = (Question)getIntent().getSerializableExtra("question");
		response = (Response)getIntent().getSerializableExtra("response");
		status = getIntent().getIntExtra("status", 0);
		rContent = response.getContent();
		String aliaString = question.getWriter().getAlias();
		if(aliaString.length()>6){
			aliaString = aliaString.substring(0,5);
			aliaString = aliaString + "……";
		}
 		titleTv.setText(aliaString+"的提问");
		registerForContextMenu(pullToRefreshList);
		loadData();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(info.position>1){
			String uid = GFUserDictionary.getUserId(getApplicationContext());
			if(uid!=null){
				
				RComment comment = comments.get(info.position-1);
				if(comment.getWriter().getId().equals(uid)){
					menu.add(0, 0, 0, "加入配方");
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
		switch (item.getItemId()) {
		case 0:
			RComment comment = comments.get(info.position-1);
			Intent intent2 = new Intent(this,PrescriptionEditActivity.class);
			intent2.putExtra("content", comment.getContent());
			startActivity(intent2);
			break;

		default:
			break;
		}
		
		 
		return super.onContextItemSelected(item);
	}
	
	private void loadData(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jsonString;
				if(status==0){
					jsonString = HttpUtil.getSingleResponse(question.getId(),response.getId());
				}
				else{
					jsonString = HttpUtil.getGossipsResponse(question.getId(),response.getId());
				}
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
				String uid = GFUserDictionary.getUserId(getApplicationContext());
				NetResponse netResponse;
				if(status==0){
					netResponse = HttpUtil.commentResponse(question.getId(),response.getId(),uid,content);
				}
				else{
					netResponse = HttpUtil.commentGossipResponse(question.getId(),response.getId(),uid,content);
				}
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 2;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = 0;
					msg.obj = netResponse.getMessage();
				}				
				msg.sendToTarget();
			}
		}).start();
	}
	
	class CommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return comments.size()+1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(position==0){
				return response;
			}
			else{
				return comments.get(position-1);
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
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if(position==0){
				return 0;
			}
			else{
				return 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			int type = getItemViewType(position);
			HolderCommentQr holder1;
			HolderCommentTextMessage holderCommentTextMessage;
			if (convertView == null) {
				if(type==0){
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
				holder1.contentTv.setHtmlText(rContent);
				holder1.contentTv.setUrlOnClick(CommentActivity.this);
				holder1.timeTv.setText(response.getTime());
				holder1.nameTv.setText(response.getWriter().getAlias());
				holder1.cropTv.setVisibility(View.GONE);
				holder1.countTv.setText("评论：" + response.getCommentCount());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ response.getWriter().getThumbnail(), holder1.headIv,
						ImageOptions.optionsNoPlaceholder);
				switch (response.getWriter().getLevelID()) {
				case 1:
					holder1.levelTextView.setText(response.getWriter().getIdentifier()+"农友");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder1.levelTextView.setText(response.getWriter().getIdentifier()+"农技达人");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(response.getWriter().isTeamwork()){
						holder1.levelTextView.setText(response.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holder1.levelTextView.setText(response.getWriter().getIdentifier()+"农资店");
					}
					holder1.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder1.levelTextView.setText("专家");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder1.levelTextView.setText("官方");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				break;
			case 1:
				RComment comment = comments.get(position-1);
				holderCommentTextMessage = (HolderCommentTextMessage) convertView.getTag();
				holderCommentTextMessage.contentTv.setHtmlText(comment.getContent());
				holderCommentTextMessage.contentTv.setUrlOnClick(CommentActivity.this);
				holderCommentTextMessage.timeTv.setText(comment.getTime());
				holderCommentTextMessage.nameTv.setText(comment.getWriter().getAlias());
				if(position==1){
					holderCommentTextMessage.spcLayout.setVisibility(View.VISIBLE);
				}
				else{
					holderCommentTextMessage.spcLayout.setVisibility(View.GONE);
				}
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"users/small/"
										+ comment.getWriter().getThumbnail(),
										holderCommentTextMessage.headIv,
								ImageOptions.optionsNoPlaceholder);
				switch (comment.getWriter().getLevelID()) {
				case 1:
					holderCommentTextMessage.levelTextView.setText(comment.getWriter().getIdentifier()+"农友");
					holderCommentTextMessage.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holderCommentTextMessage.levelTextView.setText(comment.getWriter().getIdentifier()+"农技达人");
					holderCommentTextMessage.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(comment.getWriter().isTeamwork()){
						holderCommentTextMessage.levelTextView.setText(comment.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holderCommentTextMessage.levelTextView.setText(comment.getWriter().getIdentifier()+"农资店");
					}					
					holderCommentTextMessage.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holderCommentTextMessage.levelTextView.setText("专家");
					holderCommentTextMessage.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holderCommentTextMessage.levelTextView.setText("官方");
					holderCommentTextMessage.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
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
		case R.id.prescription_button:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
			Intent intent = new Intent(this,PrescriptionsActivity.class);
			this.startActivityForResult(intent, 100);
			break;

		default:
			break;
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 100) {
			String content = data.getStringExtra("content");
			if(content!=null&&content.length()>0){
				chatEv.setText(content);
				chatEv.setSelection(content.length());
				Timer timer = new Timer();  
			     timer.schedule(new TimerTask()  
			     {  
			           
			         public void run()  
			         {  
			             InputMethodManager inputManager =  
			                 (InputMethodManager)chatEv.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
			             inputManager.showSoftInput(chatEv, 0);  
			         }  
			           
			     },  998);  
			}
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
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
				GFToast.show(getApplicationContext(),"获取评论失败");
			}
			break;
		case 2:
			loadData();
			GFToast.show(getApplicationContext(),"评论成功");
			break;
		
		default:
			break;
		}
	}

}

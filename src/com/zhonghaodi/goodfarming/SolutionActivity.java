package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderCommentTextMessage;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SolutionHolder;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.RComment;
import com.zhonghaodi.model.Solution;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

public class SolutionActivity extends Activity implements HandMessage,OnClickListener {

	private TextView titleTv;
	private MyEditText chatEv;
	private MyTextButton sendMessageBtn;
	private ListView pullToRefreshList;
	public SolutionAdapter adapter;
	private GFHandler<SolutionActivity> handler = new GFHandler<SolutionActivity>(this);
	private Solution solution;
	private List<RComment> comments;
	private RComment selectComment;
	private int sid;
	private int did;
	private String dname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_solution);
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
		adapter = new SolutionAdapter();
		pullToRefreshList.setAdapter(adapter);
		registerForContextMenu(pullToRefreshList);
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
			}
		});
		solution = (Solution)getIntent().getSerializableExtra("solution");
		sid = solution.getId();
		did = getIntent().getIntExtra("did", 0);
		dname = getIntent().getStringExtra("dname");
		titleTv.setText(dname+"的妙招");
		loadData();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(info.position>0){
			String uid = GFUserDictionary.getUserId(getApplicationContext());
			if(uid!=null){
				
				RComment comment = comments.get(info.position-1);
				if(comment.getWriter().getId().equals(uid)){
					menu.add(0, 0, 0, "删除");
				} 
				comment=null;
			}
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo();
		selectComment = comments.get(info.position-1);
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
				
				delete(did,sid);
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
		return super.onContextItemSelected(item);
	}
	
	private void loadData(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String uid = GFUserDictionary.getUserId(getApplicationContext());
				String jsonString = HttpUtil.getSingleSolution(did,sid,uid);
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
				NetResponse netResponse = HttpUtil.commentSolution(did,sid,uid,content);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 2;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = -1;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void delete(final int did,final int sid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deleteSolutionComment(did,sid,selectComment.getId());
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void zan(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String uid = GFUserDictionary.getUserId(getApplicationContext());
				NetResponse netResponse = HttpUtil.zanSolution(did, solution.getId(), uid);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 4;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = -1;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
			}
		}).start();
	}
	private void cancelzan(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String uid = GFUserDictionary.getUserId(getApplicationContext());
				NetResponse netResponse = HttpUtil.cancelZanSolution(did, solution.getId(), uid);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 4;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = -1;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
			}
		}).start();
	}
	
	class SolutionAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return comments.size()+1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(position==0){
				return solution;
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
			SolutionHolder holder1;
			HolderCommentTextMessage holderCommentTextMessage;
			if (convertView == null) {
				if(type==0){
					convertView = getLayoutInflater().inflate(
							R.layout.cell_solution, parent, false);
					holder1 = new SolutionHolder(convertView);
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
				holder1 = (SolutionHolder) convertView.getTag();
				if (solution.getWriter().getThumbnail()!=null) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"users/small/"+solution.getWriter().getThumbnail(), holder1.headIv, ImageOptions.optionsNoPlaceholder);
				}
				holder1.solcountTv.setVisibility(View.GONE);
				holder1.nameTv.setText(solution.getWriter().getAlias());
				holder1.timeTv.setText(solution.getTime());
				holder1.contentTv.setText(solution.getContent());
				holder1.zancountTv.setText(String.valueOf(solution.getZan()));
				if(solution.isLiked()){
					holder1.zanIv.setSelected(true);
				}
				else{
					holder1.zanIv.setSelected(false);
				}
				holder1.zanLayout.setOnClickListener(SolutionActivity.this);
				holder1.commentTv.setText(String.valueOf(solution.getCommentCount()));
				break;
			case 1:
				RComment comment = comments.get(position-1);
				holderCommentTextMessage = (HolderCommentTextMessage) convertView.getTag();
				holderCommentTextMessage.contentTv.setHtmlText(comment.getContent());
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
		if(v.getId()==R.id.zan_layout){
			if(GFUserDictionary.getUserId(getApplicationContext())==null){
				GFToast.show(getApplicationContext(),"请您先登录！");
				return;
			}
			if(solution.isLiked()){
				cancelzan();
			}
			else{
				zan();
			}
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				String jsonString = (String) msg.obj;
				solution = gson.fromJson(jsonString, Solution.class);
				comments.clear();
				if(solution.getComments()!=null&&solution.getComments().size()>0){
					for(RComment comment:solution.getComments()){
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
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"操作失败");
			}
			else{
				loadData();
				GFToast.show(getApplicationContext(),"评论成功");
			}
			break;
		case 3:
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				GFToast.show(getApplicationContext(),strerr);
			}
			else{
				comments.remove(selectComment);
				solution.setCommentCount(solution.getCommentCount()-1);
				adapter.notifyDataSetChanged();
			}
			break;
		case 4:
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"操作失败");
			}
			else{
				loadData();
				GFToast.show(getApplicationContext(),"操作成功");
			}
			break;
		
		default:
			break;
		}
	}

}

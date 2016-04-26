package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HoldMessage;
import com.zhonghaodi.model.GFMessage;
import com.zhonghaodi.networking.GFDate;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.zhonghaodi.goodfarming.R;

public class SysMessageActivity extends Activity {
	private PullToRefreshListView pullToRefreshList;
	private List<GFMessage> messages;
	private MessageAdapter adapter = new MessageAdapter();
	private ImageView clearBtn;
	private String userName;
	private EMConversation emConversation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sysmessage);
		messages = (List<GFMessage>)getIntent().getSerializableExtra("messages");
		userName =  getIntent().getStringExtra("userName");
		emConversation = EMChatManager.getInstance().getConversation(userName);
		emConversation.resetUnreadMsgCount();
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		clearBtn = (ImageView)findViewById(R.id.clear_button);
		clearBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupDialog();
			}
		});
		pullToRefreshList = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		pullToRefreshList.setAdapter(adapter);
		pullToRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GFMessage message = messages.get(position - 1);			
				if(message.getType().equals("question")){
					String qidstr = message.getExcontent();
					Intent intent = new Intent(SysMessageActivity.this, QuestionActivity.class);
					if(!qidstr.isEmpty()){
						
						intent.putExtra("questionId", Integer.parseInt(qidstr));
						SysMessageActivity.this.startActivityForResult(intent, 2);
						
					}
				}
				else if(message.getType().equals("gossip")){
					String qidstr = message.getExcontent();
					Intent intent = new Intent(SysMessageActivity.this, QuestionActivity.class);
					if(!qidstr.isEmpty()){
						
						intent.putExtra("questionId", Integer.parseInt(qidstr));
						intent.putExtra("status", 1);
						SysMessageActivity.this.startActivityForResult(intent, 2);
						
					}
				}
				else if(message.getType().equals("plant")){
					String qidstr = message.getExcontent();
					Intent intent = new Intent(SysMessageActivity.this, QuestionActivity.class);
					if(!qidstr.isEmpty()){
						
						intent.putExtra("questionId", Integer.parseInt(qidstr));
						intent.putExtra("status", 2);
						SysMessageActivity.this.startActivityForResult(intent, 2);
						
					}
				}
				else if(message.getType().equals("questionReply")){
					String exContent = message.getExcontent().toString();
					String[] idstr = exContent.split("-");
					Intent intent = new Intent(SysMessageActivity.this, CommentActivity.class);
					if(idstr!=null&&idstr.length>1){
						
						intent.putExtra("qid", Integer.parseInt(idstr[0]));
						intent.putExtra("rid", Integer.parseInt(idstr[1]));
						SysMessageActivity.this.startActivity(intent);
						
					}
				}
				else if(message.getType().equals("gossipReply")){
					String exContent = message.getExcontent().toString();
					String[] idstr = exContent.split("-");
					Intent intent = new Intent(SysMessageActivity.this, CommentActivity.class);
					if(idstr!=null&&idstr.length>1){
						
						intent.putExtra("qid", Integer.parseInt(idstr[0]));
						intent.putExtra("rid", Integer.parseInt(idstr[1]));
						intent.putExtra("status", 1);
						SysMessageActivity.this.startActivity(intent);
						
					}
				}
				else if(message.getType().equals("plantReply")){
					String exContent = message.getExcontent().toString();
					String[] idstr = exContent.split("-");
					Intent intent = new Intent(SysMessageActivity.this, CommentActivity.class);
					if(idstr!=null&&idstr.length>1){
						
						intent.putExtra("qid", Integer.parseInt(idstr[0]));
						intent.putExtra("rid", Integer.parseInt(idstr[1]));
						intent.putExtra("status", 2);
						SysMessageActivity.this.startActivity(intent);
						
					}
				}
				else if(message.getType().equals("user")){
					
				}
				else if(message.getType().isEmpty()){
					Intent it = new Intent();
					it.setClass(SysMessageActivity.this, ChatActivity.class);
					it.putExtra("userName", message.getUser()==null?message.getTitle():message.getTitle());
					it.putExtra("title", message.getUser()==null?message.getTitle():message.getUser().getAlias());
					it.putExtra("thumbnail", message.getUser()==null?"":message.getUser().getThumbnail());
					SysMessageActivity.this.startActivityForResult(it, 2);
				}
				
			}
		});
	}
	
	private void popupDialog(){
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
				messages.clear();
				EMChatManager.getInstance().clearConversation(userName);
				adapter.notifyDataSetChanged();
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
        contentView.setText("确定要清空对话吗？");
        dialog.show();
	}

	class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HoldMessage holdMessage;
			if (convertView == null) {
				convertView = SysMessageActivity.this.getLayoutInflater().inflate(
						R.layout.cell_message, parent, false);
				holdMessage = new HoldMessage(convertView);
				convertView.setTag(holdMessage);
			} else {
				holdMessage = (HoldMessage) convertView.getTag();
			}
			GFMessage message = messages.get(position);
			if (message.getCount() == 0) {
				holdMessage.countTv.setVisibility(View.INVISIBLE);
			} else {
				holdMessage.countTv.setVisibility(View.VISIBLE);
			}
			if (message.getUser() != null) {
				holdMessage.titleTv.setText(message.getUser().getAlias());
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"users/small/"+message.getUser().getThumbnail(), holdMessage.headIv, ImageOptions.options);
			} else {
				holdMessage.titleTv.setText(message.getTitle());
			}
			holdMessage.headIv.setImageDrawable(SysMessageActivity.this.getResources().getDrawable(R.drawable.appicon));
			holdMessage.titleTv.setText(message.getTitle());
			holdMessage.contentTv.setText(message.getContent());
			holdMessage.timeTv
					.setText(GFDate.getStandardDate(message.getTime()));
			return convertView;
		}

	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		this.setResult(2);
		super.finish();
	}
	
	/***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
	        	this.finish();
	        	return true;
	        } 
	    } 
		return super.dispatchKeyEvent(event);

	}
	
}

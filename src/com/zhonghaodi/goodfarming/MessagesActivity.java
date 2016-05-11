package com.zhonghaodi.goodfarming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HoldMessage;
import com.zhonghaodi.model.GFMessage;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFDate;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class MessagesActivity extends Activity implements OnClickListener {

	private PullToRefreshListView pullToRefreshList;
	private ArrayList<GFMessage> messages;
	private MessageAdapter adapter = new MessageAdapter();
	private MessageHandle handler = new MessageHandle();
	private List<EMConversation> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		MobclickAgent.openActivityDurationTrack(false);
		pullToRefreshList = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		messages = new ArrayList<GFMessage>();
		Button contactsBtn = (Button)findViewById(R.id.contacts_button);
		contactsBtn.setOnClickListener(this);
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshList.setAdapter(adapter);
		pullToRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GFMessage message = messages.get(position - 1);
				if(message.getType()==null || message.getType()==""){
					Intent it = new Intent();
					it.setClass(MessagesActivity.this, ChatActivity.class);
					it.putExtra("userName", message.getUser()==null?message.getTitle():message.getTitle());
					it.putExtra("title", message.getUser()==null?message.getTitle():message.getUser().getAlias());
					it.putExtra("thumbnail", message.getUser()==null?"":message.getUser().getThumbnail());
					MessagesActivity.this.startActivity(it);
					EMConversation emConversation1 = EMChatManager.getInstance().getConversation(message.getUser().getPhone());
					emConversation1.resetUnreadMsgCount();
				}
				else{
					if(message.getType().equals("question") 
							|| message.getType().equals("gossip")
							|| message.getType().equals("plant")
							||message.getType().equals("questionReply")
							||message.getType().equals("gossipReply")
							||message.getType().equals("plantReply")){
						EMConversation emConversation = list.get(position-1);
						List<GFMessage> gfMessages = new ArrayList<GFMessage>();
						List<EMMessage> emMessages = emConversation.getAllMessages();
						for (int i=emMessages.size()-1;i>=0;i--) {
							EMMessage emMessage = emMessages.get(i);
							GFMessage gfMessage = new GFMessage();
							gfMessage.setTitle(emConversation.getUserName());
							TextMessageBody body = (TextMessageBody) emMessage.getBody();
							gfMessage.setContent(body.getMessage());
							String type="";
							String content="";
							try {
								type = emMessage.getStringAttribute("type");
								if(type.equals("questionReply")||type.equals("gossipReply")||type.equals("plantReply")){
									 int qid = emMessage.getIntAttribute("qid");
									 int rid = emMessage.getIntAttribute("rid");
									 content = qid+"-"+rid;
								 }
								 else{
									 content = emMessage.getStringAttribute("content"); 
								 }
							} catch (Exception e) {
								// TODO: handle exception
							}
							gfMessage.setType(type);
							gfMessage.setExcontent(content);
							gfMessage.setTime(emMessage.getMsgTime());
							gfMessages.add(gfMessage);
							
						}
						EMConversation emConversation1 = EMChatManager.getInstance().getConversation("种好地");
						emConversation1.resetUnreadMsgCount();
						Intent intent = new Intent(MessagesActivity.this, SysMessageActivity.class);
						intent.putExtra("messages", (Serializable)gfMessages);
						intent.putExtra("userName", "种好地");
						MessagesActivity.this.startActivity(intent);
						
					}
					else if(message.getType().equals("user")){
						EMConversation emConversation1 = EMChatManager.getInstance().getConversation("种好地");
						emConversation1.resetUnreadMsgCount();
						Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
						MessagesActivity.this.startActivity(intent);
					}
					
				}
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
		MobclickAgent.onPageStart("消息");
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("消息");
		MobclickAgent.onPause(this);
	}


	public void loadData() {
		list = loadConversationsWithRecentChat();
		messages.clear();
		if (list.size()==0) {
			adapter.notifyDataSetChanged();
			return;
		}
		for (EMConversation emConversation : list) {
			GFMessage message = new GFMessage();
			message.setTitle(emConversation.getUserName());
			if (emConversation.getLastMessage().getType()==Type.TXT) {
				EMMessage lastMessage = emConversation
						.getLastMessage();
				TextMessageBody body = (TextMessageBody) lastMessage.getBody();
				message.setContent(body.getMessage());
				String type="";
				String content = "";
				try {
					 type = lastMessage.getStringAttribute("type");
					 message.setType(type);
					 if(type.equals("questionReply")||type.equals("gossipReply")||type.equals("plantReply")){
						 int qid = lastMessage.getIntAttribute("qid");
						 int rid = lastMessage.getIntAttribute("rid");
						 content = qid+"-"+rid;
					 }
					 else{
						 content = lastMessage.getStringAttribute("content"); 
					 }
					 message.setExcontent(content);
					 
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (emConversation.getLastMessage().getType()==Type.VOICE) {
				message.setContent("[语音]");
			}
			if (emConversation.getLastMessage().getType()==Type.IMAGE) {
				message.setContent("[图片]");
			}
			message.setCount(emConversation.getUnreadMsgCount());
			message.setTime(emConversation.getLastMessage().getMsgTime());
			messages.add(message);
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NetResponse netResponse = HttpUtil.getUsers(messages);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 1;
						msg.obj = netResponse.getResult();
					}
					else{
						msg.what = 0;
						msg.obj = netResponse.getMessage();
					}
					
					msg.sendToTarget();
				} catch (Exception e) {
					// TODO: handle exception
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = e.getMessage();
					msg.sendToTarget();
				}
			}
		}).start();
		
	}
	
	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {

						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}

				});
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
				convertView = MessagesActivity.this.getLayoutInflater().inflate(
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
			holdMessage.countTv.setText(String.valueOf(message.getCount()));
			holdMessage.contentTv.setText(message.getContent());
			holdMessage.timeTv
					.setText(GFDate.getStandardDate(message.getTime()));
			return convertView;
		}

	}

	private User findUser(List<User> users, String phone) {
		for (User user : users) {
			if (user.getPhone().equals(phone)) {
				return user;
			}
		}
		return null;
	}
	
	class MessageHandle extends Handler{
		 
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0){
				
			}
			else if(msg.what==1) {
				if(msg.obj != null) {
					Gson gson = new Gson();
					List<User> users = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<User>>() {
							}.getType());
					if (users==null) {
						GFToast.show(getApplicationContext(),"获取消息失败");
						return;
					}
					for (GFMessage message :messages) {
						User user = findUser(users, message.getTitle());
						if (user != null) {
							message.setUser(user);
						}				
					}
					int count=0;
					for (GFMessage gfMessage : messages) {
						count+=gfMessage.getCount();
					}
					adapter.notifyDataSetChanged();
					
				} else {
					GFToast.show(getApplicationContext(),"获取消息失败");
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getApplicationContext());
		if(uid==null){
			Intent intent = new Intent(MessagesActivity.this, LoginActivity.class);
			this.startActivity(intent);
		}
		else{
			Intent intent = new Intent(MessagesActivity.this, MyFollowsActivity.class);
			this.startActivity(intent);
		}
		
	}

}

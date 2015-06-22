package com.zhonghaodi.goodfarming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HoldMessage;
import com.zhonghaodi.model.GFMessage;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFDate;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class MessageFragment extends Fragment implements OnClickListener {
	private PullToRefreshListView pullToRefreshList;
	private ArrayList<GFMessage> messages;
	private MessageAdapter adapter = new MessageAdapter();
	private MessageHandle handler = new MessageHandle(this);
	private List<EMConversation> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_message, container,
				false);
		pullToRefreshList = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		messages = new ArrayList<GFMessage>();
		Button contactsBtn = (Button)view.findViewById(R.id.contacts_button);
		contactsBtn.setOnClickListener(this);
		pullToRefreshList.setAdapter(adapter);
		pullToRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GFMessage message = messages.get(position - 1);
				if(message.getType()==null || message.getType()==""){
					Intent it = new Intent();
					it.setClass(getActivity(), ChatActivity.class);
					it.putExtra("userName", message.getUser()==null?message.getTitle():message.getTitle());
					it.putExtra("title", message.getUser()==null?message.getTitle():message.getUser().getAlias());
					it.putExtra("thumbnail", message.getUser()==null?"":message.getUser().getThumbnail());
					getActivity().startActivityForResult(it, 2);
				}
				else{
					if(message.getType().equals("question")){

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
								content = emMessage
										.getStringAttribute("content");
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
						((MainActivity)getActivity()).setUnreadMessageCount(emConversation1.getUnreadMsgCount());
						Intent intent = new Intent(getActivity(), SysMessageActivity.class);
						intent.putExtra("messages", (Serializable)gfMessages);
						getActivity().startActivityForResult(intent, 2);
						
					}
					else if(message.getType().equals("user")){
						((MainActivity)getActivity()).seletFragmentIndex(3);
						EMConversation emConversation1 = EMChatManager.getInstance().getConversation("种好地");
						emConversation1.resetUnreadMsgCount();
						((MainActivity)getActivity()).setUnreadMessageCount(emConversation1.getUnreadMsgCount());
					}
					
				}
				
			}
		});
//		loadData();
		return view;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId();
		if(uid==null){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent);
		}
		else{
			Intent intent = new Intent(getActivity(), MyFollowsActivity.class);
			getActivity().startActivity(intent);
		}
		
	}

	public void loadData() {
		list = loadConversationsWithRecentChat();
		if (list.size()==0) {
			return;
		}
		messages.clear();
		for (EMConversation emConversation : list) {
			GFMessage message = new GFMessage();
			message.setTitle(emConversation.getUserName());
			if (emConversation.getLastMessage().getType()==Type.TXT) {
				TextMessageBody body = (TextMessageBody) emConversation
						.getLastMessage().getBody();
				message.setContent(body.getMessage());
				String type="";
				String content = "";
				try {
					 type = emConversation.getLastMessage().getStringAttribute("type");
					 content = emConversation.getLastMessage().getStringAttribute("content");
					 message.setType(type);
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
				String jsonString = HttpUtil.getUsers(messages);
				Message msg = handler.obtainMessage();
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
		// getActivity().runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// adapter.notifyDataSetChanged();
		// }
		// });
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
				convertView = getActivity().getLayoutInflater().inflate(
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
		 MessageFragment fragment;
		 public MessageHandle(MessageFragment fm){
			 fragment=fm;
		 }
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<User> users = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<User>>() {
						}.getType());
				if (users==null) {
					GFToast.show("获取消息失败");
					return;
				}
				for (GFMessage message :messages) {
					User user = fragment.findUser(users, message.getTitle());
					if (user != null) {
						message.setUser(user);
					}				
				}
				int count=0;
				for (GFMessage gfMessage : messages) {
					count+=gfMessage.getCount();
				}
				if(getActivity()!=null){
					((MainActivity)getActivity()).setUnreadMessageCount(count);
					 adapter.notifyDataSetChanged();
				}
				else{
//					GFToast.show("空");
				}
				
			} else {
				GFToast.show("获取消息失败");
			}
		}
	}

	
}

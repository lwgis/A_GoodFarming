package com.zhonghaodi.goodfarming;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.NetUtils;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		EMEventListener {
	private long exitTime = 0;
	HomeFragment homeFragment;
	MessageFragment messageFragment;
	DiscoverFragment discoverFragment;
	MeFragment meFragment;
	ImageView homeIv;
	ImageView messageIv;
	ImageView discoverIv;
	ImageView meIv;
	TextView homeTv;
	TextView messageTv;
	TextView discoverTv;
	TextView meTv;
	View homeView;
	View messageView;
	View discoverView;
	View meView;
	int pageIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		homeView = findViewById(R.id.home_layout);
		messageView = findViewById(R.id.message_layout);
		discoverView = findViewById(R.id.discover_layout);
		meView = findViewById(R.id.me_layout);
		homeIv = (ImageView) findViewById(R.id.home_image);
		messageIv = (ImageView) findViewById(R.id.message_image);
		discoverIv = (ImageView) findViewById(R.id.discover_image);
		meIv = (ImageView) findViewById(R.id.me_image);
		homeTv = (TextView) findViewById(R.id.home_text);
		messageTv = (TextView) findViewById(R.id.message_text);
		discoverTv = (TextView) findViewById(R.id.discover_text);
		meTv = (TextView) findViewById(R.id.me_text);

		homeView.setOnClickListener(this);
		messageView.setOnClickListener(this);
		discoverView.setOnClickListener(this);
		meView.setOnClickListener(this);
		seletFragmentIndex(0);
		pageIndex = 0;
		if (GFUserDictionary.getUserId() != null)
			loginEm();
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		EMChatManager
				.getInstance()
				.registerEventListener(
						this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
		EMChat.getInstance().setAppInited();
	}

	private void loginEm() {
		EMChatManager.getInstance().login(GFUserDictionary.getPhone(),
				GFUserDictionary.getPassword(), new EMCallBack() {

					@Override
					public void onSuccess() {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						EMChatManager.getInstance().updateCurrentUserNick(
								GFUserDictionary.getAlias());
					}

					@Override
					public void onProgress(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void seletFragmentIndex(int i) {
		FragmentTransaction transction = getFragmentManager()
				.beginTransaction();
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
			transction.add(R.id.content, homeFragment);
		}
		if (messageFragment == null) {
			messageFragment = new MessageFragment();
			transction.add(R.id.content, messageFragment);
		}
		if (discoverFragment == null) {
			discoverFragment = new DiscoverFragment();
			transction.add(R.id.content, discoverFragment);
		}
		if (meFragment == null) {
			meFragment = new MeFragment();
			transction.add(R.id.content, meFragment);
		}
		transction.hide(homeFragment);
		transction.hide(messageFragment);
		transction.hide(discoverFragment);
		transction.hide(meFragment);
		homeIv.setImageResource(R.drawable.home);
		messageIv.setImageResource(R.drawable.message);
		discoverIv.setImageResource(R.drawable.discover);
		meIv.setImageResource(R.drawable.me);
		homeTv.setTextColor(Color.rgb(128, 128, 128));
		messageTv.setTextColor(Color.rgb(128, 128, 128));
		discoverTv.setTextColor(Color.rgb(128, 128, 128));
		meTv.setTextColor(Color.rgb(128, 128, 128));

		switch (i) {
		case 0:
			transction.show(homeFragment);
			homeIv.setImageResource(R.drawable.home_s);
			homeTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 1:
			transction.show(messageFragment);
			messageIv.setImageResource(R.drawable.message_s);
			messageTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 2:
			transction.show(discoverFragment);
			discoverIv.setImageResource(R.drawable.discover_s);
			discoverTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 3:
			if (meFragment.getUser() == null) {
				meFragment.loadData();
			}
			transction.show(meFragment);
			meIv.setImageResource(R.drawable.me_s);
			meTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		default:
			break;
		}
		transction.commit();
		pageIndex = i;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == homeView && pageIndex != 0) {
			seletFragmentIndex(0);
		}
		if (v == messageView && pageIndex != 1) {
			seletFragmentIndex(1);
		}
		if (v == discoverView && pageIndex != 2) {
			seletFragmentIndex(2);
		}
		if (v == meView && pageIndex != 3) {
			if (GFUserDictionary.getUserId() == null) {
				Intent it = new Intent();
				it.setClass(this, LoginActivity.class);
				startActivityForResult(it, 0);
				return;
			}
			seletFragmentIndex(3);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 4) {
			seletFragmentIndex(3);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if ((System.currentTimeMillis() - exitTime) > 2000) {
		// Toast.makeText(getApplicationContext(), "再按一次退出种好地",
		// Toast.LENGTH_SHORT).show();
		// exitTime = System.currentTimeMillis();
		// } else {
		// finish();
		// System.exit(0);
		// }
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}

	public void loginOut(View view) {
		GFUserDictionary.removeUserInfo();
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();
			final TextMessageBody textMessageBody = (TextMessageBody) message
					.getBody();
			if (UILApplication.isBackground(getApplicationContext())) {
				notificationTextMessage(message, textMessageBody);
			} else {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						GFToast.show(textMessageBody.getMessage());

					}
				});
			}
			// 提示新消息

			// refreshUI();
			break;
		}

		case EventOfflineMessage: {
			// refreshUI();
			break;
		}

		default:
			break;
		}
	}

	/**
	 * 文本消息通知
	 * 
	 * @param message
	 * @param textMessageBody
	 */
	private void notificationTextMessage(EMMessage message,
			final TextMessageBody textMessageBody) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, MainActivity.class);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.appicon)
				.setContentTitle(message.getFrom())
				.setContentText(textMessageBody.getMessage())
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(
						PendingIntent.getActivity(MainActivity.this, 0, intent,
								0)).build();
		// 发出通知
		manager.notify(0, notification);
	}

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// chatHistoryFragment.errorItem.setVisibility(View.GONE);
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(
					R.string.Less_than_chat_server_connection);
			final String st2 = getResources().getString(
					R.string.the_current_network);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						// showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						// showConflictDialog();
					} else {
						// chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
						if (NetUtils.hasNetwork(MainActivity.this))
							// chatHistoryFragment.errorText.setText(st1);
							;
						else
							// chatHistoryFragment.errorText.setText(st2);
							;

					}
				}

			});
		}
	}

}

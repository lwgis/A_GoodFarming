package com.zhonghaodi.goodfarming;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.NetUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		EMEventListener {
	// private long exitTime = 0;
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
	private TextView countTv;
	private boolean isLogin = false;
	private MainHandler handler = new MainHandler(this);
	private EMMessage currenEmMsg;
	private final static String lancherActivityClassName = WelcomeActivity.class.getName();

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
		countTv = (TextView) findViewById(R.id.count_text);
		homeView.setOnClickListener(this);
		messageView.setOnClickListener(this);
		discoverView.setOnClickListener(this);
		meView.setOnClickListener(this);
		seletFragmentIndex(0);
		pageIndex = 0;
		// initEm();
	}

	/**
	 * 初始化环信
	 */
	public void initEm() {
		if (GFUserDictionary.getUserId() == null)
			return;
		EMChatManager.getInstance().login(GFUserDictionary.getPhone(),
				GFUserDictionary.getPassword(), new EMCallBack() {

					@Override
					public void onSuccess() {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						EMChatManager.getInstance().updateCurrentUserNick(
								GFUserDictionary.getAlias());
						EMChatManager.getInstance().addConnectionListener(
								new MyConnectionListener());
						EMChatManager
								.getInstance()
								.registerEventListener(
										MainActivity.this,
										new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
						EMChat.getInstance().setAppInited();
						messageFragment.loadData();
						isLogin = true;
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

	public void seletFragmentIndex(int i) {
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
//		homeFragment.hidePopueWindow();
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
			if (GFUserDictionary.getUserId() == null) {
				return;
			}
			messageFragment.loadData();
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
		if (!isLogin) {
			initEm();
		} else {
			messageFragment.loadData();
		}
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
			initEm();
		}
		if (resultCode == 2) {
			messageFragment.loadData();
		}
		if(resultCode == RESULT_OK && requestCode == 100){
			ArrayList<Crop> selectCrops = data.getParcelableArrayListExtra("crops");
			meFragment.getUser().setCrops(null);
			if (selectCrops!=null&&selectCrops.size()>0) {
				String cropString = "";
				List<UserCrop> userCrops = new ArrayList<UserCrop>();
				for (Crop c : selectCrops) {
					cropString = cropString + c.getName() + "  ";
					UserCrop userCrop = new UserCrop();
					userCrop.setCrop(c);
					userCrops.add(userCrop);
				}
				meFragment.getUser().setCrops(userCrops);
			}
			updateCrops(meFragment.getUser());
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

	/**
	 * 注销用户
	 * @param view
	 */
	public void loginOut(View view) {
		
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				GFUserDictionary.removeUserInfo();
				EMChatManager.getInstance().logout();
				seletFragmentIndex(0);
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
        titleView.setText("注销提示");
        contentView.setText("确定要注销吗？");
        dialog.show();
		
	}
	
	/**
	 * 更新我的作物
	 * @param user
	 */
	private void updateCrops(final User user){
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					Message msgUser = handler.obtainMessage();
					msgUser.what = 2;
					msgUser.obj = HttpUtil.modifyUser(user);
					msgUser.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msgUser = handler.obtainMessage();
					msgUser.what = 0;
					msgUser.sendToTarget();
				}
			}
		}).start();
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			final EMMessage message = (EMMessage) event.getData();
			currenEmMsg = message;
			if (UILApplication.isBackground(getApplicationContext())) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						String jsonString;
						if(message.getFrom().equals("种好地")){
							User user = new User();
							user.setAlias(message.getFrom());
							List<User> users = new ArrayList<User>();
							users.add(user);
							Gson sGson=new Gson();
							jsonString=sGson.toJson(users);
						}
						else{
							jsonString= HttpUtil.getUserByPhone(message
									.getFrom());
						}
						
						Message msg = handler.obtainMessage();
						msg.what =1;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
				}).start();
			} else {
				messageFragment.loadData();
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
	 * 消息通知
	 * 
	 * @param message
	 */
	private void notificationTextMessage(EMMessage message, String nick) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String content = "";
		if (message.getType() == Type.TXT) {
			TextMessageBody body = (TextMessageBody) message.getBody();
			content = body.getMessage();
		}
		if (message.getType() == Type.VOICE) {
			content = "[语音]";
		}
		if (message.getType() == Type.IMAGE) {
			content = "[图片]";
		}
		Intent intent = new Intent(this, MainActivity.class);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.appicon)
				.setContentTitle(nick)
				.setContentText(content)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(
						PendingIntent.getActivity(MainActivity.this, 0, intent,
								0)).build();
		// 发出通知
		manager.notify(0, notification);
//		int count = EMChatManager.getInstance().getUnreadMsgsCount();
//		if(count>0)
//		{
//			sendBadgeNumber(String.valueOf(count));
//		}
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
			isLogin = false;
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

	public void setUnreadMessageCount(int count) {
		if (count == 0) {
			countTv.setVisibility(View.GONE);
		} else {
			countTv.setVisibility(View.VISIBLE);
			countTv.setText(String.valueOf(count));
		}
	}
	
	private void sendBadgeNumber(String number) {
		Log.d("sendBadgeNumber", number);
        if (TextUtils.isEmpty(number)) {
            number = "0";
        } else {
            int numInt = Integer.valueOf(number);
            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }
        Log.d("sendBadgeNumber", number);
        Log.d("sendBadgeNumber", Build.MANUFACTURER);
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(number);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSony(number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSamsumg(number);
        } else {
            
        }
    }
 
    private void sendToXiaoMi(String number) {
    	Log.d("sendToXiaoMi", number);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this); 
            builder.setContentTitle("您有"+number+"未读消息");
            builder.setTicker("您有"+number+"未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.design_red_point);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build(); 
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, Integer.valueOf(number));// 设置信息数
            field = notification.getClass().getField("extraNotification"); 
            field.setAccessible(true);
        field.set(notification, miuiNotification);  
        }catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
                Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
                localIntent.putExtra("android.intent.extra.update_application_component_name",getPackageName() + "/"+ lancherActivityClassName );
                localIntent.putExtra("android.intent.extra.update_application_message_text",number);
                sendBroadcast(localIntent);
        }
        finally
        {
          if(notification!=null && isMiUIV6 )
           {
               //miui6以上版本需要使用通知发送
            nm.notify(101010, notification); 
           }
        }
 
    }
 
    private void sendToSony(String number) {
    	Log.d("sendToSony", number);
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",lancherActivityClassName );//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",getPackageName());//包名
        sendBroadcast(localIntent);
 
    }
 
    private void sendToSamsumg(String number) 
    {
    	Log.d("sendToSamsumg", number);
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", number);//数字
        localIntent.putExtra("badge_count_package_name", getPackageName());//包名
        localIntent.putExtra("badge_count_class_name",lancherActivityClassName ); //启动页
        sendBroadcast(localIntent);
    }

	static class MainHandler extends Handler {
		MainActivity activity;

		public MainHandler(MainActivity may) {
			activity = may;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (msg.obj != null) {
					Gson gson = new Gson();
					List<User> users = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<User>>() {
							}.getType());
					if (users != null) {
						activity.notificationTextMessage(activity.currenEmMsg,
								users.get(0).getAlias());
					}
				}
				break;
			case 2:
				try {
					User user1 = (User) GsonUtil.fromJson(msg.obj.toString(),
							User.class);
					if(user1!=null){
						GFToast.show("更新成功");
						GFUserDictionary.saveLoginInfo(user1, GFUserDictionary.getPassword(), activity);
					}
					else{
						GFToast.show("更新失败");
					}
				} catch (Exception e) {
					// TODO: handle exception
					GFToast.show("更新失败");
				}
				break;

			default:
				break;
			}
			
		}
	}


}

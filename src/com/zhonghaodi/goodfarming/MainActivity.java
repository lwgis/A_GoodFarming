package com.zhonghaodi.goodfarming;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

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
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.PointDic;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.utils.PublicHelper;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		EMEventListener {
	HomeFragment homeFragment;
	ForumFragment forumFragment;
	DiscoverFragment discoverFragment;
	MeFragment meFragment;
	ImageView homeIv;	
	ImageView forumIv;
	ImageView discoverIv;
	ImageView meIv;
	TextView homeTv;
	TextView forumTv;
	TextView discoverTv;
	TextView meTv;
	View homeView;	
	View forumView;
	View discoverView;
	View meView;
	int pageIndex;	
	private boolean isLogin = false;
	private MainHandler handler = new MainHandler(this);
	private EMMessage currenEmMsg;
	private final static String lancherActivityClassName = WelcomeActivity.class.getName();
	
	public IWXAPI wxApi;
	public Tencent mTencent;
	SharePopupwindow sharePopupwindow;
	
	private boolean bUpdate;
	private int markString;
	private ProgressBar progressBar;
	private TextView proTextView1;
	private TextView proTextView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		homeView = findViewById(R.id.home_layout);
		forumView = findViewById(R.id.forum_layout);
		discoverView = findViewById(R.id.discover_layout);
		meView = findViewById(R.id.me_layout);
		homeIv = (ImageView) findViewById(R.id.home_image);
		forumIv = (ImageView)findViewById(R.id.forum_image);
		discoverIv = (ImageView) findViewById(R.id.discover_image);
		meIv = (ImageView) findViewById(R.id.me_image);
		homeTv = (TextView) findViewById(R.id.home_text);
		forumTv = (TextView)findViewById(R.id.forum_text);
		discoverTv = (TextView) findViewById(R.id.discover_text);
		meTv = (TextView) findViewById(R.id.me_text);
		homeView.setOnClickListener(this);
		forumView.setOnClickListener(this);
		discoverView.setOnClickListener(this);
		meView.setOnClickListener(this);
		seletFragmentIndex(0);
		pageIndex = 0;
		
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
		
		//检查该不该请求更新
		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
        String updatetime = deviceInfo.getString("updatetime", "");
        if (updatetime.equals("") || updatetime==null) {
			bUpdate = true;
		}
        else{
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	try {
				Date upDate = dateFormat.parse(updatetime);
				Date curDate = new Date();
				bUpdate = checkUpdate(upDate, curDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				bUpdate = false;
			}
        }
		
		loadPointdics();

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
						forumFragment.loadData();
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
	
	public boolean checkUpdate(Date upDate,Date currentDate){
		Calendar cal1 = Calendar.getInstance(); 
	    Calendar cal2 = Calendar.getInstance(); 
	    cal1.setTime(currentDate); 
	    cal2.setTime(upDate);
	    
	    int yi = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
	    if (yi>0) {
			return true;
		}
	    else{
	    	int di = cal1.get(Calendar.DAY_OF_YEAR)-cal2.get(Calendar.DAY_OF_YEAR);
	    	if (di>0) {
				return true;
			}
	    	else{
	    		return false;
	    	}
	    }
	}
	
	public void tryUpdate(){
		markString = getVersion();
		requestVersion();
	}

	/**
	 * 获取版本号
	 * @return
	 */
	public int getVersion(){
		PackageManager packageManager = this.getPackageManager();
		int versionString;
		try {
		PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
		versionString = packageInfo.versionCode;
	} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		versionString = 0;
	}
		return versionString;
	}

	public void requestVersion(){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String jsonString = HttpUtil.getAppVersion();
					Message msg = handler.obtainMessage();
					msg.what = 4;
					msg.obj = jsonString;
					msg.sendToTarget();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj = "";
					msg.sendToTarget();
				}
				
			}
		}).start();
		
	}
	
	/**
	*  
	* 弹出对话框通知用户更新程序  
	*  
	* 弹出对话框的步骤： 
	*  1.创建alertDialog的builder.   
	*  2.要给builder设置属性, 对话框的内容,样式,按钮 
	*  3.通过builder 创建一个对话框 
	*  4.对话框show()出来   
	*/  
	public void showUpdataDialog(final AppVersion appVersion) {  
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());
		String curtime = dateFormat.format(curDate);
		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
		deviceInfo.edit().putString("updatetime", curtime).commit();
		
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
				downLoadApk(appVersion);  
	           dialog.dismiss();
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
	   titleView.setText("版本升级");
	   contentView.setText("检测到新版本，请及时更新");
	   dialog.show();
	} 
	
	/**
	* 从服务器中下载APK 
	*/  
	public void downLoadApk(final AppVersion appVersion) {  
	   final Dialog pd;    //进度条对话框   
	   pd = new  Dialog(this,R.style.MyDialog);  
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	   View layout = inflater.inflate(R.layout.progressdialog, null);
	   pd.setContentView(layout);
	   pd.setCancelable(false);
	   pd.show();  
	   
	   progressBar = (ProgressBar)layout.findViewById(R.id.proBar);
	   proTextView1 = (TextView)layout.findViewById(R.id.pro_bfb);
	   proTextView2 = (TextView)layout.findViewById(R.id.pro_value);
	   
	   new Thread(){  
	       @Override  
	       public void run() {  
	           try {  
	               File file = HttpUtil.getFileFromServer(appVersion.getUrl(), progressBar,handler);  
	               sleep(3000);  
	               Message msg = handler.obtainMessage();
					msg.what = 6;
					msg.obj = file;
					msg.sendToTarget(); 
	               pd.dismiss(); //结束掉进度条对话框   
	           } catch (Exception e) {  
	           	Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj = "下载错误";
					msg.sendToTarget(); 
	               e.printStackTrace();  
	           }  
	       }}.start();  
	}  
	
	/**
	* 安装apk
	* @param file
	*/
	protected void installApk(File file) {  
	   Intent intent = new Intent();  
	   //执行动作   
	   intent.setAction(Intent.ACTION_VIEW);  
	   //执行的数据类型   
	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了    
	   this.startActivity(intent);  
	}
	
	/**
	 * 获取积分字典
	 */
	public void loadPointdics(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getPointdicsString();
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 切换tab
	 * @param i
	 */
	public void seletFragmentIndex(int i) {
		FragmentTransaction transction = getFragmentManager()
				.beginTransaction();
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
			transction.add(R.id.content, homeFragment);
		}
		if (forumFragment == null) {
			forumFragment = new ForumFragment();
			transction.add(R.id.content, forumFragment);
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
		transction.hide(forumFragment);
		transction.hide(discoverFragment);
		transction.hide(meFragment);
		homeIv.setImageResource(R.drawable.home);
		forumIv.setImageResource(R.drawable.tian);
		discoverIv.setImageResource(R.drawable.discover);
		meIv.setImageResource(R.drawable.me);
		homeTv.setTextColor(Color.rgb(128, 128, 128));
		forumTv.setTextColor(Color.rgb(128, 128, 128));
		discoverTv.setTextColor(Color.rgb(128, 128, 128));
		meTv.setTextColor(Color.rgb(128, 128, 128));

		switch (i) {
		case 0:
			transction.show(homeFragment);
			homeIv.setImageResource(R.drawable.home_s);
			homeTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 1:
			transction.show(forumFragment);
			forumIv.setImageResource(R.drawable.tian_s);
			forumTv.setTextColor(Color.rgb(12, 179, 136));
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

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == homeView && pageIndex != 0) {
			seletFragmentIndex(0);
		}
		if (v == forumView && pageIndex != 1) {
			seletFragmentIndex(1);
//			if (GFUserDictionary.getUserId() == null) {
//				return;
//			}
			forumFragment.loadData();
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
		
		switch (v.getId()) {
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show("您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.zhonghaodi.goodfarming";
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "种好地APP:让种地不再难";
			msg.description = "下载APP，享受优惠农资产品，众多专家，农艺师为您解决病虫害问题，让您种地更科学，丰收更简单。";
			Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.drawable.app108);
			msg.thumbData = PublicHelper.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene=SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_circlefriends:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show("您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage1 = new WXWebpageObject();
			webpage1.webpageUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.zhonghaodi.goodfarming";
			WXMediaMessage msg1 = new WXMediaMessage(webpage1);
			msg1.title = "种好地APP:让种地不再难";
			msg1.description = "下载APP，享受优惠农资产品，众多专家，农艺师为您解决病虫害问题，让您种地更科学，丰收更简单。";
			Bitmap thumb1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.app108);
			msg1.thumbData = PublicHelper.bmpToByteArray(thumb1, true);
			
			SendMessageToWX.Req req1 = new SendMessageToWX.Req();
			req1.transaction = buildTransaction("webpage");
			req1.message = msg1;
			req1.scene=SendMessageToWX.Req.WXSceneTimeline;
			wxApi.sendReq(req1);
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_qq:
			Bundle params = new Bundle();
		    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		    params.putString(QQShare.SHARE_TO_QQ_TITLE, "种好地APP:让种地不再难");
		    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "下载APP，享受优惠农资产品，众多专家，农艺师为您解决病虫害问题，让您种地更科学，丰收更简单。");
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://a.app.qq.com/o/simple.jsp?pkgname=com.zhonghaodi.goodfarming");
		    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://121.40.62.120/appimage/apps/appicon.png");
		    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
		    mTencent.shareToQQ(this, params, new BaseUiListener());
		    sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_qzone:
			Bundle params1 = new Bundle();
			params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, "种好地APP:让种地不再难");//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "下载APP，享受优惠农资产品，众多专家，农艺师为您解决病虫害问题，让您种地更科学，丰收更简单。");//选填
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=com.zhonghaodi.goodfarming");//必填
		    ArrayList<String> urlsList = new ArrayList<String>();
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_1_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_2_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_3_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_4_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_5_1440519318/550");
		    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlsList);
		    mTencent.shareToQzone(this, params1, new BaseUiListener());
		    sharePopupwindow.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isLogin) {
			initEm();
		} else {
			
			if(pageIndex==3){
				meFragment.loadData();
			}
		}
		homeFragment.setUnreadMessageCount();
		if(bUpdate){
			tryUpdate();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==3) {
			seletFragmentIndex(3);
			initEm();
			
		}
		if(resultCode==4){
			seletFragmentIndex(3);
			initEm();
			
			Intent it1 = new Intent(this,
					SelectCropActivity.class);
			
			this.startActivityForResult(it1, 100);
		}
		if (resultCode == 2) {
			forumFragment.loadData();
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
		mTencent.onActivityResult(requestCode, resultCode, data);
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
						msg.what =5;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
				}).start();
			} else {
				forumFragment.loadData();
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
		Intent intent = new Intent(this, MessagesActivity.class);
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
    
    public void popwindow(){
    	sharePopupwindow = new SharePopupwindow(this,this);
    	sharePopupwindow.setFocusable(true);
    	sharePopupwindow.setOutsideTouchable(true);
    	sharePopupwindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	sharePopupwindow.setBackgroundDrawable(dw);
		sharePopupwindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    
    private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
    
    class BaseUiListener implements IUiListener {
		
		protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
		}
		@Override
		public void onCancel() {
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
		}
		
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
			case 5:
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
				
			case 3:
				if (msg.obj != null) {
					Gson gson = new Gson();
					List<PointDic> pointdics = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<PointDic>>() {
							}.getType());
					if (pointdics != null && pointdics.size()>0) {
						GFPointDictionary.savePointDics(pointdics);
					}
				}
				break;
			case 4:
				if(msg.obj==null){
					return;
				}
				Gson gson = new Gson();
				AppVersion appVersion = gson.fromJson(msg.obj.toString(),
						new TypeToken<AppVersion>() {
						}.getType());
				if(activity.markString<appVersion.getVersion()){
					activity.showUpdataDialog(appVersion);
				}
				else{
				}
				break;
			case 1:
				int[] values = (int[])msg.obj;
				float bf = (values[0]*100*1.000f/values[1]);
				bf = (float)(Math.round(bf*100))/100;
				activity.proTextView1.setText(bf+"%");
				float pf = (values[0]*1.000f/(1024*1024));
				pf = (float)(Math.round(pf*100))/100;
				float cf = (values[1]*1.000f/(1024*1024));
				cf = (float)(Math.round(cf*100))/100;
				activity.proTextView2.setText(pf+"M/"+cf+"M");
				break;
			case 6:
				File file = (File)msg.obj;
				activity.installApk(file);
				break;
			case -1:
				String errString = msg.obj.toString();
				GFToast.show(errString);
				break;
			default:
				break;
			}
			
		}
	}
}

package com.zhonghaodi.goodfarming;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.nostra13.universalimageloader.core.ImageLoader;
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
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.api.ShareContainer;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.goodfarming.WelcomeActivity.WelcomeLocationListenner;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GFVersionAndAds;
import com.zhonghaodi.model.PointDic;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.req.MainReq;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;
import com.zhonghaodi.view.MainView;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements MainView, OnClickListener,
		EMEventListener,ShareContainer {
	private HomeFragment homeFragment;
	private SaveFragment saveFragment;
	private DiscoverFragment discoverFragment;
	private MeFragment meFragment;
	private ImageView homeIv;	
	private ImageView fineIv;
	private ImageView discoverIv;
	private ImageView meIv;
	private TextView homeTv;
	private TextView fineTv;
	private TextView discoverTv;
	private TextView meTv;
	private View homeView;	
	private View fineView;
	private View discoverView;
	private View meView;
	private int pageIndex;	
	private boolean isLogin = false;
	private EMMessage currenEmMsg;
	private final static String lancherActivityClassName = WelcomeActivity.class.getName();
	private boolean bUpdate;
	private int markString;
	private ProgressBar progressBar;
	private TextView proTextView1;
	private TextView proTextView2;
	private long lastClick;
	public  IWXAPI wxApi;
	public  Tencent mTencent;
	private Question shareQue;
	public  SharePopupwindow sharePopupwindow;
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	private TextView ndsText;
	private Dialog pd;	
	// 定位相关
	private LocationClient mLocClient;
	public MainLocationListenner myListener = new MainLocationListenner();
	
	public ArrayList<Question> allQuestions;
	public ArrayList<Question> fineQuestions;	
	private MainReq req;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
		req = new MainReq(this);
		allQuestions = new ArrayList<Question>();
		fineQuestions = new ArrayList<Question>();
		homeView = findViewById(R.id.home_layout);
		fineView = findViewById(R.id.fine_layout);
		discoverView = findViewById(R.id.discover_layout);
		meView = findViewById(R.id.me_layout);
		homeIv = (ImageView) findViewById(R.id.home_image);
		fineIv = (ImageView)findViewById(R.id.fine_image);
		discoverIv = (ImageView) findViewById(R.id.discover_image);
		meIv = (ImageView) findViewById(R.id.me_image);
		homeTv = (TextView) findViewById(R.id.home_text);
		fineTv = (TextView)findViewById(R.id.fine_text);
		discoverTv = (TextView) findViewById(R.id.discover_text);
		meTv = (TextView) findViewById(R.id.me_text);
		ndsText = (TextView)findViewById(R.id.ndes_text);
		homeView.setOnClickListener(this);
		fineView.setOnClickListener(this);
		discoverView.setOnClickListener(this);
		meView.setOnClickListener(this);
		seletFragmentIndex(0);
		pageIndex = 0;
		//定位
		location();
		
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
        
      //注册一个广播接收器
        receiver = new BroadcastReceiver() {
	    	@Override
	        public void onReceive(Context ctx, Intent intent) {
	    		if (intent.getAction().equals("sharequestion")) {
	    				shareQue = (Question)intent.getSerializableExtra("question");
	    				String url = intent.getStringExtra("url");
	    				UILApplication.sharefolder = intent.getStringExtra("folder");
	    				UILApplication.sharestatus=1;
	    				String[] strs = url.split("id=");
	    				if(strs!=null && strs[1]!=null){
	    					UILApplication.sharequeid = Integer.parseInt(strs[1].trim());
	    				}	    				
	    				Bitmap bitmap = UILApplication.sharebit;
	    				if(shareQue==null){
	    					return;
	    				}
	    				if(bitmap==null){
	    					bitmap =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
	    				}
	    				Bitmap b = Bitmap.createScaledBitmap(bitmap, PublicHelper.WX_THUMB_SIZE, PublicHelper.WX_THUMB_SIZE, true);
	    				setShareToCircle(url,b);
	    				UILApplication.sharebit = null;
	 	    		}
	    	}
	    };
	    filter = new IntentFilter();
	    filter.addAction("sharequestion");
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
        
        wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
		req.loadPointdics();
		String auth = GFUserDictionary.getAuth(this);
		if(TextUtils.isEmpty(auth)){
			String phone = GFUserDictionary.getPhone(this);
			if(!TextUtils.isEmpty(phone)){
				Intent loginit1 = new Intent(MainActivity.this,  
		                LoginActivity.class);  
				loginit1.putExtra("index", 1);
		        this.startActivity(loginit1);  

			}			
		}
		String userid = GFUserDictionary.getUserId(this);
		if(!TextUtils.isEmpty(userid)){
			req.loadUser(userid);
		}
	}

	
	/**
	 * 初始化环信
	 */
	public void initEm() {
		if (GFUserDictionary.getUserId(getApplicationContext()) == null)
			return;
		EMChatManager.getInstance().login(GFUserDictionary.getPhone(getApplicationContext()),
				GFUserDictionary.getPassword(getApplicationContext()), new EMCallBack() {

					@Override
					public void onSuccess() {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						EMChatManager.getInstance().updateCurrentUserNick(
								GFUserDictionary.getAlias(getApplicationContext()));
						EMChatManager.getInstance().addConnectionListener(
								new MyConnectionListener());
						EMChatManager
								.getInstance()
								.registerEventListener(
										MainActivity.this,
										new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
						EMChat.getInstance().setAppInited();
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
		req.requestVersion();
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
	   //进度条对话框   
	   pd = new  Dialog(this,R.style.MyDialog);  
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	   View layout = inflater.inflate(R.layout.progressdialog, null);
	   pd.setContentView(layout);
	   pd.setCancelable(false);
	   pd.show();  
	   
	   progressBar = (ProgressBar)layout.findViewById(R.id.proBar);
	   proTextView1 = (TextView)layout.findViewById(R.id.pro_bfb);
	   proTextView2 = (TextView)layout.findViewById(R.id.pro_value);
	   
	   req.downLoad(appVersion, progressBar);
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
	* 切换tab
	* @param i
	*/
	public void seletFragmentIndex(int i) {
		
		FragmentTransaction transction = getSupportFragmentManager().beginTransaction();
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
			homeFragment.setShareContainer(this);
		}

		if(saveFragment == null){
			saveFragment = new SaveFragment();
		}
		if (discoverFragment == null) {
			discoverFragment = new DiscoverFragment();
			discoverFragment.setShareContainer(this);
		}
		if (meFragment == null) {
			meFragment = new MeFragment();
//			meFragment.setShareContainer(this);
		}
		
		homeIv.setImageResource(R.drawable.home);
		fineIv.setImageResource(R.drawable.fine1);
		discoverIv.setImageResource(R.drawable.discover);
		meIv.setImageResource(R.drawable.me);
		homeTv.setTextColor(Color.rgb(128, 128, 128));
		fineTv.setTextColor(Color.rgb(128, 128, 128));
		discoverTv.setTextColor(Color.rgb(128, 128, 128));
		meTv.setTextColor(Color.rgb(128, 128, 128));
	
		switch (i) {
		case 0:
			if (homeFragment == null) {
				homeFragment = new HomeFragment();
				homeFragment.setShareContainer(this);
				transction.replace(R.id.content, homeFragment);
			}
			else{
				transction.replace(R.id.content, homeFragment);
			}
			homeIv.setImageResource(R.drawable.home_s);
			homeTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 1:
			if(saveFragment == null){
				saveFragment = new SaveFragment();
			}
			transction.replace(R.id.content, saveFragment);
			fineIv.setImageResource(R.drawable.fine1_s);
			fineTv.setTextColor(Color.rgb(12, 179, 136));
			break;

		case 3:
			if (discoverFragment == null) {
				discoverFragment = new DiscoverFragment();
				discoverFragment.setShareContainer(this);
			}
			transction.replace(R.id.content, discoverFragment);
			discoverIv.setImageResource(R.drawable.discover_s);
			discoverTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 4:
			if (meFragment == null) {
				meFragment = new MeFragment();
//				meFragment.setShareContainer(this);
			}
			transction.replace(R.id.content, meFragment);
			meIv.setImageResource(R.drawable.me_s);
			meTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		default:
			break;
		}
		transction.commit();
		pageIndex = i;
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	@Override
	public void popupShareWindow(User user) {
		// TODO Auto-generated method stub
		
		UILApplication.sharestatus = 0;
		sharePopupwindow = new SharePopupwindow(this,this);
    	sharePopupwindow.setFocusable(true);
    	sharePopupwindow.setOutsideTouchable(true);
    	sharePopupwindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	sharePopupwindow.setBackgroundDrawable(dw);
		sharePopupwindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	@Override
	public void shareQuestionWindow(Question question,String folder) {
		// TODO Auto-generated method stub
		shareQue=question;
		UILApplication.sharestatus = 1;
		UILApplication.sharefolder = folder;
		UILApplication.sharequeid= question.getId();
		sharePopupwindow = new SharePopupwindow(this,this);
    	sharePopupwindow.setFocusable(true);
    	sharePopupwindow.setOutsideTouchable(true);
    	sharePopupwindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	sharePopupwindow.setBackgroundDrawable(dw);
		sharePopupwindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	@Override
	public void shareWeixin(String url, String title, String des, Bitmap thumb) {
		// TODO Auto-generated method stub
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		UILApplication.shareUrl = webpage.webpageUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = des;
		msg.thumbData = PublicHelper.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene=SendMessageToWX.Req.WXSceneSession;
		wxApi.sendReq(req);
	}


	@Override
	public void shareCirclefriends(String url, String title, String des, Bitmap thumb) {
		// TODO Auto-generated method stub
		WXWebpageObject webpage1 = new WXWebpageObject();
		webpage1.webpageUrl = url;
		UILApplication.shareUrl = webpage1.webpageUrl;
		WXMediaMessage msg1 = new WXMediaMessage(webpage1);
		msg1.title = title;
		msg1.description = des;
		msg1.thumbData = PublicHelper.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req1 = new SendMessageToWX.Req();
		req1.transaction = buildTransaction("webpage");
		req1.message = msg1;
		req1.scene=SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req1);
	}


	@Override
	public void shareQQ(String url, String title, String des, String imgUrl) {
		// TODO Auto-generated method stub
		Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
	    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  des);
	    UILApplication.shareUrl= url;
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl );
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,imgUrl);
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
	    mTencent.shareToQQ(this, params, new BaseUiListener());
	}


	@Override
	public void shareQZone(String url, String title, String des, ArrayList<String> urList,String img1) {
		// TODO Auto-generated method stub
		Bundle params1 = new Bundle();
		params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
	    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
	    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);//选填
	    UILApplication.shareUrl= url;
	    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl);//必填
	    if(img1!=null){
	    	params1.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, img1);
	    }
	    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urList);
	    mTencent.shareToQzone(this, params1, new BaseUiListener());
	}
	
	private void setShareToCircle(String url,Bitmap bitmap){
		
		String content;
		if(shareQue.getContent().length()>40){
			content = shareQue.getContent().substring(0, 40);
		}
		else{
			content = shareQue.getContent();
		} 
		
		shareCirclefriends(url, content, content, bitmap);
	}
	
	public boolean preChangeStatus(){
		if(System.currentTimeMillis() - lastClick > 1000)
			return true;
		else {
			return false;
		}
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//大于一秒方个通过  
        if (!preChangeStatus())  
        {  
            return;  
        }  
        lastClick = System.currentTimeMillis(); 
		if (v == homeView && pageIndex != 0) {
			seletFragmentIndex(0);
			
		}
		if (v == fineView && pageIndex != 1) {

			seletFragmentIndex(1);
			
		}

		if (v == discoverView && pageIndex != 3) {

			seletFragmentIndex(3);
			
		}
		if (v == meView && pageIndex != 4) {
			if (GFUserDictionary.getUserId(getApplicationContext()) == null) {
				Intent it = new Intent();
				it.setClass(this, SignActivity.class);
				startActivityForResult(it, 0);
				return;
			}
			seletFragmentIndex(4);
		}
		
		switch (v.getId()) {
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			if(UILApplication.sharestatus==0){
				shareWeixin(HttpUtil.ViewUrl+"appshare?code="+UILApplication.user.getTjCode(), "种好地APP:让种地不再难",
						"下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。", 
						BitmapFactory.decodeResource(this.getResources(), R.drawable.app108));
			}
			else{
				String url=HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
				String title ="种好地APP：让种地不再难";
				String content;
				if(shareQue.getContent().length()>40){
					content = shareQue.getContent().substring(0, 40);
				}
				else{
					content = shareQue.getContent();
				} 
				Bitmap b;
				if(shareQue.getAttachments()==null || shareQue.getAttachments().size()==0){
					b =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
					
				}
				else{
					String path;
					String img;
					if(UILApplication.sharefolder.equals("plantinfo")){
						img = HttpUtil.ImageUrl+"plant/small/"+ shareQue.getAttachments().get(0).getUrl();
					}
					else{
						img = HttpUtil.ImageUrl+"questions/small/"+ shareQue.getAttachments().get(0).getUrl();
					}
					
					b=ImageLoader.getInstance().loadImageSync(img);
				}
				Bitmap bitmap = Bitmap.createScaledBitmap(b, PublicHelper.WX_THUMB_SIZE, PublicHelper.WX_THUMB_SIZE, true);
				shareWeixin(url, title, content, bitmap);
			}
			
			sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_circlefriends:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			if(UILApplication.sharestatus==0){
				shareCirclefriends(HttpUtil.ViewUrl+"appshare?code="+UILApplication.user.getTjCode(), 
						"种好地APP:让种地不再难", 
						"下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。", 
						BitmapFactory.decodeResource(this.getResources(), R.drawable.app108));
			}else{
				String url=HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
				Bitmap b;
				if(shareQue.getAttachments()==null || shareQue.getAttachments().size()==0){
					b =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
					
				}
				else{
					String img;
					if(UILApplication.sharefolder.equals("plantinfo")){
						img = HttpUtil.ImageUrl+"plant/small/"+ shareQue.getAttachments().get(0).getUrl();
					}
					else{
						img = HttpUtil.ImageUrl+"questions/small/"+ shareQue.getAttachments().get(0).getUrl();
					}					
					b=ImageLoader.getInstance().loadImageSync(img);
					if(b==null){
						b =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
					}
				}
				Bitmap bitmap = Bitmap.createScaledBitmap(b, PublicHelper.WX_THUMB_SIZE, PublicHelper.WX_THUMB_SIZE, true);
				setShareToCircle(url,bitmap);
			}
			
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_qq:
			if(UILApplication.sharestatus==0){
				shareQQ(HttpUtil.ViewUrl+"appshare?code="+UILApplication.user.getTjCode(),
			    		"种好地APP:让种地不再难", 
			    		"下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。", 
			    		HttpUtil.ImageUrl+"apps/appicon.png");
			}else{
				String url = HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
				String title ="种好地APP：让种地不再难";
				String content2;
				if(shareQue.getContent().length()>40){
					content2 = shareQue.getContent().substring(0, 40);
				}
				else{
					content2 = shareQue.getContent();
				}
				String img;
				if(shareQue.getAttachments()!=null && shareQue.getAttachments().size()>0){
					if(UILApplication.sharefolder.equals("plantinfo")){
						img = HttpUtil.ImageUrl+"plant/small/"+ shareQue.getAttachments().get(0).getUrl();
					}
					else{
						img = HttpUtil.ImageUrl+"questions/small/"+ shareQue.getAttachments().get(0).getUrl();
					}
				}
				else{
					img = HttpUtil.ImageUrl+"apps/appicon.png";
				}
				
				shareQQ(url, title, content2, img);
			}		    
		    sharePopupwindow.dismiss();			
			break;
		case R.id.img_share_qzone:
			if(UILApplication.sharestatus==0){
				ArrayList<String> urlsList = new ArrayList<String>();
			    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_1_1440519318/550");
			    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_2_1440519318/550");
			    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_3_1440519318/550");
			    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_4_1440519318/550");
			    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_5_1440519318/550");
			    shareQZone(HttpUtil.ViewUrl+"appshare?code="+UILApplication.user.getTjCode(), 
			    		"种好地APP:让种地不再难", 
			    		"下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。", 
			    		urlsList,null);
			}
			else{
				String url = HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
				String title ="种好地APP：让种地不再难";
				String content2;
				if(shareQue.getContent().length()>40){
					content2 = shareQue.getContent().substring(0, 40);
				}
				else{
					content2 = shareQue.getContent();
				}
				ArrayList<String> urlsList = new ArrayList<String>();		    
			    String imgurl1;
			    if(shareQue.getAttachments()!=null && shareQue.getAttachments().size()>0){
			    	if(UILApplication.sharefolder.equals("plantinfo")){
			    		imgurl1 = HttpUtil.ImageUrl+"plant/small/"
								+ shareQue.getAttachments().get(0)
								.getUrl();
			    	}
			    	else{
			    		imgurl1 = HttpUtil.ImageUrl+"questions/small/"
								+ shareQue.getAttachments().get(0)
								.getUrl();
			    	}
			    	for(int i=0;i<shareQue.getAttachments().size();i++){
			    		if(UILApplication.sharefolder.equals("plantinfo")){
			    			urlsList.add(HttpUtil.ImageUrl+"plant/small/"
									+ shareQue.getAttachments().get(i)
									.getUrl());
			    		}
			    		else{
			    			urlsList.add(HttpUtil.ImageUrl+"questions/small/"
									+ shareQue.getAttachments().get(i)
									.getUrl());
			    		}
			    	}
			    }
			    else{
			    	imgurl1 = HttpUtil.ImageUrl+"apps/appicon.png";
			    	urlsList.add(HttpUtil.ImageUrl+"apps/appicon.png");
			    }
			    shareQZone(url,title,content2,urlsList,imgurl1);
			}
		    
		    sharePopupwindow.dismiss();
			break;

		default:
			break;
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		registerReceiver(receiver, filter);
		if (!isLogin) {
			initEm();
		} else {		

		}
		
		if(bUpdate){
			tryUpdate();
		}
		if(GFVersionAndAds.getAll(this)){
			ndsText.setVisibility(View.VISIBLE);
		}
		else{
			ndsText.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		unregisterReceiver(receiver);
	}


	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void location() {
		
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==3) {
			seletFragmentIndex(4);
			initEm();
			
		}
		if(resultCode==4){
			seletFragmentIndex(4);
			initEm();
			
			Intent it1 = new Intent(this,
					SelectCropActivity.class);
			
			this.startActivityForResult(it1, 100);
		}
//		if (resultCode == 2) {
//			forumFragment.loadData();
//		}
		if(resultCode == RESULT_OK){
			if(requestCode==100){
				ArrayList<Crop> selectCrops = data.getParcelableArrayListExtra("crops");
				UILApplication.user.setCrops(null);
				if (selectCrops!=null&&selectCrops.size()>0) {
					String cropString = "";
					List<UserCrop> userCrops = new ArrayList<UserCrop>();
					for (Crop c : selectCrops) {
						cropString = cropString + c.getName() + "  ";
						UserCrop userCrop = new UserCrop();
						userCrop.setCrop(c);
						userCrops.add(userCrop);
					}
					UILApplication.user.setCrops(userCrops);
				}
				req.updateCrops(UILApplication.user);
			}
			else if(requestCode==PublicHelper.CITY_REQUEST_CODE){
				//切换区域后设置标题，重新读取问题
				homeFragment.resetArea();
			}
			
		}
		mTencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
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
				GFUserDictionary.removeUserInfo(getApplicationContext());
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
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			final EMMessage message = (EMMessage) event.getData();
			currenEmMsg = message;
			req.customEmFunction(MainActivity.this, message);
			break;
		}
		case EventOfflineMessage: {
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
	@Override
	public void notificationTextMessage(String nick) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String content = "";
		if (currenEmMsg.getType() == Type.TXT) {
			TextMessageBody body = (TextMessageBody) currenEmMsg.getBody();
			content = body.getMessage();
		}
		if (currenEmMsg.getType() == Type.VOICE) {
			content = "[语音]";
		}
		if (currenEmMsg.getType() == Type.IMAGE) {
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
						
					} else if (error == EMError.CONNECTION_CONFLICT) {
					
					} else {
						if (NetUtils.hasNetwork(MainActivity.this))
							;
						else
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
    
    class BaseUiListener implements IUiListener {
		
		protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
//			GFToast.show(MainActivity.this, "分享失败");
		}
		@Override
		public void onCancel() {
//			GFToast.show(MainActivity.this, "分享取消");
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			MobclickAgent.onEvent(MainActivity.this, UmengConstants.APP_SHARE_ID);
			req.sharePoint(getApplicationContext(), shareQue);
			
		}
		
	}


	@Override
	public void compareVersion(AppVersion version) {
		// TODO Auto-generated method stub
		if(markString<version.getVersion()){
			showUpdataDialog(version);
		}
	}


	@Override
	public void downComplete(File file) {
		// TODO Auto-generated method stub
		pd.dismiss();
		installApk(file);
	}


	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(this, mess);
	}

	@Override
	public void savePointDics(List<PointDic> dics) {
		// TODO Auto-generated method stub
		GFPointDictionary.savePointDics(this,dics);
	}


	@Override
	public void saveUserInfo(User user) {
		// TODO Auto-generated method stub
		GFUserDictionary.saveLoginInfo(this,user, GFUserDictionary.getPassword(this),
				this,GFUserDictionary.getAuth(this));
	}


	@Override
	public void setUnredMessageCount() {
		// TODO Auto-generated method stub
		homeFragment.setUnreadMessageCount();
	}


	@Override
	public void displayProgress(float bf, float pf, float cf) {
		// TODO Auto-generated method stub
		
		proTextView1.setText(bf+"%");
		proTextView2.setText(pf+"M/"+cf+"M");
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MainLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}				
			UILApplication.x=location.getLongitude();
			UILApplication.y=location.getLatitude();
//			x=118.798632;
//			y=36.858719;
//			UILApplication.x = 118.40;
//			UILApplication.y = 36.40;
			mLocClient.stop();
			
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
}

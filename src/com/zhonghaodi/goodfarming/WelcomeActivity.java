package com.zhonghaodi.goodfarming;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.CustomProgressDialog;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SettingPopupwindow;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.goodfarming.StoresActivity.StoreLocationListenner;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.CityDto;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.Manifest.permission;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements HandMessage {
	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private GFHandler<WelcomeActivity> handler = new GFHandler<WelcomeActivity>(this);
	// 定位相关
	private LocationClient mLocClient;
	public WelcomeLocationListenner myListener = new WelcomeLocationListenner();
	private double x,y;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_welcome);
		if(!UILApplication.checkNetworkState()){
			GFToast.show(getApplicationContext(),"没有有效的网络连接");
		}
		int zone = GFAreaUtil.getCityId(this);
		if(zone==0){
			location();
		}
		else{
			sleep();
		}
		
	}
                  
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("启动页");
		MobclickAgent.onResume(this);
//		 new Handler().postDelayed(new Runnable(){  
//		     public void run() {  
//		     //execute the task  
//		    	 popupwindow();
//		     }  
//		  }, 1000); 
		
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("启动页");
		MobclickAgent.onPause(this);
	}
	public void popupwindow(){
		final SettingPopupwindow settingPopupwindow = new SettingPopupwindow(this);
		OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sharedPre = WelcomeActivity.this.getSharedPreferences("test",
						Context.MODE_PRIVATE);
				String surl = settingPopupwindow.serviceEditText.getText().toString();
				String iurl = settingPopupwindow.imageEditText.getText().toString();
				if(!surl.isEmpty()){
					HttpUtil.RootURL = surl;
				}
				if(!iurl.isEmpty()){
					HttpUtil.ImageUrl = iurl;
				}
				// 获取Editor对象
				Editor editor = sharedPre.edit();
				// 设置参数
				editor.putString("serviceurl", HttpUtil.RootURL);
				editor.putString("imageurl", HttpUtil.ImageUrl);
				// 提交
				editor.commit();
				settingPopupwindow.dismiss();
				sleep();
			}
		};
		settingPopupwindow.setlistener(clickListener);
		SharedPreferences sharedPre = WelcomeActivity.this.getSharedPreferences("test",
				Context.MODE_PRIVATE);
		String serviceurl = sharedPre.getString("serviceurl", "");
		if(serviceurl.isEmpty()){
			serviceurl = HttpUtil.RootURL;
		}
		String imageurl = sharedPre.getString("imageurl", "");
		if(imageurl.isEmpty())
		{
			imageurl = HttpUtil.ImageUrl;
		}
		settingPopupwindow.serviceEditText.setText(serviceurl);
		settingPopupwindow.imageEditText.setText(imageurl);
		settingPopupwindow.showAtLocation(findViewById(R.id.welcome), 
				Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
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
	
	private void sleep(){
		new Handler().postDelayed(new Runnable() {  
            public void run() {  
                 
                tomain();
            }  
  
        }, SPLASH_DISPLAY_LENGHT); 
	}
	
	private void tomain(){
		Intent mainIntent = new Intent(WelcomeActivity.this,  
                MainActivity.class);  
        WelcomeActivity.this.startActivity(mainIntent);  

        WelcomeActivity.this.finish(); 
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class WelcomeLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			x=location.getLongitude();
			y=location.getLatitude();
//			x=118.780813;
//			y=36.815181;
			loadArea();
			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	public void loadArea(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAreaString(x, y);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}

	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				CityDto areadto = gson.fromJson(msg.obj.toString(),
						new TypeToken<CityDto>() {
						}.getType());
				if(areadto!=null && areadto.isResult()){
					GFAreaUtil.saveAreaInfo(this, areadto.getZone());
				}
				else{
					GFAreaUtil.saveAreaInfo(this, new City());
				}
				
			} else {
				GFAreaUtil.saveAreaInfo(this, new City());
			}
			tomain();
			break;
		case -1:
			String errString = msg.obj.toString();
			GFToast.show(getApplicationContext(),errString);
			tomain();
			break;
		default:
			break;
		}
	}

}

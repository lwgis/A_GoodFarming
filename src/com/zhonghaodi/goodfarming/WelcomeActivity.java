package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Advertising;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.CityDto;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.GFVersionAndAds;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.FileUtils;
import com.zhonghaodi.utils.StoredData;
import com.zhonghaodi.networking.HttpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements HandMessage {
	
	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private GFHandler<WelcomeActivity> handler = new GFHandler<WelcomeActivity>(this);
	// 定位相关
	private LocationClient mLocClient;
	public WelcomeLocationListenner myListener = new WelcomeLocationListenner();
	private double x,y;
	private ImageView img;
	private TextView timer_text;
	private boolean isad;
	private int recLen = 3;
	Timer timer = new Timer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_welcome);
		isad = false;
		img = (ImageView)findViewById(R.id.ad_image);
		timer_text = (TextView)findViewById(R.id.timer_count);
		timer_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timer.cancel(); 
				tomain();
			}
		});
		StoredData.getThis().markOpenApp();
		if(!UILApplication.checkNetworkState()){
			GFToast.show(getApplicationContext(),"没有有效的网络连接");
		}
		
		int zone = GFAreaUtil.getCity(this);
		if(zone==0){
			location();
		}
		long delta_time = (new Date().getTime())-GFVersionAndAds.getAdstime(this);
		if(delta_time>(60*60*1000)){
			loadAdvertising();
		}
		else{
			tomain();
		}	
	}
                  
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("启动页");
		MobclickAgent.onResume(this);

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("启动页");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
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
	
	private void sleep(){
		new Handler().postDelayed(new Runnable() {  
            public void run() {  
                if(!isad){
                	tomain();
                } 
                else{
                	countdown();
                }
            }  
  
        }, SPLASH_DISPLAY_LENGHT); 
	}
	
	private void countdown(){
		timer_text.setVisibility(View.VISIBLE);
    	timer.schedule(task, 1000, 1000);
	}
	
	private void tomain(){
		
		if(StoredData.getThis().isFirstOpen()){
			
			Intent mainIntent = new Intent(WelcomeActivity.this,  
	                VersionActivity.class);  
	        WelcomeActivity.this.startActivity(mainIntent);  

	        WelcomeActivity.this.finish(); 
		}
		else{
			Intent mainIntent = new Intent(WelcomeActivity.this,  
	                MainActivity.class);  
	        WelcomeActivity.this.startActivity(mainIntent);  

	        WelcomeActivity.this.finish(); 
		}
		
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class WelcomeLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}				
			x=location.getLongitude();
			y=location.getLatitude();
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
	
	public void loadAdvertising(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAdvertising();
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void displaydeafult(){
		img.setImageResource(R.drawable.welcome);
	}
	
	private void displayAdvertisings(List<Advertising> ads){
		
		Advertising advertising = chanceSelect(ads);
		
        try {
			FileUtils.cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/goodfarming/ads/");
			if (!FileUtils.cacheDir.exists()) {
				FileUtils.cacheDir.mkdirs();
			}
			File imagefile = new File(FileUtils.cacheDir + File.separator + advertising.getUrl());
			if (!imagefile.exists()) {
				displaydeafult();
				sleep();
				UILApplication application = (UILApplication) getApplication();
				application.downloadAds(ads);
			} else {
				ImageLoader.getInstance()
						.displayImage("file://" + FileUtils.cacheDir + File.separator + advertising.getUrl(), img);
				isad = true;
				long time = (new Date()).getTime();
				GFVersionAndAds.saveAdstime(this, time);
				sleep();
			} 
		} catch (Exception e) {
			// TODO: handle exception
			displaydeafult();
			sleep();
		}
	}
	
	public Advertising chanceSelect(List<Advertising> ads){
		Integer sum = 0;
		List<Advertising> advertisings = new ArrayList<Advertising>();
		for (Iterator iterator = ads.iterator(); iterator.hasNext();) {
			Advertising advertising = (Advertising) iterator.next();
			sum+=advertising.getSort();
			for(int i=0;i<advertising.getSort();i++){
				advertisings.add(advertising);
			}
		}
		Collections.shuffle(advertisings);
		Integer rand=new Random().nextInt(sum);
		return advertisings.get(rand);
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
			break;
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				List<Advertising> advertisings = gson.fromJson(msg.obj.toString(), 
						new TypeToken<List<Advertising>>() {}.getType());
				if(advertisings!=null && advertisings.size()>0){
					displayAdvertisings(advertisings);
				}
				else{
					displaydeafult();
					sleep();
				}
			}
			else{
				displaydeafult();
				sleep();
			}
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
	
	TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
  
            runOnUiThread(new Runnable() {      // UI thread  
                @Override  
                public void run() {  
                    recLen--;  
                    timer_text.setText(recLen+"跳过");  
                    if(recLen < 1){  
                        timer.cancel();  
                        timer_text.setVisibility(View.GONE);  
                        tomain();
                    }  
                }  
            });  
        }  
    };

}

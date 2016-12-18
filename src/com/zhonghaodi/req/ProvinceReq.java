package com.zhonghaodi.req;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.CityDto;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.Province;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.ProvinceView;

import android.content.Context;
import android.os.Message;

public class ProvinceReq implements HandMessage {
	private ProvinceView view;
	private GFHandler<ProvinceReq> handler;
	private List<Province> provinces;
	private List<Province> cities;
	private List<Province> counties;
	private String proname;
	private String cityname;
	
	// 定位相关
	private LocationClient mLocClient;
	private double x,y;
	public ProvinceLocationListenner myListener = new ProvinceLocationListenner();
	private Context mContext;
	
	public ProvinceReq(ProvinceView v,Context context){
		view = v;
		handler = new GFHandler<ProvinceReq>(this);
		mContext = context;
	}
	
	/**
	 * 请求所有省份
	 */
	public void loadProvince(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "region/province";
				String jsonString =HttpUtil.executeHttpGet(urlString);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void backtoProvinces(){
		view.displayProvinces(provinces,1);
	}
	
	public void loadCities(final long code,String pname){
		proname = pname;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "region/city?pcode="+code;
				String jsonString =HttpUtil.executeHttpGet(urlString);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void backtoCities(){
		view.displayCities(cities, proname,1);
	}
	
	public void loadCounties(final long code,String cname){
		cityname = cname;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "region/county?pcode="+code;
				String jsonString =HttpUtil.executeHttpGet(urlString);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void location() {
		
		mLocClient = new LocationClient(mContext);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	public void loadArea(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAreaString(x, y);
				Message msg = handler.obtainMessage();
				msg.what = 3;
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
				provinces = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Province>>() {
						}.getType());
				if(provinces==null){
					view.showMessage("获取区域失败");
				}
				else{
					view.displayProvinces(provinces,0);
				}
				
			} else {
				view.showMessage("获取区域失败");
			}
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				cities = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Province>>() {
						}.getType());
				if(cities==null){
					view.showMessage("获取区域失败");
				}
				else{
					view.displayCities(cities,proname,0);
				}
				
			} else {
				view.showMessage("获取区域失败");
			}
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				counties = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Province>>() {
						}.getType());
				if(counties==null){
					view.showMessage("获取区域失败");
				}
				else{
					view.displayCounties(counties,cityname,0);
				}
				
			} else {
				view.showMessage("获取区域失败");
			}
			break;
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				CityDto areadto = gson.fromJson(msg.obj.toString(),
						new TypeToken<CityDto>() {
						}.getType());
				if(areadto!=null && areadto.isResult()){
					view.displayGPSZone((int)areadto.getZone().getCode(),areadto.getZone().getName());
				}
				else{
					view.displayGPSZone(0,"无");
				}
				
			} else {
				view.displayGPSZone(0,"无");
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class ProvinceLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}				
			x=location.getLongitude();
			y=location.getLatitude();
//			x=118.798632;
//			y=36.858719;
			loadArea();
			mLocClient.stop();
			
		}
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
}

package com.zhonghaodi.req;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.Advertising;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.CityDto;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.WelcomeView;

import android.os.Message;

public class WelcomeReq implements HandMessage {

	public WelcomeView view;
	public GFHandler<WelcomeReq> handler;
	
	public  WelcomeReq(WelcomeView v) {
		// TODO Auto-generated constructor stub
		view = v;
		handler = new GFHandler<WelcomeReq>(this);
	}
	
	public void loadArea(final double x,final double y){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				String url = HttpUtil.RootURL + "zone/my?x="+x+"&y="+y;
				String jsonString = HttpUtil.executeHttpGetNotToken(url);
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
				String url = HttpUtil.RootURL + "launch/online";
				String jsonString = HttpUtil.executeHttpGetNotToken(url);
				Message msg = handler.obtainMessage();
				msg.what = 1;
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
					City city = new City();
					city.setId((int)areadto.getZone().getCode());
					city.setName(areadto.getZone().getName());
					view.saveCity(city);
				}
				else{
					view.saveCity(new City());
				}
				
			} else {
				view.saveCity(new City());
			}
			break;
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				List<Advertising> advertisings = gson.fromJson(msg.obj.toString(), 
						new TypeToken<List<Advertising>>() {}.getType());
				if(advertisings!=null && advertisings.size()>0){
					view.displayAdvertisings(advertisings);
				}
				else{
					
					view.displayWelcome();
				}
			}
			else{
				view.displayWelcome();
			}
			break;
		case -1:
			String errString = msg.obj.toString();
			view.showMessage(errString);
			view.goTomain();
			break;
		default:
			break;
		}
	}

}

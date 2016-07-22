package com.zhonghaodi.req;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.City;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.CityView;
import android.os.Message;

public class CityReq implements HandMessage {
	
	private CityView view;
	private GFHandler<CityReq> handler;
	
	public CityReq(CityView cityView) {
		// TODO Auto-generated constructor stub
		view = cityView;
		handler = new GFHandler<CityReq>(this);
	}
	
	/**
	 * 请求获取区域数组
	 */
	public void loadCities(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "zone";
				String jsonString =HttpUtil.executeHttpGet(urlString);
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
				List<City> cities = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<City>>() {
						}.getType());
				if(cities==null){
					view.showMessage("获取区域失败");
				}
				else{
					view.displayCities(cities);
				}
				
			} else {
				view.showMessage("获取区域失败");
			}
			break;

		default:
			break;
		}
	}

}

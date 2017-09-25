package com.zhonghaodi.req;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.SpinnerDto;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.PopupFairView;

import android.os.Message;

public class PopupFairReq implements HandMessage {

	PopupFairView view;
	GFHandler<PopupFairReq> handler;
	public PopupFairReq(PopupFairView v) {
		// TODO Auto-generated constructor stub
		view = v;
		handler = new GFHandler<PopupFairReq>(this);
	}
	
	public void loadFairCates(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getFairCatesString();
				if(jsonString!=null){
					if (!jsonString.equals("")) {
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
				}
				else{
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void loadSaveCates(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getZfbtCatesString();
				if(jsonString!=null){
					if (!jsonString.equals("")) {
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
				}
				else{
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.sendToTarget();
				}
				
			}
		}).start();
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<SpinnerDto> cps = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<SpinnerDto>>() {
						}.getType());
				view.showCates(cps);
			}
			else{
				return;
			}
			break;
			
		default:
			break;
		}
	}

}

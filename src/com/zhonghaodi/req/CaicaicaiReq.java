package com.zhonghaodi.req;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.CaicaicaiView;

import android.os.Message;

public class CaicaicaiReq implements HandMessage {

	private CaicaicaiView view;
	private GFHandler<CaicaicaiReq> handler;
	
	public CaicaicaiReq(CaicaicaiView mView) {
		// TODO Auto-generated constructor stub
		view = mView;
		handler = new GFHandler<CaicaicaiReq>(this);
	}
	
	/**
	 * caicaicai数组
	 */
	public void loadCaicaicai(final int fromid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "caicaicai?fromid="+fromid;
				String jsonString =HttpUtil.executeHttpGet(urlString);
				Message msg = handler.obtainMessage();
				if(fromid==0){
					msg.what = 0;
				}
				else{
					msg.what = 1;
				}
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
				List<Caicaicai> caicaicais = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Caicaicai>>() {
						}.getType());
				if(caicaicais==null){
					view.showMessage("获取猜农资信息失败");
				}
				else{
					view.displayCaicaicai(caicaicais, false);
				}
				
			} else {
				view.showMessage("获取猜农资信息失败");
			}
			view.refreshComplete();
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Caicaicai> caicaicais = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Caicaicai>>() {
						}.getType());
				if(caicaicais==null){
					view.showMessage("获取猜农资信息失败");
				}
				else{
					view.displayCaicaicai(caicaicais, true);
				}
				
			} else {
				view.showMessage("获取猜农资信息失败");
			}
			view.refreshComplete();
			break;

		default:
			break;
		}
	}
	
}

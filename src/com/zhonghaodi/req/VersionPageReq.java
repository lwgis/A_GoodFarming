package com.zhonghaodi.req;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import com.google.gson.Gson;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.TokenResult;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.view.VersionPageView;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.os.Message;

public class VersionPageReq implements HandMessage {

	private GFHandler<VersionPageReq> handler;
	private VersionPageView view;
	
	public VersionPageReq(VersionPageView v) {
		// TODO Auto-generated constructor stub
		handler = new GFHandler<VersionPageReq>(this);
		view = v;
	}
	
	public void loadToken(final String uid,final String phone){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String urlString = HttpUtil.RootURL  + "users/myToken";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				NameValuePair uidValuePair1 = new NameValuePair() {
					@Override
					public String getValue() {
						// TODO Auto-generated method stub
						return uid;
					}
					@Override
					public String getName() {
						// TODO Auto-generated method stub
						return "uid";
					}
				};				
				nameValuePairs.add(uidValuePair1);				
				NameValuePair phoneValuePair = new NameValuePair() {
					@Override
					public String getValue() {
						// TODO Auto-generated method stub
						
						return phone;
					}

					@Override
					public String getName() {
						// TODO Auto-generated method stub
						return "phone";
					}
				};				
				nameValuePairs.add(phoneValuePair);
				NetResponse netResponse;
				try {
					netResponse = HttpUtil.nottokenHttpPost(urlString, nameValuePairs);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					netResponse = new NetResponse();
					netResponse.setStatus(0);
					netResponse.setMessage(e.getMessage());
				}
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = netResponse;
				msg.sendToTarget();
			}
		}).start();
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			NetResponse netResponse1 = (NetResponse)msg.obj;
			if(netResponse1.getStatus()==1){
				String jsonstr = netResponse1.getResult();
				Gson gson = new Gson();
				TokenResult tokenResult = gson.fromJson(jsonstr, TokenResult.class);
				if(tokenResult==null){
					
				}
				else{
					if (tokenResult.isResult()) {
						view.savetoken(tokenResult.getMessage());
					}
				}
			}
			else{
				
			}
			break;

		default:
			break;
		}
	}

}

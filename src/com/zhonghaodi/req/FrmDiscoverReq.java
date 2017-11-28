package com.zhonghaodi.req;

import java.math.MathContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.CityDto;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.FrmDiscoverView;

import android.content.Context;
import android.os.Message;

public class FrmDiscoverReq implements HandMessage {

	private FrmDiscoverView view;
	private GFHandler<FrmDiscoverReq> handler;
	private Context mContext;
	private User mUser;
	
	public FrmDiscoverReq(FrmDiscoverView v,Context context) {
		// TODO Auto-generated constructor stub
		view =v;
		mContext = context;
		handler = new GFHandler<FrmDiscoverReq>(this);
	}
	
	public void loadData() {
		
		try {
			String uid = GFUserDictionary.getUserId(mContext);
			if (uid == null) {
				return;
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					String jsonString = HttpUtil.getUser(GFUserDictionary.getUserId(mContext));
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void loadArea(final double x,final double y){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				String url = HttpUtil.RootURL + "zone/my?x="+x+"&y="+y;
				String jsonString = HttpUtil.executeHttpGetNotToken(url);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		
		if(msg.what==1){
			if(msg.obj!=null){
				User user = (User) GsonUtil
						.fromJson(msg.obj.toString(), User.class);
				view.displayPoints(user.getPoint()+"");
				mUser = user;
			}
		}
		else{
			view.displayPoints("");
			if(msg.what==2){
				if (msg.obj != null) {
					Gson gson = new Gson();
					CityDto areadto = gson.fromJson(msg.obj.toString(),
							new TypeToken<CityDto>() {
							}.getType());
					if(areadto!=null && areadto.isResult()){
						City city = new City();
						city.setId((int)areadto.getZone().getCode());
						city.setName(areadto.getZone().getName());
						view.confirmCity(city);
					}
					else{
						view.confirmCity(new City());
					}
					
				} else {
					view.confirmCity(new City());
				}
			}
		}
		
	}

	public User getmUser() {
		return mUser;
	}

	public void setmUser(User mUser) {
		this.mUser = mUser;
	}

}

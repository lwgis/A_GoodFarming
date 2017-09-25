package com.zhonghaodi.req;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.MainActivity;
import com.zhonghaodi.goodfarming.UILApplication;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PointDic;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.MainView;

import android.app.DownloadManager.Query;
import android.content.Context;
import android.os.Message;
import android.widget.ProgressBar;

public class MainReq implements HandMessage {
	
	private GFHandler<MainReq> handler;
	private MainView view;
	
	public MainReq(MainView v) {
		// TODO Auto-generated constructor stub
		view = v;
		handler = new GFHandler<MainReq>(this);
	}
	
	public void loadUser(final String uid) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString=HttpUtil.getUser(uid);
				Message msg = handler.obtainMessage();
				msg.what=8;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void requestVersion(){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String url = HttpUtil.RootURL + "apps/android";
					String jsonString = HttpUtil.executeHttpGetNotToken(url);
					Message msg = handler.obtainMessage();
					msg.what = 4;
					msg.obj = jsonString;
					msg.sendToTarget();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj = "";
					msg.sendToTarget();
				}
				
			}
		}).start();
		
	}
	
	public void sharePoint(final Context context,final Question shareQue){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpUtil.sharePoint(GFUserDictionary.getUserId(context), UILApplication.shareUrl);
					if(UILApplication.sharestatus==1){
						if(UILApplication.sharefolder.contains("question")){
							HttpUtil.addForwardcount("question", shareQue.getId());
						}
						else if(UILApplication.sharefolder.contains("gossip")){
							HttpUtil.addForwardcount("gossip", shareQue.getId());
						}
						else{
							HttpUtil.addForwardcount("plantinfo", shareQue.getId());
						}
					}
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					
				}
			}
		}).start();
	}
	
	public void downLoad(final AppVersion appVersion,final ProgressBar progressBar){
		new Thread(){  
		       @Override  
		       public void run() {  
		           try {  
		               File file = HttpUtil.getFileFromServer(appVersion.getUrl(), progressBar,handler);  
		               sleep(3000);  
		               Message msg = handler.obtainMessage();
					   msg.what = 6;
					   msg.obj = file;
					   msg.sendToTarget(); 
		           } catch (Exception e) {  
		           	Message msg = handler.obtainMessage();
						msg.what = -1;
						msg.obj = "下载错误";
						msg.sendToTarget(); 
		                e.printStackTrace();  
		           }  
		       }}.start(); 
	}
	
	/**
	 * 获取积分字典
	 */
	public void loadPointdics(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.executeHttpGet(HttpUtil.RootURL + "dics/all");
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 更新我的作物
	 * @param user
	 */
	public void updateCrops(final User user){
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					NetResponse netResponse = HttpUtil.modifyUser(user);
					Message msgUser = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msgUser.what = 2;
						msgUser.obj = netResponse.getResult();
					}
					else{
						msgUser.what = -1;
						msgUser.obj = netResponse.getMessage();
					}
					msgUser.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msgUser = handler.obtainMessage();
					msgUser.what = -1;
					msgUser.obj = e.getMessage();
					msgUser.sendToTarget();
				}
			}
		}).start();
	}
	
	public void customEmFunction(final Context context,final EMMessage message){
		if (UILApplication.isBackground(context.getApplicationContext())) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String jsonString;
					if(message.getFrom().equals("种好地")){
						User user = new User();
						user.setAlias(message.getFrom());
						List<User> users = new ArrayList<User>();
						users.add(user);
						Gson sGson=new Gson();
						jsonString=sGson.toJson(users);
					}
					else{
						NetResponse netResponse= HttpUtil.getUserByPhone(message
								.getFrom());
						jsonString = netResponse.getResult();
					}
					
					Message msg = handler.obtainMessage();
					msg.what =5;
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}).start();
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					Message msg = handler.obtainMessage();
					msg.what =7;
					msg.sendToTarget();
				}
			}).start();
		}

		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 5:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<User> users = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<User>>() {
						}.getType());
				if (users != null) {
					view.notificationTextMessage(users.get(0).getAlias());
				}
			}
			break;
		case 2:
			try {
				User user1 = (User) GsonUtil.fromJson(msg.obj.toString(),
						User.class);
				if(user1!=null){
					view.showMessage("更新成功");
					view.saveUserInfo(user1);
				}
				else{
					view.showMessage("更新失败");
				}
			} catch (Exception e) {
				// TODO: handle exception
				view.showMessage("更新失败");
			}
			break;
			
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<PointDic> pointdics = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<PointDic>>() {
						}.getType());
				if (pointdics != null && pointdics.size()>0) {
					view.savePointDics(pointdics);					
				}
			}
			break;
		case 4:
			if(msg.obj==null){
				return;
			}
			Gson gson = new Gson();
			AppVersion appVersion = gson.fromJson(msg.obj.toString(),
					new TypeToken<AppVersion>() {
					}.getType());
			view.compareVersion(appVersion);
			break;
		case 1:
			int[] values = (int[])msg.obj;
			float bf = (values[0]*100*1.000f/values[1]);
			bf = (float)(Math.round(bf*100))/100;
//			activity.proTextView1.setText(bf+"%");
			float pf = (values[0]*1.000f/(1024*1024));
			pf = (float)(Math.round(pf*100))/100;
			float cf = (values[1]*1.000f/(1024*1024));
			cf = (float)(Math.round(cf*100))/100;
//			activity.proTextView2.setText(pf+"M/"+cf+"M");
			view.displayProgress(bf, pf, cf);
			break;
		case 6:
			File file = (File)msg.obj;
			view.downComplete(file);			
			break;
		case -1:
			if(msg.obj!=null){
				String errString = msg.obj.toString();
				view.showMessage(errString);
			}
			
			break;
		case 7:
			view.setUnredMessageCount();
			break;
		case 8:
			if(msg.obj!=null){
				UILApplication.user = (User) GsonUtil.fromJson(msg.obj.toString(), User.class);
			}
			
			break;
		default:
			break;
		}
	}

}

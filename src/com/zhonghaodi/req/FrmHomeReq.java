package com.zhonghaodi.req;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.goodfarming.UILApplication;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetMessage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PostResponse;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.FrmHomeView;

import android.content.Context;
import android.os.Message;

public class FrmHomeReq implements HandMessage {

	private FrmHomeView view;
	private GFHandler<FrmHomeReq> handler;
	private Context context;
	public City area1;
	public String zonestr;
	
	public FrmHomeReq(FrmHomeView v,Context c){
		view = v;
		handler = new GFHandler<FrmHomeReq>(this);
		context = c;
	}
	
	public void loadNewQuestion() {
		if(UILApplication.diseaseStatus==0){
			new Thread(new Runnable() {

				@Override
				public void run() {
					String jsonString;
					jsonString = HttpUtil.getQuestionsString("");
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}).start();
		}
		else{
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					String jsonString;
					int zone=0;
					if(area1!=null){
						zone = area1.getId();
					}
					String uid = GFUserDictionary.getUserId(context);
					jsonString = HttpUtil.getAscQuestionsString(uid,zone);
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}).start();
		}
		
	}

	public void loadMoreQuestion(final int qid) {
		if(UILApplication.diseaseStatus==0){
			new Thread(new Runnable() {

				@Override
				public void run() {
					String jsonString;
					int zone=0;
					if(area1!=null){
						zone = area1.getId();
					}
					jsonString = HttpUtil.getQuestionsString(qid,"");
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}).start();
		}
		else{
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					String jsonString;
					int zone=0;
					if(area1!=null){
						zone = area1.getId();
					}
					String uid = GFUserDictionary.getUserId(context);
					jsonString = HttpUtil.getAscQuestionsString(uid,qid,zone);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}).start();
		}
	}

	public void loadNewGossips() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				String jsonString = HttpUtil.getGossipsString(zonestr);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	public void loadMoreGossips(final int qid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				String jsonString = HttpUtil.getGossipsString(qid,zonestr);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
//	public void loadNewPlant(final int cid,final String key,final String deal) {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				int zone=0;
//				String jsonString = HttpUtil.searchFairsString(0,cid,key,zonestr,deal);
//				Message msg = handler.obtainMessage();
//				msg.what = 0;
//				msg.obj = jsonString;
//				msg.sendToTarget();
//			}
//		}).start();
//	}
//
//	public void loadMorePlant(final int qid,final int cid,final String key,final String deal) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int zone=0;
//				String jsonString = HttpUtil.searchFairsString(qid,cid,key,zonestr,deal);
//				Message msg = handler.obtainMessage();
//				msg.what = 1;
//				msg.obj = jsonString;
//				msg.sendToTarget();
//			}
//		}).start();
//	}
	
	public void searchNewPlant(final double x,final double y,final double distance,final String key,final int page,final String deal){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.searchFairs(x,y,distance,key,zonestr,page,deal);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void searchMorePlant(final double x,final double y,final double distance,final String key,final int page,final String deal){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.searchFairs(x,y,distance,key,zonestr,page,deal);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void dianZan(final Question question,final String uid2){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				NetResponse netResponse=HttpUtil.agreePlant(question.getId(),uid2);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 9;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = 0;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void delete(final int qid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(UILApplication.displayStatus==0){
					jsonString = HttpUtil.deleteQuestion(qid);
				}
				else if(UILApplication.displayStatus==1){
					jsonString = HttpUtil.deleteGossip(qid);
				}
				else{
					jsonString = HttpUtil.deletePlant(qid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void checkPublish(final String uid){
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				NetResponse netResponse;
				String urlString = HttpUtil.RootURL + "plantinfo/hasPublish";
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
				
				try {
					netResponse = HttpUtil.executeHttpPost(urlString, nameValuePairs);
					Message msg = handler.obtainMessage();
					msg.what = 4;
					msg.obj = netResponse;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = e.getMessage();
					msg.sendToTarget();
				}
			}
		}).start();
	}
	public void threadSleep(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					Message msg = handler.obtainMessage();
					msg.what = 10;
					msg.sendToTarget();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}

	@Override
	public void handleMessage(Message msg,Object object) {
			
			if(msg.what==0||msg.what==1){
				List<Question> questions = null;
				if (msg.obj != null) {
					Gson gson = new Gson();
					questions = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<Question>>() {
							}.getType());
					
				} 
				if(view!=null && msg!=null && questions!=null){
					view.onLoaded(msg.what, questions);
				}
				
			}
			else if(msg.what==3){
				
				String str = msg.obj.toString();
				if(!str.isEmpty()){
					view.showMessage(str);
				}
				else{
					view.onDeleted();
					
				}
				
			}
			else if(msg.what==4){
				NetResponse netResponse = (NetResponse)msg.obj;
				if(netResponse!=null&&netResponse.getStatus()==1){
					NetMessage netMessage= (NetMessage) GsonUtil
							.fromJson(netResponse.getResult(), NetMessage.class);
					if(netMessage!=null){
						if(netMessage.isResult()){
							view.showMessage("亲，每天只能发一条赶大集哟");
						}
						else{
							view.popNewPlant();
						}
					}
					else{
						view.showMessage("操作失败！");
					}
				}
				else{
					view.showMessage("操作失败！");
				}
			}
			else if(msg.what==9){
				if(msg.obj!=null){
					Gson gson2 = new Gson();
					String jString = (String) msg.obj;
					PostResponse reportResponse = gson2.fromJson(jString, PostResponse.class);
					if(reportResponse == null){
						view.showMessage("点赞操作错误");
					}
					else{
						if(reportResponse.isResult()){
							view.onZan();
						}
						else{
							view.showMessage(reportResponse.getMessage());
						}
					}
				}
				else{
					view.showMessage("点赞操作错误");
				}
			}
			else if(msg.what==10){
				view.onlistpullstop();
			}
			else if(msg.what==-1){
				if(msg.obj!=null){
					view.showMessage(msg.obj.toString());
				}
			}
	
	}
	
}

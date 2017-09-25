package com.zhonghaodi.req;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.UILApplication;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PostResponse;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Zan;
import com.zhonghaodi.networking.GFHandler;
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
	
	public void loadNewPlant(final int cid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int zone=0;
				String jsonString = HttpUtil.searchFairsString(0,cid,"",zonestr);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	public void loadMorePlant(final int qid,final int cid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int zone=0;
				String jsonString = HttpUtil.searchFairsString(qid,cid,"",zonestr);
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
				view.onLoaded(msg.what, questions);
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
			else if(msg.what==-1){
				if(msg.obj!=null){
					view.showMessage(msg.obj.toString());
				}
			}
	
	}
	
}

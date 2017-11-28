package com.zhonghaodi.req;

import java.net.URLEncoder;
import java.util.List;

import org.jivesoftware.smack.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.Disease;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.HotQuestionView;

import android.R.bool;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

public class HotQuestionReq implements HandMessage {
	
	private GFHandler<HotQuestionReq> handler;
	private int cid;
	private String phrase="";
	private Context mContext;
	private HotQuestionView view;
	private int resCount;
	private boolean bq,bd,br;
	
	public HotQuestionReq(int c,String phr,Context context,HotQuestionView v) {
		// TODO Auto-generated constructor stub
		cid = c;
		if(!TextUtils.isEmpty(phr)){
			phrase = phr;
		}
		mContext = context;
		view = v;
		resCount=0;
		bq=false;
		bd=false;
		br=false;
		handler = new GFHandler<HotQuestionReq>(this);
	}
	
	public void getHotQuestions(final String zone){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = HttpUtil.RootURL + "crops/hotQuestions?zone="+zone+"&cid="+cid+"&phrase="+URLEncoder.encode(phrase);
				String jsonString = HttpUtil.executeHttpGet(url);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	public void getHotDiseases(final String zone){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = HttpUtil.RootURL + "crops/hotDiseases?zone="+zone+"&cid="+cid+"&phrase="+URLEncoder.encode(phrase);
				String jsonString = HttpUtil.executeHttpGet(url);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	public void getHotRecipes(final String zone){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = HttpUtil.RootURL + "crops/hotRecipes?zone="+zone+"&cid="+cid+"&phrase="+URLEncoder.encode(phrase);
				String jsonString = HttpUtil.executeHttpGet(url);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	public void checkData(){
		if(resCount==3 && !bq && !bd && !br){
			view.showMessage("没有相关信息!");
		}
	}
	

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		resCount++;
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Question> questions = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Question>>() {
						}.getType());
				if(questions==null || questions.size()==0){
					bq=false;
					checkData();
				}
				else{
					bq=true;
					view.showQuestions(questions);
				}
			} 
			else{
				bq=false;
				checkData();
			}
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Disease> diseases = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Disease>>() {
						}.getType());
				if(diseases==null || diseases.size()==0){
					bd=false;
					checkData();
				}
				else{
					bd=true;
					view.showDiseases(diseases);
				}
			} 
			else{
				bd=false;
				checkData();
			}
			break;
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Recipe> recipes = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Recipe>>() {
						}.getType());
				if(recipes==null || recipes.size()==0){
					br=false;
					checkData();
				}
				else{
					br=true;
					view.showRecipes(recipes);
				}
			} 
			else{
				br=false;
				checkData();
			}
			break;
		default:
			break;
		}
	}

}

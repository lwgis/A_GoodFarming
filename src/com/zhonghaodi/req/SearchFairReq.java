package com.zhonghaodi.req;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.FavoriteQuestion;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.SearchFairView;

import android.content.Context;
import android.os.Message;

public class SearchFairReq implements HandMessage {
	private SearchFairView view;
	private Context context;
	private GFHandler<SearchFairReq> handler;
	public int cityid;
	
	public SearchFairReq(SearchFairView v,Context c){
		view = v;
		context = c;
		handler = new GFHandler<SearchFairReq>(this);
	}
	
	public void Search(final int fromid,final int cate,final String key){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString =HttpUtil.searchFairsString(fromid,cate,key,cityid+"","");
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
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Question> questions = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Question>>() {
						}.getType());
				if(questions==null || questions.size()==0){
					
					view.showMessage("没有符合条件的记录");
				}
				view.showQuestions(questions, msg.what);
				
			} else {
				view.showQuestions(null, msg.what);
				view.showMessage("连接服务器失败,请稍候再试!");
			}			
			break;

		default:
			break;
		}
	}
}

package com.zhonghaodi.req;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.QuestionActivity;
import com.zhonghaodi.model.CaiResponse;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.ResponseDto;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFString;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.JsonUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.UmengConstants;
import com.zhonghaodi.view.CaiView;

import android.os.Message;

public class CaiReq implements HandMessage {

	private CaiView view;
	private GFHandler<CaiReq> handler;
	
	public CaiReq(CaiView v) {
		// TODO Auto-generated constructor stub
		view = v;
		handler = new GFHandler<CaiReq>(this);
	}
	
	public void loadCaicaicai(final int id){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "caicaicai/"+id;
				String jsonString =HttpUtil.executeHttpGet(urlString);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void replyCai(String content,String uid,final int cid){
		final ResponseDto response = new ResponseDto();
		response.setContent(content);
		User writer = new User();
		writer.setId(uid);
		response.setWriter(writer);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String urlString = HttpUtil.RootURL + "caicaicai/" + String.valueOf(cid)+ "/reply";
					String jsonString = JsonUtil.convertObjectToJson(response,"yyyy-MM-dd HH:mm:ss",
							new String[] { Response.class.toString(), });
					NetResponse netResponse=HttpUtil.executeHttpPost(urlString, jsonString);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = netResponse;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj = e.getMessage().toString();
					msg.sendToTarget();
				}
			}
		}).start();
	}
	
	public void commentCai(final String content,final String uid,final int cid){
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				String urlString = HttpUtil.RootURL  + "caicaicai/" + String.valueOf(cid)+ "/comment";
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
				NameValuePair contentValuePair = new NameValuePair() {
					@Override
					public String getValue() {
						// TODO Auto-generated method stub
						return content;
					}

					@Override
					public String getName() {
						// TODO Auto-generated method stub
						return "content";
					}
				};				
				nameValuePairs.add(contentValuePair);
				NetResponse netResponse;
				try {
					netResponse = HttpUtil.executeHttpPost(urlString, nameValuePairs);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					netResponse = new NetResponse();
					netResponse.setStatus(0);
					netResponse.setMessage(e.getMessage());
				}
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = netResponse;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void deleteResponse(final int cid,final int rid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "caicaicai/"+cid+"/responses/"+rid+"/delete";
				String jsonString =HttpUtil.executeHttpDelete(urlString);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void deleteComment(final int cid,final int coid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String urlString = HttpUtil.RootURL + "caicaicai/"+cid+"/comments/"+coid+"/delete";
				String jsonString =HttpUtil.executeHttpDelete(urlString);
				Message msg = handler.obtainMessage();
				msg.what = 4;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			view.showMessage(msg.obj.toString());
			break;
		case 0:
			Gson gson = new Gson();
			String jsonString = (String) msg.obj;
			Caicaicai caicaicai = gson.fromJson(jsonString, Caicaicai.class);
			if(caicaicai==null){
				view.showMessage("数据不存在或已删除");
			}
			else{
				view.displayCaicaicai(caicaicai);
			}
			view.refreshComplete();
			break;
		case 1:
			NetResponse netResponse = (NetResponse)msg.obj;
			if(netResponse.getStatus()==1){
				view.refrenshCai();
			}
			else{
				view.showMessage("发送失败");
			}
			break;
		case 2:
			NetResponse netResponse1 = (NetResponse)msg.obj;
			if(netResponse1.getStatus()==1){
				view.refrenshCai();
			}
			else{
				view.showMessage("发送失败");
			}
			break;
		case 3:
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				view.showMessage(strerr);
			}
			else{
				view.refrenshCai();
			}
			break;
		case 4:
			String strerr1 = msg.obj.toString();
			if(!strerr1.isEmpty()){
				view.showMessage(strerr1);
			}
			else{
				view.refrenshCai();
			}
			break;

		default:
			break;
		}
	}

}

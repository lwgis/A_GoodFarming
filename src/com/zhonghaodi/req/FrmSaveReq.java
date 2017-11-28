package com.zhonghaodi.req;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.SpinnerDto;
import com.zhonghaodi.model.Zfbt;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.FrmSaveView;

import android.content.Context;
import android.os.Message;

public class FrmSaveReq implements HandMessage {
	
	private FrmSaveView view;
	private GFHandler<FrmSaveReq> handler;
	private Context context;
	public List<Zfbt> zfbts;
	public List<SecondOrder> secondOrders;
	private int zone;
	public int cate;
	public List<SpinnerDto> spinnerDtos;
	
	public FrmSaveReq(FrmSaveView v,Context c) {
		// TODO Auto-generated constructor stub
		view = v;
		context = c;
		handler = new GFHandler<FrmSaveReq>(this);
		zfbts = new ArrayList<Zfbt>();
		spinnerDtos = new ArrayList<SpinnerDto>();
		SpinnerDto spinnerDto = new SpinnerDto();
		spinnerDto.setId(0);
		spinnerDto.setName("全部");
		spinnerDtos.add(spinnerDto);
		secondOrders = new ArrayList<SecondOrder>();
		zone = GFAreaUtil.getCity(context);
		cate = 0;
	}
	
	public void loadNewOrders(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = HttpUtil.RootURL + "zfbt/recentOrder?size=10";
				if(zone!=0){
					url = url+"&zone="+zone;
				}
				String jsonString = HttpUtil.executeHttpGet(url);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	public void loadData(final double x,final double y){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = getZfbt(x,y,0,zone);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
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
						msg.what = 3;
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
	
	public void loadMoreData(final double x,final double y,final int page){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = getZfbt(x,y,page,zone);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
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
					msg.what = 4;
					msg.sendToTarget();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}
	
	public String getZfbt(double x,double y,int page,int zone){
		String url = HttpUtil.RootURL + "zfbt/new?x="+x+"&y="+y+"&page="+page;
		if(cate!=0){
			url = url +"&cate="+cate;
		}
		if(zone!=0){
			url = url +"&zone="+zone;
		}
		
		String jsonString = HttpUtil.executeHttpGet(url);
		return jsonString;
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Zfbt> bts = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Zfbt>>() {
						}.getType());
				zfbts.clear();
				for (Zfbt bt : bts) {
					zfbts.add(bt);
				}
				view.refreshData();
				if(zfbts.size()==0){
					view.showMessage("附近没有超实惠商品抢购活动！");
				}
				
			} else {
				view.refreshData();
				view.showMessage("连接服务器失败,请稍候再试!");
				return;
			}
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Zfbt> btss = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Zfbt>>() {
						}.getType());
				for (Zfbt bt : btss) {
					zfbts.add(bt);
				}
				view.refreshData();
				if(zfbts.size()==0){
					view.showMessage("附近没有超实惠商品抢购活动！");
				}
				
			} else {
				view.refreshData();
				view.showMessage("连接服务器失败,请稍候再试!");
				return;
			}
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<SecondOrder> ses = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<SecondOrder>>() {
						}.getType());
				secondOrders.clear();
				for (SecondOrder secondOrder : ses) {
					secondOrders.add(secondOrder);
				}
				view.showOrders(secondOrders);
				
			} else {
				view.showMessage("连接服务器失败,请稍候再试!");
			}
			break;
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<SpinnerDto> cps = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<SpinnerDto>>() {
						}.getType());
				if(cps!=null && cps.size()>0){
					for (Iterator iterator = cps.iterator(); iterator.hasNext();) {
						SpinnerDto spinnerDto = (SpinnerDto) iterator.next();
						spinnerDtos.add(spinnerDto);
					}
					view.showCates(spinnerDtos,cate);
				}
				
			}
			else{
				return;
			}
			break;
		case 4:
			view.refreshData();
			break;
		default:
			break;
		}
	}

	
	
}

package com.zhonghaodi.goodfarming;

import java.util.List;
import java.util.Random;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.duguang.baseanimation.ui.customview.serchfly.KeywordsFlow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.GuaGuaKa;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Gua;
import com.zhonghaodi.model.GuaResult;
import com.zhonghaodi.model.onWipeListener;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

public class RubblerActivity extends Activity implements OnClickListener,onWipeListener,HandMessage {

	private GuaGuaKa guaGuaKa;    
    public String[] keywords;  
    private GuaResult guaResult;
    private KeywordsFlow keywordsFlow;   
    private boolean isOpen = false;
    private GFHandler<RubblerActivity> handler = new GFHandler<RubblerActivity>(this);
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbler);       
        keywordsFlow = (KeywordsFlow) findViewById(R.id.frameLayout1);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnClickListener(this);
		
		guaGuaKa = (GuaGuaKa)findViewById(R.id.guagua);
		guaGuaKa.setmWipeListener(this);
 
		loadData();
    }
    
    private void loadData(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getGuaGua();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();	
				
				String uid = GFUserDictionary.getUserId();
				String jsonString1 = HttpUtil.qian(uid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 1;
				msg1.obj = jsonString1;
				msg1.sendToTarget();
			}
		}).start();
    }
    
    private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.frameLayout1:
			keywordsFlow.rubKeywords();
			feedKeywordsFlow(keywordsFlow, keywords);
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			break;

		default:
			break;
		}
	}

	@Override
	public void onWipe(int percent) {
		// TODO Auto-generated method stub
		isOpen = true;
		if(guaResult.isSuccess()){
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
	        //设置它的ContentView
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout = inflater.inflate(R.layout.dialog, null);
	        dialog.setContentView(layout);
	        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
	        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
	        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
	        okBtn.setText("查看订单");
	        okBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
	        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
	        cancelButton.setText("返回");
	        cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
	        titleView.setText("提示");
	        contentView.setText("恭喜您抽中"+guaResult.getGuagua().getName());
	        dialog.show();
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Gua> agrs = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Gua>>() {
						}.getType());
				if(agrs!=null && agrs.size()>0){
					keywords = new String[agrs.size()];
					for(int i = 0;i<agrs.size();i++){
						Gua gua = agrs.get(i);
						keywords[i]=gua.getName();
					}
					// 添加
					feedKeywordsFlow(keywordsFlow, keywords);
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				}
				
			} else {
				GFToast.show("连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				guaResult = gson.fromJson(msg.obj.toString(),
						new TypeToken<GuaResult>() {
						}.getType());
				if(guaResult.isSuccess()){
					guaGuaKa.setmText(guaResult.getGuagua().getName());
				}
				else{
					guaGuaKa.setmText("谢谢参与");
				}
			}
			else{
				GFToast.show("连接服务器失败,请稍候再试!");
			}
			break;

		default:
			break;
		}
	}
}

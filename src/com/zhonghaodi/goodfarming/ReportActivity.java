package com.zhonghaodi.goodfarming;

import com.google.gson.Gson;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PostResponse;
import com.zhonghaodi.model.Solution;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ReportActivity extends Activity implements HandMessage,OnClickListener {

	private RadioGroup radioGroup;
	private MyEditText reportEditText;
	private MyTextButton cancelButton;
	private MyTextButton sendButton;
	private int qid,rid,status;
	private GFHandler<ReportActivity> handler = new GFHandler<ReportActivity>(
			this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		reportEditText = (MyEditText)findViewById(R.id.desc_edit);
		cancelButton = (MyTextButton)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		sendButton = (MyTextButton)findViewById(R.id.ok_button);
		sendButton.setOnClickListener(this);
		
		qid = getIntent().getIntExtra("qid", 0);
		rid = getIntent().getIntExtra("rid", 0);
		status = getIntent().getIntExtra("status", 0);
		
		loadItems();
	}
	
	/**
	 * 获
	 */
	public void loadItems(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void sendReport(){
		final int scoring = radioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton)radioGroup.findViewById(scoring);
		String rpContext = reportEditText.getText().toString();
		String reportStr="";
		if(scoring==4){
			if(rpContext.isEmpty()){
				GFToast.show(getApplicationContext(),"请输入举报内容");
				return;
			}
			else{
				reportStr=radioButton.getText()+":"+rpContext;
			}
		}
		else{
			if(rpContext.isEmpty()){
				reportStr=radioButton.getText()+"。";
			}
			else{
				reportStr=radioButton.getText()+":"+rpContext;
			}
		}
		final String rp=reportStr;
		sendButton.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try 
				{
					String uid = GFUserDictionary.getUserId(getApplicationContext());					
					NetResponse netResponse = HttpUtil.sendReport(status,uid,qid,rid,rp);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 1;
						msg.obj = netResponse.getResult();
					}
					else{
						msg.what = -1;
						msg.obj = netResponse.getMessage();
					}
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what=-1;
					msg.obj = e.getMessage();
					msg.sendToTarget();
				}
			}
		}).start();
		
	}
	
	private void displayDics(){
		radioGroup.removeAllViews();
		
		RadioButton rblj = new RadioButton(this);
		rblj.setButtonDrawable(R.drawable.radiobg);
		rblj.setId(1);
		rblj.setText("垃圾信息");
		rblj.setChecked(true);
		rblj.setTextColor(Color.rgb(68, 68, 68));
		rblj.setPadding(5, 0, 5, 0);
		LayoutParams rParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		radioGroup.addView(rblj, rParams);
		
		RadioButton bysnr = new RadioButton(this);
		bysnr.setButtonDrawable(R.drawable.radiobg);
		bysnr.setId(2);
		bysnr.setText("不友善内容");
		bysnr.setTextColor(Color.rgb(68, 68, 68));
		bysnr.setPadding(5, 0, 5, 0);
		radioGroup.addView(bysnr, rParams);
		
		RadioButton gg = new RadioButton(this);
		gg.setButtonDrawable(R.drawable.radiobg);
		gg.setId(3);
		gg.setText("广告");
		gg.setTextColor(Color.rgb(68, 68, 68));
		gg.setPadding(5, 0, 5, 0);
		radioGroup.addView(gg, rParams);
		
		RadioButton qt = new RadioButton(this);
		qt.setButtonDrawable(R.drawable.radiobg);
		qt.setId(4);
		qt.setText("其他");
		qt.setTextColor(Color.rgb(68, 68, 68));
		qt.setPadding(5, 0, 5, 0);
		radioGroup.addView(qt, rParams);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.ok_button:
			sendReport();
			break;

		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			sendButton.setEnabled(true);
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 0:
			displayDics();
			break;
		case 1:
			sendButton.setEnabled(true);
			if(msg.obj!=null){
				Gson gson = new Gson();
				String jsonString = (String) msg.obj;
				PostResponse reportResponse = gson.fromJson(jsonString, PostResponse.class);
				if(reportResponse == null){
					GFToast.show(getApplicationContext(),"举报错误");
				}
				else{
					if(reportResponse.isResult()){
						GFToast.show(getApplicationContext(),"举报成功");
						this.finish();
					}
					else{
						GFToast.show(getApplicationContext(),reportResponse.getMessage());
					}
				}
			}
			else{
				GFToast.show(getApplicationContext(),"举报错误");
			}			

			break;

		default:
			break;
		}
	}

}

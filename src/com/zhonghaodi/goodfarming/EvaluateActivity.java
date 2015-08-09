package com.zhonghaodi.goodfarming;

import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.ScoringDic;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EvaluateActivity extends Activity implements HandMessage,OnClickListener {

	private RadioGroup radioGroup;
	private MyEditText evaluateEditText;
	private MyTextButton cancelButton;
	private MyTextButton sendButton;
	private int urid,rid;
	private String nzdid;
	private GFHandler<EvaluateActivity> handler = new GFHandler<EvaluateActivity>(
			this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		evaluateEditText = (MyEditText)findViewById(R.id.desc_edit);
		cancelButton = (MyTextButton)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		sendButton = (MyTextButton)findViewById(R.id.ok_button);
		sendButton.setOnClickListener(this);
		urid = getIntent().getIntExtra("urid", 0);
		rid = getIntent().getIntExtra("rid", 0);
		nzdid = getIntent().getStringExtra("nzdid");
		loadScoring();
	}
	
	/**
	 * 获取评分字典
	 */
	public void loadScoring(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getScoringdics();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void displayDics(List<ScoringDic> dics){
		radioGroup.removeAllViews();
		if(dics!=null && dics.size()>0){
			int i = 0;
			for (Iterator iterator = dics.iterator(); iterator.hasNext();) {
				ScoringDic scoringDic = (ScoringDic) iterator.next();
				RadioButton radioButton = new RadioButton(this);
				radioButton.setButtonDrawable(R.drawable.radiobg);
				radioButton.setId(scoringDic.getId());
				radioButton.setText(scoringDic.getName());
				if(i==0){
					radioButton.setChecked(true);
				}
				radioButton.setTextColor(Color.rgb(68, 68, 68));
				LayoutParams rParams = new LayoutParams((int)getResources().getDimension(R.dimen.radio_width),LayoutParams.WRAP_CONTENT);
				radioGroup.addView(radioButton, rParams);
				i++;
			}
		}
	}
	
	private void sendEvaluate(){
		final int scoring = radioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton)radioGroup.findViewById(scoring);
		final String evaluate =radioButton.getText()+"。"+evaluateEditText.getText().toString();
		if(evaluate.isEmpty()){
			GFToast.show("请输入评价意见");
			return;
		}
		sendButton.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try 
				{
					String uid = GFUserDictionary.getUserId();
					String jsonString = HttpUtil.sendEvaluate(nzdid, urid, rid, scoring, evaluate);
					Message msg = handler.obtainMessage();
					msg.what=1;
					msg.obj = jsonString;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.ok_button:
			sendEvaluate();
			break;

		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<ScoringDic> dics = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<ScoringDic>>() {
						}.getType());
				displayDics(dics);
				
			} else {
				GFToast.show("连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			sendButton.setEnabled(true);
			if(msg.obj!=null&&!msg.obj.toString().isEmpty()){
				GFToast.show(msg.obj.toString());
			}
			else{
				this.finish();
			}
			break;

		default:
			break;
		}
		
	}

}

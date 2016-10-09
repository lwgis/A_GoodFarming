package com.zhonghaodi.goodfarming;

import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.ScoringDic;
import com.zhonghaodi.model.ZfbtOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CreateZfbtEvaluateActivity extends Activity implements HandMessage,OnClickListener  {

	private RadioGroup radioGroup;
	private MyEditText evaluateEditText;
	private TextView titleText;
	private MyTextButton cancelButton;
	private MyTextButton sendButton;
	private ZfbtOrder zfbtOrder;
	private GFHandler<CreateZfbtEvaluateActivity> handler = new GFHandler<CreateZfbtEvaluateActivity>(
			this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createzfbtevaluate);
		titleText = (TextView)findViewById(R.id.title_text);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		evaluateEditText = (MyEditText)findViewById(R.id.desc_edit);
		cancelButton = (MyTextButton)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		sendButton = (MyTextButton)findViewById(R.id.ok_button);
		sendButton.setOnClickListener(this);
		zfbtOrder = (ZfbtOrder)getIntent().getSerializableExtra("order");
		titleText.setText("评价"+zfbtOrder.getSecond().getTitle());
		loadScoring();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("评价政府补贴订单");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("评价政府补贴订单");
		MobclickAgent.onPause(this);
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
			GFToast.show(getApplicationContext(),"请输入评价意见");
			return;
		}
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		sendButton.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try 
				{
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					NetResponse netResponse = HttpUtil.sendZfbtEvaluate(uid, zfbtOrder.getSecond().getId(), 
							zfbtOrder.getId(), scoring, evaluate);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						switch (scoring) {
						case 1:
							zfbtOrder.getSecond().setHaocount(zfbtOrder.getSecond().getHaocount()+1);
							break;
						case 2:
							zfbtOrder.getSecond().setZhongcount(zfbtOrder.getSecond().getZhongcount()+1);
							break;
						case 3:
							zfbtOrder.getSecond().setChacount(zfbtOrder.getSecond().getChacount()+1);
							break;

						default:
							break;
						}
						zfbtOrder.getSecond().setSumcount(zfbtOrder.getSecond().getSumcount()+1);
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
		case -1:
			sendButton.setEnabled(true);
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<ScoringDic> dics = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<ScoringDic>>() {
						}.getType());
				displayDics(dics);
				
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			sendButton.setEnabled(true);
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			Intent intent = new Intent(this, ZfbtEvaluatesActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("zfbt", zfbtOrder.getSecond());
			intent.putExtras(bundle);
			startActivity(intent);
			this.finish();
//			if(msg.obj!=null&&!msg.obj.toString().isEmpty()){
//				GFToast.show(msg.obj.toString());
//			}
//			else{
//				int point = GFPointDictionary.getScoringPoint();
//				if(point>0){
//					GFToast.show("评价成功，积分+"+point+" ^-^");
//				}
//				this.finish();
//			}
			break;

		default:
			break;
		}
		
	}
}

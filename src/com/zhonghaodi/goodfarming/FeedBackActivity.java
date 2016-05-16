package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FeedBackActivity extends Activity implements HandMessage,OnClickListener,TextWatcher {
	private MyTextButton cancelButton;
	private MyTextButton sendBtn;
	private MyEditText contentEt;
	private GFHandler<FeedBackActivity> handler = new GFHandler<FeedBackActivity>(
			this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		cancelButton = (MyTextButton)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		sendBtn = (MyTextButton)findViewById(R.id.send_button);
		sendBtn.setOnClickListener(this);
		sendBtn.setEnabled(false);
		contentEt = (MyEditText)findViewById(R.id.content_edit);
		contentEt.addTextChangedListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("意见反馈");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("意见反馈");
		MobclickAgent.onPause(this);
	}
	
	private void send(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					String content = contentEt.getText().toString();
					HttpUtil.feedBack(uid, content);
					Message msg = handler.obtainMessage();
					msg.what=0;
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
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (!contentEt.getText().toString().trim().isEmpty()){
			sendBtn.setEnabled(true);
		} else {
			sendBtn.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.send_button:
			send();
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
			GFToast.show(getApplicationContext(),"发送成功");
			this.finish();
			break;
		case -1:
			GFToast.show(getApplicationContext(),msg.obj.toString());
			
			break;

		default:
			break;
		}
	}

}

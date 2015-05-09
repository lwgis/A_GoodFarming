package com.zhonghaodi.goodfarming;


import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class CreateResponseActivity extends Activity implements HandMessage {
	private MyTextButton cancelBtn;
	private MyTextButton sendBtn;
	private MyEditText contentEt;
	private int questionId;
	private GFHandler<CreateResponseActivity> handler=new GFHandler<CreateResponseActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_create_response);
		contentEt = (MyEditText) findViewById(R.id.content_edit);
		questionId = getIntent().getIntExtra("questionId", 0);
		cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		contentEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (contentEt.length()>0) {
					CreateResponseActivity.this.sendBtn.setEnabled(true);
				}
				else {
					CreateResponseActivity.this.sendBtn.setEnabled(false);
				}
			}
		});
		sendBtn = (MyTextButton) findViewById(R.id.send_button);
		sendBtn.setEnabled(false);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager im = (InputMethodManager) CreateResponseActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(
						contentEt.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				Response response=new Response();
				response.setContent(contentEt.getText().toString());
				User writer = new User();
				writer.setId(GFUserDictionary.getUserId());
				response.setWriter(writer);
				sendResponse(response,questionId);
				sendBtn.setText("发送中...");
				sendBtn.setEnabled(false);
			}

			private void sendResponse(final Response response, final int qid) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							HttpUtil.sendResponse(response, qid);
							Message msg=handler.obtainMessage();
							msg.what=1;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(CreateResponseActivity.this, "发送失败", Toast.LENGTH_SHORT).show();;
							sendBtn.setText("发送");
							sendBtn.setEnabled(true);
						}
					}
				}).start();;
			}
		});

	}
	@Override
	public void handleMessage(Message msg,Object object) {
		CreateResponseActivity activity=(CreateResponseActivity)object;
		if (msg.what==1) {
			activity.setResult(2);
			activity.finish();
			Toast.makeText(activity, "发送成功", Toast.LENGTH_SHORT).show();;
		}
		else {
			Toast.makeText(activity, "发送失败", Toast.LENGTH_SHORT).show();
		}		
	}

}

package com.zhonghaodi.goodfarming;

import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PassBackActivity extends Activity implements HandMessage,OnClickListener {

	private EditText phoneEt;
	private Button okBtn;
	private Button cancelBtn;
	private GFHandler<PassBackActivity> handler = new GFHandler<PassBackActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passback);
		phoneEt = (EditText)findViewById(R.id.phone_edit);
		okBtn = (Button)findViewById(R.id.ok_button);
		okBtn.setOnClickListener(this);
		cancelBtn = (Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ok_button:
			modify();
			break;
			
		case R.id.cancel_button:
			this.finish();
			break;

		default:
			break;
		}
	}
	
	public void modify(){
		final String phonestr = phoneEt.getText().toString();
		if(phonestr.isEmpty()){
			GFToast.show("请输入你的手机号");
			return;
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String jsonString = HttpUtil.passback(phonestr,UILApplication.sendcount);
					UILApplication.sendcount++;
					Message msg = handler.obtainMessage();
					msg.obj = jsonString;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					GFToast.show("找回密码错误");
				}
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		try {
			String bstr = msg.obj.toString();
			int code = Integer.parseInt(bstr);
			GFToast.show("操作成功，新密码将以短信的形式发送");
			this.finish();
		} catch (Exception e) {
			// TODO: handle exception
			GFToast.show("找回密码错误");
		}
		
	}

}

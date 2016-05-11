package com.zhonghaodi.goodfarming;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Contact;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ContactActivity extends Activity implements OnClickListener,HandMessage {
	
	private EditText nameEditText;
	private EditText phoneEditText;
	private EditText postEditText;
	private EditText addressEditText;
	private GFHandler<ContactActivity> handler = new GFHandler<ContactActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		MobclickAgent.openActivityDurationTrack(false);
		Button cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		Button okButton = (Button)findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		nameEditText = (EditText)findViewById(R.id.name_edit);
		phoneEditText = (EditText)findViewById(R.id.phone_edit);
		postEditText = (EditText)findViewById(R.id.post_edit);
		addressEditText = (EditText)findViewById(R.id.address_edit);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("收货地址编辑");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("收货地址编辑");
		MobclickAgent.onPause(this);
	}
	
	public void add(){
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		final String name = nameEditText.getText().toString();
		final String phone = phoneEditText.getText().toString();
		final String post = postEditText.getText().toString();
		final String address = addressEditText.getText().toString();
		if(name.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入收货人姓名");
			return;
		}
		if(phone.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入收货人电话");
			return;
		}
		if(post.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入邮编");
			return;
		}
		if(address.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入详细地址");
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NetResponse netResponse = HttpUtil.addContact(uid,name,phone,post,address);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 1;
						msg.obj = netResponse.getResult();
					}
					else{
						msg.what = 0;
						msg.obj = netResponse.getMessage();
					}
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.ok_button:
			add();
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
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 1:
			if (msg.obj != null) {
				try {
					Gson gson = new Gson();
					Contact contact = gson.fromJson(msg.obj.toString(),
							new TypeToken<Contact>() {
							}.getType());
					ContactActivity.this.finish();
				} catch (Exception e) {
					// TODO: handle exception
					GFToast.show(getApplicationContext(),msg.obj.toString());
				}
				
			}
			break;

		default:
			break;
		}
		 
	}	
	
}

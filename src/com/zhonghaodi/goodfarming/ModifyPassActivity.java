package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.LoginUser;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyPassActivity extends Activity implements HandMessage,OnClickListener {
	
	private EditText oldPassEt;
	private EditText newPassEt;
	private EditText confirmPassEt;
	private Button okBtn;
	private Button cancelBtn;
	private TextView passButton;
	private GFHandler<ModifyPassActivity> handler = new GFHandler<ModifyPassActivity>(this);
	private String newpass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifypass);
		MobclickAgent.openActivityDurationTrack(false);
		oldPassEt = (EditText)findViewById(R.id.oldpass_edit);
		newPassEt = (EditText)findViewById(R.id.newpass_edit);
		confirmPassEt = (EditText)findViewById(R.id.confirmpass_edit);
		okBtn = (Button)findViewById(R.id.ok_button);
		okBtn.setOnClickListener(this);
		cancelBtn = (Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		passButton = (TextView)findViewById(R.id.passback_button);
		passButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ModifyPassActivity.this, PassBackActivity.class);
				ModifyPassActivity.this.startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("修改密码");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("修改密码");
		MobclickAgent.onPause(this);
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
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		String pass = GFUserDictionary.getPassword(getApplicationContext());
		String oldpass = oldPassEt.getText().toString();
		newpass = newPassEt.getText().toString();
		String confirmpass = confirmPassEt.getText().toString();
		if(oldpass.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入目前的密码");
			return;
		}
		if(newpass.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入新的密码");
			return;
		}
		if(confirmpass.isEmpty()){
			GFToast.show(getApplicationContext(),"请确认输入新的密码");
			return;
		}
		if(!pass.equals(oldpass)){
			GFToast.show(getApplicationContext(),"密码错误");
			return;
		}
		if(!newpass.equals(confirmpass)){
			GFToast.show(getApplicationContext(),"两次输入的新密码不一致");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NetResponse netResponse = HttpUtil.modifyPass(newpass,uid);
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
					GFToast.show(getApplicationContext(),"修改密码错误");
				}
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 1:
			String bstr = msg.obj.toString();
			if (bstr.equals("true")) {
				User user = new User();
				user.setPhone(GFUserDictionary.getPhone(getApplicationContext()));
				user.setPassword(newpass);
				user.setAlias(GFUserDictionary.getAlias(getApplicationContext()));
				user.setThumbnail(GFUserDictionary.getThumbnail(getApplicationContext()));
				user.setId(GFUserDictionary.getUserId(getApplicationContext()));
				GFUserDictionary.saveLoginInfo(getApplicationContext(),user, newpass, this);
				this.setResult(4);
				this.finish();
				GFToast.show(getApplicationContext(),"密码修改成功");
			}
			else {
				GFToast.show(getApplicationContext(),"密码修改失败");
			}
			break;

		default:
			break;
		}
		
	}

}

package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.MyTextButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SignActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		MyTextButton signbtn = (MyTextButton)findViewById(R.id.sign_button);
		signbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent loginit1 = new Intent(SignActivity.this,  
		                LoginActivity.class);  
				loginit1.putExtra("index", 1);
		        startActivity(loginit1); 
		        SignActivity.this.finish();
			}
		});
		MyTextButton registerbtn = (MyTextButton)findViewById(R.id.register_button);
		registerbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent loginit1 = new Intent(SignActivity.this,  
		                LoginActivity.class);  
		        startActivity(loginit1); 
		        SignActivity.this.finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("登录入口");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("登录入口");
		MobclickAgent.onPause(this);
	}

	
	
}

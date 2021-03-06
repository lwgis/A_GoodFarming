package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.model.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyTransactionActivity extends Activity implements OnClickListener {

	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mytransaction);
		user = (User)getIntent().getSerializableExtra("user");
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
//		View orderView = findViewById(R.id.layout1);
//		orderView.setOnClickListener(this);
		View payView = findViewById(R.id.layout2);
		payView.setOnClickListener(this);
		View exView = findViewById(R.id.layout3);
		exView.setOnClickListener(this);
//		View addView = findViewById(R.id.layout4);
//		addView.setOnClickListener(this);
		View nzdView = findViewById(R.id.layout5);
		nzdView.setOnClickListener(this);
		if(user.getLevel().getId()==3){
			nzdView.setVisibility(View.VISIBLE);
		}
		else{
			nzdView.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=null;
		switch (v.getId()) {
//		case R.id.layout1:
//			intent = new Intent(this, OrdersActivity.class);
//			break;
		case R.id.layout2:
			intent = new Intent(this, PayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", user);
			intent.putExtras(bundle);
			break;
		case R.id.layout3:
			intent = new Intent(this, ExchangeActivity.class);
			break;
//		case R.id.layout4:
//			intent = new Intent(this, ContactsActivity.class);
//			break;
		case R.id.layout5:
			intent = new Intent(this, NzdTransactionActivity.class);
			break;
		default:
			break;
		}
		startActivity(intent);
	}

}

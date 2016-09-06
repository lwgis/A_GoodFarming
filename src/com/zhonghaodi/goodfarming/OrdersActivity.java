package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OrdersActivity extends Activity implements OnClickListener {

	private View recipeView;
	private View miaoView;
	private View pointView;
	private View guaguaView;
	private View zfbtView;
	private View postView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders);
		int level = getIntent().getIntExtra("level", 0);
		pointView = findViewById(R.id.layout1);
		pointView.setOnClickListener(this);
		miaoView = findViewById(R.id.layout2);
		miaoView.setOnClickListener(this);
		recipeView = findViewById(R.id.layout3);
		recipeView.setOnClickListener(this);
		guaguaView = findViewById(R.id.layout4);
		guaguaView.setOnClickListener(this);
		zfbtView = findViewById(R.id.layout5);
		zfbtView.setOnClickListener(this);
		postView = findViewById(R.id.layout6);
		if(level!=3){
			postView.setVisibility(View.GONE);
		}
		else{
			postView.setVisibility(View.VISIBLE);
			postView.setOnClickListener(this);
		}
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
		switch (v.getId()) {
		case R.id.layout1:
			Intent intent = new Intent(this, PointOrdersActivity.class);
			startActivity(intent);
			break;
		case R.id.layout2:
			Intent intent1 = new Intent(this, MiaoOrdersActivity.class);
			startActivity(intent1);
			break;
		case R.id.layout3:
			Intent intent2 = new Intent(this, ShoppingCartActivity.class);
			startActivity(intent2);
			break;
		case R.id.layout4:
			Intent intent3 = new Intent(this, GuaOrdersActivity.class);
			startActivity(intent3);
			break;
		case R.id.layout5:
			Intent intent4 = new Intent(this, ZfbtOrdersActivity.class);
			startActivity(intent4);
			break;
		case R.id.layout6:
			Intent intent5 = new Intent(this, PostOrdersActivity.class);
			startActivity(intent5);
			break;
		default:
			break;
		}
	}

}

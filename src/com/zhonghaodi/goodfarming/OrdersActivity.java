package com.zhonghaodi.goodfarming;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders);
		pointView = findViewById(R.id.layout1);
		pointView.setOnClickListener(this);
		miaoView = findViewById(R.id.layout2);
		miaoView.setOnClickListener(this);
		recipeView = findViewById(R.id.layout3);
		recipeView.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
		default:
			break;
		}
	}

}

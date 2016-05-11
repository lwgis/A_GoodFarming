package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NzdsActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nzds);
		MobclickAgent.openActivityDurationTrack(false);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		View questionView = findViewById(R.id.layout1);
		questionView.setOnClickListener(this);
		View lalaguaView = findViewById(R.id.layout2);
		lalaguaView.setOnClickListener(this);
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
		Intent intent;
		
		switch (v.getId()) {
		case R.id.layout1:
			intent = new Intent(this, StoresActivity.class);
			break;
		case R.id.layout2:
			intent = new Intent(this, RecipesActivity.class);
			break;

		default:
			intent = null;
			break;
		}
		startActivity(intent);
	}

}

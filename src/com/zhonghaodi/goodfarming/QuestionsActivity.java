package com.zhonghaodi.goodfarming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class QuestionsActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questions);
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
		View adzwView = findViewById(R.id.layout3);
		adzwView.setOnClickListener(this);
		View wdzzfxView = findViewById(R.id.layout4);
		wdzzfxView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		intent = new Intent(this, MyQuestionsActivity.class);
		switch (v.getId()) {
		case R.id.layout1:
			
			break;
		case R.id.layout2:
			intent.putExtra("status", 1);
			break;
		case R.id.layout3:
			intent.putExtra("status", 3);
			break;
		case R.id.layout4:
			intent.putExtra("status", 2);
			break;
		default:
			break;
		}
		startActivity(intent);
	}

}

package com.zhonghaodi.goodfarming;

import com.zhonghaodi.customui.MyTextButton;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AppdownActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		MyTextButton cancelBtn = (MyTextButton)findViewById(R.id.back_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppdownActivity.this.finish();
			}
		});
	}

	
	
}

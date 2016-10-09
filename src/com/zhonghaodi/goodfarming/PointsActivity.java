package com.zhonghaodi.goodfarming;

import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PointsActivity extends Activity implements OnClickListener {

	private View commodityView;
//	private View rubblerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_points);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		commodityView = findViewById(R.id.layout6);
		commodityView.setOnClickListener(this);
//		rubblerView = findViewById(R.id.layout8);
//		rubblerView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.layout6:
			Intent intent5 = new Intent(this, CommoditiesActivity.class);
			startActivity(intent5);
			break;
//		case R.id.layout8:
//			int point = GFUserDictionary.getPoint(this.getApplicationContext());
//			int guagua = GFPointDictionary.getGuaguaPoint(this);
//			if(point>=guagua){
//				Intent intent7 = new Intent(this, RubblerActivity.class);
//				startActivity(intent7);
//			}
//			else{
//				GFToast.show(this.getApplicationContext(),"您的积分不足！");
//			}
//			break;
		default:
			break;
		}
	}

}

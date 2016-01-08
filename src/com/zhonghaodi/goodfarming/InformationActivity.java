package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;

import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InformationActivity extends Activity implements OnClickListener {
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		user = (User)getIntent().getSerializableExtra("user");
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		View cropView = findViewById(R.id.layout1);
		cropView.setOnClickListener(this);
		View nysView = findViewById(R.id.layout2);
		nysView.setOnClickListener(this);
		View nzdView = findViewById(R.id.layout3);
		nzdView.setOnClickListener(this);
		View infoView = findViewById(R.id.layout4);
		infoView.setOnClickListener(this);
		View passView = findViewById(R.id.layout5);
		passView.setOnClickListener(this);
		View zjView = findViewById(R.id.layout6);
		zjView.setOnClickListener(this);
		if(user.getLevel().getId()==1){
			nysView.setVisibility(View.VISIBLE);
			nzdView.setVisibility(View.VISIBLE);
		}
		else{
			nysView.setVisibility(View.GONE);
			nzdView.setVisibility(View.GONE);
		}
		if(user.getLevel().getId()==2){
			zjView.setVisibility(View.VISIBLE);
		}
		else{
			zjView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=null;
		switch (v.getId()) {
		case R.id.layout1:
			intent = new Intent(this, SelectCropActivity.class);
			ArrayList<Crop> selectCrops = null;
			if(user.getCrops()!=null && user.getCrops().size()>0){
				selectCrops = new ArrayList<Crop>();
				for (Iterator iterator = user.getCrops().iterator(); iterator.hasNext();) {
					UserCrop userCrop = (UserCrop) iterator.next();
					selectCrops.add(userCrop.getCrop());
				}
			}
			if (selectCrops != null) {
				intent.putParcelableArrayListExtra("crops", selectCrops);
			}
			startActivityForResult(intent, 100);
			break;
		case R.id.layout2:
			intent = new Intent(this, UpdateNysActivity.class);
			startActivity(intent);
			break;
		case R.id.layout3:
			intent = new Intent(this, UpdateNzdActivity.class);
			startActivity(intent);
			break;
		case R.id.layout4:
			intent = new Intent(this, ModifyInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", user);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.layout5:
			intent = new Intent(this, ModifyPassActivity.class);
			startActivity(intent);
			break;
		case R.id.layout6:
			intent = new Intent(this, UpdateZjActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}

}

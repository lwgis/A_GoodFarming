package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.MainActivity.MainHandler;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PointDic;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InformationActivity extends Activity implements OnClickListener {
	private User user;
	private InfoHandler handler = new InfoHandler(this);
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
		View addressView = findViewById(R.id.layout7);
		addressView.setOnClickListener(this);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("资料设置");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("资料设置");
		MobclickAgent.onPause(this);
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
		case R.id.layout7:
			intent = new Intent(this, ContactsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 100){
			ArrayList<Crop> selectCrops = data.getParcelableArrayListExtra("crops");
			user.setCrops(null);
			if (selectCrops!=null&&selectCrops.size()>0) {
				String cropString = "";
				List<UserCrop> userCrops = new ArrayList<UserCrop>();
				for (Crop c : selectCrops) {
					cropString = cropString + c.getName() + "  ";
					UserCrop userCrop = new UserCrop();
					userCrop.setCrop(c);
					userCrops.add(userCrop);
				}
				user.setCrops(userCrops);
			}
			updateCrops(user);
		}
	}
	
	/**
	 * 更新我的作物
	 * @param user
	 */
	private void updateCrops(final User user){
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					NetResponse netResponse = HttpUtil.modifyUser(user);
					Message msgUser = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msgUser.what = 2;
						msgUser.obj = netResponse.getResult();
					}
					else{
						msgUser.what = -1;
						msgUser.obj = netResponse.getMessage();
					}
					msgUser.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msgUser = handler.obtainMessage();
					msgUser.what = -1;
					msgUser.obj = e.getMessage();
					msgUser.sendToTarget();
				}
			}
		}).start();
	}
	
	static class InfoHandler extends Handler {
		InformationActivity activity;

		public InfoHandler(InformationActivity may) {
			activity = may;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			
			case 2:
				try {
					User user1 = (User) GsonUtil.fromJson(msg.obj.toString(),
							User.class);
					if(user1!=null){
						GFToast.show(activity,"更新成功");
						GFUserDictionary.saveLoginInfo(activity,user1, GFUserDictionary.getPassword(activity), activity);
					}
					else{
						GFToast.show(activity,"更新失败");
					}
				} catch (Exception e) {
					// TODO: handle exception
					GFToast.show(activity,"更新失败");
				}
				break;		
			case -1:
				if(msg.obj!=null){
					String errString = msg.obj.toString();
					GFToast.show(activity,errString);
				}
				
				break;
			default:
				break;
			}
			
		}
	}

}

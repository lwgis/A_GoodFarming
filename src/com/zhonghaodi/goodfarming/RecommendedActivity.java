package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.RecommendedAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class RecommendedActivity extends Activity implements HandMessage {
	
	private ListView listView;
	private List<User> users;
	private RecommendedAdapter adapter;
	private GFHandler<RecommendedActivity> handler = new GFHandler<RecommendedActivity>(this);
	private String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommended);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		listView = (ListView)findViewById(R.id.pull_refresh_list);
		uid = GFUserDictionary.getUserId(this);
		users = new ArrayList<User>();
		adapter = new RecommendedAdapter(users, this);
		listView.setAdapter(adapter);
		loadData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("我推荐的人");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("我推荐的人");
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 获取我推荐的人
	 */
	private void loadData(){
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String jsString = HttpUtil.getRecommendeds(uid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 1;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if(msg.what == 1){
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<User> us = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<User>>() {
						}.getType());
				users.clear();
				for (User user : us) {
					users.add(user);
				}
				adapter.notifyDataSetChanged();
				if(users.size()==0){
					GFToast.show(getApplicationContext(),"您没有推荐任何人");
				}
				
			} else {
				users.clear();
				adapter.notifyDataSetChanged();
				GFToast.show(getApplicationContext(),"您没有推荐任何人");
			}
		}
	}

}

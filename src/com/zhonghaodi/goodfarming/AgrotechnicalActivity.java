package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AgrotechnicalActivity extends Activity implements HandMessage {

	private ListView pullToRefreshListView;
	private List<Agrotechnical> agrotechnicals;
//	private RecipeAdapter adapter;
	private GFHandler<AgrotechnicalActivity> handler = new GFHandler<AgrotechnicalActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agrotechnical);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
		agrotechnicals = new ArrayList<Agrotechnical>();
//		adapter = new RecipeAdapter();
//		pullToRefreshListView.setAdapter(adapter);	
		loadData();
	}
	
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAgrotechnical();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();				
			}
		}).start();
		
	}	

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final AgrotechnicalActivity agrotechnicalactivity =(AgrotechnicalActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<Agrotechnical> agrs = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<Agrotechnical>>() {
					}.getType());
			if (msg.what == 0) {
				agrotechnicalactivity.agrotechnicals.clear();
			}
			for (Agrotechnical agrotechnical: agrs) {
				agrotechnicalactivity.agrotechnicals.add(agrotechnical);
			}
//			agrotechnicalactivity.adapter.notifyDataSetChanged();
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}

}

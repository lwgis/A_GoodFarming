package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderEvaluate;
import com.zhonghaodi.model.RecipeEvaluate;
import com.zhonghaodi.model.Zfbt;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ZfbtEvaluatesActivity extends Activity implements HandMessage,OnClickListener  {

	private ListView pullToRefreshListView;
	private TextView titleTextView;
	private List<RecipeEvaluate> allEvaluates;
	private List<RecipeEvaluate> evaluates;
	private EvaluateAdapter adapter;
	private TextView tabTextView0;
	private TextView tabTextView1;
	private TextView tabTextView2;
	private TextView tabTextView3;
	private GFHandler<ZfbtEvaluatesActivity> handler = new GFHandler<ZfbtEvaluatesActivity>(this);
	private Zfbt zfbt;
	private int disStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zfbtevaluates);
		zfbt = (Zfbt)getIntent().getSerializableExtra("zfbt");
		titleTextView = (TextView)findViewById(R.id.title_text);
		titleTextView.setText(zfbt.getTitle()+"的评价");
		tabTextView0 = (TextView)findViewById(R.id.tabtxt0);
		tabTextView0.setText("全部("+zfbt.getSumcount()+")");
		tabTextView0.setOnClickListener(this);
		tabTextView1 = (TextView)findViewById(R.id.tabtxt1);
		tabTextView1.setText("好评("+zfbt.getHaocount()+")");
		tabTextView1.setOnClickListener(this);
		tabTextView2 = (TextView)findViewById(R.id.tabtxt2);
		tabTextView2.setText("中评("+zfbt.getZhongcount()+")");
		tabTextView2.setOnClickListener(this);
		tabTextView3 = (TextView)findViewById(R.id.tabtxt3);
		tabTextView3.setText("差评("+zfbt.getChacount()+")");
		tabTextView3.setOnClickListener(this);
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		pullToRefreshListView = (ListView)findViewById(R.id.pull_refresh_list);
		adapter = new EvaluateAdapter();
		allEvaluates = new ArrayList<RecipeEvaluate>();
		evaluates = new ArrayList<RecipeEvaluate>();
		pullToRefreshListView.setAdapter(adapter);
		disStatus=0;
		selectTextView(tabTextView0);
		loadEvaluates();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("政府补贴订单评价");
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("政府补贴订单评价");
		MobclickAgent.onPause(this);
	}
	
	public void selectTextView(View view){
		tabTextView0.setTextColor(Color.rgb(128, 128, 128));
		tabTextView0.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		tabTextView1.setTextColor(Color.rgb(128, 128, 128));
		tabTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		tabTextView2.setTextColor(Color.rgb(128, 128, 128));
		tabTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		tabTextView3.setTextColor(Color.rgb(128, 128, 128));
		tabTextView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
	}
	
	private void loadEvaluates(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getZfbtEvaluates(zfbt.getId());
				Message msg = handler.obtainMessage();
				msg.what=1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void displayDatas(){
		allEvaluates.clear();
		switch (disStatus) {
		case 0:
			for (Iterator iterator = evaluates.iterator(); iterator.hasNext();) {
				RecipeEvaluate recipeEvaluate = (RecipeEvaluate) iterator.next();
				allEvaluates.add(recipeEvaluate);
			}
			break;

		default:
			for (Iterator iterator = evaluates.iterator(); iterator.hasNext();) {
				RecipeEvaluate recipeEvaluate = (RecipeEvaluate) iterator.next();
				if(recipeEvaluate.getScoring().getId()==disStatus){
					allEvaluates.add(recipeEvaluate);
				}
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	class EvaluateAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allEvaluates.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allEvaluates.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			HolderEvaluate holderEvaluate = null;
			RecipeEvaluate recipeEvaluate = null;
			recipeEvaluate = allEvaluates.get(position);
			
			convertView = LayoutInflater.from(ZfbtEvaluatesActivity.this).inflate(
					R.layout.cell_evaluate, parent, false);
			holderEvaluate = new HolderEvaluate(convertView);
			convertView.setTag(holderEvaluate);
			
			holderEvaluate = (HolderEvaluate)convertView.getTag();
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ recipeEvaluate.getUser().getThumbnail(),
							holderEvaluate.headIv, ImageOptions.options);
			holderEvaluate.nameTv.setText(recipeEvaluate.getUser().getAlias());
			holderEvaluate.contentTv.setText(recipeEvaluate.getEvaluate());
			holderEvaluate.timeTv.setText(recipeEvaluate.getTime());
			
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		selectTextView(v);
		switch (v.getId()) {
		case R.id.tabtxt0:
			disStatus = 0;
			break;
		case R.id.tabtxt1:
			disStatus = 1;
			break;
		case R.id.tabtxt2:
			disStatus = 2;		
			break;
		case R.id.tabtxt3:
			disStatus = 3;
			break;

		default:
			break;
		}
		displayDatas();
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<RecipeEvaluate> evaluates1 = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<RecipeEvaluate>>() {
						}.getType());
				evaluates.clear();
				for (RecipeEvaluate recipeEvaluate : evaluates1) {
					evaluates.add(recipeEvaluate);
				}
				displayDatas();
			} else {
				GFToast.show(getApplicationContext(),"请求失败，请检查网络状态稍后再试。");
			}
			break;

		default:
			break;
		}
	}
	
}

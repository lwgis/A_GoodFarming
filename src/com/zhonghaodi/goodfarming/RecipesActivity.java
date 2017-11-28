package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.RecipeAdapter;
import com.zhonghaodi.customui.CustomProgressDialog;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

public class RecipesActivity extends Activity implements HandMessage,OnClickListener,OnItemClickListener {
	private PullToRefreshListView pullToRefreshListView;
	private List<Recipe> recipes;
	private RecipeAdapter adapter;
	private GFHandler<RecipesActivity> handler = new GFHandler<RecipesActivity>(this);
	private double x;
	private double y;
	private CustomProgressDialog progressDialog;
	// 定位相关
	LocationClient mLocClient;
	public RecipeLocationListenner myListener = new RecipeLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipes);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);	
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if(recipes.size()==0){
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				loadMoreData(x,y,recipes.size());				
			}			
		});
		recipes = new ArrayList<Recipe>();
		adapter = new RecipeAdapter(recipes,this);
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);	
		location();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("配方团购");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("配方团购");
		MobclickAgent.onPause(this);
	}
	
	private void location() {
		if(progressDialog==null){
			progressDialog = new CustomProgressDialog(this, "定位中...");
		}
		progressDialog.show();
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	private void loadData(final double x,final double y){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getRecipes(x,y,0);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	private void loadMoreData(final double x,final double y,final int position){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getRecipes(x,y,position);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class RecipeLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			x=location.getLongitude();
			y=location.getLatitude();
//			x=118.7139351318554;
//			y=36.80689424778121;
			
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			loadData(x,y);
			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent it=new Intent();
		it.setClass(this, RecipeActivity.class);
		it.putExtra("recipeId", recipes.get(position-1).getId());
		it.putExtra("nzdCode", recipes.get(position-1).getNzd().getId());
		this.startActivity(it);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final RecipesActivity recipesactivity =(RecipesActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<Recipe> rs = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<Recipe>>() {
					}.getType());
			if (msg.what == 0) {
				recipesactivity.recipes.clear();
			}
			for (Recipe recipe : rs) {
				recipesactivity.recipes.add(recipe);
			}
			recipesactivity.adapter.notifyDataSetChanged();
			
		} else {
			GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
		}
		pullToRefreshListView.onRefreshComplete();
		if(recipes.size()==0){
			GFToast.show(getApplicationContext(),"附近没有配方！");
		}
	}

}

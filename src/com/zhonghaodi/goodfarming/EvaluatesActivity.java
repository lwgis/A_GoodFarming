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
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderEvaluate;
import com.zhonghaodi.customui.HolderRecipeInfo;
import com.zhonghaodi.goodfarming.AgrotechnicalActivity.AgroAdapter;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.RecipeEvaluate;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.integer;
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

public class EvaluatesActivity extends Activity implements HandMessage,OnClickListener {

	private PullToRefreshListView pullToRefreshListView;
	private TextView titleTextView;
	private List<RecipeEvaluate> allEvaluates;
	private List<RecipeEvaluate> evaluates;
	private EvaluateAdapter adapter;
	private TextView tabTextView0;
	private TextView tabTextView1;
	private TextView tabTextView2;
	private TextView tabTextView3;
	private GFHandler<EvaluatesActivity> handler = new GFHandler<EvaluatesActivity>(this);
	private String nzdCode;
	private int recipeId;
	private String recipeName;
	private int disStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluates);
		titleTextView = (TextView)findViewById(R.id.title_text);
		nzdCode = getIntent().getStringExtra("nzdCode");
		recipeId = getIntent().getIntExtra("recipeId", 0);
		recipeName = getIntent().getStringExtra("recipeName");
		int sumcount = getIntent().getIntExtra("sum", 0);
		int haocount = getIntent().getIntExtra("hao", 0);
		int zhongcount = getIntent().getIntExtra("zhong", 0);
		int chacount = getIntent().getIntExtra("cha", 0);
		titleTextView.setText(recipeName+"的评价");
		tabTextView0 = (TextView)findViewById(R.id.tabtxt0);
		tabTextView0.setText("全部("+sumcount+")");
		tabTextView0.setOnClickListener(this);
		tabTextView1 = (TextView)findViewById(R.id.tabtxt1);
		tabTextView1.setText("好评("+haocount+")");
		tabTextView1.setOnClickListener(this);
		tabTextView2 = (TextView)findViewById(R.id.tabtxt2);
		tabTextView2.setText("中评("+zhongcount+")");
		tabTextView2.setOnClickListener(this);
		tabTextView3 = (TextView)findViewById(R.id.tabtxt3);
		tabTextView3.setText("差评("+chacount+")");
		tabTextView3.setOnClickListener(this);
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadNewEvaluates();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allEvaluates.size() == 0) {
							return;
						}
						loadMoreEvaluates(allEvaluates.size());
					}

				});
		adapter = new EvaluateAdapter();
		allEvaluates = new ArrayList<RecipeEvaluate>();
		evaluates = new ArrayList<RecipeEvaluate>();
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);
		disStatus=0;
		selectTextView(tabTextView0);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadNewEvaluates();
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
	
	private void loadNewEvaluates(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getEvaluates(nzdCode, recipeId,0);
				Message msg = handler.obtainMessage();
				msg.what=1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadMoreEvaluates(final int position){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getEvaluates(nzdCode, recipeId,position);
				Message msg = handler.obtainMessage();
				msg.what=2;
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
			
			convertView = LayoutInflater.from(EvaluatesActivity.this).inflate(
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
				GFToast.show("请求失败，请检查网络状态稍后再试。");
			}
			pullToRefreshListView.onRefreshComplete();
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<RecipeEvaluate> evaluates1 = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<RecipeEvaluate>>() {
						}.getType());
				for (RecipeEvaluate recipeEvaluate : evaluates1) {
					evaluates.add(recipeEvaluate);
				}
				displayDatas();
			} else {
				GFToast.show("请求失败，请检查网络状态稍后再试。");
			}
			pullToRefreshListView.onRefreshComplete();
			break;

		default:
			break;
		}
	}

}

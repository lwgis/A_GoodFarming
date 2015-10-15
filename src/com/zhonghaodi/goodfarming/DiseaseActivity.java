package com.zhonghaodi.goodfarming;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderDisease;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.Disease;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiseaseActivity extends Activity implements HandMessage {
	private PullToRefreshListView pullToRefreshListView;
	private TextView titleTv;
	private int diseaseId;
	private Disease disease;
	private DiseaseAdapter adapter;
	private GFHandler<DiseaseActivity> handler = new GFHandler<DiseaseActivity>(
			this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		adapter = new DiseaseAdapter();
		super.setContentView(R.layout.activity_disease);
		titleTv = (TextView) findViewById(R.id.title_text);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setAdapter(adapter);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if(getIntent().hasExtra("diseaseId")){
			diseaseId = getIntent().getIntExtra("diseaseId", 0);
		}
		else{
			diseaseId=0;
		}
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
					}
				});
		loadData();
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position==1) {
					return;
				}
				Intent it=new Intent();
				it.setClass(DiseaseActivity.this, RecipeActivity.class);
				it.putExtra("recipeId", disease.getRecipes().get(position-2).getId());
				it.putExtra("nzdCode", disease.getRecipes().get(position-2).getNzd().getId());
				DiseaseActivity.this.startActivity(it);
			}
		});
	}

	protected void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String jsonString = HttpUtil.getDisease(diseaseId);
					Message msg = handler.obtainMessage();
					msg.obj = jsonString;
					msg.sendToTarget();
				} catch (Exception e) {
					Toast.makeText(DiseaseActivity.this, "获取病虫害信息失败",
							Toast.LENGTH_SHORT).show();
					pullToRefreshListView.onRefreshComplete();
					e.printStackTrace();
				}
			}
		}).start();
	}

	class DiseaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (disease == null) {
				return 0;
			}
			if (disease.getRecipes() == null
					|| disease.getRecipes().size() == 0) {
				return 1;
			}
			return disease.getRecipes().size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderDisease holderDisease;
			HolderRecipe holderRecipe;
			if (convertView == null) {
				switch (getItemViewType(position)) {
				case 0:
					convertView = LayoutInflater.from(DiseaseActivity.this)
							.inflate(R.layout.cell_disease, parent, false);
					holderDisease = new HolderDisease(convertView);
					convertView.setTag(holderDisease);
					break;
				case 1:
					convertView = LayoutInflater.from(DiseaseActivity.this)
							.inflate(R.layout.cell_recipe, parent, false);
					holderRecipe = new HolderRecipe(convertView);
					convertView.setTag(holderRecipe);
					break;
				default:
					break;
				}
			}
			switch (getItemViewType(position)) {
			case 0:
				holderDisease = (HolderDisease) convertView.getTag();
				if (disease.getThumbnail() != null) {
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"diseases/small/"
									+ disease.getThumbnail(),
							holderDisease.headIv, ImageOptions.options);
					holderDisease.headIv.setImages(disease.getAttachments(),
							"diseases");
					holderDisease.countTv.setText("共"
							+ String.valueOf(disease.getAttachments().size())
							+ "图片");
				}
				holderDisease.contentTv.setText(disease.getDescription());
				break;
			case 1:
				holderRecipe=(HolderRecipe)convertView.getTag();
				if (disease.getRecipes().get(position-1).getThumbnail()!=null) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"recipes/small/"+disease.getRecipes().get(position-1).getThumbnail(), holderRecipe.recipeIv, ImageOptions.optionsNoPlaceholder);
				}
				holderRecipe.titleTv.setText(disease.getRecipes().get(position-1).getTitle());
				holderRecipe.oldPriceTv.setText(String.valueOf(disease.getRecipes().get(position-1).getPrice()));
				holderRecipe.newPriceTv.setText(String.valueOf(disease.getRecipes().get(position-1).getNewprice()));
			default:
				break;
			}
			return convertView;
		}

	}

	@Override
	public void handleMessage(Message msg, Object object) {
		if (msg.obj != null) {
			DiseaseActivity activity = (DiseaseActivity) object;
			Gson gson = new Gson();
			try {
				activity.disease = gson.fromJson(msg.obj.toString(),
						Disease.class);
				titleTv.setText(disease.getName());
				adapter.notifyDataSetChanged();
				pullToRefreshListView.onRefreshComplete();
			} catch (JsonSyntaxException e) {
				pullToRefreshListView.onRefreshComplete();
				Toast.makeText(this, "获取病虫害信息失败", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "获取病虫害信息失败", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

}

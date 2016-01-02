package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Commodity;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Level;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommoditiesActivity extends Activity implements HandMessage,OnItemClickListener {

	private PullToRefreshGridView pullToRefreshGridView;
	private List<Commodity> commodities;
	private GFHandler<CommoditiesActivity> handler = new GFHandler<CommoditiesActivity>(this);
	private CommodityAdapter adapter;
	private Commodity selCommodity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commodities);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshGridView = (PullToRefreshGridView)findViewById(R.id.pull_refresh_grid);
		pullToRefreshGridView.setMode(Mode.BOTH);
		pullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						loaddata();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						int count = commodities.size();
						loadMoredata(count);
					}

				});
		pullToRefreshGridView.setOnItemClickListener(this);
		commodities = new ArrayList<Commodity>();
		adapter = new CommodityAdapter();
		pullToRefreshGridView.getRefreshableView().setAdapter(adapter);
		loaddata();
	}
	
	/**
	 * 获取积分商品
	 * @param position
	 */
	public void loaddata(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getCommodities(0);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	/**
	 * 获取积分商品
	 * @param position
	 */
	public void loadMoredata(final int position){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getCommodities(position);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	class CommodityHolder{
		public ImageView imageView;
		public TextView nameTextView;
		public TextView pointTextView;
		public TextView levelTextView;
		public Button exButton;
		public CommodityHolder(View view){
			imageView = (ImageView)view.findViewById(R.id.cImg);
			nameTextView = (TextView)view.findViewById(R.id.cName);
			pointTextView = (TextView)view.findViewById(R.id.cPoint);
			levelTextView = (TextView)view.findViewById(R.id.cLevels);
		}
	}
	
	class CommodityAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commodities.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return commodities.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			CommodityHolder commdityHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(CommoditiesActivity.this)
						.inflate(R.layout.cell_commodity, parent, false);
				commdityHolder = new CommodityHolder(convertView);
				convertView.setTag(commdityHolder);
			}
			commdityHolder = (CommodityHolder)convertView.getTag();
			Commodity commodity = commodities.get(position);
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"commodities/small/"
							+ commodity.getImage(),
							commdityHolder.imageView, ImageOptions.options);
			commdityHolder.nameTextView.setText(commodity.getName());
			commdityHolder.pointTextView.setText("积分："+String.valueOf(commodity.getPoint()));
			
			if(commodity.getLevels()!=null && commodity.getLevels().size()>0){
				String levelsStr = "";
				for (Iterator iterator = commodity.getLevels().iterator(); iterator
						.hasNext();) {
					Level level = (Level) iterator.next();
					levelsStr = levelsStr+level.getName()+"、";
				}
				levelsStr = levelsStr.substring(0, levelsStr.length()-1);
				levelsStr+="可换";
				commdityHolder.levelTextView.setText(levelsStr);
			}
			
			return convertView;
		}
		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		selCommodity = commodities.get(position);
		Intent intent = new Intent(CommoditiesActivity.this, CommodityActivity.class);
		intent.putExtra("cid", selCommodity.getId());
		this.startActivity(intent);

	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Commodity> comms = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Commodity>>() {
						}.getType());
				commodities.clear();
				for (Commodity commodity : comms) {
					commodities.add(commodity);
				}
				adapter.notifyDataSetChanged();
				
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			pullToRefreshGridView.onRefreshComplete();
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Commodity> comms = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Commodity>>() {
						}.getType());
				for (Commodity commodity : comms) {
					commodities.add(commodity);
				}
				adapter.notifyDataSetChanged();
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			pullToRefreshGridView.onRefreshComplete();
			break;

		default:
			break;
		}
		
	}	
}

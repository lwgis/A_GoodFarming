package com.zhonghaodi.goodfarming;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StoresActivity extends Activity implements HandMessage,OnClickListener {

	private PullToRefreshListView pullToRefreshListView;
	private List<Store> stores;
	private StoreAdapter adapter;
	private GFHandler<StoresActivity> handler = new GFHandler<StoresActivity>(this);
	private double distance=10000;
	private double x;
	private double y;
	// 定位相关
	LocationClient mLocClient;
	public StoreLocationListenner myListener = new StoreLocationListenner();
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stores);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
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
				distance = distance *2;
				loadData();
				
			}

			
		});
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Store store = stores.get(position-1);
				Intent intent = new Intent(StoresActivity.this, StoreActivity.class);
				intent.putExtra("name", store.getAlias());
				intent.putExtra("id", store.getId());
				startActivity(intent);
			}
		});		
		
		stores = new ArrayList<Store>();
		adapter = new StoreAdapter();
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);	
		location();
	}
	
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getStoresString(x, y, distance);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	private void location() {
		
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class StoreLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			x=location.getLongitude();
			y=location.getLatitude();
			
			loadData();
			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	class StoreHolder{
		public ImageView imageView;
		public TextView nameTextView;
		public TextView disTextView;
		public ImageView locationView;
		public StoreHolder(View view){
			imageView = (ImageView)view.findViewById(R.id.head_image);
			nameTextView = (TextView)view.findViewById(R.id.name_text);
			disTextView = (TextView)view.findViewById(R.id.dis_text);
			locationView = (ImageView)view.findViewById(R.id.map_image);
		}
	}
	
	class StoreAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return stores.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return stores.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Store store = stores.get(position);
			StoreHolder holder;
			if(convertView==null){
				convertView = LayoutInflater.from(StoresActivity.this)
						.inflate(R.layout.cell_stores, parent,false);
				holder = new StoreHolder(convertView);
				convertView.setTag(holder);
			}
			
			holder = (StoreHolder)convertView.getTag();
			ImageLoader.getInstance().displayImage(
					"http://121.40.62.120/appimage/users/small/"
							+ store.getThumbnail(),
							holder.imageView, ImageOptions.options);
			holder.nameTextView.setText(store.getAlias());
			if(store.getDistance()!=null){
				DecimalFormat    df   = new DecimalFormat("######0.00");   
				holder.disTextView.setText("距离："+df.format(store.getDistance())+"公里");
			}
			holder.locationView.setTag(store);
			holder.locationView.setOnClickListener(StoresActivity.this);
			return convertView;
		}
		
	}
	
	/**
	 * 点击定位按钮
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Store store = (Store)v.getTag();
		if(store==null){
			return;
		}
		Intent intent = new Intent(this, NearNzdMapActivity.class);
		intent.putExtra("x", store.getX());
		intent.putExtra("y", store.getY());
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final StoresActivity storeactivity =(StoresActivity)object;
		if (msg.obj != null) {
			if(msg.obj.toString().equals("80001")){
				
			}
			else{
				Gson gson = new Gson();
				List<Store> stores = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Store>>() {
						}.getType());
				if (msg.what == 0) {
					storeactivity.stores.clear();
				}
				for (Store store : stores) {
					storeactivity.stores.add(store);
				}
				storeactivity.adapter.notifyDataSetChanged();
			}
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
		storeactivity.pullToRefreshListView.onRefreshComplete();
	}

}

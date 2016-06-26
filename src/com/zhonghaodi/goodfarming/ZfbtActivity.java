package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.zhonghaodi.customui.CustomProgressDialog;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Second;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class ZfbtActivity extends Activity implements HandMessage,OnClickListener,OnItemClickListener {
	
	private PullToRefreshListView pullToRefreshListView;
	private List<Second> seconds;
	private SecondAdapter adapter;
	private GFHandler<ZfbtActivity> handler = new GFHandler<ZfbtActivity>(this);
	private double x;
	private double y;
	private CustomProgressDialog progressDialog;
	// 定位相关
	LocationClient mLocClient;
	public MiaoLocationListenner myListener = new MiaoLocationListenner();
	private int page=0;
	
	//顶部轮播相关
	private TextView ordersTv;
	private List<SecondOrder> secondOrders;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zfbt);
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
				if(seconds.size()==0){
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				int k =seconds.size()%20;
				if(k==0){
					page = seconds.size()/20;
					loadMoreData(x,y,page);
				}
				else{
					page = seconds.size()/20+1;
					loadMoreData(x,y,page);
				}
			}

			
		});
		seconds = new ArrayList<Second>();
		adapter = new SecondAdapter();
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);	
		location();
		initAdData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("政府补贴产品");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("政府补贴产品");
		MobclickAgent.onPause(this);
	}
	private void initAdData() {
		// 广告数据
		ordersTv = (TextView) findViewById(R.id.orders_tv);
		secondOrders = new ArrayList<SecondOrder>();
		loadNewOrders();
	}
	
	private void loadNewOrders(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getNewZfbtOrders();
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void loadData(final double x,final double y){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getZfbt(x,y,0);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	private void loadMoreData(final double x,final double y,final int page){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getZfbt(x,y,page);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
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
	
	/**
	 * 定位SDK监听函数
	 */
	public class MiaoLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
//			x=location.getLongitude();
//			y=location.getLatitude();
			x=118.798632;
			y=36.858719;
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			loadData(x,y);
			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	private void addDynamicView() {
		// 动态添加图片和下面指示的圆点
		// 初始化图片资源
		String strOrders = "";
		for (int i = 0; i < secondOrders.size(); i++) {
			strOrders+="            "+secondOrders.get(i).getUser().getAlias()+"抢购到了："+secondOrders.get(i).getSecond().getTitle();
		}
		ordersTv.setText(strOrders);
	}
	
	class HolderSecond {
		public ImageView secondIv;
		public TextView titleTv;
		public TextView oldPriceTv;
		public TextView newPriceTv;
		public TextView countTv;
		public TextView timeTv;
		public TextView nzdTv;
		 public HolderSecond(View view){
			 secondIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 oldPriceTv=(TextView)view.findViewById(R.id.oldprice_text);
			 newPriceTv=(TextView)view.findViewById(R.id.newprice_text);
			 oldPriceTv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
			 countTv = (TextView)view.findViewById(R.id.count_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
			 nzdTv=(TextView)view.findViewById(R.id.nzd_text);
		 }
	}
	
	class SecondAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return seconds.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return seconds.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			HolderSecond holdersecond;;
			if(convertView==null){
				convertView = LayoutInflater.from(ZfbtActivity.this)
						.inflate(R.layout.cell_second, parent, false);
				holdersecond = new HolderSecond(convertView);
				convertView.setTag(holdersecond);
			}
			
			holdersecond=(HolderSecond)convertView.getTag();
			Second second = seconds.get(position);
			if (second.getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/big/"+second.getImage(), holdersecond.secondIv, ImageOptions.optionsNoPlaceholder);
			}
			holdersecond.titleTv.setText(second.getTitle());
			holdersecond.nzdTv.setText(second.getNzd().getAlias());
			holdersecond.oldPriceTv.setText("￥"+String.valueOf(second.getOprice()));
			holdersecond.newPriceTv.setText("￥"+String.valueOf(second.getNprice()));
			holdersecond.countTv.setText("数量："+String.valueOf(second.getCount()));
			holdersecond.timeTv.setText("时间"+second.getStarttime());
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Second second = seconds.get(position-1);
		if(second!=null){
			Intent intent = new Intent(this,SecondActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("second", second);
			intent.putExtras(bundle);
			intent.putExtra("status", 1);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Second> ses = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Second>>() {
						}.getType());
				seconds.clear();
				for (Second second : ses) {
					seconds.add(second);
				}
				adapter.notifyDataSetChanged();
				pullToRefreshListView.onRefreshComplete();
				if(seconds.size()==0){
					GFToast.show(getApplicationContext(),"附近没有种好地补贴商品抢购活动！");
				}
				
			} else {
				pullToRefreshListView.onRefreshComplete();
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
				return;
			}
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Second> ses = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Second>>() {
						}.getType());
				for (Second second : ses) {
					seconds.add(second);
				}
				adapter.notifyDataSetChanged();
				pullToRefreshListView.onRefreshComplete();
				if(seconds.size()==0){
					GFToast.show(getApplicationContext(),"附近没有种好地补贴商品抢购活动！");
				}
				
			} else {
				pullToRefreshListView.onRefreshComplete();
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
				return;
			}
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<SecondOrder> ses = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<SecondOrder>>() {
						}.getType());
				secondOrders.clear();
				for (SecondOrder secondOrder : ses) {
					secondOrders.add(secondOrder);
				}
				addDynamicView();
				
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		
	}

}

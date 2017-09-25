package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.ZfbtOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ZfbtOrdersActivity extends Activity implements HandMessage,OnItemClickListener,OnClickListener {
	
	private ListView pullToRefreshListView;
	private TextView tabTextView0;
	private TextView tabTextView1;
	private TextView tabTextView2;
	private List<ZfbtOrder> zfbtOrders;
	private List<ZfbtOrder> cOrders;
	private ZfbtOrderAdapter adapter;
	private LinearLayout emptyLayout;
	private GFHandler<ZfbtOrdersActivity> handler = new GFHandler<ZfbtOrdersActivity>(this);
	private int disStatus;
	
	private double x;
	private double y;
	// 定位相关
	LocationClient mLocClient;
	public MiaoLocationListenner myListener = new MiaoLocationListenner();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zfbtorders);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);		
		tabTextView0 = (TextView)findViewById(R.id.tabtxt0);
		tabTextView0.setOnClickListener(this);
		tabTextView1 = (TextView)findViewById(R.id.tabtxt1);
		tabTextView1.setOnClickListener(this);
		tabTextView2 = (TextView)findViewById(R.id.tabtxt2);
		tabTextView2.setOnClickListener(this);
		emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
		zfbtOrders = new ArrayList<ZfbtOrder>();
		cOrders = new ArrayList<ZfbtOrder>();
		disStatus=0;
		adapter = new ZfbtOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);
		selectTextView(tabTextView0);
		location();
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
	public class MiaoLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			x=location.getLongitude();
			y=location.getLatitude();
//			x=118.798632;
//			y=36.858719;
			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	public void selectTextView(View view){
		tabTextView0.setTextColor(Color.rgb(128, 128, 128));
		tabTextView0.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		tabTextView1.setTextColor(Color.rgb(128, 128, 128));
		tabTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		tabTextView2.setTextColor(Color.rgb(128, 128, 128));
		tabTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
		MobclickAgent.onPageStart("政府补贴订单");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("政府补贴订单");
		MobclickAgent.onPause(this);
	}
	
	private void loadData(){
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString;
				jsonString = HttpUtil.getMyZfbts(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	private void displayDatas(){
		zfbtOrders.clear();
		for (Iterator iterator = cOrders.iterator(); iterator.hasNext();) {
			ZfbtOrder order = (ZfbtOrder) iterator.next();
			
			if(disStatus==0){
				if(order.getStatus()==0 || order.getStatus()==3){
					zfbtOrders.add(order);
				}
			}
			else{
				if(order.getStatus()==disStatus){
					zfbtOrders.add(order);
				}
			}
		}
		adapter.notifyDataSetChanged();
		if(zfbtOrders.size()>0){
			emptyLayout.setVisibility(View.GONE);
		}
		else{
			emptyLayout.setVisibility(View.VISIBLE);
		}
	}
	
	class HolderSecondOrder {
		public ImageView secondIv;
		public TextView titleTv;
//		public TextView nzdTv;
		public TextView countPriceTv;
		public TextView sumPriceTv;
		public TextView statusTv;
		public TextView timeTv;
		public TextView posterTv;
		 public HolderSecondOrder(View view){
			 secondIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
//			 nzdTv = (TextView)view.findViewById(R.id.nzd_text);
			 countPriceTv=(TextView)view.findViewById(R.id.countprice_text);
			 sumPriceTv=(TextView)view.findViewById(R.id.sumprice_text);
			 statusTv = (TextView)view.findViewById(R.id.status_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
			 posterTv = (TextView)view.findViewById(R.id.poster_text);
		 }
	}
	
	class ZfbtOrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return zfbtOrders.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return zfbtOrders.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			HolderSecondOrder holdersecondorder;;
			if(convertView==null){
				convertView = LayoutInflater.from(ZfbtOrdersActivity.this)
						.inflate(R.layout.cell_zfbtorder, parent, false);
				holdersecondorder = new HolderSecondOrder(convertView);
				convertView.setTag(holdersecondorder);
			}
			
			holdersecondorder=(HolderSecondOrder)convertView.getTag();
			ZfbtOrder zfbtOrder = zfbtOrders.get(position);
			if (zfbtOrder.getSecond().getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+zfbtOrder.getSecond().getImage(), holdersecondorder.secondIv, ImageOptions.optionsNoPlaceholder);
			}
			holdersecondorder.titleTv.setText(zfbtOrder.getSecond().getTitle());
//			holdersecondorder.nzdTv.setText(zfbtOrder.getSecond().getNzd().getAlias());
			holdersecondorder.countPriceTv.setVisibility(View.GONE);
			String str = "总价:";
			if(zfbtOrder.getPrice()!=null && zfbtOrder.getPrice()>0){
				str+="￥"+zfbtOrder.getPrice();
			}
			if(zfbtOrder.getCoupon()>0){
				str+=",优惠币"+zfbtOrder.getCoupon()+"个";
			}
			holdersecondorder.sumPriceTv.setText(str);
			if(zfbtOrder.getStatus()==0){
				holdersecondorder.statusTv.setText("待发货");
			}
			else if(zfbtOrder.getStatus()==1){
				holdersecondorder.statusTv.setText("待评价");
			}
			else if(zfbtOrder.getStatus()==2){
				holdersecondorder.statusTv.setText("已评价");
			}
			else{
				holdersecondorder.statusTv.setText("已到农资店");
			}
			holdersecondorder.timeTv.setText("时间:"+zfbtOrder.getTime());
			if(zfbtOrder.getNzd()==null){
				holdersecondorder.posterTv.setText("发货农资店：分配中...");
			}
			else{
				holdersecondorder.posterTv.setText("发货农资店："+zfbtOrder.getNzd().getAlias());
			}
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.tabtxt0:
			selectTextView(v);
			disStatus = 0;
			displayDatas();
			break;
		case R.id.tabtxt1:
			selectTextView(v);
			disStatus = 1;
			displayDatas();
			break;
		case R.id.tabtxt2:
			selectTextView(v);
			disStatus = 2;	
			displayDatas();
			break;
		case R.id.cancel_button:
			this.finish();
			break;
		default:
			break;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		// TODO Auto-generated method stub
		ZfbtOrder zfbtOrder = zfbtOrders.get(position);
		if(zfbtOrder!=null){
			if(zfbtOrder.getStatus()==0 || zfbtOrder.getStatus()==3){
				Intent intent = new Intent(this, SecondCodeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", zfbtOrder);
				intent.putExtras(bundle);
				intent.putExtra("status", 1);
				startActivity(intent);
			}
			else if(zfbtOrder.getStatus()==1){
				Intent intent = new Intent(this, CreateZfbtEvaluateActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", zfbtOrder);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			else{
//				GFToast.show(this, "订单已完成");
				Intent intent = new Intent(this,ZfbtInfoActivity.class);
				intent.putExtra("zid", zfbtOrder.getSecond().getId());
				intent.putExtra("x", x);
				intent.putExtra("y", y);
				startActivity(intent);
			}
			
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<ZfbtOrder> zfbts = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<ZfbtOrder>>() {
					}.getType());
			if (msg.what == 0) {
				cOrders.clear();
			}
			for (ZfbtOrder zfbtOrder : zfbts) {
				cOrders.add(zfbtOrder);
			}
			displayDatas();
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}

}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.PointOrder;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PointOrdersActivity extends Activity implements OnItemClickListener,HandMessage {

	private ListView pullToRefreshListView;
	private List<PointOrder> pointOrders;
	private PointOrderAdapter adapter;
	private LinearLayout emptyLayout;
	private GFHandler<PointOrdersActivity> handler = new GFHandler<PointOrdersActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pointorders);
		emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PointOrdersActivity.this.finish();
				
			}
		});
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);		
		pointOrders = new ArrayList<PointOrder>();
		adapter = new PointOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
		MobclickAgent.onPageStart("积分订单");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("积分订单");
		MobclickAgent.onPause(this);
	}


	public void loadData(){
		
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getPointOrders(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	class HolderPointOrder {
		public ImageView pointIv;
		public TextView titleTv;
		public TextView pointTv;
		public TextView statusTv;
		public TextView timeTv;
		public TextView posterTv;
		 public HolderPointOrder(View view){
			 pointIv=(ImageView)view.findViewById(R.id.point_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 pointTv = (TextView)view.findViewById(R.id.point_text);
			 statusTv = (TextView)view.findViewById(R.id.status_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
			 posterTv = (TextView)view.findViewById(R.id.poster_text);
		 }
	}
	
	class PointOrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pointOrders.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pointOrders.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			HolderPointOrder holderpointorder;;
			if(convertView==null){
				convertView = LayoutInflater.from(PointOrdersActivity.this)
						.inflate(R.layout.cell_pointorder, parent, false);
				holderpointorder = new HolderPointOrder(convertView);
				convertView.setTag(holderpointorder);
			}
			
			holderpointorder=(HolderPointOrder)convertView.getTag();
			PointOrder pointOrder = pointOrders.get(position);
			if (pointOrder.getCommodity().getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"commodities/small/"+pointOrder.getCommodity().getImage(), holderpointorder.pointIv, ImageOptions.optionsNoPlaceholder);
			}
			holderpointorder.titleTv.setText(pointOrder.getCommodity().getName());
			if(pointOrder.getCommodity().getExchange()==null || pointOrder.getCommodity().getExchange()==0){
				holderpointorder.pointTv.setText("积分："+String.valueOf(pointOrder.getCommodity().getPoint()));
			}
			else{
				holderpointorder.pointTv.setText("推荐币："+String.valueOf(pointOrder.getCommodity().getTjcoin()==null?0:pointOrder.getCommodity().getTjcoin()));
			}
			if(pointOrder.getStatus()==1){
				holderpointorder.statusTv.setText("已发货");
			}
			else if(pointOrder.getStatus()==0){
				holderpointorder.statusTv.setText("配货中");
			}
			else if(pointOrder.getStatus()==2){
				holderpointorder.statusTv.setText("交易完成");
			}
			else{
				holderpointorder.statusTv.setText("已到农资店");
			}
			holderpointorder.timeTv.setText("时间:"+pointOrder.getTime());
			if(pointOrder.getNzd()==null){
				holderpointorder.posterTv.setText("发货农资店：分配中...");
			}
			else{
				holderpointorder.posterTv.setText("发货农资店："+pointOrder.getNzd().getAlias());
			}
			return convertView;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		PointOrder pointOrder = pointOrders.get(position);
		Intent intent = new Intent(this, PointOrderActivity.class);
		intent.putExtra("order", pointOrder);
		startActivity(intent);
	}
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<PointOrder> poos = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<PointOrder>>() {
					}.getType());
			if (msg.what == 0) {
				pointOrders.clear();
			}
			for (PointOrder pointOrder : poos) {
				pointOrders.add(pointOrder);
			}
			adapter.notifyDataSetChanged();
			if(pointOrders.size()>0){
				emptyLayout.setVisibility(View.GONE);
			}
			else{
				emptyLayout.setVisibility(View.VISIBLE);
			}
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}
}

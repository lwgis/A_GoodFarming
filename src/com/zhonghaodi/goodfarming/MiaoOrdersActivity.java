package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.MiaoActivity.HolderSecond;
import com.zhonghaodi.goodfarming.MiaoActivity.SecondAdapter;
import com.zhonghaodi.model.GFUserDictionary;
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

public class MiaoOrdersActivity extends Activity implements HandMessage,OnItemClickListener,OnClickListener {
	private ListView pullToRefreshListView;
	private List<SecondOrder> secondOrders;
	private SecondOrderAdapter adapter;
	private LinearLayout emptyLayout;
	private GFHandler<MiaoOrdersActivity> handler = new GFHandler<MiaoOrdersActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miaoorders);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);		
		emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
		secondOrders = new ArrayList<SecondOrder>();
		adapter = new SecondOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);
		loadData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("秒杀订单");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("秒杀订单");
		MobclickAgent.onPause(this);
	}
	
	private void loadData(){
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString;
				jsonString = HttpUtil.getMySeconds(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	class HolderSecondOrder {
		public ImageView secondIv;
		public TextView titleTv;
		public TextView nzdTv;
		public TextView countPriceTv;
		public TextView sumPriceTv;
		public TextView statusTv;
		public TextView timeTv;
		 public HolderSecondOrder(View view){
			 secondIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 nzdTv = (TextView)view.findViewById(R.id.nzd_text);
			 countPriceTv=(TextView)view.findViewById(R.id.countprice_text);
			 sumPriceTv=(TextView)view.findViewById(R.id.sumprice_text);
			 statusTv = (TextView)view.findViewById(R.id.status_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class SecondOrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return secondOrders.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return secondOrders.get(position);
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
				convertView = LayoutInflater.from(MiaoOrdersActivity.this)
						.inflate(R.layout.cell_order, parent, false);
				holdersecondorder = new HolderSecondOrder(convertView);
				convertView.setTag(holdersecondorder);
			}
			
			holdersecondorder=(HolderSecondOrder)convertView.getTag();
			SecondOrder secondOrder = secondOrders.get(position);
			if (secondOrder.getSecond().getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+secondOrder.getSecond().getImage(), holdersecondorder.secondIv, ImageOptions.optionsNoPlaceholder);
			}
			holdersecondorder.titleTv.setText(secondOrder.getSecond().getTitle());
			holdersecondorder.nzdTv.setText(secondOrder.getSecond().getNzd().getAlias());
			holdersecondorder.countPriceTv.setText("￥"+String.valueOf(secondOrder.getSecond().getNprice())+" X 1份");
			holdersecondorder.sumPriceTv.setText("共￥"+String.valueOf(secondOrder.getSecond().getNprice()));
			if(secondOrder.getStatus()==0){
				holdersecondorder.statusTv.setText("等待发货");
			}
			else if(secondOrder.getStatus()==1){
				holdersecondorder.statusTv.setText("交易成功");
			}
			else{
				holdersecondorder.statusTv.setText("已评价");
			}
			holdersecondorder.timeTv.setText("时间:"+secondOrder.getTime());
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		SecondOrder secondOrder = secondOrders.get(position);
		if(secondOrder!=null){
			if(secondOrder.getStatus()==1){
				GFToast.show(getApplicationContext(),"交易已经完成！");
				return;
			}
			Intent intent = new Intent(this, SecondCodeActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("order", secondOrder);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<SecondOrder> ses = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<SecondOrder>>() {
					}.getType());
			if (msg.what == 0) {
				secondOrders.clear();
			}
			for (SecondOrder secondOrder : ses) {
				secondOrders.add(secondOrder);
			}
			adapter.notifyDataSetChanged();
			if(secondOrders.size()>0){
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

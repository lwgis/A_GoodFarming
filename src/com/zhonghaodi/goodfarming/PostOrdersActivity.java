package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

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

public class PostOrdersActivity extends Activity implements HandMessage,OnItemClickListener,OnClickListener {
	private ListView pullToRefreshListView;
	private List<ZfbtOrder> zfbtOrders;
	private PostOrderAdapter adapter;
	private LinearLayout emptyLayout;
	private GFHandler<PostOrdersActivity> handler = new GFHandler<PostOrdersActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_postorders);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);		
		emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
		zfbtOrders = new ArrayList<ZfbtOrder>();
		adapter = new PostOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("我派送的订单");
		MobclickAgent.onResume(this);
		loadData();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("我派送的订单");
		MobclickAgent.onPause(this);
	}
	
	private void loadData(){
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString= HttpUtil.getMyPostOrders(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	class HolderZfbtOrder {
		public ImageView zfbtIv;
		public TextView titleTv;
		public TextView contactTv;
		public TextView addressTv;
		public TextView statusTv;
		public TextView timeTv;
		 public HolderZfbtOrder(View view){
			 zfbtIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 contactTv=(TextView)view.findViewById(R.id.contact_text);
			 addressTv=(TextView)view.findViewById(R.id.address_text);
			 statusTv = (TextView)view.findViewById(R.id.status_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class PostOrderAdapter extends BaseAdapter{

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
			HolderZfbtOrder holderpostorder;;
			if(convertView==null){
				convertView = LayoutInflater.from(PostOrdersActivity.this)
						.inflate(R.layout.cell_postorder, parent, false);
				holderpostorder = new HolderZfbtOrder(convertView);
				convertView.setTag(holderpostorder);
			}
			
			holderpostorder=(HolderZfbtOrder)convertView.getTag();
			ZfbtOrder zfbtOrder = zfbtOrders.get(position);
			if (zfbtOrder.getSecond().getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+zfbtOrder.getSecond().getImage(), holderpostorder.zfbtIv, ImageOptions.optionsNoPlaceholder);
			}
			String title = zfbtOrder.getSecond().getTitle();
			
			holderpostorder.titleTv.setText(title);
			if(zfbtOrder.getContact()!=null){
				holderpostorder.contactTv.setVisibility(View.VISIBLE);
				holderpostorder.addressTv.setVisibility(View.VISIBLE);
				holderpostorder.contactTv.setText("收件人："+zfbtOrder.getContact().getName());
				holderpostorder.addressTv.setText("地址："+zfbtOrder.getContact().getAddress());
			}
			else{
				holderpostorder.contactTv.setVisibility(View.GONE);
				holderpostorder.addressTv.setVisibility(View.GONE);
			}
			if(zfbtOrder.getStatus()==1){
				holderpostorder.statusTv.setText("交易完成");
			}
			else{
				holderpostorder.statusTv.setText("交易未完成");
			}
			holderpostorder.timeTv.setText("时间:"+zfbtOrder.getTime());
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.cancel_button){
			this.finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ZfbtOrder order = zfbtOrders.get(position);
		Intent intent = new Intent(this, SecondOrderActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", order);
		intent.putExtras(bundle);
		intent.putExtra("status", 1);
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<ZfbtOrder> ses = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<ZfbtOrder>>() {
					}.getType());
			zfbtOrders.clear();
			for (ZfbtOrder zfbtOrder : ses) {
				zfbtOrders.add(zfbtOrder);
			}
			adapter.notifyDataSetChanged();
			if(zfbtOrders.size()>0){
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

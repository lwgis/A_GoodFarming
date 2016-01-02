package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.goodfarming.PointOrdersActivity.HolderPointOrder;
import com.zhonghaodi.goodfarming.PointOrdersActivity.PointOrderAdapter;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GuaOrder;
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

public class GuaOrdersActivity extends Activity implements OnItemClickListener,HandMessage {
	
	private ListView pullToRefreshListView;
	private List<GuaOrder> guaOrders;
	private GuaOrderAdapter adapter;
	private LinearLayout emptyLayout;
	private GFHandler<GuaOrdersActivity> handler = new GFHandler<GuaOrdersActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guaorders);
		emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GuaOrdersActivity.this.finish();
				
			}
		});
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);		
		guaOrders = new ArrayList<GuaOrder>();
		adapter = new GuaOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
	}
	
	public void loadData(){
		
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getGuaOrders(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	class HolderGuaOrder {
		public ImageView pointIv;
		public TextView titleTv;
		public TextView statusTv;
		public TextView timeTv;
		 public HolderGuaOrder(View view){
			 pointIv=(ImageView)view.findViewById(R.id.point_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 statusTv = (TextView)view.findViewById(R.id.status_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class GuaOrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return guaOrders.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return guaOrders.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			HolderGuaOrder holderguaorder;;
			if(convertView==null){
				convertView = LayoutInflater.from(GuaOrdersActivity.this)
						.inflate(R.layout.cell_guaorder, parent, false);
				holderguaorder = new HolderGuaOrder(convertView);
				convertView.setTag(holderguaorder);
			}
			
			holderguaorder=(HolderGuaOrder)convertView.getTag();
			GuaOrder guaOrder = guaOrders.get(position);
			if (guaOrder.getGuagua().getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"guagua/small/"+guaOrder.getGuagua().getImage(), holderguaorder.pointIv, ImageOptions.optionsNoPlaceholder);
			}
			holderguaorder.titleTv.setText(guaOrder.getGuagua().getName());
			if(guaOrder.getStatus()==1){
				holderguaorder.statusTv.setText("未出库");
			}
			else if(guaOrder.getStatus()==2){
				holderguaorder.statusTv.setText("已发货");
			}
			else if(guaOrder.getStatus()==3){
				holderguaorder.statusTv.setText("已完成");
			}
			holderguaorder.timeTv.setText("时间:"+guaOrder.getTime());
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		GuaOrder guaOrder = guaOrders.get(position);
		Intent intent = new Intent(this, GuaOrderActivity.class);
		intent.putExtra("order", guaOrder);
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<GuaOrder> poos = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<GuaOrder>>() {
					}.getType());
			if (msg.what == 0) {
				guaOrders.clear();
			}
			for (GuaOrder guaOrder : poos) {
				guaOrders.add(guaOrder);
			}
			adapter.notifyDataSetChanged();
			if(guaOrders.size()>0){
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

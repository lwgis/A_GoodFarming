package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShoppingCartActivity extends Activity implements HandMessage,OnClickListener {

	private String userid;
	private ListView pullToRefreshListView;
	private List<RecipeOrder> recipeOrders;
	private MyOrderAdapter adapter;
	private TextView orderTextView0;
	private TextView orderTextView1;
	private TextView orderTextView2;
	private LinearLayout emptyLayout;
	private int status = 0;
	private GFHandler<ShoppingCartActivity> handler = new GFHandler<ShoppingCartActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppingcart);
		emptyLayout = (LinearLayout)findViewById(R.id.empty_layout);
		orderTextView0 = (TextView)findViewById(R.id.ordertxt0);
		orderTextView0.setOnClickListener(this);
		orderTextView1 = (TextView)findViewById(R.id.ordertxt1);
		orderTextView1.setOnClickListener(this);
		orderTextView2 = (TextView)findViewById(R.id.ordertxt2);
		orderTextView2.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		userid = GFUserDictionary.getUserId(getApplicationContext());
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (status) {
				case 0:
					RecipeOrder recipeOrder = recipeOrders.get(position);
					Intent it=new Intent();
					it.setClass(ShoppingCartActivity.this, RecipeOrderActivity.class);
					it.putExtra("nzdId", recipeOrder.getRecipe().getNzd().getId());
					it.putExtra("recipeId", recipeOrder.getRecipe().getId());
					it.putExtra("userId", recipeOrder.getUser().getId());
					it.putExtra("count", recipeOrder.getCount());
					it.putExtra("recipeName", recipeOrder.getRecipe().getTitle());
					it.putExtra("qrcode", recipeOrder.getCode());
					ShoppingCartActivity.this.startActivity(it);
					break;
				case 1:
					RecipeOrder rOrder = recipeOrders.get(position);
					Intent intent1= new Intent(ShoppingCartActivity.this,EvaluateActivity.class);
					intent1.putExtra("urid", rOrder.getId());
					intent1.putExtra("rid", rOrder.getRecipe().getId());
					intent1.putExtra("nzdid", rOrder.getRecipe().getNzd().getId());
					ShoppingCartActivity.this.startActivity(intent1);
					break;
				case 2:
					RecipeOrder order = recipeOrders.get(position);
					Intent intent = new Intent(ShoppingCartActivity.this, CompleteOrderActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("order", order);
					intent.putExtras(bundle);
					ShoppingCartActivity.this.startActivity(intent);
					break;

				default:
					break;
				}
				
				
			}
		});
		recipeOrders = new ArrayList<RecipeOrder>();
		adapter = new MyOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);	
		selectTextView(orderTextView0);
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		switch (status) {
		case 0:
			loadData();
			break;
		case 1:
			loadScoringData();
			break;
		case 2:
			loadCompleteData();
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 获取未完成订单
	 */
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getMyOrders(userid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	/**
	 * 获取待评价订单
	 */
	private void loadScoringData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getScoringOrders(userid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	/**
	 * 获取已完成订单
	 */
	private void loadCompleteData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getCompleteOrders(userid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	public void selectTextView(View view){
		orderTextView0.setTextColor(Color.rgb(128, 128, 128));
		orderTextView0.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		orderTextView1.setTextColor(Color.rgb(128, 128, 128));
		orderTextView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		orderTextView2.setTextColor(Color.rgb(128, 128, 128));
		orderTextView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
	}
	
	class MyOrderHolder{
		public ImageView secondIv;
		public TextView titleTv;
		public TextView nzdTv;
		public TextView countPriceTv;
		public TextView sumPriceTv;
		public TextView statusTv;
		public TextView timeTv;
		 public MyOrderHolder(View view){
			 secondIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 nzdTv = (TextView)view.findViewById(R.id.nzd_text);
			 countPriceTv=(TextView)view.findViewById(R.id.countprice_text);
			 sumPriceTv=(TextView)view.findViewById(R.id.sumprice_text);
			 statusTv = (TextView)view.findViewById(R.id.status_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class MyOrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return recipeOrders.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return recipeOrders.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyOrderHolder orderHolder;;
			if(convertView==null){
				convertView = LayoutInflater.from(ShoppingCartActivity.this)
						.inflate(R.layout.cell_order, parent, false);
				orderHolder = new MyOrderHolder(convertView);
				convertView.setTag(orderHolder);
			}
			
			orderHolder=(MyOrderHolder)convertView.getTag();
			RecipeOrder recipeOrder = recipeOrders.get(position);
			if (recipeOrder.getRecipe().getThumbnail()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"recipes/small/"+recipeOrder.getRecipe().getThumbnail(), orderHolder.secondIv, ImageOptions.optionsNoPlaceholder);
			}
			orderHolder.titleTv.setText(recipeOrder.getRecipe().getTitle());
			orderHolder.nzdTv.setText(recipeOrder.getRecipe().getNzd().getAlias());
			orderHolder.countPriceTv.setText("￥"+recipeOrder.getRecipe().getNewprice()+" X "+recipeOrder.getCount()+"份");
			orderHolder.sumPriceTv.setText("共￥"+recipeOrder.getPrice());
			orderHolder.timeTv.setText("时间:"+recipeOrder.getTime());
			if(recipeOrder.getStatus()==2){
				orderHolder.statusTv.setText("已完成");
			}
			else if(recipeOrder.getStatus()==1){
				orderHolder.statusTv.setText("待评价");
			}
			else{
				orderHolder.statusTv.setText("交易未完成");
			}
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ordertxt0:
			selectTextView(v);
			loadData();
			status = 0;
			
			break;
		case R.id.ordertxt1:
			selectTextView(v);
			loadScoringData();
			status = 1;
			
			break;
		case R.id.ordertxt2:
			selectTextView(v);
			loadCompleteData();
			status = 2;
			break;

		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final ShoppingCartActivity shoppingcartactivity =(ShoppingCartActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<RecipeOrder> orders = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<RecipeOrder>>() {
					}.getType());
			if (msg.what == 0) {
				shoppingcartactivity.recipeOrders.clear();
			}
			for (RecipeOrder recipeorder : orders) {
				shoppingcartactivity.recipeOrders.add(recipeorder);
			}
			shoppingcartactivity.adapter.notifyDataSetChanged();
			if(shoppingcartactivity.recipeOrders.size()>0){
				emptyLayout.setVisibility(View.GONE);
			}
			else{
				emptyLayout.setVisibility(View.VISIBLE);
			}
			
		} else {
			GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
		}
	}
	
}

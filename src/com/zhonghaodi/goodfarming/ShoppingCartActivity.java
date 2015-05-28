package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShoppingCartActivity extends Activity implements HandMessage {

	private String userid;
	private ListView pullToRefreshListView;
	private List<RecipeOrder> recipeOrders;
	private MyOrderAdapter adapter;
	private GFHandler<ShoppingCartActivity> handler = new GFHandler<ShoppingCartActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppingcart);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		userid = GFUserDictionary.getUserId();
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
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
				
			}
		});
		recipeOrders = new ArrayList<RecipeOrder>();
		adapter = new MyOrderAdapter();
		pullToRefreshListView.setAdapter(adapter);	
		
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
	}



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
	
	class MyOrderHolder{
		public ImageView recipeImageView;
		public TextView recipeTitleView;
		public TextView priceTextView;
		public TextView countTextView;
		public TextView sumTextView;
		public MyOrderHolder(View view){
			recipeImageView = (ImageView)view.findViewById(R.id.recipe_image);
			recipeTitleView = (TextView)view.findViewById(R.id.title_text);
			priceTextView = (TextView)view.findViewById(R.id.price_text);
			countTextView = (TextView)view.findViewById(R.id.count_text);
			sumTextView = (TextView)view.findViewById(R.id.pricesum_text);
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
						.inflate(R.layout.cell_recipeorder, parent, false);
				orderHolder = new MyOrderHolder(convertView);
				convertView.setTag(orderHolder);
			}
			
			orderHolder=(MyOrderHolder)convertView.getTag();
			RecipeOrder recipeOrder = recipeOrders.get(position);
			if (recipeOrder.getRecipe().getThumbnail()!=null) {
				ImageLoader.getInstance().displayImage("http://121.40.62.120/appimage/recipes/small/"+recipeOrder.getRecipe().getThumbnail(), orderHolder.recipeImageView, ImageOptions.optionsNoPlaceholder);
			}
			orderHolder.recipeTitleView.setText(recipeOrder.getRecipe().getTitle());
			orderHolder.priceTextView.setText(recipeOrder.getRecipe().getNewprice()+"");
			orderHolder.countTextView.setText(recipeOrder.getCount()+"");
			orderHolder.sumTextView.setText(recipeOrder.getPrice()+"");
			return convertView;
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
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}
	
}

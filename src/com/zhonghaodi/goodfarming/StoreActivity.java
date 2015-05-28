package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.Recipe;
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

public class StoreActivity extends Activity implements HandMessage {

	private TextView titleView;
	private String id;
	private String name;
	private ListView pullToRefreshListView;
	private List<Recipe> recipes;
	private RecipeAdapter adapter;
	private GFHandler<StoreActivity> handler = new GFHandler<StoreActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		titleView = (TextView)findViewById(R.id.title_text);
		id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		if(name!=null && name!=""){
			titleView.setText(name);
		}
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it=new Intent();
				it.setClass(StoreActivity.this, RecipeActivity.class);
				it.putExtra("recipeId", recipes.get(position).getId());
				it.putExtra("nzdCode", recipes.get(position).getNzd().getId());
				StoreActivity.this.startActivity(it);
				
			}
		});
		
		recipes = new ArrayList<Recipe>();
		adapter = new RecipeAdapter();
		pullToRefreshListView.setAdapter(adapter);	
		loadData();
	}
	
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getRecipesByUid(id);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	
	
	class RecipeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return recipes.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return recipes.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			HolderRecipe holderRecipe;;
			if(convertView==null){
				convertView = LayoutInflater.from(StoreActivity.this)
						.inflate(R.layout.cell_recipe, parent, false);
				holderRecipe = new HolderRecipe(convertView);
				convertView.setTag(holderRecipe);
			}
			
			holderRecipe=(HolderRecipe)convertView.getTag();
			Recipe recipe = recipes.get(position);
			if (recipe.getThumbnail()!=null) {
				ImageLoader.getInstance().displayImage("http://121.40.62.120/appimage/recipes/small/"+recipe.getThumbnail(), holderRecipe.recipeIv, ImageOptions.optionsNoPlaceholder);
			}
			holderRecipe.titleTv.setText(recipe.getTitle());
			holderRecipe.oldPriceTv.setText(String.valueOf(recipe.getPrice()));
			holderRecipe.newPriceTv.setText(String.valueOf(recipe.getNewprice()));
			return convertView;
		}
		
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final StoreActivity storeactivity =(StoreActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<Recipe> recipes = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<Recipe>>() {
					}.getType());
			if (msg.what == 0) {
				storeactivity.recipes.clear();
			}
			for (Recipe recipe : recipes) {
				storeactivity.recipes.add(recipe);
			}
			storeactivity.adapter.notifyDataSetChanged();
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}

}

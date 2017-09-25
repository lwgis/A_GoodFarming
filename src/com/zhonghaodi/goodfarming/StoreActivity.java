package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.Follow;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Quan;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

public class StoreActivity extends Activity implements HandMessage,OnClickListener {

	private TextView titleView;
	private String id;
	private String name;
	private User store;
	private boolean bfollow;
	private ListView pullToRefreshListView;
	private List<Recipe> recipes;
	private RecipeAdapter adapter;
	private View clickview;
	private GFHandler<StoreActivity> handler = new GFHandler<StoreActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);
		titleView = (TextView)findViewById(R.id.title_text);
		bfollow = getIntent().getBooleanExtra("bfollow", false);
		store = (User)getIntent().getSerializableExtra("store");
		id = store.getId();
		name = store.getAlias();
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
				it.putExtra("recipeId", recipes.get(position-1).getId());
				it.putExtra("nzdCode", recipes.get(position-1).getNzd().getId());
				StoreActivity.this.startActivity(it);
				
			}
		});
		
		recipes = new ArrayList<Recipe>();
		adapter = new RecipeAdapter();
		pullToRefreshListView.setAdapter(adapter);	
		loadData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
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
				
				String myid = GFUserDictionary.getUserId(getApplicationContext());
				String jsString = HttpUtil.getFollowids(myid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 3;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
		
	}
	
	/**
	 * 关注
	 */
	public void follow(){
		final String mid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				NetResponse netResponse = HttpUtil.follow(mid,store);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 1;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = -1;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();

			}
		}).start();
	}
	
	/**
	 * 取消关注
	 */
	public void cancelfollow(){
		final String mid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.cancelfollow(mid,store);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
	}
	
	class NzdInfoHolder{
		public RoundedImageView headIv;
		public TextView titleTv;
		public TextView desTv;
		public Button chatButton;
		public Button followButton;
		public NzdInfoHolder(View view){
			headIv=(RoundedImageView)view.findViewById(R.id.head_image);
			titleTv=(TextView)view.findViewById(R.id.nzdname);
			desTv=(TextView)view.findViewById(R.id.nzddes);
			chatButton = (Button)view.findViewById(R.id.chat_btn);
			followButton = (Button)view.findViewById(R.id.follow_btn);
		}
	}
	
	class RecipeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(store==null){
				return 0;
			}
			return recipes.size()+1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return recipes.get(position-1);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 1;
			}
			else{
				return 0;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			NzdInfoHolder nzdInfoHolder;
			HolderRecipe holderRecipe;
			int cellType = getItemViewType(position);
			Recipe recipe = null;
			if(position>0){
				recipe = recipes.get(position-1);
			}
			if(convertView==null){
				switch (cellType) {
				case 0:
					convertView = LayoutInflater.from(StoreActivity.this)
							.inflate(R.layout.cell_recipe, parent, false);
					holderRecipe = new HolderRecipe(convertView);
					convertView.setTag(holderRecipe);
					break;
				case 1:
					convertView = LayoutInflater.from(StoreActivity.this)
						.inflate(R.layout.cell_nzd_info, parent,false);
					nzdInfoHolder = new NzdInfoHolder(convertView);
					convertView.setTag(nzdInfoHolder);
					break;

				default:
					break;
				}
				
			}
			
			switch (cellType) {
			case 0:
				holderRecipe=(HolderRecipe)convertView.getTag();
				if (recipe.getThumbnail()!=null) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"recipes/small/"+recipe.getThumbnail(), holderRecipe.recipeIv, ImageOptions.optionsNoPlaceholder);
				}
				holderRecipe.titleTv.setText(recipe.getTitle());
				holderRecipe.oldPriceTv.setText(String.valueOf(recipe.getPrice()));
				holderRecipe.newPriceTv.setText(String.valueOf(recipe.getNewprice()));
				break;
			case 1:
				nzdInfoHolder = (NzdInfoHolder)convertView.getTag();
				if(store.getThumbnail()!=null){
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"users/small/"+store.getThumbnail(), nzdInfoHolder.headIv, ImageOptions.optionsNoPlaceholder);
				}
				nzdInfoHolder.titleTv.setText(store.getAlias());
				nzdInfoHolder.desTv.setText(store.getDescription());
				nzdInfoHolder.chatButton.setOnClickListener(StoreActivity.this);
				if(bfollow){
					nzdInfoHolder.followButton.setText("取消关注");
				}
				else{
					nzdInfoHolder.followButton.setText("关注");
				}
				nzdInfoHolder.followButton.setOnClickListener(StoreActivity.this);
				break;

			default:
				break;
			}
			
			
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chat_btn:
			Intent it = new Intent();
			it.setClass(this, ChatActivity.class);
			it.putExtra("userName", store.getPhone());
			it.putExtra("title", store.getAlias());
			it.putExtra("thumbnail", store.getThumbnail());
			startActivity(it);
			break;
		case R.id.follow_btn:
			Button btn = (Button)v;
			clickview = v;
			if(btn.getText().equals("关注")){
				follow();
				v.setEnabled(false);
			}
			else{
				
				final Dialog dialog = new Dialog(this, R.style.MyDialog);
		        //设置它的ContentView
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        View layout = inflater.inflate(R.layout.dialog, null);
		        dialog.setContentView(layout);
		        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
		        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
		        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
		        okBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						cancelfollow();
						v.setEnabled(false);
					}
				});
		        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
		        cancelButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		        titleView.setText("提示");
		        contentView.setText("确定要取消关注吗？");
		        dialog.show();
				
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final StoreActivity storeactivity =(StoreActivity)object;
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 0:
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
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			if(clickview!=null){
				clickview.setEnabled(true);
			}
			if(msg.obj != null){
				Gson gson = new Gson();
				Follow follow = gson.fromJson(msg.obj.toString(),
						new TypeToken<Follow>() {
						}.getType());
				if(follow!=null){
					bfollow = true;
					adapter.notifyDataSetChanged();
					Toast.makeText(this, "关注成功!",
							Toast.LENGTH_SHORT).show();
				}
				
			}
			else{
				Toast.makeText(this, "关注农技达人失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if(clickview!=null){
				clickview.setEnabled(true);
			}
			if (msg.obj != null) {
				String jsString = msg.obj.toString();
				if(jsString!=""){
					Toast.makeText(this, "取消关注出错！",
							Toast.LENGTH_SHORT).show();
				}
				else{
					bfollow = false;
					adapter.notifyDataSetChanged();
					
				}
				
			} else {
				Toast.makeText(this, "取消关注出错！",
						Toast.LENGTH_SHORT).show();
			}
			break; 
			
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<String> fids = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<String>>() {
						}.getType());
				if(fids!=null&&fids.size()>0){
					if(fids.contains(id)){
						bfollow = true;
					}
					else{
						bfollow = false;
					}
				}
				else{
					bfollow = false;
				}
				adapter.notifyDataSetChanged();
				
			} else {
				Toast.makeText(this, "获取关注列表失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		
	}

	

}

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
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderEvaluate;
import com.zhonghaodi.customui.HolderRecipeInfo;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyProgressBar;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.QuanHolder;
import com.zhonghaodi.goodfarming.NysActivity.NysAdapter;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Quan;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.RecipeEvaluate;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeActivity extends Activity implements HandMessage,OnClickListener,TextWatcher {

	public ImageView recipeIv;
	public TextView repiceTitleTv;
	public TextView priceNewTv;
	public TextView priceOldTv;
	public TextView descriptionTv;
	public TextView storeNameTextView;
	public MyTextButton addBtn;
	public MyTextButton removeBtn;
	public ImageView mapImageView;
	public LinearLayout evaluateLayout;
	public TextView evaluaTextView;
	public MyProgressBar haoProgressBar;
	public MyProgressBar zhongProgressBar;
	public MyProgressBar chaProgressBar;
	public TextView evaluateBtn;
	private MyEditText recipeCountEt;
	private String nzdCode;
	private int recipeId;
	private Recipe recipe;
	private int recipeCount=1;
	private MyTextButton orderButton;
	private GFHandler<RecipeActivity> handler = new GFHandler<RecipeActivity>(
			this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_recipe);
		MobclickAgent.openActivityDurationTrack(false);
		recipeIv = (ImageView)findViewById(R.id.recipe_image);
		repiceTitleTv = (TextView)findViewById(R.id.recipetitle_text);
		priceOldTv = (TextView)findViewById(R.id.oldprice_text);
		priceOldTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		priceNewTv = (TextView)findViewById(R.id.newprice_text);
		storeNameTextView = (TextView)findViewById(R.id.nzdName_text);
		descriptionTv = (TextView)findViewById(R.id.description_text);
		addBtn=(MyTextButton)findViewById(R.id.addrecipe_button);
		removeBtn=(MyTextButton)findViewById(R.id.removerecipe_button);
		recipeCountEt=(MyEditText)findViewById(R.id.recipecount_edit);
		recipeCountEt.setSelection(recipeCountEt.getText().length());
		mapImageView = (ImageView)findViewById(R.id.map_image);
		evaluateLayout = (LinearLayout)findViewById(R.id.evaluateLayout);
		evaluaTextView = (TextView)findViewById(R.id.sumevaluate);
		haoProgressBar = (MyProgressBar)findViewById(R.id.haopro);
		zhongProgressBar = (MyProgressBar)findViewById(R.id.zhongpro);
		chaProgressBar = (MyProgressBar)findViewById(R.id.chapro);
		nzdCode = getIntent().getStringExtra("nzdCode");
		recipeId = getIntent().getIntExtra("recipeId", 0);
		orderButton = (MyTextButton)findViewById(R.id.order_button);
		orderButton.setOnClickListener(this);
		evaluateBtn = (TextView)findViewById(R.id.evaluateBtn);
		evaluateBtn.setOnClickListener(this);
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		loadData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("农资店配方内容");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("农资店配方内容");
		MobclickAgent.onPause(this);
	}

	private void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getRecipe(nzdCode, recipeId);
				Message msg = handler.obtainMessage();
				msg.what =0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}	
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (recipeCountEt.getText().toString().equals("0")||recipeCountEt.getText().toString().equals("")) {
			recipeCount=1;
			recipeCountEt.setText("1");
		}
		else {
			recipeCount=Integer.parseInt(recipeCountEt.getText().toString()) ;
		}
		recipeCountEt.setSelection(recipeCountEt.getText().length());
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.map_image:
			Intent intent = new Intent(this, NearNzdMapActivity.class);
			intent.putExtra("x",recipe.getNzd().getX() );
			intent.putExtra("y", recipe.getNzd().getY());
			startActivity(intent);
			break;
		case R.id.addrecipe_button:
			recipeCount++;
			recipeCountEt.setText(String.valueOf(recipeCount));
			break;
		case R.id.removerecipe_button:
			if (recipeCount==1) {
				return;
			}
			recipeCount--;
			recipeCountEt.setText(String.valueOf(recipeCount));
			break;
		case R.id.order_button:
			if (GFUserDictionary.getUserId(getApplicationContext())==null) {
				Intent it=new Intent();
				it.setClass(RecipeActivity.this, LoginActivity.class);
				startActivity(it);
				return;
			}
			Intent it=new Intent();
			it.setClass(RecipeActivity.this, RecipeOrderActivity.class);
			it.putExtra("nzdId", recipe.getNzd().getId());
			it.putExtra("recipeId", recipe.getId());
			it.putExtra("userId", GFUserDictionary.getUserId(getApplicationContext()));
			it.putExtra("count", recipeCount);
			it.putExtra("recipeName", recipe.getTitle());
			RecipeActivity.this.startActivity(it);
			break;
		case R.id.evaluateBtn:
			Intent it1=new Intent();
			it1.setClass(RecipeActivity.this, EvaluatesActivity.class);
			it1.putExtra("nzdCode", recipe.getNzd().getId());
			it1.putExtra("recipeId", recipe.getId());
			it1.putExtra("recipeName", recipe.getTitle());
			it1.putExtra("sum", recipe.getSumcount());
			it1.putExtra("hao", recipe.getHaocount());
			it1.putExtra("zhong", recipe.getZhongcount());
			it1.putExtra("cha", recipe.getChacount());
			RecipeActivity.this.startActivity(it1);
			break;

		default:
			break;
		}
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				recipe = (Recipe) GsonUtil.fromJson(
						msg.obj.toString(), Recipe.class);
				if (recipe.getThumbnail() != null) {
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"recipes/small/"
								+ recipe.getThumbnail(),
								recipeIv,
						ImageOptions.optionsNoPlaceholder);
				}
				repiceTitleTv.setText(recipe
						.getTitle());
				priceOldTv.setText(String
						.valueOf(recipe.getPrice()));
				priceNewTv.setText(String
						.valueOf(recipe.getNewprice()));
				descriptionTv.setText(recipe
						.getDescription());
				storeNameTextView.setText(recipe.getNzd().getAlias());
				recipeCountEt.addTextChangedListener(RecipeActivity.this);
				addBtn.setOnClickListener(RecipeActivity.this);
				removeBtn.setOnClickListener(RecipeActivity.this);
				mapImageView.setOnClickListener(RecipeActivity.this);
				evaluaTextView.setText("商品评价："+recipe.getSumcount());
				
				if(recipe.getSumcount()>0){
					haoProgressBar.setProgress(recipe.getHaocount()*100/recipe.getSumcount());
					zhongProgressBar.setProgress(recipe.getZhongcount()*100/recipe.getSumcount());
					chaProgressBar.setProgress(recipe.getChacount()*100/recipe.getSumcount());
					evaluateLayout.setVisibility(View.VISIBLE);
				}
				else{
					haoProgressBar.setProgress(0);
					zhongProgressBar.setProgress(0);
					chaProgressBar.setProgress(0);
					evaluateLayout.setVisibility(View.GONE);
				}

			}
			else{
				GFToast.show(getApplicationContext(),"请求失败，请检查网络状态稍后再试。");
			}
			break;

		default:
			break;
		}
		
	}

}

package com.zhonghaodi.goodfarming;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CompleteOrderActivity extends Activity implements HandMessage {

	private ImageView recipeIv;
	private TextView repiceTitleTv;
	private TextView priceNewTv;
	private TextView priceOldTv;
	private TextView descriptionTv;
	private TextView countTv;
	private TextView sumTv;
	private RecipeOrder recipeOrder;
	
	private GFHandler<CompleteOrderActivity> handler = new GFHandler<CompleteOrderActivity>(
			this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_completeorder);
		recipeIv = (ImageView) findViewById(R.id.recipe_image);
		repiceTitleTv = (TextView) findViewById(R.id.recipetitle_text);
		priceOldTv = (TextView) findViewById(R.id.oldprice_text);
		priceOldTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		priceNewTv = (TextView) findViewById(R.id.newprice_text);
		descriptionTv = (TextView) findViewById(R.id.description_text);
		countTv = (TextView)findViewById(R.id.count_text);
		sumTv = (TextView)findViewById(R.id.sum_text);
		
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		recipeOrder = (RecipeOrder)getIntent().getSerializableExtra("order");
		if(recipeOrder!=null){
			setData(recipeOrder);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("已完成订单内容");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("已完成订单内容");
		MobclickAgent.onPause(this);
	}
	
	public void setData(RecipeOrder recipeOrder){
		
		if(recipeOrder==null){
			return;
		}
		
		if (recipeOrder.getRecipe().getThumbnail() != null) {
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"recipes/small/"
							+ recipeOrder.getRecipe().getThumbnail(),
					recipeIv,
					ImageOptions.optionsNoPlaceholder);
		}
		repiceTitleTv.setText(recipeOrder.getRecipe()
				.getTitle());
		priceOldTv.setText(String
				.valueOf(recipeOrder.getRecipe().getPrice()));
		priceNewTv.setText(String
				.valueOf(recipeOrder.getRecipe().getNewprice()));
		descriptionTv.setText(recipeOrder.getRecipe()
				.getDescription());
		countTv.setText(String.valueOf(recipeOrder.getCount()));
		sumTv.setText(String.valueOf(recipeOrder.getPrice()));
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		
	}
	
}

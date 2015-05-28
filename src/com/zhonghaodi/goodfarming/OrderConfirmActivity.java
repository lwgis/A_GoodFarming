package com.zhonghaodi.goodfarming;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderConfirmActivity extends Activity implements HandMessage {

	private ImageView recipeIv;
	private TextView repiceTitleTv;
	private TextView priceNewTv;
	private TextView priceOldTv;
	private TextView descriptionTv;
	private TextView countTv;
	private TextView sumTv;
	private MyTextButton confirmBtn;
	private RecipeOrder recipeOrder;
	
	private GFHandler<OrderConfirmActivity> handler = new GFHandler<OrderConfirmActivity>(
			this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderconfirm);
		recipeIv = (ImageView) findViewById(R.id.recipe_image);
		repiceTitleTv = (TextView) findViewById(R.id.recipetitle_text);
		priceOldTv = (TextView) findViewById(R.id.oldprice_text);
		priceOldTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		priceNewTv = (TextView) findViewById(R.id.newprice_text);
		descriptionTv = (TextView) findViewById(R.id.description_text);
		countTv = (TextView)findViewById(R.id.count_text);
		sumTv = (TextView)findViewById(R.id.sum_text);
		confirmBtn=(MyTextButton)findViewById(R.id.confirm_button);	
		
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OrderConfirmActivity.this.setResult(2);
				finish();
			}
		});
		confirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				confirmOrder(recipeOrder.getRecipe().getNzd().getId(),recipeOrder.getRecipe().getId(),recipeOrder.getId());
				
			}
		});
		
		recipeOrder = (RecipeOrder)getIntent().getSerializableExtra("order");
		if(recipeOrder!=null){
			setData(recipeOrder);
		}
	}
	
	public void setData(RecipeOrder recipeOrder){
		
		if(recipeOrder==null){
			return;
		}
		
		if (recipeOrder.getRecipe().getThumbnail() != null) {
			ImageLoader.getInstance().displayImage(
					"http://121.40.62.120/appimage/recipes/small/"
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
	
	public void confirmOrder(final String nzdId,final int recipeId,final int orderid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.deleteOrder(nzdId, recipeId, orderid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		
		if (msg.obj != null) {
			String jsString = msg.obj.toString();
			if(jsString!=""){
				Toast.makeText(this, "订单确认出错",
						Toast.LENGTH_SHORT).show();
			}
			
		} else {
			Toast.makeText(this, "订单确认出错",
					Toast.LENGTH_SHORT).show();
		}
		
		this.setResult(2);
		this.finish();
		
	}

	
}

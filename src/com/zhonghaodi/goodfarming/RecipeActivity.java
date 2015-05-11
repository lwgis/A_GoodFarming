package com.zhonghaodi.goodfarming;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeActivity extends Activity implements HandMessage {
	private ImageView recipeIv;
	private TextView repiceTitleTv;
	private TextView priceNewTv;
	private TextView priceOldTv;
	private TextView descriptionTv;
	private MyEditText recipeCountEt;
	private MyTextButton addBtn;
	private MyTextButton removeBtn;
	private MyTextButton orderBtn;
	private int userId;
	private int recipeId;
	private Recipe recipe;
	private int recipeCount=1;
	private GFHandler<RecipeActivity> handler = new GFHandler<RecipeActivity>(
			this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_recipe);
		userId = getIntent().getIntExtra("userId", 0);
		recipeId = getIntent().getIntExtra("recipeId", 0);
		recipeIv = (ImageView) findViewById(R.id.recipe_image);
		repiceTitleTv = (TextView) findViewById(R.id.recipetitle_text);
		priceOldTv = (TextView) findViewById(R.id.oldprice_text);
		priceOldTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		priceNewTv = (TextView) findViewById(R.id.newprice_text);
		descriptionTv = (TextView) findViewById(R.id.description_text);
		orderBtn=(MyTextButton)findViewById(R.id.order_button);
		addBtn=(MyTextButton)findViewById(R.id.addrecipe_button);
		removeBtn=(MyTextButton)findViewById(R.id.removerecipe_button);
		recipeCountEt=(MyEditText)findViewById(R.id.recipecount_edit);
		recipeCountEt.setSelection(recipeCountEt.getText().length());
		Button cancelBtn=(Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		orderBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent();
				it.setClass(RecipeActivity.this, RecipeOrderActivity.class);
				RecipeActivity.this.startActivity(it);
			}
		});
		addBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				recipeCount++;
				recipeCountEt.setText(String.valueOf(recipeCount));
			}
		});
		removeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (recipeCount==1) {
					return;
				}
				recipeCount--;
				recipeCountEt.setText(String.valueOf(recipeCount));
			}
		});
		recipeCountEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (recipeCountEt.getText().toString().equals("0")||recipeCountEt.getText().toString().equals("")) {
					recipeCount=1;
					recipeCountEt.setText("1");
				}
				else {
					recipeCount=Integer.parseInt(recipeCountEt.getText().toString()) ;
				}
				recipeCountEt.setSelection(recipeCountEt.getText().length());
			}
		});
		loadData();
	}

	private void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getRecipe(userId, recipeId);
				Message msg = handler.obtainMessage();
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		RecipeActivity recipeActivity = (RecipeActivity) object;
		if (msg.obj != null) {
			recipeActivity.recipe = (Recipe) GsonUtil.fromJson(
					msg.obj.toString(), Recipe.class);
			if (recipeActivity.recipe != null) {
				if (recipeActivity.recipe.getThumbnail() != null) {
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/recipes/small/"
									+ recipeActivity.recipe.getThumbnail(),
							recipeActivity.recipeIv,
							ImageOptions.optionsNoPlaceholder);
				}
				recipeActivity.repiceTitleTv.setText(recipeActivity.recipe
						.getTitle());
				recipeActivity.priceOldTv.setText(String
						.valueOf(recipeActivity.recipe.getPrice()));
				recipeActivity.priceNewTv.setText(String
						.valueOf(recipeActivity.recipe.getNewprice()));
				recipeActivity.descriptionTv.setText(recipeActivity.recipe
						.getDescription());
			}
		}
	}

}

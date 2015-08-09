package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HolderRecipeInfo {
	public ImageView recipeIv;
	public TextView repiceTitleTv;
	public TextView priceNewTv;
	public TextView priceOldTv;
	public TextView descriptionTv;
	public TextView storeNameTextView;
	public MyEditText recipeCountEt;
	public MyTextButton addBtn;
	public MyTextButton removeBtn;
	public ImageView mapImageView;
	public LinearLayout evaluateLayout;
	public TextView evaluaTextView;
	public MyProgressBar haoProgressBar;
	public MyProgressBar zhongProgressBar;
	public MyProgressBar chaProgressBar;
	
	public HolderRecipeInfo(View view){
		recipeIv = (ImageView) view.findViewById(R.id.recipe_image);
		repiceTitleTv = (TextView) view.findViewById(R.id.recipetitle_text);
		priceOldTv = (TextView) view.findViewById(R.id.oldprice_text);
		priceOldTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		priceNewTv = (TextView) view.findViewById(R.id.newprice_text);
		storeNameTextView = (TextView)view.findViewById(R.id.nzdName_text);
		descriptionTv = (TextView) view.findViewById(R.id.description_text);
		addBtn=(MyTextButton)view.findViewById(R.id.addrecipe_button);
		removeBtn=(MyTextButton)view.findViewById(R.id.removerecipe_button);
		recipeCountEt=(MyEditText)view.findViewById(R.id.recipecount_edit);
		recipeCountEt.setSelection(recipeCountEt.getText().length());
		mapImageView = (ImageView)view.findViewById(R.id.map_image);
		evaluateLayout = (LinearLayout)view.findViewById(R.id.evaluateLayout);
		evaluaTextView = (TextView)view.findViewById(R.id.sumevaluate);
		haoProgressBar = (MyProgressBar)view.findViewById(R.id.haopro);
		zhongProgressBar = (MyProgressBar)view.findViewById(R.id.zhongpro);
		chaProgressBar = (MyProgressBar)view.findViewById(R.id.chapro);
	}
}

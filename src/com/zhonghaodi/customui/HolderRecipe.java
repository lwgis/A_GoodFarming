package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HolderRecipe {
	public ImageView recipeIv;
	public TextView titleTv;
	public TextView oldPriceTv;
	public TextView newPriceTv;
	 public HolderRecipe(View view){
		 recipeIv=(ImageView)view.findViewById(R.id.recipe_image);
		 titleTv=(TextView)view.findViewById(R.id.title_text);
		 oldPriceTv=(TextView)view.findViewById(R.id.oldprice_text);
		 newPriceTv=(TextView)view.findViewById(R.id.newprice_text);
		 oldPriceTv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); 
	 }
}

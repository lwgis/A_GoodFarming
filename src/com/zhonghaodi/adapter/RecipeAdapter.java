package com.zhonghaodi.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RecipeAdapter extends BaseAdapter{
	
	List<Recipe> recipes;
	Context mContext;
	
	public RecipeAdapter(List<Recipe> rs,Context c) {
		// TODO Auto-generated constructor stub
		recipes = rs;
		mContext = c;
	}

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
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub 
		HolderRecipe holderRecipe;
		Recipe recipe = null;
		recipe = recipes.get(position);
		convertView = LayoutInflater.from(mContext)
				.inflate(R.layout.cell_recipe, parent, false);
		holderRecipe = new HolderRecipe(convertView);
		convertView.setTag(holderRecipe);
		holderRecipe=(HolderRecipe)convertView.getTag();
		if (recipe.getThumbnail()!=null) {
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"recipes/small/"+recipe.getThumbnail(), holderRecipe.recipeIv, ImageOptions.optionsNoPlaceholder);
		}
		holderRecipe.titleTv.setText(recipe.getTitle());
		holderRecipe.oldPriceTv.setText(String.valueOf(recipe.getPrice()));
		holderRecipe.newPriceTv.setText(String.valueOf(recipe.getNewprice()));
		
		return convertView;
	}
	
}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import org.bitlet.weupnp.Main;

import com.zhonghaodi.adapter.BannerAdapter;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.utils.PublicHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

public class VersionActivity extends Activity {

	private LinearLayout dotLayout;
	private ViewPager adViewPager;
	private List<GFImageView> imageViews;
	private List<View> dots;
	private BannerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		dotLayout = (LinearLayout)findViewById(R.id.dot_layout);
		adViewPager = (ViewPager)findViewById(R.id.vp);
		imageViews = new ArrayList<GFImageView>();
		dots = new ArrayList<View>();
		for (int i=0;i<3;i++) {
			
			GFImageView imageView = new GFImageView(this);
			if(i==0){
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.ver1));
			}else if(i==1){
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.ver2));
			}
			else{
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.ver3));
			}			
			imageView.setIndex(i);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageViews.add(imageView);
			
			View dot = new View(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PublicHelper.dip2px(this, 5),PublicHelper.dip2px(this, 5));
			layoutParams.setMargins(PublicHelper.dip2px(this, 2), 0, PublicHelper.dip2px(this, 2), 0);
			dot.setLayoutParams(layoutParams);
			dot.setBackgroundResource(R.drawable.dot_normal);
			dot.setVisibility(View.VISIBLE);
			dotLayout.addView(dot);
			dots.add(dot);
		}
		adapter = new BannerAdapter(imageViews);
		adViewPager.setAdapter(adapter);
		adViewPager.setOnPageChangeListener(new MyPageChangeListener());
	}
	
	private class MyPageChangeListener implements OnPageChangeListener {

		private int oldPosition = 0;

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
			if(position==2){
				Intent mainIntent = new Intent(VersionActivity.this,  
						MainActivity.class);  
				VersionActivity.this.startActivity(mainIntent);  

				VersionActivity.this.finish(); 
			}
		}
	}

}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import org.bitlet.weupnp.Main;

import com.easemob.chat.core.r;
import com.zhonghaodi.adapter.BannerAdapter;
import com.zhonghaodi.adapter.VersionAdapter;
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
	private List<Integer> imgResources;
	private List<View> dots;
	private VersionAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		dotLayout = (LinearLayout)findViewById(R.id.dot_layout);
		dotLayout.setVisibility(View.INVISIBLE);
		adViewPager = (ViewPager)findViewById(R.id.vp);
		imgResources = new ArrayList<Integer>();
		imgResources.add(R.drawable.guide1);
		imgResources.add(R.drawable.guide2);
		dots = new ArrayList<View>();
		View dot = new View(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PublicHelper.dip2px(this, 5),PublicHelper.dip2px(this, 5));
		layoutParams.setMargins(PublicHelper.dip2px(this, 2), 0, PublicHelper.dip2px(this, 2), 0);
		dot.setLayoutParams(layoutParams);
		dot.setBackgroundResource(R.drawable.dot_normal);
		dot.setVisibility(View.VISIBLE);
		dotLayout.addView(dot);
		dots.add(dot);
		View dot1 = new View(this);
		dot1.setLayoutParams(layoutParams);
		dot1.setBackgroundResource(R.drawable.dot_normal);
		dot1.setVisibility(View.VISIBLE);
		dotLayout.addView(dot1);
		dots.add(dot1);
		View dot2 = new View(this);
		dot2.setLayoutParams(layoutParams);
		dot2.setBackgroundResource(R.drawable.dot_normal);
		dot2.setVisibility(View.VISIBLE);
		dotLayout.addView(dot2);
		dots.add(dot2);
		adapter = new VersionAdapter(imgResources,this);
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
		}
	}
	
	public void toMain(View view){
		Intent mainIntent = new Intent(VersionActivity.this,  
				MainActivity.class);  
		mainIntent.putExtra("ver", 1);
		VersionActivity.this.startActivity(mainIntent);  

		VersionActivity.this.finish(); 
	}

}

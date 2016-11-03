package com.zhonghaodi.adapter;

import java.util.List;


import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AdvertisingAdapter extends PagerAdapter {

private List<ImageView> imageviews;
	
	public AdvertisingAdapter(List<ImageView> views) {
		// TODO Auto-generated constructor stub
		imageviews = views;
	}

	@Override
	public int getCount() {
		return imageviews.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView iv = imageviews.get(position);
		LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		iv.setLayoutParams(layoutParams2);
		((ViewPager) container).addView(iv);
		return iv;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}
}

package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.goodfarming.R;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class VersionAdapter extends PagerAdapter {

	private List<Integer> imgresourses;
	private Context mContext;
	
	public VersionAdapter(List<Integer> resources,Context context) {
		// TODO Auto-generated constructor stub
		imgresourses = resources;
		mContext=context;
	}

	@Override
	public int getCount() {
		return imgresourses.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view ;
		if(position!=imgresourses.size()-1){
			view = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_versionpage, container,false);
		}
		else{
			view = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_versionpagelast, container,false);
		}
		ImageView imageView = (ImageView)view.findViewById(R.id.versionimg);
		imageView.setImageResource(imgresourses.get(position));
		((ViewPager) container).addView(view);
		return view;
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

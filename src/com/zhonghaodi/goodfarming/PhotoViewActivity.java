package com.zhonghaodi.goodfarming;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class PhotoViewActivity extends Activity {
	private ViewPager viewPage;
	private TextView countTv;
	private List<NetImage> images;
	private int index;
	private ArrayList<PhotoView> photoViews;
	private String folder;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_view);
		viewPage=(ViewPager)findViewById(R.id.view_pager);
		countTv=(TextView)findViewById(R.id.count_text);
		images=(List<NetImage>)getIntent().getSerializableExtra("images");
		folder=getIntent().getStringExtra("folder");
		photoViews=new ArrayList<PhotoView>();
		
		for (NetImage netImage  : images) {
			if (netImage.getUrl()!=null) {
				PhotoView photoView = new PhotoView(this);
				photoView.setTag(netImage.getUrl());
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+folder+"/small/"+photoView.getTag().toString(),photoView,ImageOptions.optionsNoPlaceholder);
				photoViews.add(photoView);
			}
		}
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (PhotoView pView  : photoViews) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+folder+"/big/"+pView.getTag().toString(),pView,ImageOptions.optionsNoPlaceholder);
				}
			}
		}, 300);

		viewPage.setAdapter(new GFPagerAdapter(this));
		index=getIntent().getIntExtra("index", 0);
		viewPage.setCurrentItem(index);
		countTv.setText((index+1)+"/"+images.size());
		viewPage.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				countTv.setText((arg0+1)+"/"+images.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	 public List<NetImage> getImages() {
		return images;
	}
	public void setImages(List<NetImage> images) {
		this.images = images;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			finish();
			overridePendingTransition(0, 
            		R.anim.zoomout);
			return false;
		}
		return super.onKeyDown(keyCode, event);

	}
	
	static class GFPagerAdapter extends PagerAdapter {
			WeakReference<PhotoViewActivity> reference;
			public GFPagerAdapter(PhotoViewActivity activity){
				reference=new WeakReference<PhotoViewActivity>(activity);
			}
			@Override
			public int getCount() {
				return reference.get().images.size();
			}

			@Override
			public View instantiateItem(ViewGroup container, int position) {
				
				// Now just add PhotoView to ViewPager and return it
				container.addView(reference.get().photoViews.get(position), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				
				reference.get().photoViews.get(position).setOnViewTapListener(new OnViewTapListener() {
					
					@Override
					public void onViewTap(View view, float x, float y) {
						
						reference.get().finish();
						reference.get().overridePendingTransition(0, 
		                		R.anim.zoomout);
					}
				});
				return reference.get().photoViews.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView((View) object);
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

		}
}

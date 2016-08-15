package com.zhonghaodi.customui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;





import com.zhonghaodi.goodfarming.PhotoViewActivity;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.NetImage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class GFImageView extends ImageView {
	private int index;
	private Context mContext;
	private ArrayList<Bitmap> bitmaps;
	public GFImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public GFImageView(Context context){
		super(context);
		mContext = context;
	}
	public GFImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public List<NetImage> getImages() {
		return images;
	}
	public void setImages(List<NetImage> images,final String folder) {
		this.images = images;
		if (images!=null&&images.size()>0) {
			this.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent it=new Intent(mContext, PhotoViewActivity.class);
					it.putExtra("images", (Serializable)GFImageView.this.images);
					it.putExtra("index", index);
					it.putExtra("folder", folder);
					mContext.startActivity(it);
					((Activity)mContext).overridePendingTransition(R.anim.zoomin, 
	                		0); 
				}
			});
		}
	}
	public ArrayList<Bitmap> getBitmaps() {
		return bitmaps;
	}
	public void setBitmaps(ArrayList<Bitmap> bitmaps) {
		this.bitmaps = bitmaps;
	}
	private List<NetImage> images;
	private List<NetImage> images2;
	public List<NetImage> getImages2() {
		return images2;
	}
	public void setImages2(List<NetImage> images,List<NetImage> images2,final String folder) {
		this.images = images;
		this.images2 = images2;
		if (images!=null&&images.size()>0) {
			this.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					List<NetImage> imgs = new ArrayList<NetImage>();
					for (int i = 0; i < GFImageView.this.images.size(); i++) {
						NetImage image1 = GFImageView.this.images.get(i);
						imgs.add(image1);
						NetImage image2 = GFImageView.this.images2.get(i);
						imgs.add(image2);
					}
					Intent it=new Intent(mContext, PhotoViewActivity.class);
					it.putExtra("images", (Serializable)imgs);
					it.putExtra("index", index);
					it.putExtra("folder", folder);
					mContext.startActivity(it);
					((Activity)mContext).overridePendingTransition(R.anim.zoomin, 
	                		0); 
				}
			});
		}
	}
	

 
}

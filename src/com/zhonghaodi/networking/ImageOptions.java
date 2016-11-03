package com.zhonghaodi.networking;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zhonghaodi.goodfarming.R;

public class ImageOptions {
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.placeholder)
	.showImageForEmptyUri(R.drawable.placeholder)
	.showImageOnFail(R.drawable.placeholder)
	.cacheInMemory(true)
	.cacheOnDisk(true)
	.considerExifParams(true)
	.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
	.bitmapConfig(Bitmap.Config.RGB_565)
//	.displayer(new RoundedBitmapDisplayer(20))
	.build();
	public static DisplayImageOptions optionsNoPlaceholder = new DisplayImageOptions.Builder()
	.cacheInMemory(true)
	.cacheOnDisk(true)
	.considerExifParams(true)
	.imageScaleType(ImageScaleType.NONE)
	.bitmapConfig(Bitmap.Config.RGB_565)
//	.displayer(new RoundedBitmapDisplayer(20))
	.build();
	public static File getCache(Context context){
		return StorageUtils.getCacheDirectory(context);
	}
	
	public static DisplayImageOptions optionswelcom = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.welcome)
			.showImageForEmptyUri(R.drawable.welcome)
			.showImageOnFail(R.drawable.welcome)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.bitmapConfig(Bitmap.Config.RGB_565)
//			.displayer(new RoundedBitmapDisplayer(20))
			.build();
}

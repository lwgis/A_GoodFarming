package com.zhonghaodi.networking;

import java.io.File;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
//	.displayer(new RoundedBitmapDisplayer(20))
	.build();
	public static File getCache(Context context){
		return StorageUtils.getCacheDirectory(context);
	}
}

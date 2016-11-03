package com.zhonghaodi.utils;

import java.io.File;

import android.os.Environment;

public class FileUtils {
	
	public static File cacheDir;
	private String SDCardRoot;
	public FileUtils() {
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;
	}

}

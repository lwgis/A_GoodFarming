package com.zhonghaodi.model;

import java.util.ArrayList;
import java.util.List;

import com.baidu.a.a.a.b;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GFVersionHint {
	
	public static void saveCnzHintInfo(Context context,int count) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("hintconfig",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.putInt("cnzcount", count);
		// 提交
		editor.commit();
		
	}
	
	public static int getCnzcount(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("hintconfig",
				Context.MODE_PRIVATE);
		int count = sharedPre.getInt("cnzcount", 0);
		return count;
	}

	public static boolean getAll(Context context){
		SharedPreferences sharedPre = context.getSharedPreferences("hintconfig",
				Context.MODE_PRIVATE);
		int count = sharedPre.getInt("cnzcount", 0);
		boolean b = false;
		if(count==0){
			b=true;
		}
		return b;
	}
}

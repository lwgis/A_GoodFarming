package com.zhonghaodi.model;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GFAreaUtil {

	public static void saveAreaInfo(Context context,final City city) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.putInt("cityid", city.getId());
		editor.putString("cityname", city.getName());
		// 提交
		editor.commit();
		
	}
	
	public static void saveAreaInfo1(Context context,final City city) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig1",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.putInt("cityid1", city.getId());
		editor.putString("cityname1", city.getName());
		// 提交
		editor.commit();
		
	}

	public static void removeAreaInfo(Context context) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.remove("cityid");
		editor.remove("cityname");
		// 提交
		editor.commit();
		
		SharedPreferences sharedPre1 = context.getSharedPreferences("areaconfig1",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor1 = sharedPre1.edit();
		editor1.remove("cityid1");
		editor1.remove("cityname1");
		// 提交
		editor1.commit();
	}

	//原来的提问等都是通过此方法获取分区的，现在需要屏蔽
	public static int getCityId(Context context) {
//		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig",
//				Context.MODE_PRIVATE);
//		int id = sharedPre.getInt("cityid", 0);
		return 0;
	}
	//临时增加此方法只给拉拉呱和田间地头使用
	public static int getCity(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig",
				Context.MODE_PRIVATE);
		int id = sharedPre.getInt("cityid", 0);
		return id;
	}
	
	public static String getCityName(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig",
				Context.MODE_PRIVATE);
		String name = sharedPre.getString("cityname", null);
		return name;
	}
	
	public static int getCityId1(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig1",
				Context.MODE_PRIVATE);
		int id = sharedPre.getInt("cityid1", 0);
		return id;
	}
	
	public static String getCityName1(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("areaconfig1",
				Context.MODE_PRIVATE);
		String name = sharedPre.getString("cityname1", null);
		return name;
	}
	
}

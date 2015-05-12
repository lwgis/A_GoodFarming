package com.zhonghaodi.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GFUserDictionary {
	public static Context context;

	/**
	 * 保存用户信息
	 * 
	 * @param userid
	 *            用户id
	 * @param alias
	 *            用户昵称
	 */
	public static void saveLoginInfo(String userid, String alias) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		// 设置参数
		editor.putString("alias", alias);
		editor.putString("userid", userid);
		// 提交
		editor.commit();
	}

	public static void removeUserInfo() {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.remove("userid");
		editor.remove("alias");
		// 提交
		editor.commit();
	}

	public static String getUserId() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String userid = sharedPre.getString("userid", null);
		return userid;
	}
}

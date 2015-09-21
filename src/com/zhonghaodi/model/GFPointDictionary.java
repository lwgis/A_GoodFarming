package com.zhonghaodi.model;

import java.util.Iterator;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class GFPointDictionary {
	public static Context context;
	
	/**
	 * 保存积分字典
	 * 
	 * @param userid
	 *            用户id
	 * @param alias
	 *            用户昵称
	 */
	public static void savePointDics(List<PointDic> dics) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		for (Iterator iterator = dics.iterator(); iterator.hasNext();) {
			PointDic pointDic = (PointDic) iterator.next();
			editor.putInt(pointDic.getName(), pointDic.getVal());
		}

		// 提交
		editor.commit();
	}
	
	public static int getResponsePoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		int point = sharedPre.getInt("response", 0);
		return point;
	}
	
	public static int getSigninPoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		int point = sharedPre.getInt("signin", 0);
		return point;
	}
	
	public static int getZanPoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		int point = sharedPre.getInt("zan", 0);
		return point;
	}
	
	public static int getRegPoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		int point = sharedPre.getInt("reg", 0);
		return point;
	}
	
	public static int getScoringPoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		int point = sharedPre.getInt("scoring", 0);
		return point;
	}
	
	public static int getGuaguaPoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("dics",
				Context.MODE_PRIVATE);
		int point = sharedPre.getInt("guagua", 0);
		return point;
	}
}

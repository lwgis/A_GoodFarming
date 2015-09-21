package com.zhonghaodi.model;

import java.util.Iterator;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

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
	public static void saveLoginInfo(final User user,String password,final Activity activity) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		// 设置参数
		editor.putString("alias", user.getAlias());
		editor.putString("userid", user.getId());
		editor.putString("password", password);
		editor.putString("phone", user.getPhone());
		editor.putString("thumbnail", user.getThumbnail());
		editor.putInt("point", user.getPoint());
		String croids = "";
		if(user.getCrops()!=null && user.getCrops().size()>0){
			for (Iterator iterator = user.getCrops().iterator(); iterator.hasNext();) {
				UserCrop userCrop = (UserCrop) iterator.next();
				croids+=userCrop.getCrop().getId()+" ";
			}
		}
		croids.trim();
		editor.putString("croids", croids);

		// 提交
		editor.commit();
		EMChatManager.getInstance().login(user.getPhone(), password, new EMCallBack() {
			
			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						EMChatManager.getInstance().updateCurrentUserNick(user.getAlias());

						Log.d("main", "登陆聊天服务器成功！");		
					}
				});
			}
			
			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public static void removeUserInfo() {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.remove("userid");
		editor.remove("alias");
		editor.remove("password");
		editor.remove("phone");
		editor.remove("croids");
		editor.remove("point");
		// 提交
		editor.commit();
	}

	public static String getUserId() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String userid = sharedPre.getString("userid", null);
		return userid;
	}
	
	public static String getAlias() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sharedPre.getString("alias", null);
	}
	
	public static String getPassword() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sharedPre.getString("password", null);
	}
	public static String getPhone() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sharedPre.getString("phone", null);
	}
	public static String getThumbnail() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sharedPre.getString("thumbnail", null);
	}
	
	public static String getCroids() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sharedPre.getString("croids", null);
	}
	
	public static int getPoint() {
		SharedPreferences sharedPre = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sharedPre.getInt("point", 0);
	}
}

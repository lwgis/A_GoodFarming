package com.zhonghaodi.utils;

import com.zhonghaodi.goodfarming.UILApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class StoredData {
	  public static final int LMODE_NEW_INSTALL = 1; // 启动-模式,首次安装-首次启动、覆盖安装-首次启动、已安装-二次启动
	  public static final int LMODE_UPDATE = 2;
	  public static final int LMODE_AGAIN = 3;

	  private boolean isOpenMarked = false;
	  private int launchMode = LMODE_AGAIN; // 启动-模式

	  private static StoredData instance;

	  private SharedPreferences share; // 一般信息

	  public static StoredData getThis() {
	    if (instance == null)
	      instance = new StoredData();

	    return instance;
	  }

	  // -------启动状态------------------------------------------------------------

	  // 标记-打开app,用于产生-是否首次打开
	  public void markOpenApp() {
	    // 防止-重复调用
//	    if (isOpenMarked)
//	      return;
//	    isOpenMarked = true;
	    share = UILApplication.applicationContext.getSharedPreferences("gflaunchmode",Context.MODE_PRIVATE);
	    String lastVersion = share.getString("gflastVersion", "");
	    String thisVersion = getAppVersion();

	    // 首次启动
	    if (TextUtils.isEmpty(lastVersion)) {
	      launchMode = LMODE_NEW_INSTALL;
	      share.edit().putString("gflastVersion", thisVersion).commit();
	    }
	    // 更新
	    else if (!thisVersion.equals(lastVersion)) {
	      launchMode = LMODE_UPDATE;
	      share.edit().putString("gflastVersion", thisVersion).commit();
	    }
	    // 二次启动(版本未变)
	    else
	      launchMode = LMODE_AGAIN;
	  }

	  public int getLaunchMode() {
	    return launchMode;
	  }

	  // 首次打开,新安装、覆盖(版本号不同)
	  public boolean isFirstOpen() {
	    return launchMode != LMODE_AGAIN;
	  }

	  // -------------------------
	  // 软件-版本
	  public static String getAppVersion() {

	    String versionName = "";
	    try {
	      PackageManager pkgMng = UILApplication.applicationContext.getPackageManager();
	      PackageInfo pkgInfo = pkgMng
	          .getPackageInfo(UILApplication.applicationContext.getPackageName(), 0);
	      versionName = pkgInfo.versionName;
	    } catch (Exception e) {
	      // TODO: handle exception
	      e.printStackTrace();
	    }

	    return versionName;
	  }
}

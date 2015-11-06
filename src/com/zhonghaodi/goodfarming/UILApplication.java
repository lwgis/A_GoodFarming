/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.zhonghaodi.goodfarming;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends Application {
	public static int sendcount=0;
	public static Context applicationContext;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		
		applicationContext=this;
		super.onCreate();
		initImageLoader(getApplicationContext());
		GFToast.GFContext=getApplicationContext();
		GFUserDictionary.context=getApplicationContext();
		GFPointDictionary.context = getApplicationContext();
		SDKInitializer.initialize(getApplicationContext());		
		EMChat.getInstance().init(getApplicationContext());
		EMChat.getInstance().setDebugMode(true);
	}
    
	public static void initImageLoader(Context context) {
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(10 * 1024 * 1024))
				.memoryCacheSize(10 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(200 * 1024 * 1024)				
				.diskCacheFileCount(300)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}
	public static boolean isBackground(Context context) {
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(context.getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	                          return true;
	                }else{
	                          return false;
	                }
	           }
	    }
	    return false;
	}
	
	/**
     * 检测网络是否连接
     * @return
     */
    public static boolean checkNetworkState() {
            boolean flag = false;
            //得到网络连接信息
            ConnectivityManager manager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            //去进行判断网络是否连接
            if (manager.getActiveNetworkInfo() != null) {
                    flag = manager.getActiveNetworkInfo().isAvailable();
            }

            return flag;
    }
 
}
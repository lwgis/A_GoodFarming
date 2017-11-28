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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.graphics.Bitmap;
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

import com.baidu.a.a.a.c;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.EMChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Advertising;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.utils.FileUtils;
import com.zhonghaodi.utils.StoredData;
import com.zhonghaodi.networking.GFHandler.HandMessage;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class UILApplication extends Application {
	ArrayList<Activity> list = new ArrayList<Activity>(); 
	public static User user;
	public static int sendcount=0;
	public static Context applicationContext;
	public static String shareUrl="";
	public static int sharestatus=0;
	public static String sharefolder;
	public static int sharequeid;
	public static Bitmap sharebit;
	public static String auth;
	
	public static int displayStatus;//0病害1拉拉呱2赶大集
	public static int diseaseStatus;//0病害问题1我的作物
	public static int fairStatus;//0全部
//	public static int contenStatus;//0查看全部1只看精选
	
	public static int fineStatus;
	public static int finediseaseStatus;
	public static double x=118.798632;
	public static double y=36.858719;


	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		
		applicationContext=this;
		super.onCreate();
//		MobclickAgent.setDebugMode(true);
//		MobclickAgent.openActivityDurationTrack(false); 
		ErrorReport catchExcep = new ErrorReport(this);  
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
		initImageLoader(getApplicationContext());
		SDKInitializer.initialize(getApplicationContext());		
		EMChat.getInstance().init(getApplicationContext());
//		EMChat.getInstance().setDebugMode(true); 
	}
	
	
    
	public static void initImageLoader(Context context) {
		
		try {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
					.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
					.memoryCache(new UsingFreqLimitedMemoryCache(10 * 1024 * 1024)).memoryCacheSize(10 * 1024 * 1024)
					.diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(200 * 1024 * 1024)
					.diskCacheFileCount(300).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove for release app
					.build();
			ImageLoader.getInstance().init(config);
		} catch (Exception e) {
			// TODO: handle exception
			
		}
	}
	
	/** 
     * Activity关闭时，删除Activity列表中的Activity对象*/  
    public void removeActivity(Activity a){  
        list.remove(a);  
    }  
      
    /** 
     * 向Activity列表中添加Activity对象*/  
    public void addActivity(Activity a){  
        list.add(a);  
    }  
      
    /** 
     * 关闭Activity列表中的所有Activity*/  
    public void finishActivity(){  
        for (Activity activity : list) {    
            if (null != activity) {    
                activity.finish();    
            }    
        }  
        //杀死该应用进程  
       android.os.Process.killProcess(android.os.Process.myPid());    
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
            if(applicationContext==null){
            	return true;
            }
            //得到网络连接信息
            ConnectivityManager manager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            //去进行判断网络是否连接
            if (manager!=null && manager.getActiveNetworkInfo() != null) {
                    flag = manager.getActiveNetworkInfo().isAvailable();
            }
            else{
            	flag = false;
            }

            return flag;
    }
 
    public static String getAuth(){
    	if(auth==null){
    		auth=GFUserDictionary.getAuth(applicationContext);
    	}
    	return auth;
    }

    public void downloadAds(final List<Advertising> ads){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					List<String> filenames = new ArrayList<String>();
					for (Iterator iterator = ads.iterator(); iterator.hasNext();) {
						Advertising advertising = (Advertising) iterator.next();
						filenames.add(advertising.getUrl());
						File f = new File(FileUtils.cacheDir + advertising.getUrl());						
						if (f.exists()) {
							continue;
						}
						Bitmap bitmap = ImageLoader.getInstance()
								.loadImageSync(HttpUtil.ImageUrl + "launch/big/" + advertising.getUrl());
						if (bitmap != null) {
							saveMyBitmap(bitmap, advertising.getUrl());
						}
					} 
					//删除过期的广告图片
					File[] files = FileUtils.cacheDir.listFiles();
					if(files!=null && files.length>0){
						for(File f:files){
							if(!filenames.contains(f.getName())){
								f.delete();
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}			
			}
		}).start();
    }
    
    public void saveMyBitmap(Bitmap mBitmap,String bitName)  {
        File f = new File(FileUtils.cacheDir+File.separator+bitName);
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        if(bitName.contains(".jpg")||bitName.contains(".JPG")){
        	mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        }
        else{
        	mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        }
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

}
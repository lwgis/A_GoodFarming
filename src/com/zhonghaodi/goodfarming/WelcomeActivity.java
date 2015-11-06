package com.zhonghaodi.goodfarming;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SettingPopupwindow;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements HandMessage {
	private final int SPLASH_DISPLAY_LENGHT = 2000;
//	private boolean bUpdate;
	private GFHandler<WelcomeActivity> handler = new GFHandler<WelcomeActivity>(this);
//	private int markString;
//	private ProgressBar progressBar;
//	private TextView proTextView1;
//	private TextView proTextView2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_welcome);
		if(!UILApplication.checkNetworkState()){
			GFToast.show("没有有效的网络连接");
		}
//		Init();
		 
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		 new Handler().postDelayed(new Runnable(){  
//		     public void run() {  
//		     //execute the task  
//		    	 popupwindow();
//		     }  
//		  }, 1000); 
//		if(bUpdate){
//			((UILApplication)getApplicationContext()).tryUpdate();
//		}
//		else{
//			sleep();
//		}
		sleep();
	}
	
	public void popupwindow(){
		final SettingPopupwindow settingPopupwindow = new SettingPopupwindow(this);
		OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sharedPre = WelcomeActivity.this.getSharedPreferences("test",
						Context.MODE_PRIVATE);
				String surl = settingPopupwindow.serviceEditText.getText().toString();
				String iurl = settingPopupwindow.imageEditText.getText().toString();
				if(!surl.isEmpty()){
					HttpUtil.RootURL = surl;
				}
				if(!iurl.isEmpty()){
					HttpUtil.ImageUrl = iurl;
				}
				// 获取Editor对象
				Editor editor = sharedPre.edit();
				// 设置参数
				editor.putString("serviceurl", HttpUtil.RootURL);
				editor.putString("imageurl", HttpUtil.ImageUrl);
				// 提交
				editor.commit();
				settingPopupwindow.dismiss();
//				if(bUpdate){
//					tryUpdate();
//				}
//				else{
//					sleep();
//				}
			}
		};
		settingPopupwindow.setlistener(clickListener);
		SharedPreferences sharedPre = WelcomeActivity.this.getSharedPreferences("test",
				Context.MODE_PRIVATE);
		String serviceurl = sharedPre.getString("serviceurl", "");
		if(serviceurl.isEmpty()){
			serviceurl = HttpUtil.RootURL;
		}
		String imageurl = sharedPre.getString("imageurl", "");
		if(imageurl.isEmpty())
		{
			imageurl = HttpUtil.ImageUrl;
		}
		settingPopupwindow.serviceEditText.setText(serviceurl);
		settingPopupwindow.imageEditText.setText(imageurl);
		settingPopupwindow.showAtLocation(findViewById(R.id.welcome), 
				Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
	}
	
//	private void Init(){
//		
//		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
//        String updatetime = deviceInfo.getString("updatetime", "");
//        if (updatetime.equals("") || updatetime==null) {
//			bUpdate = true;
//		}
//        else{
//        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        	try {
//				Date upDate = dateFormat.parse(updatetime);
//				Date curDate = new Date();
//				bUpdate = checkUpdate(upDate, curDate);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				bUpdate = false;
//			}
//        }
//		
//	}
	
	private void sleep(){
		new Handler().postDelayed(new Runnable() {  
            public void run() {  
                 
                tomain();
            }  
  
        }, SPLASH_DISPLAY_LENGHT); 
	}
	
	private void tomain(){
		Intent mainIntent = new Intent(WelcomeActivity.this,  
                MainActivity.class);  
        WelcomeActivity.this.startActivity(mainIntent);  

        WelcomeActivity.this.finish(); 
	}
	
//	public void tryUpdate(){
//		markString = getVersion();
//		requestVersion();
//	}
	
//	/**
//	 * 获取版本号
//	 * @return
//	 */
//	public int getVersion(){
//  		PackageManager packageManager = this.getPackageManager();
//  		int versionString;
//  		try {
//			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
//			versionString = packageInfo.versionCode;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			versionString = 0;
//		}
//  		return versionString;
//  	}
	
//	public void requestVersion(){
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					String jsonString = HttpUtil.getAppVersion();
//					Message msg = handler.obtainMessage();
//					msg.what = 0;
//					msg.obj = jsonString;
//					msg.sendToTarget();
//					
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					Message msg = handler.obtainMessage();
//					msg.what = -1;
//					msg.obj = "";
//					msg.sendToTarget();
//				}
//				
//			}
//		}).start();
//		
//	}
	
//	public boolean checkUpdate(Date upDate,Date currentDate){
//		Calendar cal1 = Calendar.getInstance(); 
//	    Calendar cal2 = Calendar.getInstance(); 
//	    cal1.setTime(currentDate); 
//	    cal2.setTime(upDate);
//	    
//	    int yi = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
//	    if (yi>0) {
//			return true;
//		}
//	    else{
//	    	int di = cal1.get(Calendar.DAY_OF_YEAR)-cal2.get(Calendar.DAY_OF_YEAR);
//	    	if (di>0) {
//				return true;
//			}
//	    	else{
//	    		return false;
//	    	}
//	    }
//	}
	
//	/**
//	 *  
//	 * 弹出对话框通知用户更新程序  
//	 *  
//	 * 弹出对话框的步骤： 
//	 *  1.创建alertDialog的builder.   
//	 *  2.要给builder设置属性, 对话框的内容,样式,按钮 
//	 *  3.通过builder 创建一个对话框 
//	 *  4.对话框show()出来   
//	 */  
//	public void showUpdataDialog(final AppVersion appVersion) {  
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Date curDate = new Date(System.currentTimeMillis());
//		String curtime = dateFormat.format(curDate);
//		SharedPreferences deviceInfo = getSharedPreferences("StartInfo", 0);
//		deviceInfo.edit().putString("updatetime", curtime).commit();
//		
//		final Dialog dialog = new Dialog(this, R.style.MyDialog);
//        //设置它的ContentView
//		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.dialog, null);
//        dialog.setContentView(layout);
//        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
//        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
//        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
//        okBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				downLoadApk(appVersion);  
//	            dialog.dismiss();
//			}
//		});
//        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
//        cancelButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				tomain(); 
//			}
//		});
//        titleView.setText("版本升级");
//        contentView.setText("检测到新版本，请及时更新");
//        dialog.show();
//	} 
	
//	/**
//	 * 从服务器中下载APK 
//	 */  
//	public void downLoadApk(final AppVersion appVersion) {  
//	    final Dialog pd;    //进度条对话框   
//	    pd = new  Dialog(this,R.style.MyDialog);  
//	    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.progressdialog, null);
//        pd.setContentView(layout);
//	    pd.setCancelable(false);
//	    pd.show();  
//	    
//	    progressBar = (ProgressBar)layout.findViewById(R.id.proBar);
//	    proTextView1 = (TextView)layout.findViewById(R.id.pro_bfb);
//	    proTextView2 = (TextView)layout.findViewById(R.id.pro_value);
//	    
//	    new Thread(){  
//	        @Override  
//	        public void run() {  
//	            try {  
//	                File file = HttpUtil.getFileFromServer(appVersion.getUrl(), progressBar,handler);  
//	                sleep(3000);  
//	                Message msg = handler.obtainMessage();
//					msg.what = 2;
//					msg.obj = file;
//					msg.sendToTarget(); 
//	                pd.dismiss(); //结束掉进度条对话框   
//	            } catch (Exception e) {  
//	            	Message msg = handler.obtainMessage();
//					msg.what = -1;
//					msg.obj = "下载错误";
//					msg.sendToTarget(); 
//	                e.printStackTrace();  
//	            }  
//	        }}.start();  
//	}  
	
//	/**
//	 * 安装apk
//	 * @param file
//	 */
//	protected void installApk(File file) {  
//	    Intent intent = new Intent();  
//	    //执行动作   
//	    intent.setAction(Intent.ACTION_VIEW);  
//	    //执行的数据类型   
//	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了    
//	    this.startActivity(intent);  
//	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		
//		case 0:
//			if(msg.obj==null){
//				tomain();
//				return;
//			}
//			Gson gson = new Gson();
//			AppVersion appVersion = gson.fromJson(msg.obj.toString(),
//					new TypeToken<AppVersion>() {
//					}.getType());
//			if(markString<appVersion.getVersion()){
//				showUpdataDialog(appVersion);
//			}
//			else{
//				tomain();
//			}
//			break;
//		case 1:
//			int[] values = (int[])msg.obj;
//			float bf = (values[0]*100*1.000f/values[1]);
//			bf = (float)(Math.round(bf*100))/100;
//			proTextView1.setText(bf+"%");
//			float pf = (values[0]*1.000f/(1024*1024));
//			pf = (float)(Math.round(pf*100))/100;
//			float cf = (values[1]*1.000f/(1024*1024));
//			cf = (float)(Math.round(cf*100))/100;
//			proTextView2.setText(pf+"M/"+cf+"M");
//			break;
//		case 2:
//			File file = (File)msg.obj;
//			installApk(file);
//			break;
		case -1:
			String errString = msg.obj.toString();
			GFToast.show(errString);
			tomain();
			break;

		default:
			break;
		}
	}

}

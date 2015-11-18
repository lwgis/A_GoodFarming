package com.zhonghaodi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.view.WindowManager;

public class PublicHelper {

	public static String TrimRight(String sString){
        String sResult = "";
         
        if (sString.startsWith(" ")){
            sResult = sString.substring(0,sString.indexOf(sString.trim().substring(0, 1))
                    +sString.trim().length());
        }
        else    sResult = sString.trim();
         
        return sResult;
    }
	
	public static int dip2px(Context context, float dipValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
	} 

	public static int px2dip(Context context, float pxValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(pxValue / scale + 0.5f); 
	} 
	
    public static int getPhoneWidth(Context context){
    	
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        return wm.getDefaultDisplay().getWidth();
    }
	
	public static Bitmap getBitmap(String path) throws IOException{

	    URL url = new URL(path);
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    conn.setConnectTimeout(5000);
	    conn.setRequestMethod("GET");
	    if(conn.getResponseCode() == 200){
	    InputStream inputStream = conn.getInputStream();
	    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	    return bitmap;
	    }
	    return null;
	    }
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}

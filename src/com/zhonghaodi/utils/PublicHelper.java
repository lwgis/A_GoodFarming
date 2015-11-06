package com.zhonghaodi.utils;

import android.content.Context;

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
	
}

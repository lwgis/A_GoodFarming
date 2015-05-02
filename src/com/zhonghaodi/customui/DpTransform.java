package com.zhonghaodi.customui;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DpTransform {
public static int px2dip(Context con,float pxValue) {
	final float scale = con.getResources().getDisplayMetrics().density; 
    return (int)(pxValue / scale + 0.5f); 
}
public static int dip2px(Context context, float dipValue){ 
    final float scale = context.getResources().getDisplayMetrics().density; 
    return (int)(dipValue * scale + 0.5f); 
} 
public static int getScreenHeight(Activity con) {
    DisplayMetrics metric = new DisplayMetrics();
    con.getWindowManager().getDefaultDisplay().getMetrics(metric);
//    int width = metric.widthPixels;     // 屏幕宽度（像素）
    int height = metric.heightPixels;   // 屏幕高度（像素）
//    float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
//    int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
    return height;
}
public static int getScreenWidth(Activity con) {
    DisplayMetrics metric = new DisplayMetrics();
    con.getWindowManager().getDefaultDisplay().getMetrics(metric);
    int width = metric.widthPixels;     // 屏幕宽度（像素）
//    int height = metric.heightPixels;   // 屏幕高度（像素）
//    float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
//    int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
    return width;
}
}

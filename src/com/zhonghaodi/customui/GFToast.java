package com.zhonghaodi.customui;

import android.content.Context;
import android.widget.Toast;

public class GFToast {
	public static Context GFContext;
	public static void show(String text) {
		Toast.makeText(GFContext, text, Toast.LENGTH_SHORT).show();;
	}
	public static void show(Context context ,String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}

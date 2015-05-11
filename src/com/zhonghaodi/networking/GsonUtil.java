package com.zhonghaodi.networking;

import com.google.gson.Gson;

public class GsonUtil {
	private static Gson gson;
	public static Object fromJson(String jsonString, Class<?> classofT) {
		if (gson==null) {
			gson=new Gson();
		}
		return gson.fromJson(jsonString, classofT);
	}
}

package com.zhonghaodi.networking;




import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.Question;

public class JsonUtil {
	
	public static String convertObjectToJson(Object object){
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();
		
		String jsString = gson.toJson(object);
		
		return jsString;
		
	}
	public static String convertObjectToJson(Object object,String formart){
		
		Gson gson = new GsonBuilder().setDateFormat(formart)
				.create();
		
		String jsString = gson.toJson(object);
		
		return jsString;
		
	}
	public static String convertObjectToJson(Object object,String formart,final String fieldName){
		
		Gson gson = new GsonBuilder().setDateFormat(formart).setExclusionStrategies(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes arg0) {
	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==Question.class) return true;
	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==NetImage.class) return true;
	            return false;
			}
			
			@Override
			public boolean shouldSkipClass(Class<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		})
				.create();
		
		String jsString = gson.toJson(object);
		
		return jsString;
		
	}
}

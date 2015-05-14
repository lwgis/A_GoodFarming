package com.zhonghaodi.networking;




import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


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
//	public static String convertObjectToJson(Object object,String formart,final String fieldName){
//		
//		Gson gson = new GsonBuilder().setDateFormat(formart).setExclusionStrategies(new ExclusionStrategy() {
//			
//			@Override
//			public boolean shouldSkipField(FieldAttributes arg0) {
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==Question.class) return true;
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==NetImage.class) return true;
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==Response.class) return true;
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==User.class) return true;
//	            return false;
//			}
//			
//			@Override
//			public boolean shouldSkipClass(Class<?> arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		})
//				.create();
//		
//		String jsString = gson.toJson(object);
//		
//		return jsString;
//		
//	}
	public static String convertObjectToJson(Object object,String formart,final String[] classes){
		
		Gson gson = new GsonBuilder().setDateFormat(formart).setExclusionStrategies(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes arg0) {
				for (int i = 0; i < classes.length; i++) {
					if (arg0.getDeclaringClass().toString().equals(classes[i])&&arg0.getName().equals("id")) {
						return true;
					}
				}
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==Question.class) return true;
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==NetImage.class) return true;
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==Response.class) return true;
//	            if(fieldName.equals(arg0.getName())&&arg0.getDeclaringClass()==User.class) return true;
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

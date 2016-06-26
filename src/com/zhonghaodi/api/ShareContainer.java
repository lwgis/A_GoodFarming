package com.zhonghaodi.api;

import java.util.ArrayList;
import java.util.List;

import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;

import android.graphics.Bitmap;

public interface ShareContainer {

	public void popupShareWindow(User user);
	
	public void shareQuestionWindow(Question question,String folder);
	
	public void shareWeixin(String url,String title,String des,Bitmap thumb);
	
	public void shareCirclefriends(String url,String title,String des,Bitmap thumb);
	
	public void shareQQ(String url,String title,String des,String imgUrl);
	
	public void shareQZone(String url,String title,String des,ArrayList<String> urList,String img1);
	
}

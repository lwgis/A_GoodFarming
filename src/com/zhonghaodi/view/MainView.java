package com.zhonghaodi.view;

import java.io.File;
import java.util.List;

import com.easemob.chat.EMMessage;
import com.zhonghaodi.model.AppVersion;
import com.zhonghaodi.model.PointDic;
import com.zhonghaodi.model.User;

public interface MainView {
	
	public void compareVersion(AppVersion version);
	public void downComplete(File file);
	public void showMessage(String mess);
	public void savePointDics(List<PointDic> dics);
	public void saveUserInfo(User user);
	public void notificationTextMessage(String nick);
	public void setUnredMessageCount();
	public void displayProgress(float bf,float pf,float cf);

}

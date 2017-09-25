package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.Question;

public interface FrmHomeView {
	
	public void showMessage(String mess);
	public void onLoaded(int what,List<Question> questions);
	public void onDeleted();
	public void onZan();

}

package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.Question;

public interface SearchFairView {

	public void showMessage(String mess);
	public void showQuestions(List<Question> questions,int what);
	
}

package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.Disease;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Recipe;

public interface HotQuestionView {
	public void showMessage(String mess);
	public void showQuestions(List<Question> questions);
	public void showDiseases(List<Disease> diseases);
	public void showRecipes(List<Recipe> recipes);
}

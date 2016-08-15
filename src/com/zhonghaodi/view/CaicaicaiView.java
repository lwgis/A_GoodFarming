package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.Caicaicai;

public interface CaicaicaiView {
	
	public void showMessage(String message);
	
	public void displayCaicaicai(List<Caicaicai> caicaicais,boolean isAdd);
	
	public void refreshComplete();

}

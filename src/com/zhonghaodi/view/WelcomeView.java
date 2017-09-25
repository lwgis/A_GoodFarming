package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.Advertising;
import com.zhonghaodi.model.City;

public interface WelcomeView {
	public void saveCity(City city);
	public void displayAdvertisings(List<Advertising> advertisings);
	public void showMessage(String mess);
	public void displayWelcome();
	public void goTomain();
	
}

package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.City;

public interface CityView {
	
	public void displayCities(List<City> cities);
	
	public void showMessage(String message);

}

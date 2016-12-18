package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.Province;

import android.R.integer;

public interface ProvinceView {
	
	public void displayProvinces(List<Province> provinces,int status);
	
	public void displayCities(List<Province> cities,String proname,int status);
	
	public void displayCounties(List<Province> counties,String cityname,int status);
	
	public void displayGPSZone(int code,String zone);
	
	public void showMessage(String mess);

}

package com.zhonghaodi.view;

import java.util.List;

import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.SpinnerDto;

public interface FrmSaveView {
	
	public void showMessage(String mess);
	public void showOrders(List<SecondOrder> orders);
	public void refreshData();
	public void showCates(List<SpinnerDto> spds,int select);

}

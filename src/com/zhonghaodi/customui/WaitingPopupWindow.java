package com.zhonghaodi.customui;


import java.util.ArrayList;

import com.zhonghaodi.adapter.SpinnerDtoAdapter;
import com.zhonghaodi.api.DiseaseListView;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.SpinnerDto;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class WaitingPopupWindow extends PopupWindow {

	private View mainview;
	
	public WaitingPopupWindow(Context context) {
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainview = inflater.inflate(R.layout.popupwindow_waiting, null);		
		//设置SelectPicPopupWindow的View  
        this.setContentView(mainview);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(DpTransform.dip2px(context, 150));  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(DpTransform.dip2px(context, 60));  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);   
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0x00000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);
	}
}

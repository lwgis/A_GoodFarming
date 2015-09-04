package com.zhonghaodi.customui;

import java.util.List;

import com.zhonghaodi.adapter.MySpinnerAdapter;
import com.zhonghaodi.goodfarming.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SpinnerPopupwindow extends PopupWindow {

	private TextView clickView;
	private TextView titleView;
	private ListView listView;
	private List<Object> objects;
	private View mMenuView;
	private Activity mActivity;
	private String selectString;
	MySpinnerAdapter adapter;
	public SpinnerPopupwindow(Activity activity,String displaystr,List<Object> os,TextView textView,String title){
		super(activity);
		mActivity = activity;
		LayoutInflater inflater = (LayoutInflater) activity  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popupwindow_spinner, null);
		objects = os;
		selectString = displaystr;
		clickView = textView;
		titleView = (TextView)mMenuView.findViewById(R.id.title_text);
		titleView.setText(title);
		listView = (ListView)mMenuView.findViewById(R.id.pull_refresh_list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectString = objects.get(position).toString();
				adapter.setSelectIndex(position);
				adapter.notifyDataSetChanged();
				clickView.setText(selectString);
				clickView.setTag(objects.get(position));
				dismiss();
			}
		});
		int selectindex = 0;
		for (int i = 0;i<objects.size();i++) {
			Object object = objects.get(i);
			if(selectString.equals(object.toString())){
				selectindex = i;
				break;
			}
		}
		adapter = new MySpinnerAdapter(mActivity, objects);
		adapter.setSelectIndex(selectindex);
		listView.setAdapter(adapter);
		
		//设置SelectPicPopupWindow的View  
        this.setContentView(mMenuView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.MATCH_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.MATCH_PARENT);  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);   
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0xb0000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);
	}
	public List<Object> getObjects() {
		return objects;
	}
	public void setObjects(List<Object> objects) {
		this.objects = objects;
	}
	public String getSelectString() {
		return selectString;
	}
	public void setSelectString(String selectString) {
		this.selectString = selectString;
	}
	
	
	
}

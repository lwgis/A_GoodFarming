package com.zhonghaodi.customui;

import java.util.ArrayList;
import java.util.List;

import com.zhonghaodi.adapter.SpinnerDtoAdapter;
import com.zhonghaodi.api.ContentListView;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.SpinnerDto;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class ContentPopupWindow extends PopupWindow {
	private View mainview;
	private ListView listView;
	private Button areaBtn;
	private Button diseaseBtn;
	private List<SpinnerDto> datas;
	private ContentListView gossipListView;
	private int displayid;
	private SpinnerDtoAdapter adapter;

	public ContentPopupWindow(ContentListView gView,int id,Context context,OnClickListener clickListener) {
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainview = inflater.inflate(R.layout.popupwindow_gossip, null);
		listView = (ListView)mainview.findViewById(R.id.pull_refresh_list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				SpinnerDto spinnerDto = datas.get(position);
				adapter.setSelectId(spinnerDto.getId());
				adapter.notifyDataSetChanged();
				gossipListView.changeContentStatus(spinnerDto);
				dismiss();
			}
		});
		areaBtn = (Button)mainview.findViewById(R.id.areabtn);
		areaBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				gossipListView.selectArea();
			}
		});
		diseaseBtn = (Button)mainview.findViewById(R.id.diseasebtn);
		diseaseBtn.setOnClickListener(clickListener);
		gossipListView = gView;
		displayid = id;
		datas = new ArrayList<SpinnerDto>();
		SpinnerDto spinnerDto = new SpinnerDto();
		spinnerDto.setId(0);
		spinnerDto.setName("查看全部");
		datas.add(spinnerDto);
		spinnerDto = new SpinnerDto();
		spinnerDto.setId(1);
		spinnerDto.setName("只看精选");
		datas.add(spinnerDto);
		
		adapter = new SpinnerDtoAdapter(datas, context, displayid);
		listView.setAdapter(adapter);
		
		//设置SelectPicPopupWindow的View  
        this.setContentView(mainview);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(DpTransform.dip2px(context, 120));  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(DpTransform.dip2px(context, 188));  
        //设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);   
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0xff000000);  
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);
	}
}

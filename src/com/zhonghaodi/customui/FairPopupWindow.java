package com.zhonghaodi.customui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zhonghaodi.adapter.CateGridAdapter;
import com.zhonghaodi.adapter.SpinnerDtoAdapter;
import com.zhonghaodi.api.FairListView;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.SpinnerDto;
import com.zhonghaodi.req.PopupFairReq;
import com.zhonghaodi.view.PopupFairView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class FairPopupWindow implements PopupFairView {
	public View mainview;
	private GridView listView;
	private ImageView searchButton;
	private List<SpinnerDto> datas;
	private FairListView plantListView;
	private int displayid;
	private CateGridAdapter adapter;
	private int type;

	public FairPopupWindow(FairListView gView,int id,Context context,int t) {
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainview = inflater.inflate(R.layout.popupwindow_plant, null);
		type = t;
		listView = (GridView)mainview.findViewById(R.id.pull_refresh_list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				SpinnerDto spinnerDto = datas.get(position);
				adapter.setSelectId(spinnerDto.getId());
				adapter.notifyDataSetChanged();
				plantListView.changePlantStatus(spinnerDto);
			}
		});
		searchButton = (ImageView)mainview.findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SpinnerDto spinnerDto = new SpinnerDto();
				spinnerDto.setId(-1);
				adapter.setSelectId(spinnerDto.getId());
				adapter.notifyDataSetChanged();
				plantListView.changePlantStatus(spinnerDto);
			}
		});
		
		plantListView = gView;
		displayid = id;
		datas = new ArrayList<SpinnerDto>();
		SpinnerDto spinnerDto = new SpinnerDto();
		spinnerDto.setId(0);
		spinnerDto.setName("全部");
		datas.add(spinnerDto);
		adapter = new CateGridAdapter(datas, context, displayid);
		listView.setAdapter(adapter);
		
        PopupFairReq req = new PopupFairReq(this);
        if(type==1)
        {
        	searchButton.setVisibility(View.VISIBLE);
        	req.loadFairCates();
        }	
        else
        {
        	searchButton.setVisibility(View.GONE);
        	req.loadSaveCates();
        }            
	}

	@Override
	public void showCates(List<SpinnerDto> cps) {
		// TODO Auto-generated method stub
		if(cps!=null && cps.size()>0){
			for (Iterator iterator = cps.iterator(); iterator.hasNext();) {
				SpinnerDto spinnerDto = (SpinnerDto) iterator.next();
				datas.add(spinnerDto);
			}
			adapter.notifyDataSetChanged();
		}
	}
}

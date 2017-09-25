package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.adapter.SaveAdapter.HolderZfbt;
import com.zhonghaodi.customui.SpinnerHolder;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.SpinnerDto;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CateGridAdapter  extends BaseAdapter {

	private List<SpinnerDto> mList;
    private Context mContext;
    private int selectId;
    
    public CateGridAdapter(List<SpinnerDto> list,Context context,int id) {
		// TODO Auto-generated constructor stub
    	mList = list;
    	mContext = context;
    	selectId = id;
	}
	
	public int getSelectId() {
		return selectId;
	}



	public void setSelectId(int selectId) {
		this.selectId = selectId;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SpinnerHolder spinnerHolder;
		SpinnerDto spinnerDto = null;
		spinnerDto = mList.get(position);
		
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_grid, parent, false);
			spinnerHolder = new SpinnerHolder(convertView);
			convertView.setTag(spinnerHolder);
		}		
		spinnerHolder=(SpinnerHolder)convertView.getTag();
		
		spinnerHolder.nameTv.setText(spinnerDto.getName());
		if(spinnerDto.getId() == selectId){
			spinnerHolder.nameTv.setTextColor(Color.rgb(56, 190, 153));
		}
		else{
			spinnerHolder.nameTv.setTextColor(Color.rgb(128,128,128));
		}
		return convertView;
	}

}
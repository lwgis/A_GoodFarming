package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.customui.SpinnerHolder;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Area;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AreaAdapter extends BaseAdapter {

	private List<Area> mList;
    private Context mContext;
    public AreaAdapter(Context context,List<Area> areas){
    	mContext = context;
    	mList = areas;
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
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SpinnerHolder spinnerHolder;
		Area area = null;
		area = mList.get(position);
		convertView = LayoutInflater.from(mContext)
				.inflate(R.layout.cell_spinner, parent, false);
		spinnerHolder = new SpinnerHolder(convertView);
		convertView.setTag(spinnerHolder);
		spinnerHolder=(SpinnerHolder)convertView.getTag();
		
		spinnerHolder.nameTv.setText(area.getText());
		return convertView;
	}
	
}

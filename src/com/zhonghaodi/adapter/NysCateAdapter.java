package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.customui.SpinnerHolder;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Area;
import com.zhonghaodi.model.Nyscate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NysCateAdapter extends BaseAdapter {
	
	private List<Nyscate> mList;
	private Context mContext;
	
	public NysCateAdapter(Context context,List<Nyscate> cates){
    	mContext = context;
    	mList = cates;
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
		Nyscate cate = null;
		cate = mList.get(position);
		convertView = LayoutInflater.from(mContext)
				.inflate(R.layout.cell_spinner, parent, false);
		spinnerHolder = new SpinnerHolder(convertView);
		convertView.setTag(spinnerHolder);
		spinnerHolder=(SpinnerHolder)convertView.getTag();
		
		spinnerHolder.nameTv.setText(cate.getName());
		return convertView;
	}
}

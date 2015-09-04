package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.customui.SpinnerHolder;
import com.zhonghaodi.goodfarming.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MySpinnerAdapter extends BaseAdapter {

	private List<Object> mList;
    private Context mContext;
    private int selectIndex;
    public MySpinnerAdapter(Context context,List<Object> objects){
    	mContext = context;
    	mList = objects;
    	selectIndex = 0;
    }
	
	public int getSelectIndex() {
		return selectIndex;
	}


	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
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
		Object object = null;
		object = mList.get(position);
		convertView = LayoutInflater.from(mContext)
				.inflate(R.layout.cell_spinner, parent, false);
		spinnerHolder = new SpinnerHolder(convertView);
		convertView.setTag(spinnerHolder);
		spinnerHolder=(SpinnerHolder)convertView.getTag();
		
		spinnerHolder.nameTv.setText(object.toString());
		if(selectIndex == position){
			spinnerHolder.selectRd.setChecked(true);
		}
		else{
			spinnerHolder.selectRd.setChecked(false);
		}
		return convertView;
	}

}

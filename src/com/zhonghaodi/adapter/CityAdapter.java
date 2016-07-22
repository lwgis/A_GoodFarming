package com.zhonghaodi.adapter;

import java.util.List;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.City;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class CityAdapter extends BaseAdapter {
	
	class CityHolder{
		public TextView nameTv;
		public RadioButton radioButton;
		
		public CityHolder(View view){
			nameTv = (TextView)view.findViewById(R.id.name_text);
			radioButton = (RadioButton)view.findViewById(R.id.rd_select);
		}
	}
	
	private List<City> mCities;
	private Context mContext;
	private int selid;
	
	public CityAdapter(List<City> cities,Context context) {
		// TODO Auto-generated constructor stub
		mCities = cities;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCities.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCities.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mCities.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		City city = mCities.get(position);
		CityHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_city, parent,false);
			holder = new CityHolder(convertView);
			convertView.setTag(holder);
		}
		
		holder = (CityHolder)convertView.getTag();
		holder.nameTv.setText(city.getName());
		if(selid==0 || selid!=city.getId()){
			holder.radioButton.setChecked(false);
		}
		else{
			holder.radioButton.setChecked(true);
		}
		return convertView;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public int getSelid() {
		return selid;
	}

	public void setSelid(int selid) {
		this.selid = selid;
	}
	
}

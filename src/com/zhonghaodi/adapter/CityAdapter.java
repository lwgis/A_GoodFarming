package com.zhonghaodi.adapter;

import java.util.List;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.MySectionIndexer;
import com.zhonghaodi.view.PinnedHeaderListView;
import com.zhonghaodi.view.PinnedHeaderListView.PinnedHeaderAdapter;

import android.R.mipmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class CityAdapter extends BaseAdapter implements PinnedHeaderAdapter,OnScrollListener {
	
	class CityHolder{
		public TextView nameTv;
		public RadioButton radioButton;
		public TextView group_title;
		public LinearLayout contentLayout;
		
		public CityHolder(View view){
			nameTv = (TextView)view.findViewById(R.id.name_text);
			radioButton = (RadioButton)view.findViewById(R.id.rd_select);
			group_title = (TextView)view.findViewById(R.id.group_title);
			contentLayout = (LinearLayout)view.findViewById(R.id.contentLayout);
		}
	}
	
	private List<City> mCities;
	private Context mContext;
	private int selid;
	private MySectionIndexer mIndexer;
	private int mLocationPosition = -1;
	
	public CityAdapter(List<City> cities,Context context,MySectionIndexer mIndexer) {
		// TODO Auto-generated constructor stub
		this.mCities = cities;
		this.mContext = context;
		this.mIndexer = mIndexer;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCities==null?0:mCities.size();
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
		int section = mIndexer.getSectionForPosition(position);
		if (mIndexer.getPositionForSection(section) == position) {
			holder.group_title.setVisibility(View.VISIBLE);
			holder.group_title.setText(city.getPname());
		} else {
			holder.group_title.setVisibility(View.GONE);
		}
		holder.nameTv.setText(city.getName());
		if(selid==0 || selid!=city.getId()){
			holder.radioButton.setChecked(false);
		}
		else{
			holder.radioButton.setChecked(true);
		}
		if(position==mCities.size()-1){
			holder.contentLayout.setVisibility(View.GONE);
		}
		else{
			holder.contentLayout.setVisibility(View.VISIBLE);
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

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0
				|| (mLocationPosition != -1 && mLocationPosition == realPosition)) {
			return PINNED_HEADER_GONE;
		}
		mLocationPosition = -1;
		int section = mIndexer.getSectionForPosition(realPosition);
		int nextSectionPosition = mIndexer.getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		int realPosition = position;
		int section = mIndexer.getSectionForPosition(realPosition);
		String title = (String) mIndexer.getSections()[section];
		((TextView) header.findViewById(R.id.group_title)).setText(title);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}

	}
}

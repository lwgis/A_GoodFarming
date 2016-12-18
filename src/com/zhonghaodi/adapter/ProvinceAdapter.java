package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.MySectionIndexer;
import com.zhonghaodi.model.Province;
import com.zhonghaodi.view.PinnedHeaderListView;
import com.zhonghaodi.view.PinnedHeaderListView.PinnedHeaderAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProvinceAdapter extends BaseAdapter implements PinnedHeaderAdapter,OnScrollListener {
	
	class ProvinceHolder{
		public TextView nameTv;
		public TextView group_title;
		public LinearLayout contentLayout;
		public ImageView rightIv;
		
		public ProvinceHolder(View view){
			nameTv = (TextView)view.findViewById(R.id.name_text);
			group_title = (TextView)view.findViewById(R.id.group_title);
			contentLayout = (LinearLayout)view.findViewById(R.id.contentLayout);
			rightIv = (ImageView)view.findViewById(R.id.right);
		}
	}
	
	private List<Province> zones;
	private Context mContext;
	private MySectionIndexer mIndexer;
	private int mLocationPosition = -1;
	private int level;
	
	public ProvinceAdapter(List<Province> provinces,Context context,MySectionIndexer indexer,int l){
		zones = provinces;
		mContext = context;
		mIndexer = indexer;
		level = l;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return zones==null?0:zones.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return zones.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return zones.get(position).getCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Province province = zones.get(position);
		ProvinceHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_province, parent,false);
			holder = new ProvinceHolder(convertView);
			convertView.setTag(holder);
		}
		
		holder = (ProvinceHolder)convertView.getTag();
		int section = mIndexer.getSectionForPosition(position);
		if (mIndexer.getPositionForSection(section) == position) {
			holder.group_title.setVisibility(View.VISIBLE);
			holder.group_title.setText(province.getHead());
		} else {
			holder.group_title.setVisibility(View.GONE);
		}
		holder.nameTv.setText(province.getName());
		
		if(level==3){
			holder.rightIv.setVisibility(View.GONE);
		}
		else{
			holder.rightIv.setVisibility(View.VISIBLE);
		}
		return convertView;
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
		if(mIndexer.getSections()!=null &&mIndexer.getSections().length>0){
			String title = (String) mIndexer.getSections()[section];
			((TextView) header.findViewById(R.id.group_title)).setText(title);
		}
		
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
			PinnedHeaderListView pView = (PinnedHeaderListView)view;
			if(pView!=null)
				pView.configureHeaderView(firstVisibleItem);
		}

	}
}

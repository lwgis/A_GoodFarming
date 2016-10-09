package com.zhonghaodi.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.RecommendedHolder;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RecommendedAdapter extends BaseAdapter {
	
	private List<User> users;
	private Context mContext;
	
	public RecommendedAdapter(List<User> us,Context context) {
		// TODO Auto-generated constructor stub
		users = us;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RecommendedHolder userHolder;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_recommended, parent, false);
			userHolder = new RecommendedHolder(convertView);
			convertView.setTag(userHolder);
		}
		userHolder = (RecommendedHolder)convertView.getTag();
		User user = users.get(position);
		ImageLoader.getInstance().displayImage(
				HttpUtil.ImageUrl+"users/small/"
						+ user.getThumbnail(),
						userHolder.headIv, ImageOptions.options);
		userHolder.nameTv.setText(user.getAlias());
		userHolder.timeTv.setText("注册时间："+user.getTime());
		
		return convertView;
	}

}

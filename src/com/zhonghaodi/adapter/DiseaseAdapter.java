package com.zhonghaodi.adapter;

import java.util.List;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.goodfarming.DiseasesActivity;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Disease;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiseaseAdapter  extends BaseAdapter{

	List<Disease> diseases;
	Context mContext;
	
	public DiseaseAdapter(List<Disease> ds,Context context) {
		// TODO Auto-generated constructor stub
		diseases = ds;
		mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return diseases.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return diseases.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DiseaseHolder diseaseholder;;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_diseases, parent, false);
			diseaseholder = new DiseaseHolder(convertView);
			convertView.setTag(diseaseholder);
		}
		
		diseaseholder=(DiseaseHolder)convertView.getTag();
		Disease disease = diseases.get(position);
		if (disease.getThumbnail()!=null) {
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"diseases/small/"+disease.getThumbnail(), diseaseholder.agroIv, ImageOptions.optionsNoPlaceholder);
		}
		diseaseholder.titleTv.setText(disease.getName());
		diseaseholder.timeTv.setText(disease.getDescription());
		return convertView;
	}
	
	class DiseaseHolder{
		public RoundedImageView agroIv;
		public TextView titleTv;
		public TextView timeTv;
		public DiseaseHolder(View view){
			 agroIv=(RoundedImageView)view.findViewById(R.id.head_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 timeTv=(TextView)view.findViewById(R.id.time_text);
		}
	}
	
}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectCropFragment extends Fragment {
	private ListView cropListView=null;
	private ArrayList<Crop> allCrops;
	private ArrayList<Crop> rootCrops;
	private int cropId;
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.obj!=null) {
				Gson gson=new Gson();
				allCrops=gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				for (Crop crop : allCrops) {
					if (crop.getCategory()==0) {
						rootCrops.add(crop);
					}
				}
				CropAdepter cropAdepter=new CropAdepter();
				cropListView.setAdapter(cropAdepter);
				cropListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
							cropId=rootCrops.get(position).getId();
					}
				});
			}
		}
		
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_select_crop, container, false);
		cropListView=(ListView)view.findViewById(R.id.crop_list);
		rootCrops=new ArrayList<Crop>();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString= HttpUtil.getAllCropsString();
				if (!jsonString.equals("")) {
					Message msg=handler.obtainMessage();
					msg.obj=jsonString;
					msg.sendToTarget();	
				}			
			}
		}).start();
		return view;
	}
	public int getCropId() {
		return cropId;
	}
	public void setCropId(int cropId) {
		this.cropId = cropId;
	}
	private class CropAdepter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return rootCrops.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return rootCrops.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return rootCrops.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView==null) {
				convertView=LayoutInflater.from(getActivity()).inflate(R.layout.cell_crop, parent, false);
			}
			TextView tiltleTv=(TextView)convertView.findViewById(R.id.title_text);
			tiltleTv.setText(rootCrops.get(position).getName());
			TextView discriptionTv=(TextView)convertView.findViewById(R.id.discription_text);
			String discription="";
			for (Crop crop : allCrops) {
				if (crop.getCategory()==rootCrops.get(position).getId()) {
					discription=discription+crop.getName()+" ";
				}
			}
			discriptionTv.setText(discription);
			return convertView;
		}
		
	}
}
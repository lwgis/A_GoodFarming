package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectCropFragment extends Fragment implements HandMessage {
	private ListView cropListView = null;
	private ArrayList<Crop> allCrops;
	private ArrayList<Crop> rootCrops;
	private GFHandler<SelectCropFragment> handler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		handler = new GFHandler<SelectCropFragment>(this);
		View view = inflater.inflate(R.layout.fragment_select_crop, container,
				false);
		cropListView = (ListView) view.findViewById(R.id.crop_list);
		rootCrops = new ArrayList<Crop>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAllCropsString();
				if(jsonString!=null){
					if (!jsonString.equals("")) {
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
				}
				else{
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = "链接服务器错误，请稍后再试！";
					msg.sendToTarget();
				}
				
			}
		}).start();
		return view;
	}

	class CropAdapter extends BaseAdapter {

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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = SelectCropFragment.this.getActivity().getLayoutInflater().inflate(
					R.layout.cell_select_crop, parent, false);
			Crop crop = rootCrops.get(position);
			TextView cropTv = (TextView) convertView
					.findViewById(R.id.crop_text);
			cropTv.setText(crop.getName());
			LinearLayout childContentView = (LinearLayout) convertView
					.findViewById(R.id.content_view);
			for (Crop childCrop : allCrops) {
				if (childCrop.getCategory() == crop.getId()) {
					View childView = SelectCropFragment.this.getActivity()
							.getLayoutInflater().inflate(
									R.layout.cell_singleselect_crop, parent,
									false);
					TextView nameView = (TextView)childView.findViewById(R.id.cropname_txt);
					nameView.setText(childCrop.getName());
					nameView.setTag(childCrop);
					nameView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Crop crop = (Crop)v.getTag();
							CreateQuestionActivity activity = (CreateQuestionActivity) SelectCropFragment.this
									.getActivity();
							activity.setCropId(crop.getId());
							activity.showFragment(1);
							activity.setTitle(crop.getName()
									+ "问题");
						}
					});
					childContentView.addView(childView);

				}
			}

			return convertView;
		}

	}

	@Override
	public void handleMessage(Message msg,Object object) {
		final SelectCropFragment fragment = (SelectCropFragment)object;
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				fragment.allCrops = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				for (Crop crop : fragment.allCrops) {
					if (crop.getCategory() == 0) {
						fragment.rootCrops.add(crop);
					}
				}
				CropAdapter cropAdepter = fragment.new CropAdapter();
				fragment.cropListView.setAdapter(cropAdepter);
				
			}
			break;
		case 0:
			GFToast.show(msg.obj.toString());
			break;

		default:
			break;
		}
				
	}

}

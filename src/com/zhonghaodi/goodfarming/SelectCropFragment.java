package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.MySectionIndexer;
import com.zhonghaodi.model.SortComparator;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.view.PinnedHeaderListView;
import com.zhonghaodi.view.PinnedHeaderListView.PinnedHeaderAdapter;

import android.R.integer;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectCropFragment extends Fragment implements HandMessage,OnItemClickListener {
	private PinnedHeaderListView cropListView = null;
	private ArrayList<Crop> childCrops;
	private GFHandler<SelectCropFragment> handler;
	private HashMap<Integer, String> keyMap = new HashMap<Integer,String>();
	private CropListAdapter adapter;
	private String[] sections;  
    private Integer[] counts;
    private int status;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		handler = new GFHandler<SelectCropFragment>(this);
		View view = inflater.inflate(R.layout.fragment_select_crop, container,
				false);
		cropListView = (PinnedHeaderListView) view.findViewById(R.id.crop_list);
		cropListView.setOnItemClickListener(this);
		childCrops = new ArrayList<Crop>();
		
		status = getActivity().getIntent().getIntExtra("status", 0);
		if(status==0){
			loadCrops();
		}
		else if(status==1){
			loadGossipCate();
		}
		else{
			loadCrops();
		}
		return view;
	}
	
	private void loadCrops(){
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
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("选择提问作物");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("选择提问作物");
	}

	private void loadGossipCate(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getGossipCropsString();
				if(jsonString!=null){
					if (!jsonString.equals("")) {
						Message msg = handler.obtainMessage();
						msg.what = 2;
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
	}
	
	class CropListAdapter extends BaseAdapter implements PinnedHeaderAdapter,OnScrollListener{
		private MySectionIndexer mIndexer;
		private Context mContext;
		private int mLocationPosition = -1;
		private LayoutInflater mInflater;

		public CropListAdapter(MySectionIndexer mIndexer,
				Context mContext) {
			this.mIndexer = mIndexer;
			this.mContext = mContext;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return childCrops == null ? 0 : childCrops.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return childCrops.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = mInflater.inflate(R.layout.cell_crop, null);

				holder = new ViewHolder();
				holder.group_title = (TextView) view.findViewById(R.id.group_title);
				holder.city_name = (TextView) view.findViewById(R.id.crop_name);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			Crop crop = childCrops.get(position);
			
			int section = mIndexer.getSectionForPosition(position);
			if (mIndexer.getPositionForSection(section) == position) {
				String pname = keyMap.get(crop.getCategory());
				holder.group_title.setVisibility(View.VISIBLE);
				holder.group_title.setText(pname);
			} else {
				holder.group_title.setVisibility(View.GONE);
			}
			
			holder.city_name.setText(crop.getName());

			return view;
		}

		public class ViewHolder {
			public TextView group_title;
			public TextView city_name;
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
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Crop crop = childCrops.get(position);
		
		if(status==0){
			CreateQuestionActivity activity = (CreateQuestionActivity) SelectCropFragment.this
					.getActivity();
			activity.setCropId(crop.getId());
			activity.showFragment(3);
			activity.setTitle(crop.getName()
					+ "问题");
		}
		else if(status==1){
			CreateQuestionActivity activity = (CreateQuestionActivity) SelectCropFragment.this
					.getActivity();
			activity.setCropId(crop.getId());
			activity.showFragment(3);
			activity.setTitle(crop.getName()
					+ "话题");
		}
		else{
			CreatePlantActivity activity = (CreatePlantActivity) SelectCropFragment.this
					.getActivity();
			activity.setCropId(crop.getId());
			activity.showFragment(1);
			activity.setTitle(crop.getName()
					+ "分享");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg,Object object) {
		final SelectCropFragment fragment = (SelectCropFragment)object;
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Crop> crops = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				childCrops.clear();
				List<String> secList = new ArrayList<String>();
				List<Integer> intList = new ArrayList<Integer>();
				if(crops!=null && crops.size()>0){
					@SuppressWarnings("rawtypes")
					Comparator comp = new SortComparator();  
			        Collections.sort(crops,comp);
			        int k = 0;
			        int index = -1;
					for (Crop crop : crops) {
						if (crop.getCategory() == 0) {
							keyMap.put(crop.getId(), crop.getName());
							secList.add(crop.getName());
						}
						else{
							childCrops.add(crop);
							if(crop.getCategory()!=k){
								index++;
								k=crop.getCategory();
								intList.add(1);
							}
							else{
								int value = intList.get(index);
								intList.set(index, ++value);
							}
						}
					}
					sections = (String[])secList.toArray(new String[secList.size()]);
			        counts = (Integer[])intList.toArray(new Integer[intList.size()]);
			        MySectionIndexer indexer = new MySectionIndexer(sections, counts);
			        adapter = new CropListAdapter(indexer, this.getActivity());
			        cropListView.setAdapter(adapter);
			        cropListView.setOnScrollListener(adapter);
			      //設置頂部固定頭部  
			        cropListView.setPinnedHeaderView(LayoutInflater.from(getActivity()).inflate(    
                            R.layout.cell_group_crop, cropListView, false)); 
				} 	        

			}
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Crop> crops = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				childCrops.clear();
				List<String> secList = new ArrayList<String>();
				List<Integer> intList = new ArrayList<Integer>();
				if(crops!=null && crops.size()>0){
			        
			        Crop secCrop = new Crop();
			        secCrop.setId(100);
			        secCrop.setName("话题类");
			        keyMap.put(secCrop.getId(), secCrop.getName());
					secList.add(secCrop.getName());
					for (Crop crop : crops) {
						childCrops.add(crop);
					}
					intList.add(childCrops.size());
					sections = (String[])secList.toArray(new String[secList.size()]);
			        counts = (Integer[])intList.toArray(new Integer[intList.size()]);
			        MySectionIndexer indexer = new MySectionIndexer(sections, counts);
			        adapter = new CropListAdapter(indexer, this.getActivity());
			        cropListView.setAdapter(adapter);
			        cropListView.setOnScrollListener(adapter);
			      //設置頂部固定頭部  
			        cropListView.setPinnedHeaderView(LayoutInflater.from(getActivity()).inflate(    
                            R.layout.cell_group_crop, cropListView, false)); 
				} 	        

			}
			break;
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Crop> crops = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				childCrops.clear();
				List<String> secList = new ArrayList<String>();
				List<Integer> intList = new ArrayList<Integer>();
				if(crops!=null && crops.size()>0){

			        Crop secCrop = new Crop();
			        secCrop.setId(100);
			        secCrop.setName("分享类");
			        keyMap.put(secCrop.getId(), secCrop.getName());
					secList.add(secCrop.getName());
					for (Crop crop : crops) {
						childCrops.add(crop);
					}
					intList.add(childCrops.size());
					sections = (String[])secList.toArray(new String[secList.size()]);
			        counts = (Integer[])intList.toArray(new Integer[intList.size()]);
			        MySectionIndexer indexer = new MySectionIndexer(sections, counts);
			        adapter = new CropListAdapter(indexer, this.getActivity());
			        cropListView.setAdapter(adapter);
			        cropListView.setOnScrollListener(adapter);
			      //設置頂部固定頭部  
			        cropListView.setPinnedHeaderView(LayoutInflater.from(getActivity()).inflate(    
                            R.layout.cell_group_crop, cropListView, false)); 
				} 	        

			}
			break;
		case 0:
			GFToast.show(getActivity().getApplicationContext(),msg.obj.toString());
			break;

		default:
			break;
		}
				
	}

}

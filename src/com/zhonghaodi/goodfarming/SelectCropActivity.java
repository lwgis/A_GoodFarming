/**
 * 
 */
package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author liwei
 *
 */
public class SelectCropActivity extends Activity implements HandMessage {
	private ListView listView;
	private CropAdapter adapter = new CropAdapter();
	private ArrayList<Crop> allCrops;
	private ArrayList<Crop> rootCrops;
	private ArrayList<Crop> selectCrops;
	private GFHandler<SelectCropActivity> handler = new GFHandler<SelectCropActivity>(
			this);
	private boolean bHave=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_select_crop);
		rootCrops = new ArrayList<Crop>();
		listView = (ListView) findViewById(R.id.crop_list);
		listView.setAdapter(adapter);
		bHave=getIntent().hasExtra("crops");
		selectCrops = getIntent().getParcelableArrayListExtra("crops");
		MyTextButton cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(selectCrops==null || selectCrops.size()==0){
					GFToast.show(getApplicationContext(),"您还没设置擅长的作物！");
					return;
				}
				if(!bHave){
					GFToast.show(getApplicationContext(),"请您点击确定设置擅长的作物！");
					return;
				}
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		MyTextButton okBtn = (MyTextButton) findViewById(R.id.ok_button);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(selectCrops==null || selectCrops.size()==0){
					GFToast.show(getApplicationContext(),"您还没设置擅长的作物！");
					return;
				}
				Intent it = getIntent();
				it.putParcelableArrayListExtra("crops", selectCrops);
				setResult(RESULT_OK, it);
				finish();
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAllCropsString();
				if (!jsonString.equals("")) {
					Message msg = handler.obtainMessage();
					msg.obj = jsonString;
					msg.sendToTarget();
				}
			}
		}).start();
	}

	/**
	 * 是否有子类别
	 * 
	 * @param id
	 * @return
	 */
	private boolean hasChildCrop(int id) {
		for (Crop crop : allCrops) {
			if (crop.getCategory() == id) {
				return true;
			}
		}
		return false;
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
			convertView = SelectCropActivity.this.getLayoutInflater().inflate(
					R.layout.cell_select_crop, parent, false);
			Crop crop = rootCrops.get(position);
			TextView cropTv = (TextView) convertView
					.findViewById(R.id.crop_text);
			cropTv.setText(crop.getName());
			LinearLayout childContentView = (LinearLayout) convertView
					.findViewById(R.id.content_view);
			for (Crop childCrop : allCrops) {
				if (childCrop.getCategory() == crop.getId()) {
					View childView = SelectCropActivity.this
							.getLayoutInflater().inflate(
									R.layout.cell_select_crop_child, parent,
									false);
					CheckBox childCropCk = (CheckBox) childView
							.findViewById(R.id.child_crop_check);

					childCropCk.setText(childCrop.getName());
					childContentView.addView(childView);
					childCropCk.setTag(childCrop);
					if (selectCrops != null) {
						for (Crop c : selectCrops) {
							if (c.getId() == childCrop.getId()) {
								childCropCk.setChecked(true);
							}
						}
					}
					childCropCk
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {

								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									Crop crop = (Crop) buttonView.getTag();
									if (selectCrops == null) {
										selectCrops = new ArrayList<Crop>();
									}
									if (isChecked) {
										selectCrops.add(crop);
									} else {
										SelectCropActivity.this
												.removeSelectCrop(crop);
									}
								}
							});

				}
			}

			return convertView;
		}

	}
	
	/***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
	        	if(selectCrops==null || selectCrops.size()==0){
					GFToast.show(getApplicationContext(),"您还没设置擅长的作物！");
					return false;
				}
	        	if(!bHave){
					GFToast.show(getApplicationContext(),"请您点击确定设置擅长的作物！");
					return false;
				}
				setResult(RESULT_CANCELED);
				finish();
	        } 
	    } 
		return false;

	}

	@Override
	public void handleMessage(Message msg, Object object) {
		SelectCropActivity activity = (SelectCropActivity) object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			activity.allCrops = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<Crop>>() {
					}.getType());
			for (Crop crop : activity.allCrops) {
				if (crop.getCategory() == 0 && hasChildCrop(crop.getId())) {
					activity.rootCrops.add(crop);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	protected void removeSelectCrop(Crop crop) {
		Crop tCrop = null;
		for (Crop c : selectCrops) {
			if (c.getId() == crop.getId()) {
				tCrop = c;
				break;
			}
		}
		if (tCrop != null) {
			selectCrops.remove(tCrop);
		}
	}

}

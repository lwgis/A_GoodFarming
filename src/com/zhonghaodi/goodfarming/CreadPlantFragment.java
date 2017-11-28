package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.AddImageAdapter;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.SpinnerPopupwindow;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.ProjectImage;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.ViewGroup;

public class CreadPlantFragment extends Fragment implements OnClickListener,HandMessage {
	private View view;
	private MyEditText nyqEditText;
	private TextView cropTextView;
	private GridView imageGridView;
	private AddImageAdapter reportAdapter;
	private List<NetImage> imgs;
	private ArrayList<ProjectImage> projectImages;
	private PopupWindow mPopupWindow;
	private View popView;
	private File currentfile;
	private List<Object> crops = new ArrayList<Object>();
	private GFHandler<CreadPlantFragment> handler = new GFHandler<CreadPlantFragment>(this);
	private int imageCount;
	private CheckBox sharecheck;
	private RadioGroup radioGroup;
	public boolean getcheck(){
		return sharecheck.isChecked();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_create_plant,container, false);
		sharecheck = (CheckBox)view.findViewById(R.id.sharecheck);
		this.imageGridView=(GridView)view.findViewById(R.id.imageGridView);
		this.nyqEditText = (MyEditText)view.findViewById(R.id.content_edit);
		this.radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
		this.projectImages=new ArrayList<ProjectImage>();
		this.reportAdapter=new AddImageAdapter(getActivity(),projectImages);
		this.imageGridView.setAdapter(reportAdapter);
		popView = LayoutInflater.from(getActivity())
					.inflate(R.layout.popupwindow_camera, null);
		cropTextView = (TextView)view.findViewById(R.id.mycrop_select);
		cropTextView.setOnClickListener(this);
		nyqEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				CreatePlantActivity activity = (CreatePlantActivity) getActivity();
				if (!nyqEditText.getText().toString().trim().isEmpty()) {
					activity.getSendBtn().setEnabled(true);
				} else {
					activity.getSendBtn().setEnabled(false);
				}
			}
		});
		
		this.imageGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (reportAdapter.getItem(position)==null) {
					if(projectImages.size()<3){
						if (mPopupWindow==null) {
							mPopupWindow=new PopupWindow(popView,DpTransform.dip2px(getActivity(), 180),DpTransform.dip2px(getActivity(), 100));
						}
						if (mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						else {
							mPopupWindow.showAsDropDown(view,-DpTransform.dip2px(getActivity(), 0),DpTransform.dip2px(getActivity(), 0));
						}
					}
					else{
						GFToast.show(getActivity(),"最多添加3张图片！");
					}
					
				}
				
			}
		});
		//相机按钮
		Button btnCamera=(Button)popView.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(this);
		//相册按钮
		Button btnPhoto=(Button)popView.findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(this);
		loadPlantInfo();
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("种植分享Fragment");
	}



	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("种植分享Fragment");
	}



	public void loadPlantInfo(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getFairCatesString();
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
	
	public void setImagePath(String path){
		ProjectImage projectImage = new ProjectImage();
		projectImage.setName(path);
		projectImage.setType(ProjectImage.LOCAL);
		projectImages.add(projectImage);
		this.reportAdapter.setImages(projectImages);
		this.reportAdapter.notifyDataSetChanged();
		LayoutParams lParams = this.imageGridView.getLayoutParams();
		int height = (this.projectImages.size() / 4 + 1);
		lParams.height = DpTransform.dip2px(getActivity(), 80 * height);
		this.imageGridView.setLayoutParams(lParams);
	}
	
	public String getContent(){
		return nyqEditText.getText().toString();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.mycrop_select){			
			SpinnerPopupwindow spinnerPopupwindow = new SpinnerPopupwindow(getActivity(), cropTextView.getText().toString(), 
					crops, cropTextView,"分享类别");
			spinnerPopupwindow.showAtLocation(view.findViewById(R.id.main), 
					Gravity.CENTER, 0, 0);
		}
		else if(v.getId()==R.id.btnCamera){
			File fileCache = ImageOptions.getCache(getActivity());
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			// intent.addCategory(Intent.CATEGORY_DEFAULT);
			currentfile = new File(fileCache.getPath() + "/"
					+ UUID.randomUUID().toString() + ".jpg");
			if (currentfile.exists()) {
				currentfile.delete();
			}
			Uri uri = Uri.fromFile(currentfile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			getActivity().startActivityForResult(intent, 3);
			mPopupWindow.dismiss();
		}
		else if(v.getId()==R.id.btnPhoto){
			Intent it = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			getActivity().startActivityForResult(it, 2);
			mPopupWindow.dismiss();
		}
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Crop> cps = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				crops.clear();
				if(cps!=null && cps.size()>0){
					cropTextView.setText(cps.get(0).getName());
					cropTextView.setTag(cps.get(0));
					for (Crop crop : cps) {
						crops.add(crop);
					}
				}
			}
			else{
				GFToast.show(getActivity(), "获取失败,请稍后再试");
				return;
			}
			break;
			
		default:
			break;
		}
	}
	
	public PopupWindow getPopupWindow() {
		return mPopupWindow;
	}

	public void setPopupWindow(PopupWindow popupWindow) {
		this.mPopupWindow = popupWindow;
	}

	public File getCurrentfile() {
		return currentfile;
	}

	public void setCurrentfile(File currentfile) {
		this.currentfile = currentfile;
	}

	public ArrayList<ProjectImage> getProjectImages() {
		return projectImages;
	}

	public void setProjectImages(ArrayList<ProjectImage> projectImages) {
		this.projectImages = projectImages;
	}

	public TextView getCropTextView() {
		return cropTextView;
	}

	public void setCropTextView(TextView cropTextView) {
		this.cropTextView = cropTextView;
	}

	public String getDeal(){
		int id = radioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		if(radioButton==null){
			return null;
		}
		
		return radioButton.getText().toString();
	}
}

package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.SpinnerPopupwindow;
import com.zhonghaodi.goodfarming.ContactsActivity.ShdzHolder;
import com.zhonghaodi.goodfarming.SelectCropFragment.CropListAdapter;
import com.zhonghaodi.model.Area;
import com.zhonghaodi.model.Contact;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.Function;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.MySectionIndexer;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.integer;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateQuestionFragment extends Fragment implements OnClickListener,HandMessage {
	private View view;
	private GFImageButton jinzhaoBtn = null;
	private GFImageButton yuanzhaoBtn = null;
	private GFImageButton zhengtiBtn = null;
	private GFImageButton jubuBtn = null;
	private GFImageButton zhengmianBtn = null;
	private GFImageButton fanmianBtn = null;
	private MyEditText contentEt;
	private PopupWindow popupWindow;
	private View popView;
	private File currentfile;
	private GFImageButton currentGFimageButton;
	private Spinner spFenbu;
	private Spinner spQingkuang;
	private Spinner spSudu;
	private Spinner spGenxi;
	private TextView cropTextView;
	private TextView tv1;
	private TextView tv2;
	private LinearLayout spinnerLayout;
	private LinearLayout myCropsLayout;
	private int status;
	private GFHandler<CreateQuestionFragment> handler = new GFHandler<CreateQuestionFragment>(this);
	private List<Object> crops = new ArrayList<Object>();
	private CheckBox sharecheck;
	public void setStatus(int status) {
		
		if(status==0){
			jinzhaoBtn.setTitle("近照");
			yuanzhaoBtn.setTitle("远照");
			zhengtiBtn.setTitle("整体照");
			jubuBtn.setTitle("局部照");
			zhengmianBtn.setTitle("正面照");
			fanmianBtn.setTitle("反面照");
			tv1.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.VISIBLE);
			spinnerLayout.setVisibility(View.VISIBLE);
			myCropsLayout.setVisibility(View.GONE);
			contentEt.setHint("病虫害的详细描述...(200字以内)");
		}
		else if(status==1){
			jinzhaoBtn.setTitle("照片1");
			yuanzhaoBtn.setTitle("照片2");
			zhengtiBtn.setTitle("照片3");
			jubuBtn.setTitle("照片4");
			zhengmianBtn.setTitle("照片5");
			fanmianBtn.setTitle("照片6");
			tv1.setVisibility(View.GONE);
			tv2.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			myCropsLayout.setVisibility(View.GONE);
			contentEt.setHint("说点啥吧...(200字以内)");
		}
		else{
			jinzhaoBtn.setTitle("照片1");
			yuanzhaoBtn.setTitle("照片2");
			zhengtiBtn.setTitle("照片3");
			jubuBtn.setTitle("照片4");
			zhengmianBtn.setTitle("照片5");
			fanmianBtn.setTitle("照片6");
			tv1.setVisibility(View.GONE);
			tv2.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			myCropsLayout.setVisibility(View.VISIBLE);
			contentEt.setHint("说点啥吧...(200字以内)");
			loadPlantInfo();
		}
	}
	
	public boolean getcheck(){
		return sharecheck.isChecked();
	}

	public File getCurrentfile() {
		return currentfile;
	}

	public void setCurrentfile(File currentfile) {
		this.currentfile = currentfile;
	}

	public TextView getCropTextView() {
		return cropTextView;
	}

	public void setCropTextView(TextView cropTextView) {
		this.cropTextView = cropTextView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.fragment_create_question,
				container, false);
		popView = inflater.inflate(R.layout.popupwindow_camera, container,
				false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(
				getActivity(), 180), DpTransform.dip2px(getActivity(), 100));
		jinzhaoBtn = (GFImageButton) view.findViewById(R.id.jinzhao_image);
		yuanzhaoBtn = (GFImageButton) view.findViewById(R.id.yuanzhao_image);
		zhengtiBtn = (GFImageButton) view.findViewById(R.id.zhengti_image);
		jubuBtn = (GFImageButton) view.findViewById(R.id.jubu_image);
		zhengmianBtn = (GFImageButton) view.findViewById(R.id.zhengmian_image);
		fanmianBtn = (GFImageButton) view.findViewById(R.id.fanmian_image);
		
		spFenbu = (Spinner)view.findViewById(R.id.spFenbu);
		spQingkuang = (Spinner)view.findViewById(R.id.spQingkuang);
		spSudu = (Spinner)view.findViewById(R.id.spSudu);
		spGenxi = (Spinner)view.findViewById(R.id.spGenxi);
		cropTextView = (TextView)view.findViewById(R.id.mycrop_select);
		cropTextView.setOnClickListener(this);
		jinzhaoBtn.setOnClickListener(this);
		yuanzhaoBtn.setOnClickListener(this);
		zhengtiBtn.setOnClickListener(this);
		jubuBtn.setOnClickListener(this);
		zhengmianBtn.setOnClickListener(this);
		fanmianBtn.setOnClickListener(this);
		tv1 = (TextView)view.findViewById(R.id.label1);
		tv2 = (TextView)view.findViewById(R.id.label2);
		spinnerLayout = (LinearLayout)view.findViewById(R.id.spinnerLayout);
		myCropsLayout = (LinearLayout)view.findViewById(R.id.myCropsLayout);
		sharecheck = (CheckBox)view.findViewById(R.id.sharecheck);
		contentEt = (MyEditText) view.findViewById(R.id.content_edit);
		contentEt.addTextChangedListener(new TextWatcher() {

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
				CreateQuestionActivity activity = (CreateQuestionActivity) getActivity();
				if (!contentEt.getText().toString().trim().isEmpty()) {
					activity.getSendBtn().setEnabled(true);
				} else {
					activity.getSendBtn().setEnabled(false);
				}
			}
		});
		status = getActivity().getIntent().getIntExtra("status", 0);
		setStatus(status);
		// 相机按钮
		Button btnCamera = (Button) popView.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
				popupWindow.dismiss();
			}
		});
		// 相册按钮
		Button btnPhoto = (Button) popView.findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				getActivity().startActivityForResult(it, 2);
				popupWindow.dismiss();
			}
		});
		return view;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("提问、拉拉呱Fragment");
	}

	public void loadUser() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId(getActivity()));
				Message msg = handler.obtainMessage();
				msg.what=1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadPlantInfo(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getPlantInfoCropsString();
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
	public void onClick(View v) {
		if(v.getId()==R.id.mycrop_select){
			
			SpinnerPopupwindow spinnerPopupwindow = new SpinnerPopupwindow(getActivity(), cropTextView.getText().toString(), 
					crops, cropTextView,"我的作物");
			spinnerPopupwindow.showAtLocation(view.findViewById(R.id.main), 
					Gravity.CENTER, 0, 0);
			return;
			
		}
		else{
			currentGFimageButton = (GFImageButton) v;
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			} else {
				popupWindow.showAsDropDown(v,
						-DpTransform.dip2px(getActivity(), 0),
						DpTransform.dip2px(getActivity(), 0));
			}
		}		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		InputMethodManager im = (InputMethodManager) this.getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(view
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		super.onPause();
		MobclickAgent.onPageEnd("提问、拉拉呱Fragment");
	}
	
	public ArrayList<Bitmap> getImages() {
		ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
		if (jinzhaoBtn.isHasImage()) {
			arrayList.add(jinzhaoBtn.getBitmap());
		}
		if (jubuBtn.isHasImage()) {
			arrayList.add(jubuBtn.getBitmap());
		}
		if (yuanzhaoBtn.isHasImage()) {
			arrayList.add(yuanzhaoBtn.getBitmap());
		}
		if (zhengmianBtn.isHasImage()) {
			arrayList.add(zhengmianBtn.getBitmap());
		}
		if (zhengtiBtn.isHasImage()) {
			arrayList.add(zhengtiBtn.getBitmap());
		}
		if (fanmianBtn.isHasImage()) {
			arrayList.add(fanmianBtn.getBitmap());
		}
		return arrayList;
	}

	public PopupWindow getPopupWindow() {
		return popupWindow;
	}

	public void setPopupWindow(PopupWindow popupWindow) {
		this.popupWindow = popupWindow;
	}

	public GFImageButton getCurrentGFimageButton() {
		return currentGFimageButton;
	}

	public void setCurrentGFimageButton(GFImageButton currentGFimageButton) {
		this.currentGFimageButton = currentGFimageButton;
	}
	public String getContentString() {
		return contentEt.getText().toString();
	}
	public String getBhzdContent(){
		String content = "";
		
		if(spFenbu.getSelectedItemPosition()!=0){
			content += "\n病害呈"+spFenbu.getSelectedItem().toString()+"分布";
		}
		if(spQingkuang.getSelectedItemPosition()!=0){
			String ss = "";
			if(content.length()>0){
				ss="、";
			}
			else{
				ss="\n";
			}
			if(spQingkuang.getSelectedItemPosition()==1||spQingkuang.getSelectedItemPosition()==2){
				content += ss+"已有"+spQingkuang.getSelectedItem().toString()+"发生";
			}
			else{
				content += ss+spQingkuang.getSelectedItem().toString();
			}
			
		}
		
		if(spSudu.getSelectedItemPosition()!=0){
			String ss = "";
			if(content.length()>0){
				ss="、";
			}
			else{
				ss="\n";
			}
			content += ss+spSudu.getSelectedItem().toString();
		}
		
		if(spGenxi.getSelectedItemPosition()!=0){
			String ss = "";
			if(content.length()>0){
				ss="、";
			}
			else{
				ss="\n";
			}
			content += ss+"植株"+spGenxi.getSelectedItem().toString();
		}	
		
		if(!content.isEmpty())
		{
			content +="。";
		}
		
		return content;
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
//			if (msg.obj == null) {
//				GFToast.show(getActivity(), "获取失败,请稍后再试");
//				return;
//			}
//			User user = (User) GsonUtil
//					.fromJson(msg.obj.toString(), User.class);
//			crops.clear();
//			if(user.getCrops()!=null && user.getCrops().size()>0){
//				cropTextView.setText(user.getCrops().get(0).getCrop().getName());
//				cropTextView.setTag(user.getCrops().get(0).getCrop());
//				for(UserCrop userCrop:user.getCrops()){
//					crops.add(userCrop.getCrop());
//				}
//			}
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
	
}

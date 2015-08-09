package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CreateQuestionFragment extends Fragment implements OnClickListener {
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
	private RadioGroup gxGroup;
	private RadioGroup fbGroup;
	private RadioGroup nsGroup;
	private RadioGroup bhGroup;
	private RadioGroup fzGroup;

	public File getCurrentfile() {
		return currentfile;
	}

	public void setCurrentfile(File currentfile) {
		this.currentfile = currentfile;
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
		jinzhaoBtn.setTitle("近照");
		yuanzhaoBtn.setTitle("远照");
		zhengtiBtn.setTitle("整体照");
		jubuBtn.setTitle("局部照");
		zhengmianBtn.setTitle("正面照");
		fanmianBtn.setTitle("反面照");
		gxGroup = (RadioGroup)view.findViewById(R.id.radioGenxi);
		fbGroup = (RadioGroup)view.findViewById(R.id.radioFenbu);
		nsGroup = (RadioGroup)view.findViewById(R.id.radioNongshi);
		bhGroup = (RadioGroup)view.findViewById(R.id.radioBhfb);
		fzGroup = (RadioGroup)view.findViewById(R.id.radioFzsd);
		jinzhaoBtn.setOnClickListener(this);
		yuanzhaoBtn.setOnClickListener(this);
		zhengtiBtn.setOnClickListener(this);
		jubuBtn.setOnClickListener(this);
		zhengmianBtn.setOnClickListener(this);
		fanmianBtn.setOnClickListener(this);
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
				if (contentEt.getText().length() > 0) {
					activity.getSendBtn().setEnabled(true);
				} else {
					activity.getSendBtn().setEnabled(false);
				}
			}
		});
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
	public void onClick(View v) {
		currentGFimageButton = (GFImageButton) v;
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(v,
					-DpTransform.dip2px(getActivity(), 0),
					DpTransform.dip2px(getActivity(), 0));
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
		
		if(fbGroup.getCheckedRadioButtonId()!=R.id.rd_fbbqc){
			RadioButton radioButton = (RadioButton)fbGroup.findViewById(fbGroup.getCheckedRadioButtonId());
			content += "病害分布规律："+radioButton.getText()+"\n";
		}
		
		if(bhGroup.getCheckedRadioButtonId()!=R.id.rd_bhfbbqc){
			RadioButton radioButton = (RadioButton)bhGroup.findViewById(bhGroup.getCheckedRadioButtonId());
			content += "病害发生情况："+radioButton.getText()+"\n";
		}
		
		if(fzGroup.getCheckedRadioButtonId()!=R.id.rd_fzsdbqc){
			RadioButton radioButton = (RadioButton)fzGroup.findViewById(fzGroup.getCheckedRadioButtonId());
			content += "病害发展速度："+radioButton.getText()+"\n";
		}
		
		if(gxGroup.getCheckedRadioButtonId()!=R.id.rd_gxbqc){
			RadioButton radioButton = (RadioButton)gxGroup.findViewById(gxGroup.getCheckedRadioButtonId());
			content += "植株根系情况："+radioButton.getText()+"\n";
		}	
		
		if(nsGroup.getCheckedRadioButtonId()!=R.id.rd_qtcz){
			RadioButton radioButton = (RadioButton)nsGroup.findViewById(nsGroup.getCheckedRadioButtonId());
			content += "农事操作情况："+radioButton.getText()+"\n";
		}
		
		if(!content.isEmpty())
		{
			content = "\n"+content;
		}
		
		return content;
	}
}

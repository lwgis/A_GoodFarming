package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Checkobj;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.LoginUser;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.ImageUtil;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class RegisterFragment2 extends Fragment implements TextWatcher,
		HandMessage {
	private MyEditText passwordEt;
//	private MyEditText passwordEt2;
	private MyEditText aliasEt;
//	private MyEditText addressEt;
	private MyEditText tjphoneEt;
	private MyTextButton registerBtn;
	private PopupWindow popupWindow;
	private View popView;
	private File currentfile;
	private GFImageButton headGfImageButton;
	private String headImageName;
	private boolean isSending = false;
	private String auth;

	public boolean isSending() {
		return isSending;
	}

	public void setSending(boolean isSending) {
		this.isSending = isSending;
	}

	public GFImageButton getHeadGfImageButton() {
		return headGfImageButton;
	}

	public void setHeadGfImageButton(GFImageButton headGfImageButton) {
		this.headGfImageButton = headGfImageButton;
	}

	private GFHandler<RegisterFragment2> handler = new GFHandler<RegisterFragment2>(
			this);

	public PopupWindow getPopupWindow() {
		return popupWindow;
	}

	public void setPopupWindow(PopupWindow popupWindow) {
		this.popupWindow = popupWindow;
	}

	public View getPopView() {
		return popView;
	}

	public void setPopView(View popView) {
		this.popView = popView;
	}

	public File getCurrentfile() {
		return currentfile;
	}

	public void setCurrentfile(File currentfile) {
		this.currentfile = currentfile;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_resgister2, container,
				false);
		popView = inflater.inflate(R.layout.popupwindow_camera, container,
				false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(
				getActivity(), 180), DpTransform.dip2px(getActivity(), 100));
		passwordEt = (MyEditText) view.findViewById(R.id.password_edit);
//		passwordEt2 = (MyEditText) view.findViewById(R.id.password2_edit);
		aliasEt = (MyEditText) view.findViewById(R.id.alias_edit);
//		addressEt = (MyEditText) view.findViewById(R.id.address_edit);
		tjphoneEt = (MyEditText)view.findViewById(R.id.tjphone_edit);
		registerBtn = (MyTextButton) view.findViewById(R.id.register_button);
		headGfImageButton = (GFImageButton) view.findViewById(R.id.head_image);
		passwordEt.addTextChangedListener(this);
//		passwordEt2.addTextChangedListener(this);
		aliasEt.addTextChangedListener(this);
//		addressEt.addTextChangedListener(this);
		registerBtn.setEnabled(false);
		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(aliasEt.getText().toString().isEmpty()){
					GFToast.show(getActivity().getApplicationContext(),"别名不能为空");
					return;
				}

				if (passwordEt.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(), "密码不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (passwordEt.getText().toString().length()<8) {
					Toast.makeText(getActivity(), "密码不足8位",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (!headGfImageButton.isHasImage()) {
					Toast.makeText(getActivity(), "请上传头像", Toast.LENGTH_LONG)
							.show();
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						NetResponse netResponse = HttpUtil.checkAlias(aliasEt.getText().toString());
						Message numMsg = handler
								.obtainMessage();
						if(netResponse.getStatus()==1){
							numMsg.what = 3;
							numMsg.obj = netResponse.getResult();
						}
						else{
							numMsg.what = -1;
							numMsg.obj = netResponse.getMessage();
						}
						numMsg.sendToTarget();
					}
				}).start();

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
				
				Intent intent;
				if (Build.VERSION.SDK_INT < 19) {
					intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");

				} else {
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				}
				getActivity().startActivityForResult(intent, 2);
				
				popupWindow.dismiss();
			}
		});
		headGfImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow.isShowing()) {
					popupWindow.dismiss();
				} else {
					popupWindow.showAsDropDown(v,
							-DpTransform.dip2px(getActivity(), 0),
							DpTransform.dip2px(getActivity(), 0));
				}
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("注册用户");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("注册用户");
	}

	private void updateImage(){
		registerBtn.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				isSending = true;
				try {
					headImageName = ImageUtil.uploadHead(
							headGfImageButton.getBitmap(), "users");
					if(headImageName==null || headImageName.isEmpty() || headImageName.equals("error")){
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.obj = "图片上传失败";
						msg.sendToTarget();
						return;
					}
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj="注册失败";
					msg.sendToTarget();
					isSending = false;
				}
			}
		}).start();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (passwordEt.getText().length() > 0
				&& aliasEt.getText().length() > 0) {
			registerBtn.setEnabled(true);
		} else {
			registerBtn.setEnabled(false);
		}
	}
	
	public boolean isMobileNO(String mobiles) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		System.out.println(m.matches() + "---");

		return m.matches();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		final RegisterFragment2 registerFragment2 = (RegisterFragment2) object;
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getActivity(),msg.obj.toString());
			}
			break;
		// 注册失败
		case 0:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getActivity(),msg.obj.toString());
			}
			else{
				GFToast.show(getActivity(),"操作失败");
			}
			isSending = false;
			registerBtn.setEnabled(true);
			registerBtn.setText("注册");
			break;
		// 上传图片
		case 1:
			isSending = true;
			new Thread(new Runnable() {

				@Override
				public void run() {
					LoginActivity activity = (LoginActivity) registerFragment2
							.getActivity();
					User user = new User();
					user.setPassword(passwordEt.getText().toString());
					user.setPhone(activity.getPhone());
//					user.setAddress(addressEt.getText().toString());
					user.setAlias(aliasEt.getText().toString());
					user.setTjCode(tjphoneEt.getText().toString());
					user.setThumbnail(registerFragment2.headImageName.trim());
					try {
						NetResponse netResponse = HttpUtil.registerUser(user);
						Message msgUser = handler.obtainMessage();
						if(netResponse.getStatus()==1){
							msgUser.what = 2;
							msgUser.obj = netResponse.getResult();
							auth = netResponse.getAuth();
						}
						else{
							msgUser.what = -1;
							msgUser.obj = netResponse.getMessage();
						}
						msgUser.sendToTarget();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message msgUser = handler.obtainMessage();
						msgUser.what = 0;
						msgUser.sendToTarget();
						isSending = false;
					}
				}
			}).start();
			break;
		// 注册成功
		case 2:
			LoginUser loginUser = (LoginUser) GsonUtil.fromJson(
					msg.obj.toString(), LoginUser.class);
			if (loginUser.getCode() == 1) {
				GFUserDictionary.saveLoginInfo(getActivity(),loginUser.getUser(),
						registerFragment2.passwordEt.getText().toString(),registerFragment2.getActivity(),auth);
				registerFragment2.getActivity().setResult(4);
				registerFragment2.getActivity().finish();
				GFToast.show(getActivity(),"注册成功");
			} else {
				GFToast.show(getActivity(),loginUser.getMessage());
			}
			isSending = false;
			registerFragment2.registerBtn.setEnabled(true);
			registerFragment2.registerBtn.setText("注册");
			break;
		case 3:
			//验证别名的返回结果解析
			if(msg.obj!=null){
				Checkobj checkobj = (Checkobj) GsonUtil.fromJson(
						msg.obj.toString(), Checkobj.class);
				if(checkobj!=null && !checkobj.isResult()){
					updateImage();
				}
				else{
					GFToast.show(getActivity(),"昵称已经存在！");
				}
			}
			else{
				GFToast.show(getActivity(),"昵称验证失败");
			}
			break;
		default:
			break;
		}
	}

}

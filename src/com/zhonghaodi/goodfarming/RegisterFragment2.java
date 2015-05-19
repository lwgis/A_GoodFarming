package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.UUID;

import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.LoginUser;
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
	private MyEditText passwordEt2;
	private MyEditText aliasEt;
	private MyEditText addressEt;
	private MyTextButton registerBtn;
	private PopupWindow popupWindow;
	private View popView;
	private File currentfile;
	private GFImageButton headGfImageButton;
	private String headImageName;
	private boolean isSending = false;

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
		passwordEt2 = (MyEditText) view.findViewById(R.id.password2_edit);
		aliasEt = (MyEditText) view.findViewById(R.id.alias_edit);
		addressEt = (MyEditText) view.findViewById(R.id.address_edit);
		registerBtn = (MyTextButton) view.findViewById(R.id.register_button);
		headGfImageButton = (GFImageButton) view.findViewById(R.id.head_image);
		passwordEt.addTextChangedListener(this);
		passwordEt2.addTextChangedListener(this);
		aliasEt.addTextChangedListener(this);
		addressEt.addTextChangedListener(this);
		registerBtn.setEnabled(false);
		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!passwordEt.getText().toString()
						.equals(passwordEt2.getText().toString())) {
					Toast.makeText(getActivity(), "两次输入的密码不相同",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (!headGfImageButton.isHasImage()) {
					Toast.makeText(getActivity(), "请上传头像", Toast.LENGTH_LONG)
							.show();
					return;
				}
				registerBtn.setEnabled(false);
//				registerBtn.setText("注册中...");
				new Thread(new Runnable() {

					@Override
					public void run() {
						isSending = true;
						try {
							headImageName = ImageUtil.uploadImage(
									headGfImageButton.getBitmap(), "users");
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Message msg = handler.obtainMessage();
							msg.what = 0;
							msg.sendToTarget();
							isSending = false;
						}
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
				Intent it = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				getActivity().startActivityForResult(it, 2);
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
				&& passwordEt2.getText().length() > 0
				&& aliasEt.getText().length() > 0
				&& addressEt.getText().length() > 0) {
			registerBtn.setEnabled(true);
		} else {
			registerBtn.setEnabled(false);
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		final RegisterFragment2 registerFragment2 = (RegisterFragment2) object;
		switch (msg.what) {
		// 注册失败
		case 0:
			Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
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
					user.setAddress(addressEt.getText().toString());
					user.setAlias(aliasEt.getText().toString());
					user.setThumbnail(registerFragment2.headImageName.trim());
					try {
						Message msgUser = handler.obtainMessage();
						msgUser.what = 2;
						msgUser.obj = HttpUtil.registerUser(user);
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
				GFUserDictionary.saveLoginInfo(loginUser.getUser(),registerFragment2.passwordEt.getText().toString(),registerFragment2.getActivity());
				registerFragment2.getActivity().setResult(4);
				registerFragment2.getActivity().finish();
				 Toast.makeText(registerFragment2.getActivity(), "注册成功",
				 Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(registerFragment2.getActivity(), "注册失败",
						Toast.LENGTH_SHORT).show();
			}
			isSending = false;
			registerFragment2.registerBtn.setEnabled(true);
			registerFragment2.registerBtn.setText("注册");
			break;
		default:
			break;
		}
	}

}

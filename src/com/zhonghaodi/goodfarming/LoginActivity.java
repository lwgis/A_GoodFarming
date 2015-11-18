package com.zhonghaodi.goodfarming;

import java.io.File;

import com.zhonghaodi.customui.MyTextButton;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private LoginFragment loginFragment;
	private RegisterFragment1 resgiterFragment1;
	private RegisterFragment2 resgiterFragment2;
	private MyTextButton cancelBtn;
	private MyTextButton resgisterBtn;
	private TextView titleTv;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_login);
		titleTv = (TextView) findViewById(R.id.title_text);
		cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginActivity.this.setResult(0);
				finish();
			}
		});
		resgisterBtn = (MyTextButton) findViewById(R.id.register_button);
		resgisterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.remove(resgiterFragment1);
				transaction.remove(loginFragment);
				transaction.remove(resgiterFragment2);
				resgiterFragment1=null;
				loginFragment = null;
				resgiterFragment2 = null;
				transaction.commit();
				selectFragment(0);
			}
		});
		selectFragment(0);
	}
	
	

	public void selectFragment(int index) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		if (resgiterFragment1 == null) {
			resgiterFragment1 = new RegisterFragment1();
			transaction.add(R.id.content_view, resgiterFragment1);
		}
		if (loginFragment == null) {
			loginFragment = new LoginFragment();
			transaction.add(R.id.content_view, loginFragment);
		}
		if (resgiterFragment2 == null) {
			resgiterFragment2 = new RegisterFragment2();
			transaction.add(R.id.content_view, resgiterFragment2);
		}
		switch (index) {
		case 0:
			resgisterBtn.setVisibility(View.INVISIBLE);
			titleTv.setText("注册");
//			transaction.setCustomAnimations(R.anim.fragment_rightin,
//					R.anim.fragment_fadeout);
			transaction.show(resgiterFragment1);
			transaction.hide(loginFragment);
			transaction.hide(resgiterFragment2);	
			break;
		case 1:
			im.hideSoftInputFromWindow(findViewById(R.id.content_view)
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			resgisterBtn.setVisibility(View.VISIBLE);
			titleTv.setText("登录");
			transaction.setCustomAnimations(R.anim.fragment_rightin,
					R.anim.fragment_fadeout);
			transaction.hide(resgiterFragment1);
			transaction.show(loginFragment);
			transaction.hide(resgiterFragment2);
			break;
		case 2:
			resgisterBtn.setVisibility(View.INVISIBLE);
			im.hideSoftInputFromWindow(findViewById(R.id.content_view)
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			transaction.setCustomAnimations(R.anim.fragment_rightin,
					R.anim.fragment_fadeout);
			transaction.show(resgiterFragment2);
			transaction.hide(resgiterFragment1);
			transaction.hide(loginFragment);
			break;
		default:
			break;
		}
		transaction.commit();
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// 相册
			if (requestCode == 2) {

				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						String picturePath="";
						Cursor cursor = getContentResolver().query(selectedImage, null, null,
								null, null);
						String st8 = getResources().getString(R.string.cant_find_pictures);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;

							if (picturePath == null || picturePath.equals("null")) {
								Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;
							}
							resgiterFragment2.getHeadGfImageButton().setImageFilePath(
							picturePath);
						} else {
							File file = new File(selectedImage.getPath());
							if (!file.exists()) {
								Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								return;

							}
							picturePath=file.getAbsolutePath();
							resgiterFragment2.getHeadGfImageButton().setImageFilePath(
							picturePath);
						}
					}
				}
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = resgiterFragment2.getCurrentfile().getPath();
				resgiterFragment2.getHeadGfImageButton().setImageFilePath(
						imgPath);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && resgiterFragment2.isSending()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (resgiterFragment2.getPopupWindow() != null
				&& resgiterFragment2.getPopupWindow().isShowing()) {
			resgiterFragment2.getPopupWindow().dismiss();
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public void finish() {
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		super.finish();
	}
}

package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Checkobj;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class ModifyInfoActivity extends Activity implements OnClickListener,HandMessage {
	private MyEditText descEt;
	private MyEditText aliasEt;
	private MyEditText addressEt;
	private MyTextButton okBtn;
	private PopupWindow popupWindow;
	private View popView;
	private File currentfile;
	private GFImageButton headGfImageButton;
	private String headImageName;
	private User user;
	private GFHandler<ModifyInfoActivity> handler = new GFHandler<ModifyInfoActivity>(
			this);
	private boolean bitUpdate=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifyinfo);
		descEt = (MyEditText)findViewById(R.id.desc_edit);
		aliasEt = (MyEditText)findViewById(R.id.alias_edit);
		addressEt = (MyEditText)findViewById(R.id.address_edit);
		headGfImageButton = (GFImageButton)findViewById(R.id.head_image);
		popView = getLayoutInflater().inflate(R.layout.popupwindow_camera,null);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(
				this, 180), DpTransform.dip2px(this, 100));
		
		headGfImageButton.setOnClickListener(this);
		Button btnCamera = (Button) popView.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(this);
		Button btnPhoto = (Button) popView.findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(this);
		okBtn = (MyTextButton)findViewById(R.id.ok_button);
		okBtn.setOnClickListener(this);
		MyTextButton cancelButton = (MyTextButton)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		user = (User)getIntent().getSerializableExtra("user");
		if(user!=null){
			
			headGfImageButton.setImageFilePath(HttpUtil.ImageUrl+"users/small/"
							+ user.getThumbnail());
			descEt.setText(user.getDescription());
			aliasEt.setText(user.getAlias());
			addressEt.setText(user.getAddress());
		}
		
	}

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_image:
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			} else {
				popupWindow.showAsDropDown(v,
						-DpTransform.dip2px(this, 0),
						DpTransform.dip2px(this, 0));
			}
			break;
		case R.id.btnCamera:
			File fileCache = ImageOptions.getCache(this);
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			currentfile = new File(fileCache.getPath() + "/"
					+ UUID.randomUUID().toString() + ".jpg");
			if (currentfile.exists()) {
				currentfile.delete();
			}
			Uri uri = Uri.fromFile(currentfile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			this.startActivityForResult(intent, 3);
			popupWindow.dismiss();
			break;
		case R.id.btnPhoto:
			
			Intent intent1;
			if (Build.VERSION.SDK_INT < 19) {
				intent1 = new Intent(Intent.ACTION_GET_CONTENT);
				intent1.setType("image/*");

			} else {
				intent1 = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			}
			this.startActivityForResult(intent1, 2);
			
			popupWindow.dismiss();
			
			break;
		case R.id.ok_button:
			clickOkButton();
			break;
		case R.id.cancel_button:
			this.finish();
			break;
		
		default:
			break;
		}
	}
	
	private void clickOkButton(){
		if(aliasEt.getText().toString().isEmpty()){
			GFToast.show("别名不能为空");
			return;
		}
		if(addressEt.getText().toString().isEmpty()){
			GFToast.show("地址不能为空");
			return;
		}
		if(descEt.getText().toString().isEmpty()){
			GFToast.show("描述不能为空");
			return;
		}
		if(!bitUpdate&&user.getThumbnail()==null&&!headGfImageButton.isHasImage()){
			GFToast.show("请上传头像");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString= HttpUtil.checkAlias(aliasEt.getText().toString());
				Message numMsg = handler
						.obtainMessage();
				numMsg.what = 3;
				numMsg.obj = jsonString;
				numMsg.sendToTarget();
			}
		}).start();
		
	}
	
	public void uploadImage(){
		if(!bitUpdate){
			okBtn.setEnabled(false);
			update();
		}
		else if(headGfImageButton.isHasImage()){
			okBtn.setEnabled(false);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						headImageName = ImageUtil.uploadImage(
								headGfImageButton.getBitmap(), "users");
						user.setThumbnail(headImageName);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.sendToTarget();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.sendToTarget();
					}
				}
			}).start();
		}
	}
	
	public void update(){
		
		final String addString = addressEt.getText().toString();
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				user.setAddress(addString);
				user.setAlias(aliasEt.getText().toString());
				user.setDescription(descEt.getText().toString());
				try {
					Message msgUser = handler.obtainMessage();
					msgUser.what = 2;
					msgUser.obj = HttpUtil.modifyUser(user);
					msgUser.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msgUser = handler.obtainMessage();
					msgUser.what = 0;
					msgUser.sendToTarget();
				}
			}
		}).start();
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
							headGfImageButton.setImageFilePath(
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
							headGfImageButton.setImageFilePath(
							picturePath);
						}
						bitUpdate = true;
					}
				}
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = currentfile.getPath();
				headGfImageButton.setImageFilePath(
						imgPath);
			}
			
		}
	}

	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		// 注册失败
		case 0:
			GFToast.show("操作失败");
			okBtn.setEnabled(true);
			break;
		// 上传图片
		case 1:
			update();
			break;
		// 注册成功
		case 2:
			okBtn.setEnabled(true);
			try {
				User user1 = (User) GsonUtil.fromJson(msg.obj.toString(),
						User.class);
				if(user1!=null){
					GFToast.show("更新成功");
					GFUserDictionary.saveLoginInfo(user1, GFUserDictionary.getPassword(), this);
					this.finish();
				}
				else{
					GFToast.show("更新失败");
				}
			} catch (Exception e) {
				// TODO: handle exception
				GFToast.show("更新失败");
			}
			break;
		case 3:
			//验证别名的返回结果解析
			if(msg.obj!=null){
				Checkobj checkobj = (Checkobj) GsonUtil.fromJson(
						msg.obj.toString(), Checkobj.class);
				if(checkobj!=null && !checkobj.isResult()){
					uploadImage();
				}
				else{
					GFToast.show("别名已经存在！");
				}
			}
			else{
				GFToast.show("别名验证失败");
			}
			break;
		default:
			break;
		}
	}
	

}

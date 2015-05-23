package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.GFImageButton.ImageChangedListener;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Level;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.UpdateCrop;
import com.zhonghaodi.model.UpdateUser;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateNysActivity extends Activity implements TextWatcher,
		HandMessage, ImageChangedListener, OnClickListener {
	private static final int TypeUpdate = 1;
	private static final int TypeImage = 2;
	private MyTextButton selectCropBtn;
	private MyEditText descriptionEv;
	private ArrayList<Crop> selectCrops;
	private GFImageButton zhengmianBtn;
	private GFImageButton fanmianBtn;
	private TextView selectCropTv;
	private PopupWindow popupWindow;
	private View popView;
	private File currentfile;
	private MyTextButton sendBtn;
	private GFImageButton currentGFimageButton;
	private ArrayList<String> images;
	private GFHandler<UpdateNysActivity> handler=new GFHandler<UpdateNysActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_update_nys);
		MyTextButton cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		zhengmianBtn = (GFImageButton) findViewById(R.id.zhengmian_image);
		fanmianBtn = (GFImageButton) findViewById(R.id.fanmian_image);
		zhengmianBtn.setTitle("身份证正面");
		fanmianBtn.setTitle("身份证反面");
		fanmianBtn.setOnClickListener(this);
		zhengmianBtn.setOnClickListener(this);
		fanmianBtn.setImageChangedListener(this);
		zhengmianBtn.setImageChangedListener(this);
		descriptionEv = (MyEditText) findViewById(R.id.description_edit);
		descriptionEv.addTextChangedListener(this);
		selectCropTv = (TextView) findViewById(R.id.select_crop_text);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sendBtn = (MyTextButton) findViewById(R.id.send_button);
		sendBtn.setEnabled(false);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendBtn.setEnabled(false);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							String zhengmianString = ImageUtil.uploadImage(
									zhengmianBtn.getBitmap(), "users");
							String fanmianString = ImageUtil.uploadImage(
									fanmianBtn.getBitmap(), "users");
							images = new ArrayList<String>();
							images.add(zhengmianString);
							images.add(fanmianString);
							Message msg = handler.obtainMessage();
							msg.what = TypeImage;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				finish();
			}
		});
		selectCropBtn = (MyTextButton) findViewById(R.id.crop_button);
		selectCropBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(UpdateNysActivity.this,
						SelectCropActivity.class);
				if (selectCrops != null) {
					it.putParcelableArrayListExtra("crops", selectCrops);
				}
				UpdateNysActivity.this.startActivityForResult(it, 100);
			}
		});
		popView = getLayoutInflater().inflate(
				R.layout.popupwindow_camera,
				(ViewGroup) getWindow().getDecorView().findViewById(
						android.R.id.content), false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(this, 180),
				DpTransform.dip2px(this, 100));
		// 相机按钮
		Button btnCamera = (Button) popView.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File fileCache = ImageOptions.getCache(UpdateNysActivity.this);
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
				startActivityForResult(intent, 3);
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
				startActivityForResult(it, 2);
				popupWindow.dismiss();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 100) {
			selectCrops = data.getParcelableArrayListExtra("crops");
			if (selectCrops!=null&&selectCrops.size()>0) {
				String cropString = "";
				for (Crop c : selectCrops) {
					cropString = cropString + c.getName() + "  ";
				}
				selectCropTv.setText("擅长作物:\n" + cropString);
			}
			else {
				selectCropTv.setText("");
			}
			checkUi();
			return;
		}
		if (resultCode == Activity.RESULT_OK) {
			// 相册
			if (requestCode == 2) {
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				if (!uri.toString().contains("file://")) {
					Cursor cursor = this.getContentResolver().query(uri, proj,
							null, null, null);
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String imgPath = cursor.getString(column_index);
					currentGFimageButton.setImageFilePath(
							imgPath);
					cursor.close();
				} else {
					currentGFimageButton.setImageFilePath(
							uri.getPath());
				}
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = currentfile.getPath();
				currentGFimageButton.setImageFilePath(imgPath);
			}
			checkUi();
		}
	}

	private void checkUi() {
		if (descriptionEv.getText().length() > 0 && zhengmianBtn.isHasImage()
				&& fanmianBtn.isHasImage()&&selectCrops!=null&&selectCrops.size()>0) {
			sendBtn.setEnabled(true);
		} else {
			sendBtn.setEnabled(false);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void imageChanged(View v, boolean isHasImage) {
		checkUi();
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
		checkUi();

	}

	@Override
	public void onClick(View v) {
		currentGFimageButton = (GFImageButton) v;
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(v, -DpTransform.dip2px(this, 0),
					DpTransform.dip2px(this, 0));
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		UpdateNysActivity activity = (UpdateNysActivity) object;
		switch (msg.what) {
		case 0:
			Toast.makeText(activity, "提交失败", Toast.LENGTH_SHORT).show();
			activity.sendBtn.setEnabled(true);
			break;
		case TypeImage:
			final UpdateUser updateUser = new UpdateUser();
			User user = new User();
			user.setId(GFUserDictionary.getUserId());
			updateUser.setUser(user);
			updateUser.setDescription(activity.descriptionEv.getText()
					.toString());
			Level level = new Level();
			level.setId(2);
			updateUser.setLevel(level);
			ArrayList<NetImage> arrayList=new ArrayList<NetImage>();
			for (String imageNameString : images) {
				NetImage netImage=new NetImage();
				netImage.setUrl(imageNameString);
				arrayList.add(netImage);
			}
			updateUser.setAttachments(arrayList);
			ArrayList<UpdateCrop> updateCrops=new ArrayList<UpdateCrop>();
			for (Crop crop : selectCrops) {
				UpdateCrop updateCrop=new UpdateCrop();
				updateCrop.setCrop(crop);
				updateCrops.add(updateCrop);
			}
			updateUser.setCrops(updateCrops);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Message msgUpdate = handler.obtainMessage();
					if (HttpUtil.updateUser(updateUser)) {
						msgUpdate.what = TypeUpdate;
					} else {
						msgUpdate.what = 0;
					}
					msgUpdate.sendToTarget();
				}
			}).start();
			
			break;
		case TypeUpdate:
			Toast.makeText(activity, "成功提交申请", Toast.LENGTH_SHORT).show();
//			activity.finish();
			break;
		default:
			break;
		}
	}
}
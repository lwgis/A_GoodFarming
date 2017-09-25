package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.AddImageAdapter;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.ProjectImage;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.req.PlantResponseReq;
import com.zhonghaodi.view.PlantResponseView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PlantResponseActivity extends Activity implements PlantResponseView,OnClickListener {

	private MyEditText nyqEditText;
	private GridView imageGridView;
	private Button cancelBtn;
	private Button sendBtn;
	private AddImageAdapter reportAdapter;
	private List<NetImage> imgs;
	private PopupWindow mPopupWindow;
	private View popView;
	private File currentfile;
	private PlantResponseReq req;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plantresponse);
		req = new PlantResponseReq(this,this);
		req.queid = getIntent().getIntExtra("qid", 0);
		this.imageGridView=(GridView)findViewById(R.id.imageGridView);
		this.nyqEditText = (MyEditText)findViewById(R.id.content_edit);		
		this.reportAdapter=new AddImageAdapter(this,req.projectImages);
		this.imageGridView.setAdapter(reportAdapter);
		cancelBtn = (Button)findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		sendBtn = (Button)findViewById(R.id.send_button);
		sendBtn.setEnabled(false);
		sendBtn.setOnClickListener(this);
		popView = LayoutInflater.from(this)
					.inflate(R.layout.popupwindow_camera, null);
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
				if (!nyqEditText.getText().toString().trim().isEmpty()) {
					sendBtn.setEnabled(true);
				} else {
					sendBtn.setEnabled(false);
				}
			}
		});
		
		this.imageGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (reportAdapter.getItem(position)==null) {
					if(req.projectImages.size()<3){
						if (mPopupWindow==null) {
							mPopupWindow=new PopupWindow(popView,DpTransform.dip2px(PlantResponseActivity.this, 180),
									DpTransform.dip2px(PlantResponseActivity.this, 100));
						}
						if (mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						else {
							mPopupWindow.showAsDropDown(view,-DpTransform.dip2px(PlantResponseActivity.this, 0),
									DpTransform.dip2px(PlantResponseActivity.this, 0));
						}
					}
					else{
						showMessage("最多添加3张图片！");
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

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("评论赶大集");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("评论赶大集");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// 相册
			if (requestCode == 2) {
				Uri uri = data.getData();
				String imgPath="";
				 String[] proj = {MediaStore.Images.Media.DATA};
				 if (!uri.toString().contains("file://")) {
						Cursor cursor = this.getContentResolver().query(uri, proj,
								null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						imgPath = cursor.getString(column_index);
						cursor.close();
				}
				 else {
					 imgPath = uri.getPath();
				}
				 setImagePath(imgPath);
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = currentfile.getPath();
				setImagePath(imgPath);
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && req.isSending) {
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
	
	@Override
	public void finish() {
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		super.finish();
	}

	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(this, mess);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.send_button:
			req.send();
			this.finish();
			break;
		case R.id.btnCamera:
			File fileCache = ImageOptions.getCache(this);
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
			this.startActivityForResult(intent, 3);
			mPopupWindow.dismiss();
			break;
		case R.id.btnPhoto:
			Intent it = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			this.startActivityForResult(it, 2);
			mPopupWindow.dismiss();
			break;
		default:
			break;
		}
	}
	
	
	public void setImagePath(String path){
		ProjectImage projectImage = new ProjectImage();
		projectImage.setName(path);
		projectImage.setType(ProjectImage.LOCAL);
		req.projectImages.add(projectImage);
		this.reportAdapter.setImages(req.projectImages);
		this.reportAdapter.notifyDataSetChanged();
		LayoutParams lParams = this.imageGridView.getLayoutParams();
		int height = (req.projectImages.size() / 4 + 1);
		lParams.height = DpTransform.dip2px(this, 80 * height);
		this.imageGridView.setLayoutParams(lParams);
	}

	@Override
	public String getContent() {
		// TODO Auto-generated method stub
		return nyqEditText.getText().toString();
	}
	
}

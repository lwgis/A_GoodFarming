package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.zhonghaodi.adapter.AddImageAdapter;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.ProjectImage;
import com.zhonghaodi.model.Quan;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.ImageOptions;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class NyqActivity extends Activity implements HandMessage {
	private static final int TypeQuan = 1;
	private static final int TypeImage = 2;
	private static final int TypeError = -1;
	private Button nyqSend;
	private MyEditText nyqEditText;
	private GridView imageGridView;
	private AddImageAdapter reportAdapter;
	private List<NetImage> imgs;
	private ArrayList<ProjectImage> projectImages;
	private PopupWindow mPopupWindow;
	private View popView;
	private File currentfile;
	private GFHandler<NyqActivity> handler = new GFHandler<NyqActivity>(this);
	private int imageCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nyq);
		 
		 this.nyqSend=(Button)findViewById(R.id.send_button);
		 this.imageGridView=(GridView)findViewById(R.id.imageGridView);
		 this.nyqEditText = (MyEditText)findViewById(R.id.content_edit);
		 this.projectImages=new ArrayList<ProjectImage>();
		 this.reportAdapter=new AddImageAdapter(this,projectImages);
		 this.imageGridView.setAdapter(reportAdapter);
		 popView = LayoutInflater.from(NyqActivity.this)
					.inflate(R.layout.popupwindow_camera, null);
		
		 this.imageGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (NyqActivity.this.reportAdapter.getItem(position)==null) {
					if(projectImages.size()<9){
						if (mPopupWindow==null) {
							mPopupWindow=new PopupWindow(popView,DpTransform.dip2px(NyqActivity.this, 180),DpTransform.dip2px(NyqActivity.this, 100));
						}
						if (mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						else {
							mPopupWindow.showAsDropDown(view,-DpTransform.dip2px(NyqActivity.this, 0),DpTransform.dip2px(NyqActivity.this, 0));
						}
					}
					else{
						GFToast.show("最多添加9张图片！");
					}
					
				}
				
			}
		});
		 
		 Button cancelButton = (Button) findViewById(R.id.cancel_button);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					NyqActivity.this.finish();
				}
			});
		 
		 //相机按钮
		 Button btnCamera=(Button)popView.findViewById(R.id.btnCamera);
		 btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File fileCache = ImageOptions.getCache(NyqActivity.this);
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
				NyqActivity.this.startActivityForResult(intent, 3);
				mPopupWindow.dismiss();
			}
		});
		 //相册按钮
		 Button btnPhoto=(Button)popView.findViewById(R.id.btnPhoto);
		 btnPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				NyqActivity.this.startActivityForResult(it, 2);
				mPopupWindow.dismiss();
			}
		});
		 this.nyqSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = nyqEditText.getText().toString();
				if(projectImages.size()==0&&str.isEmpty()){
					GFToast.show("没有任何内容");
					return;
				}
				NyqActivity.this.nyqSend.setEnabled(false);
				NyqActivity.this.uploadImages();
			}
		});
	}
	
	public void uploadImages(){
		
		nyqSend.setEnabled(false);
		imgs = new ArrayList<NetImage>();
		if(projectImages!=null&&projectImages.size()>0){
			imageCount = 0;
			for (int i=0;i<projectImages.size();i++) {
				final int index = i;
				new Thread(new Runnable() {
					
					@Override
					public void run() {

						try {
							
							
							String imageName = ImageUtil.uploadImage(projectImages.get(index).getImage()
									, "quans");
							if(imageName==null || imageName.isEmpty() || imageName.equals("error")){
								Message msg = handler.obtainMessage();
								msg.what = TypeError;
								msg.sendToTarget();
								return;
							}
							NetImage netImage = new NetImage();
							netImage.setUrl(imageName.trim());
							imgs.add(netImage);
							Message msg = handler.obtainMessage();
							msg.what = TypeImage;
							msg.sendToTarget();
							
							
						} catch (Exception e) {
							
							e.printStackTrace();
							Message msg=new Message();
							msg.what=TypeError;
							handler.sendMessage(msg);
							
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Message msg=new Message();
							msg.what=TypeError;
							handler.sendMessage(msg);
						}
						
					}
				}).start();
			} 
		}
		else{
			sendQuan();
		}
		
		this.finish();
	}
	
	private void sendQuan(){
		final Quan quan = new Quan();
		quan.setContent(nyqEditText.getText().toString());
		quan.setAttachments(imgs);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpUtil.sendQuan(GFUserDictionary.getUserId(),quan);
					Message msg = handler.obtainMessage();
					msg.what = TypeQuan;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
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
				ProjectImage projectImage = new ProjectImage();
				projectImage.setName(imgPath);
				projectImage.setType(ProjectImage.LOCAL);
				projectImages.add(projectImage);
				this.reportAdapter.setImages(projectImages);
				this.reportAdapter.notifyDataSetChanged();
				LayoutParams lParams = this.imageGridView.getLayoutParams();
				int height = (this.projectImages.size() / 4 + 1);
				lParams.height = DpTransform.dip2px(this, 80 * height);
				this.imageGridView.setLayoutParams(lParams);
			}
			if (requestCode == 3) {
				ProjectImage projectImage = new ProjectImage();
				projectImage.setName(currentfile.getPath());
				projectImage.setType(ProjectImage.LOCAL);
				projectImages.add(projectImage);
				this.reportAdapter.setImages(projectImages);
				this.reportAdapter.notifyDataSetChanged();
				LayoutParams lParams = this.imageGridView.getLayoutParams();
				int height = (this.projectImages.size() / 4 + 1);
				lParams.height = DpTransform.dip2px(this, 80 * height);
				this.imageGridView.setLayoutParams(lParams);
			}
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void finish() {
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		super.finish();
	}
	
	/**
	 * 发送刷新广播
	 */
	public void SendRefreshBroadcase()
    {
    	Intent in = new Intent();
        in.setAction("refresh");
        in.addCategory(Intent.CATEGORY_DEFAULT);
        this.sendBroadcast(in);
    }

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final NyqActivity activity = (NyqActivity) object;
		switch (msg.what) {
		case TypeError:
			nyqSend.setEnabled(true);
			GFToast.show("发送失败");
			break;
		case TypeImage:
			imageCount++;
			if(imageCount==projectImages.size()){
				sendQuan();
			}	
			break;		
		case TypeQuan:
			GFToast.show("发送成功");
			SendRefreshBroadcase();
			break;
		default:
			break;
		}
	}

}

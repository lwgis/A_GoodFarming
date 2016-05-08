package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.goodfarming.CreateQuestionActivity.QuestionLocationListenner;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.ProjectImage;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class CreatePlantActivity extends Activity implements HandMessage{

	private  final int TypeQuestion = 1;
	private  final int TypeImage = 2;
	private  final int TypeNoImage = 3;
	private  final int TypeError = -1;
	private SelectCropFragment selectCropFragment = null;
	private CreadPlantFragment createPlantFragment = null;
	private MyTextButton sendBtn;
	private ArrayList<NetImage> netImages;
	private GFHandler<CreatePlantActivity> handler = new GFHandler<CreatePlantActivity>(this);
	private ExecutorService executorService = Executors.newFixedThreadPool(4);
	private int imageCount;
	private boolean isSending;
	private TextView titleTv = null;
	private int cropId;
	private double x;
	private double y;
	// 定位相关
	LocationClient mLocClient;
	public QuestionLocationListenner myListener = new QuestionLocationListenner();
	public int getCropId() {
		return cropId;
	}

	public void setCropId(int cropId) {
		this.cropId = cropId;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_create_question);
		titleTv = (TextView) findViewById(R.id.title_text);
		isSending = false;
		netImages = new ArrayList<NetImage>();
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CreatePlantActivity.this.finish();
			}
		});
		sendBtn = (MyTextButton) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isSending = true;
				sendBtn.setEnabled(false);
				netImages = new ArrayList<NetImage>();
				if (createPlantFragment.getProjectImages()!=null&&createPlantFragment.getProjectImages().size()>0) {
					imageCount = 0;
					for (int i = 0; i <createPlantFragment.getProjectImages()
							.size(); i++) {
						final int index = i;
						executorService.submit(new Runnable() {
	                        public void run() {
	                        	try {
									String imageName;
									imageName = ImageUtil.uploadImage(createPlantFragment.getProjectImages().get(index).getImage(),
											"plant");
									if(imageName==null || imageName.isEmpty() || imageName.equals("error")){
										Message msg = handler.obtainMessage();
										msg.what = TypeError;
										msg.sendToTarget();
										return;
									}
									NetImage netImage = new NetImage();
									netImage.setUrl(imageName.trim());
									netImages.add(netImage);
									Message msg = handler.obtainMessage();
									msg.what = TypeImage;
									msg.obj = imageName.trim();
									msg.sendToTarget();
								} catch (Throwable e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									isSending = false;
									Message msg = handler.obtainMessage();
									msg.what = TypeError;
									msg.obj = e.getMessage();
									msg.sendToTarget();
								}
	                        }
	                });
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = TypeNoImage;
					msg.sendToTarget();
				}
				finish();
			}
		});
		location();
		sendBtn.setEnabled(false);
		showFragment(0);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	public void showFragment(int index) {
		FragmentTransaction transation = this.getFragmentManager()
				.beginTransaction();
		if (selectCropFragment == null) {
			selectCropFragment = new SelectCropFragment();
			transation.add(R.id.content_view, selectCropFragment);
		}
		if (createPlantFragment == null) {
			createPlantFragment = new CreadPlantFragment();
			transation.add(R.id.content_view, createPlantFragment);
		}
		switch (index) {
		case 0:
			transation.show(selectCropFragment);
			setTitle("选择农作物种类");
			transation.hide(createPlantFragment);
			break;
		case 1:
			transation.setCustomAnimations(R.anim.fragment_rightin,
					R.anim.fragment_fadeout);
			transation.show(createPlantFragment);
			transation.hide(selectCropFragment);
			break;
		default:
			break;
		}
		transation.commit();
	}
	public void setTitle(String title) {
		titleTv.setText(title);
	}
	
	private void location() {
		
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class QuestionLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			x=location.getLongitude();
			y=location.getLatitude();

			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
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
				 createPlantFragment.setImagePath(imgPath);
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = createPlantFragment.getCurrentfile()
						.getPath();
				createPlantFragment.setImagePath(imgPath);
			}
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (createPlantFragment.getPopupWindow() != null
				&& createPlantFragment.getPopupWindow().isShowing()) {
			createPlantFragment.getPopupWindow().dismiss();
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isSending) {
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
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case TypeImage:
			imageCount++;
			if (imageCount == createPlantFragment.getProjectImages().size()) {
				final Question question = new Question();
				String content = PublicHelper.TrimRight(createPlantFragment.getContent());
				question.setContent(content);
				User writer = new User();
				writer.setId(GFUserDictionary.getUserId(getApplicationContext()));
				question.setWriter(writer);
				
				Crop crop = new Crop();
				crop.setId(this.cropId);
				//设置分享类别
				Crop crop2 = (Crop)createPlantFragment.getCropTextView().getTag();
				question.setCate(crop2);					
				//设置作物类别
				question.setCrop(crop);
				
				question.setInform("0");
				question.setAttachments(netImages);
				if(x>=73&&x<=136){
					question.setX(x);
				}
				if(y>=3&&y<=54){
					question.setY(y);
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							HttpUtil.sendPlant(question);
							Message msg = handler.obtainMessage();
							msg.what = TypeQuestion;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							isSending = false;
						}
					}
				}).start();
			}
			break;
		case TypeNoImage:
			final Question question = new Question();
			String content = PublicHelper.TrimRight(createPlantFragment.getContent());
			question.setContent(content);
			User writer = new User();
			writer.setId(GFUserDictionary.getUserId(getApplicationContext()));
			question.setWriter(writer);
			Crop crop = new Crop();
			crop.setId(cropId);
			//设置分享类别
			Crop crop2 = (Crop)createPlantFragment.getCropTextView().getTag();
			question.setCate(crop2);			
			//设置作物类别
			question.setCrop(crop);
			question.setInform("0");
			if(x>=73&&x<=136){
				question.setX(x);
			}
			if(y>=3&&y<=54){
				question.setY(y);
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						HttpUtil.sendPlant(question);
						Message msg = handler.obtainMessage();
						msg.what = TypeQuestion;
						msg.sendToTarget();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			break;
		case TypeQuestion:
			isSending = false;
			GFToast.show(getApplicationContext(),"发送成功");
			break;
		case TypeError:
			if(msg.obj==null){
				Log.d("uploadimageError", "空");
			}
			else{
				Log.d("uploadimageError", msg.obj.toString());
			}
			GFToast.show(getApplicationContext(),"发送失败");
			break;
		default:
			break;
		}
	}

	public MyTextButton getSendBtn() {
		return sendBtn;
	}

	public void setSendBtn(MyTextButton sendBtn) {
		this.sendBtn = sendBtn;
	}

}

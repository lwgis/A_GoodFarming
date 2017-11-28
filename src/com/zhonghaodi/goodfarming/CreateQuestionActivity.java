package com.zhonghaodi.goodfarming;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.ShareObj;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class CreateQuestionActivity extends Activity implements HandMessage {
	private  final int TypeQuestion = 1;
	private  final int TypeImage = 2;
	private  final int TypeNoImage = 3;
	private  final int TypeError = -1;
	private SelectCropFragment selectCropFragment = null;
	private CreateQuestionFragment createQuestionFragment = null;
	private TextView titleTv = null;
	private MyTextButton sendBtn;
	private NetImage[] netImages;
	private GFHandler<CreateQuestionActivity> handler = new GFHandler<CreateQuestionActivity>(
			this);
	private ExecutorService executorService = Executors.newFixedThreadPool(4);
	private int imageCount;
	private boolean isSending;
	private boolean isshare;
	private City area;
	private Crop crop;
	private double x;
	private double y;
	// 定位相关
	private LocationClient mLocClient;
	public QuestionLocationListenner myListener = new QuestionLocationListenner();
	private int status;
	private Question question;
	
	public MyTextButton getSendBtn() {
		return sendBtn;
	}

	public void setSendBtn(MyTextButton sendBtn) {
		this.sendBtn = sendBtn;
	}

	public Crop getCrop() {
		return crop;
	}

	public void setCrop(Crop crop) {
		this.crop = crop;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_create_question);
		isSending = false;
		titleTv = (TextView) findViewById(R.id.title_text);
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CreateQuestionActivity.this.finish();
			}
		});
		sendBtn = (MyTextButton) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String phase = createQuestionFragment.getPhase();
				if(phase.isEmpty()&&status==0){
					GFToast.show(CreateQuestionActivity.this, "请选择作物生长阶段");
					return;
				}
				
				isSending = true;
				sendBtn.setEnabled(false);
				netImages = new NetImage[createQuestionFragment.getImages().size()];
				if (createQuestionFragment.getImages().size() > 0) {
					imageCount = 0;
					for (int i = 0; i < createQuestionFragment.getImages()
							.size(); i++) {
						final int index = i;
						executorService.submit(new Runnable() {
	                        public void run() {
	                        	try {
									String imageName;
									if (status==0||status==1) {
										if(index==0 && createQuestionFragment.getcheck()){
											UILApplication.sharebit = createQuestionFragment.getImages().get(index);
										}
										imageName = ImageUtil.uploadImage(createQuestionFragment.getImages().get(index),
												"questions");
									}
									else{
										imageName = ImageUtil.uploadImage(createQuestionFragment.getImages().get(index),
												"plant");
									}
									if(imageName==null || imageName.isEmpty() || imageName.equals("error")){
										Message msg = handler.obtainMessage();
										msg.what = TypeError;
										msg.sendToTarget();
										return;
									}
									NetImage netImage = new NetImage();
									netImage.setId(index);
									netImage.setUrl(imageName.trim());
									netImages[index] = netImage;
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
		status = getIntent().getIntExtra("status", 0);
		if(status==0){
			showFragment(0);
		}
		else if(status==1){
			//不选择分类直接进入提问页,话题分类强行设置为其他
			crop = new Crop();
			crop.setId(99);
			int aid = GFAreaUtil.getCity(this);
			if(aid!=0){
				area = new City();
				area.setId(aid);
			}
			showFragment(3);
			setTitle("创建话题");
		}
		else{
			showFragment(2);
		}
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
		}
		if (createQuestionFragment == null) {
			createQuestionFragment = new CreateQuestionFragment();
		}
		switch (index) {
		case 0:
			if (selectCropFragment == null) {
				selectCropFragment = new SelectCropFragment();
			}
			transation.replace(R.id.content_view, selectCropFragment);
			setTitle("选择农作物种类");
			break;
		case 1:
			if (selectCropFragment == null) {
				selectCropFragment = new SelectCropFragment();
			}
			transation.replace(R.id.content_view, selectCropFragment);
			setTitle("选择话题");
			break;
		case 2:
			if (selectCropFragment == null) {
				selectCropFragment = new SelectCropFragment();
			}
			transation.replace(R.id.content_view, selectCropFragment);
			setTitle("选择农作物种类");
			break;
		case 3:
			if (createQuestionFragment == null) {
				createQuestionFragment = new CreateQuestionFragment();
			}
			transation.replace(R.id.content_view, createQuestionFragment);
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
//			x=118.7139351318554;
//			y=36.80689424778121;
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
				 String[] proj = {MediaStore.Images.Media.DATA};
				 if (!uri.toString().contains("file://")) {
						Cursor cursor = this.getContentResolver().query(uri, proj,
								null, null, null);
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String imgPath = cursor.getString(column_index);
						createQuestionFragment.getCurrentGFimageButton()
								.setImageFilePath(imgPath);
						cursor.close();
				}
				 else {
					 createQuestionFragment.getCurrentGFimageButton().setImageFilePath(uri.getPath());
				}
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = createQuestionFragment.getCurrentfile().getPath();
				createQuestionFragment.getCurrentGFimageButton()
						.setImageFilePath(imgPath);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (createQuestionFragment.getPopupWindow() != null
				&& createQuestionFragment.getPopupWindow().isShowing()) {
			createQuestionFragment.getPopupWindow().dismiss();
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
		final CreateQuestionActivity activity = (CreateQuestionActivity) object;
		switch (msg.what) {
		case TypeImage:
			activity.imageCount++;
			if (activity.imageCount == activity.createQuestionFragment
					.getImages().size()) {
				question = new Question();
				isshare = createQuestionFragment.getcheck();
				String content = PublicHelper.TrimRight(activity.createQuestionFragment
						.getContentString());
				String zdContent = activity.createQuestionFragment
						.getBhzdContent();
				String phase = activity.createQuestionFragment.getPhase();
				if(!zdContent.isEmpty()){
					content = content+zdContent;
				}
				
				question.setContent(content);
				question.setPhase(phase);
				User writer = new User();
				writer.setId(GFUserDictionary.getUserId(getApplicationContext()));
				question.setWriter(writer);
				if(area!=null){
					question.setZone(area.getId());
				}
//				Crop crop = new Crop();
//				crop.setId(activity.cropId);
				if(status==0||status==1){
					question.setCrop(crop);
				}
				else{
					//设置分享类别
					Crop crop2 = (Crop)activity.createQuestionFragment.getCropTextView().getTag();
					question.setCate(crop2);					
					//设置作物类别
					question.setCrop(crop);
				}
				
				question.setInform("0");
				question.setTime("2015-04-08 00:00:00");
				question.setAttachments(Arrays.asList(netImages));
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
							NetResponse response;
							if(status==0){
								response = HttpUtil.sendQuestion(question);
								MobclickAgent.onEvent(CreateQuestionActivity.this, UmengConstants.ASK_DISEASE_ID);
							}
							else if(status==1){
								response = HttpUtil.sendGossip(question);
								MobclickAgent.onEvent(CreateQuestionActivity.this, UmengConstants.ASK_GOSSIP_ID);
							}
							else{
								response = HttpUtil.sendPlant(question);
							}
							Message msg = activity.handler.obtainMessage();
							msg.what = TypeQuestion;
							msg.obj = response;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							activity.isSending = false;
						}
					}
				}).start();
			}
			break;
		case TypeNoImage:
			question = new Question();
			isshare = createQuestionFragment.getcheck();
			String content = PublicHelper.TrimRight(activity.createQuestionFragment
					.getContentString());
			String zdContent = activity.createQuestionFragment
					.getBhzdContent();
			if(!zdContent.isEmpty()){
				content = content+zdContent;
			}
			
			String phase = activity.createQuestionFragment.getPhase();
			question.setContent(content);
			question.setPhase(phase);
			User writer = new User();
			writer.setId(GFUserDictionary.getUserId(getApplicationContext()));
			question.setWriter(writer);
			if(area!=null){
				question.setZone(area.getId());
			}
//			Crop crop = new Crop();
//			crop.setId(activity.cropId);
			if(status==0||status==1){
				question.setCrop(crop);
			}
			else{
				//设置分享类别
				Crop crop2 = (Crop)activity.createQuestionFragment.getCropTextView().getTag();
				question.setCate(crop2);			
				//设置作物类别
				question.setCrop(crop);
			}
			question.setInform("0");
			question.setTime("2015-04-08 00:00:00");
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
						NetResponse response;
						if(status==0){
							response = HttpUtil.sendQuestion(question);
							MobclickAgent.onEvent(CreateQuestionActivity.this, UmengConstants.ASK_DISEASE_ID);
						}
						else if(status==1){
							response = HttpUtil.sendGossip(question);
							MobclickAgent.onEvent(CreateQuestionActivity.this, UmengConstants.ASK_GOSSIP_ID);
						}
						else{
							response = HttpUtil.sendPlant(question);
						}
						Message msg = activity.handler.obtainMessage();
						msg.what = TypeQuestion;
						msg.obj=response;
						msg.sendToTarget();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			break;
		case TypeQuestion:
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"发送失败");
			}
			else{
				NetResponse response = (NetResponse)msg.obj;
				if(response.getStatus()!=1){
					GFToast.show(getApplicationContext(),response.getMessage());
				}
				else{
					GFToast.show(getApplicationContext(),"发送成功");
					if(isshare){
						Intent in= new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("question", question);
						in.putExtras(bundle);
						ShareObj shareObj= (ShareObj) GsonUtil
								.fromJson(response.getResult(), ShareObj.class);
						if(shareObj==null || shareObj.getUrl().isEmpty()){
							return;
						}
						in.putExtra("url", shareObj.getUrl());
						if(status==0){
							in.putExtra("folder", "question");
						}else if(status==1){
							in.putExtra("folder", "gossip");
						}else{
							in.putExtra("folder", "plantinfo");
						}
						in.setAction("sharequestion");
						sendBroadcast(in);
					}
				}
			}
			activity.isSending = false;
			
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

}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;

import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageUtil;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class CreateQuestionActivity extends Activity implements HandMessage {
	private static final int TypeQuestion = 1;
	private static final int TypeImage = 2;
	private static final int TypeNoImage = 3;
	private SelectCropFragment selectCropFragment = null;
	private CreateQuestionFragment createQuestionFragment = null;
	private TextView titleTv = null;
	private MyTextButton sendBtn;
	private ArrayList<NetImage> netImages;
	private GFHandler<CreateQuestionActivity> handler=new GFHandler<CreateQuestionActivity>(this) ;
	private int imageCount;
	private boolean isSending;

	public MyTextButton getSendBtn() {
		return sendBtn;
	}

	public void setSendBtn(MyTextButton sendBtn) {
		this.sendBtn = sendBtn;
	}

	private int cropId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_create_question);
		isSending = false;
		netImages = new ArrayList<NetImage>();
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
				InputMethodManager im = (InputMethodManager) CreateQuestionActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(
						findViewById(R.id.content_view).getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				isSending = true;
				sendBtn.setEnabled(false);
				netImages = new ArrayList<NetImage>();
				if (createQuestionFragment.getImages().size() > 0) {
					imageCount = 0;
					for (int i = 0; i < createQuestionFragment.getImages()
							.size(); i++) {
						// netImage.setUrl(createQuestionFragment.getImages().get(i));
						final int index = i;
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									String imageName = ImageUtil.uploadImage(
											createQuestionFragment.getImages()
													.get(index), "questions");
									NetImage netImage = new NetImage();
									netImage.setUrl(imageName.trim());
									netImages.add(netImage);
									Message msg = handler.obtainMessage();
									msg.what = TypeImage;
									msg.sendToTarget();
								} catch (Throwable e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									isSending=false;
								}
							}
						}).start();
					}
				} else {
					Message msg = handler.obtainMessage();
					msg.what = TypeNoImage;
					msg.sendToTarget();
				}
			}
		});
		sendBtn.setEnabled(false);
		showFragment(0);
	}

	public void showFragment(int index) {
		FragmentTransaction transation = this.getFragmentManager()
				.beginTransaction();
		if (selectCropFragment == null) {
			selectCropFragment = new SelectCropFragment();
			transation.add(R.id.content_view, selectCropFragment);
		}
		if (createQuestionFragment == null) {
			createQuestionFragment = new CreateQuestionFragment();
			transation.add(R.id.content_view, createQuestionFragment);
		}
		switch (index) {
		case 0:
			transation.show(selectCropFragment);
			setTitle("选择农作物种类");
			transation.hide(createQuestionFragment);
			break;
		case 1:
			transation.setCustomAnimations(R.anim.fragment_rightin,R.anim.fragment_fadeout);
			transation.show(createQuestionFragment);
			transation.hide(selectCropFragment);
		default:
			break;
		}
		transation.commit();
	}

	public void setTitle(String title) {
		titleTv.setText(title);
	}

	public int getCropId() {
		return cropId;
	}

	public void setCropId(int cropId) {
		this.cropId = cropId;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// 相册
			if (requestCode == 2) {
				Uri uri = data.getData();
				Cursor cursor = this.getContentResolver().query(uri, null,
						null, null, null);
				cursor.moveToFirst();
				String imgPath = cursor.getString(1);
				createQuestionFragment.getCurrentGFimageButton()
						.setImageFilePath(imgPath);
				cursor.close();
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = createQuestionFragment.getCurrentfile()
						.getPath();
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
	public void handleMessage(Message msg,Object object) {
		final CreateQuestionActivity activity =(CreateQuestionActivity)object;
		switch (msg.what) {
		case TypeImage:
			activity.imageCount++;
			if (activity.imageCount == activity.createQuestionFragment
					.getImages().size()) {
				final Question question = new Question();
				question.setContent(activity.createQuestionFragment
						.getContentString());
				User writer = new User();
				writer.setId(GFUserDictionary.getUserId());
				question.setWriter(writer);
				Crop crop = new Crop();
				crop.setId(activity.cropId);
				question.setCrop(crop);
				question.setInform("0");
				question.setTime("2015-04-08 00:00:00");
				question.setAttachments(activity.netImages);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							HttpUtil.sendQuestion(question);
							Message msg = activity.handler.obtainMessage();
							msg.what = TypeQuestion;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							activity.isSending=false;
						}
					}
				}).start();
			}
			break;
		case TypeNoImage:
			final Question question = new Question();
			question.setContent(activity.createQuestionFragment
					.getContentString());
			User writer = new User();
			writer.setId(GFUserDictionary.getUserId());
			question.setWriter(writer);
			Crop crop = new Crop();
			crop.setId(activity.cropId);
			question.setCrop(crop);
			question.setInform("0");
			question.setTime("2015-04-08 00:00:00");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						HttpUtil.sendQuestion(question);
						Message msg = activity.handler.obtainMessage();
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
			activity.isSending = false;
			activity.finish();
			break;
		default:
			break;
		}		
	}

}

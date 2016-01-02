package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HoldChatPhoto;
import com.zhonghaodi.customui.HoldChatTextMessage;
import com.zhonghaodi.customui.HoldChatVoice;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.easemob.ShowBigImage;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageCache;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.networking.LoadImageTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity implements TextWatcher, HandMessage,
		EMEventListener, OnClickListener {
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int LOAD_MORE = 18;
	public static final String IMAGE_DIR = "chat/image/";
	private TextView titleTv;
	private View moreView;
	private MyEditText chatEv;
	private Button moreBtn;
	private MyTextButton sendMessageBtn;
	private PullToRefreshListView pullToRefreshList;
	private InputMethodManager manager;
	private Button voiceBtn;
	private Button keyboardBtn;
	private ImageView clearBtn;
	private View speakView;
	private String userName;
	private String thumbnail;
	private EMConversation emConversation;
	public ChatAdapter adapter = new ChatAdapter();
	private VoiceRecorder voiceRecorder;
	private MediaPlayer mediaPlayer = null;
	private boolean isPlaying = false;
	private ImageView currentPlayIv;
	private AnimationDrawable voiceAnimation = null;
	private Drawable[] micImages;
	private ImageView micImage;
	private View recordingContainer;
	private TextView recordingHint;
	private LinearLayout bottomLayout;
	private File cameraFile;
	private GFHandler<ChatActivity> micImageHandler = new GFHandler<ChatActivity>(
			this);
	private GFHandler<ChatActivity> handler = new GFHandler<ChatActivity>(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_chat);
		MyTextButton cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		cancelBtn.setText("<返回");
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		clearBtn = (ImageView)findViewById(R.id.clear_button);
		clearBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupDialog();
			}
		});
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };
		titleTv = (TextView) findViewById(R.id.title_text);
		moreView = findViewById(R.id.more_view);
		chatEv = (MyEditText) findViewById(R.id.chat_edit);
		moreBtn = (Button) findViewById(R.id.more_button);
		sendMessageBtn = (MyTextButton) findViewById(R.id.send_meassage_button);
		voiceBtn = (Button) findViewById(R.id.voice_button);
		keyboardBtn = (Button) findViewById(R.id.keyboard_button);
		speakView = findViewById(R.id.speak_view);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingContainer = findViewById(R.id.recording_container);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		pullToRefreshList = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		bottomLayout = (LinearLayout)findViewById(R.id.bottom_view);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		chatEv.addTextChangedListener(this);
		userName = getIntent().getStringExtra("userName");

		if(userName!=null&&userName.equals("种好地团队")){

			bottomLayout.setVisibility(View.GONE);
		}
		thumbnail = getIntent().getStringExtra("thumbnail");
		titleTv.setText(getIntent().getStringExtra("title")!=null?getIntent().getStringExtra("title"):"");
		emConversation = EMChatManager.getInstance().getConversation(userName);
		emConversation.resetUnreadMsgCount();
		pullToRefreshList.setAdapter(adapter);
		refreshListView();
		sendMessageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendText(chatEv.getText().toString());
			}
		});
		voiceRecorder = new VoiceRecorder(micImageHandler);
		speakView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					v.setPressed(true);
					voiceRecorder.startRecording(null, userName,
							getApplicationContext());
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText("手指上滑，取消发送");
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					break;
				case MotionEvent.ACTION_MOVE:
					if (event.getY() < 0) {
						recordingHint.setText("松开手指，取消发送");
						recordingHint
								.setBackgroundResource(R.drawable.recording_text_hint_bg);
					} else {
						recordingHint.setText("手指上滑，取消发送");
						recordingHint.setBackgroundColor(Color.TRANSPARENT);
					}
					break;
				case MotionEvent.ACTION_UP:
					v.setPressed(false);
					recordingContainer.setVisibility(View.INVISIBLE);
					if (event.getY() < 0) {
						voiceRecorder.discardRecording();
						return true;
					}
					int length = voiceRecorder.stopRecoding();
					if (length > 0) {
						sendVoice(voiceRecorder.getVoiceFilePath(),
								voiceRecorder.getVoiceFileName(userName),
								Integer.toString(length), false);
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

		pullToRefreshList
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						final List<EMMessage> msgs = emConversation
								.getAllMessages();
						int msgCount = msgs != null ? msgs.size() : 0;
						if (msgCount < emConversation.getAllMsgCount()
								&& msgCount < 20) {
							String msgId = null;
							if (msgs != null && msgs.size() > 0) {
								msgId = msgs.get(0).getMsgId();
							}
							emConversation.loadMoreMsgFromDB(msgId, 20);
						}
						Message msg = handler.obtainMessage();
						msg.what = LOAD_MORE;
						msg.sendToTarget();
					}
				});

		pullToRefreshList.getRefreshableView().setOnTouchListener(
				new OnTouchListener() {

					@SuppressLint("ClickableViewAccessibility")
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						hideKeyboard();
						return false;
					}
				});
		EMChatManager
				.getInstance()
				.registerEventListener(
						this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
	}

	public void refreshListView() {
		if (emConversation == null || emConversation.getAllMsgCount() == 0) {
			return;
		}
		pullToRefreshList.getRefreshableView().setSelection(
				emConversation.getAllMsgCount() - 1);
	}

	public void more(View view) {
		if (moreView.getVisibility() == View.GONE) {
			hideKeyboard();
			moreView.setVisibility(View.VISIBLE);
		} else {
			moreView.setVisibility(View.GONE);
		}
	}
	
	private void popupDialog(){
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setText("确定");
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				EMChatManager.getInstance().clearConversation(userName);
				adapter.notifyDataSetChanged();
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setText("取消");
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
        titleView.setText("提示");
        contentView.setText("确定要清空对话吗？");
        dialog.show();
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {

		// if (getWindow().getAttributes().softInputMode !=
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
		// if (getCurrentFocus() != null)
		// manager.hideSoftInputFromWindow(getCurrentFocus()
		// .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		// }

		manager.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (!TextUtils.isEmpty(s)) {
			moreBtn.setVisibility(View.GONE);
			sendMessageBtn.setVisibility(View.VISIBLE);
		} else {
			moreBtn.setVisibility(View.VISIBLE);
			sendMessageBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		if (moreView.getVisibility() == View.VISIBLE) {
			moreView.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				refreshListView();
			}
		}, 300);
	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		chatEv.setVisibility(View.GONE);
		moreView.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		sendMessageBtn.setVisibility(View.GONE);
		moreBtn.setVisibility(View.VISIBLE);
		voiceBtn.setVisibility(View.GONE);
		keyboardBtn.setVisibility(View.VISIBLE);
		speakView.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		chatEv.setVisibility(View.VISIBLE);
		moreView.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		voiceBtn.setVisibility(View.VISIBLE);
		keyboardBtn.setVisibility(View.GONE);
		chatEv.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		speakView.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		if (TextUtils.isEmpty(chatEv.getText())) {
			moreBtn.setVisibility(View.VISIBLE);
			sendMessageBtn.setVisibility(View.GONE);
		} else {
			moreBtn.setVisibility(View.GONE);
			sendMessageBtn.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onDestroy() {
		EMChatManager.getInstance().unregisterEventListener(this);
		if (isPlaying) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public void finish() {
		this.setResult(2);
		super.finish();
	}

	public class ChatAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return emConversation == null ? 0 : emConversation.getMsgCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return emConversation.getMessage(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 6;
		}

		@Override
		public int getItemViewType(int position) {
			EMMessage message = emConversation.getMessage(position);
			// 文本
			if (message.getType() == Type.TXT) {
				if (message.direct == Direct.SEND) {
					return 0;
				}
				return 1;
			}
			// 语音
			if (message.getType() == Type.VOICE) {
				if (message.direct == Direct.SEND) {
					return 2;
				}
				return 3;
			}
			if (message.getType() == Type.IMAGE) {
				if (message.direct == Direct.SEND) {
					return 4;
				}
				return 5;
			}
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HoldChatTextMessage holdRightText;
			HoldChatTextMessage holdLeftText;
			HoldChatVoice holdRightVoice;
			HoldChatVoice holdLeftVoice;
			HoldChatPhoto holdLeftPhoto;
			HoldChatPhoto holdRightPhoto;
			EMMessage message = emConversation.getMessage(position);
			if (convertView == null) {
				// 文本
				if (message.getType() == Type.TXT) {
					if (message.direct == Direct.SEND) {
						convertView = getLayoutInflater().inflate(
								R.layout.cell_chat_text_right, parent, false);
						holdRightText = new HoldChatTextMessage(convertView);
						convertView.setTag(holdRightText);
					} else {
						convertView = getLayoutInflater().inflate(
								R.layout.cell_chat_text_left, parent, false);
						holdLeftText = new HoldChatTextMessage(convertView);
						convertView.setTag(holdLeftText);
					}
				}
				// 语音
				if (message.getType() == Type.VOICE) {
					if (message.direct == Direct.SEND) {
						convertView = getLayoutInflater().inflate(
								R.layout.cell_chat_voice_right, parent, false);
						holdRightVoice = new HoldChatVoice(convertView);
						convertView.setTag(holdRightVoice);
					} else {
						convertView = getLayoutInflater().inflate(
								R.layout.cell_chat_voice_left, parent, false);
						holdLeftVoice = new HoldChatVoice(convertView);
						convertView.setTag(holdLeftVoice);
					}
				}
				// 图片
				if (message.getType() == Type.IMAGE) {
					if (message.direct == Direct.SEND) {
						convertView = getLayoutInflater().inflate(
								R.layout.cell_chat_image_right, parent, false);
						holdLeftPhoto = new HoldChatPhoto(convertView);
						convertView.setTag(holdLeftPhoto);
					} else {
						convertView = getLayoutInflater().inflate(
								R.layout.cell_chat_image_left, parent, false);
						holdRightPhoto = new HoldChatPhoto(convertView);
						convertView.setTag(holdRightPhoto);
					}
				}
			}
			switch (getItemViewType(position)) {
			// 文本
			case 0:
				holdRightText = (HoldChatTextMessage) convertView.getTag();
				TextMessageBody rightBody = (TextMessageBody) message.getBody();
				holdRightText.contentTv.setText(rightBody.getMessage());
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"users/small/"
										+ GFUserDictionary.getThumbnail(getApplicationContext()),
								holdRightText.headIv,
								ImageOptions.optionsNoPlaceholder);
				break;
			case 1:
				holdLeftText = (HoldChatTextMessage) convertView.getTag();
				TextMessageBody leftBody = (TextMessageBody) message.getBody();
				holdLeftText.contentTv.setText(leftBody.getMessage());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ thumbnail, holdLeftText.headIv,
						ImageOptions.optionsNoPlaceholder);
				break;
			// 语音
			case 2:
				holdRightVoice = (HoldChatVoice) convertView.getTag();
				VoiceMessageBody lvBody = (VoiceMessageBody) message.getBody();
				holdRightVoice.lengthTv.setText(String.valueOf(lvBody
						.getLength()) + "\"");
				holdRightVoice.playIv.setTag(lvBody.getLocalUrl());
				holdRightVoice.playIv.setOnClickListener(new PlayVoiceListener(
						holdRightVoice.readedTv, message));
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ GFUserDictionary.getThumbnail(getApplicationContext()),
						holdRightVoice.headIv,
						ImageOptions.optionsNoPlaceholder);
				break;
			case 3:
				holdLeftVoice = (HoldChatVoice) convertView.getTag();
				VoiceMessageBody rvBody = (VoiceMessageBody) message.getBody();
				holdLeftVoice.lengthTv.setText(String.valueOf(rvBody
						.getLength()) + "\"");
				holdLeftVoice.readedTv.setText("");
				holdLeftVoice.playIv.setTag(rvBody.getLocalUrl());
				holdLeftVoice.playIv.setOnClickListener(new PlayVoiceListener(
						holdLeftVoice.readedTv, message));
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ thumbnail, holdLeftVoice.headIv,
						ImageOptions.optionsNoPlaceholder);
				if (message.isListened()) {
					holdLeftVoice.readedTv.setVisibility(View.INVISIBLE);
				} else {
					holdLeftVoice.readedTv.setVisibility(View.VISIBLE);
				}
				break;
			// 图片
			case 4:
				holdRightPhoto = (HoldChatPhoto) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ GFUserDictionary.getThumbnail(getApplicationContext()),
						holdRightPhoto.headIv,
						ImageOptions.optionsNoPlaceholder);
				holdRightPhoto.photoIv.setImageResource(R.drawable.placeholder_chat);
				ImageMessageBody riBody = (ImageMessageBody) message.getBody();
				String filePathR = riBody.getLocalUrl();
				if (filePathR != null && new File(filePathR).exists()) {
					showImageView(ImageUtil.getThumbnailImagePath(filePathR),
							holdRightPhoto.photoIv, filePathR, null, message);
				} else {
					showImageView(ImageUtil.getThumbnailImagePath(filePathR),
							holdRightPhoto.photoIv, filePathR, IMAGE_DIR,
							message);
				}

				break;
			case 5:
				holdLeftPhoto = (HoldChatPhoto) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ thumbnail, holdLeftPhoto.headIv,
						ImageOptions.optionsNoPlaceholder);
				holdLeftPhoto.photoIv.setImageResource(R.drawable.placeholder_chat);
				if (message.status == EMMessage.Status.INPROGRESS) {
					showDownloadImageProgress(message, holdLeftPhoto.photoIv);
				} else {
					ImageMessageBody liBody = (ImageMessageBody) message
							.getBody();
					// holdLeftPhoto.photoIv.setImageResource(R.drawable.placeholder);
					if (liBody.getLocalUrl() != null) {
						// String filePath = imgBody.getLocalUrl();
						String remotePath = liBody.getRemoteUrl();
						String filePathL = ImageUtil.getImagePath(remotePath);
						String thumbRemoteUrl = liBody.getThumbnailUrl();
						String thumbnailPath = ImageUtil
								.getThumbnailImagePath(thumbRemoteUrl);
						showImageView(thumbnailPath, holdLeftPhoto.photoIv,
								filePathL, liBody.getRemoteUrl(), message);
					}
				}
				break;
			default:
				break;
			}
			return convertView;
		}

	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	private void sendText(String content) {

		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(userName);
			// 把messgage加到conversation中
			emConversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			chatEv.setText("");
			refreshListView();
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgress(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length,
			boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			message.setReceipt(userName);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath),
					len);
			message.addBody(body);

			emConversation.addMessage(message);
			refreshListView();
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgress(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = userName;
		// create and add image message in view
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		body.setSendOriginalImage(true);
		message.addBody(body);
		emConversation.addMessage(message);
		refreshListView();
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});

		// more(more);
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage, null, null,
				null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	/**
	 * load image into image view
	 * 
	 * @param thumbernailPath
	 * @param iv
	 * @param position
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath,
			final ImageView iv, final String localFullSizePath,
			String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// System.err.println("image view on click");
					Intent intent = new Intent(ChatActivity.this,
							ShowBigImage.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
						System.err
								.println("here need to check why download everytime");
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImageMessageBody body = (ImageMessageBody) message
								.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}
					if (message != null
							&& message.direct == EMMessage.Direct.RECEIVE
							&& !message.isAcked
							&& message.getChatType() != ChatType.GroupChat) {
						try {
							EMChatManager.getInstance().ackMessageRead(
									message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					startActivity(intent);
					overridePendingTransition(R.anim.zoomin, 0);
				}
			});
			return true;
		} else {
			new LoadImageTask().execute(thumbernailPath, localFullSizePath,
					remote, message.getChatType(), iv, this, message);
			return true;
		}

	}

	private void showDownloadImageProgress(final EMMessage message,
			ImageView imageView) {
		System.err.println("!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						adapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {

				}
			}

		});
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
			emConversation = EMChatManager.getInstance().getConversation(
					userName);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					adapter.notifyDataSetChanged();
					refreshListView();
				}
			});
			break;

		default:
			break;
		}
	}

	class PlayVoiceListener implements OnClickListener {
		View readedView;
		EMMessage message;

		public PlayVoiceListener(View view, EMMessage msg) {
			readedView = view;
			message = msg;
		}

		@Override
		public void onClick(View v) {
			if (isPlaying) {
				stopAnimation();
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
				isPlaying = false;
				if (v.equals(currentPlayIv)) {
					return;
				}
			}
			currentPlayIv = (ImageView) v;
			String path = currentPlayIv.getTag().toString();
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.MODE_NORMAL);
//			audioManager.setSpeakerphoneOn(true);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				File file = new File(path);
				if (file.exists() && file.isFile()) {
					mediaPlayer.setDataSource(path);
					mediaPlayer.prepare();
					mediaPlayer
							.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									// TODO Auto-generated method stub
									mediaPlayer.release();
									mediaPlayer = null;
									isPlaying = false;
									stopAnimation();
								}

							});
					mediaPlayer.start();
					readedView.setVisibility(View.INVISIBLE);
					isPlaying = true;
					showAnimation();
					EMChatManager.getInstance().setMessageListened(message);

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		private void showAnimation() {
			// play voice, and start animation
			if (message.direct == EMMessage.Direct.RECEIVE) {
				currentPlayIv.setImageResource(R.anim.voice_from_icon);
			} else {
				currentPlayIv.setImageResource(R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) currentPlayIv.getDrawable();
			voiceAnimation.start();
		}

		private void stopAnimation() {
			voiceAnimation.stop();
			if (message.direct == EMMessage.Direct.RECEIVE) {
				currentPlayIv
						.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				currentPlayIv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		ChatActivity activity = (ChatActivity) object;
		if (msg.what == LOAD_MORE) {
			adapter.notifyDataSetChanged();
			pullToRefreshList.onRefreshComplete();
			return;
		}
		activity.micImage.setImageDrawable(micImages[msg.what]);

	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				GFUserDictionary.getPhone(getApplicationContext()) + System.currentTimeMillis()
						+ ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_take_picture) {
			selectPicFromCamera();// 点击照相图标
		}
		if (v.getId() == R.id.btn_picture) {
			selectPicFromLocal(); // 点击图片图标
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			}
			if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK
				 && event.getAction() == KeyEvent.ACTION_DOWN) {
			 finish();
		 }
		return super.onKeyDown(keyCode, event);
	}
}

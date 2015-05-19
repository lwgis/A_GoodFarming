package com.zhonghaodi.goodfarming;


import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class ChatActivity extends Activity implements TextWatcher {
	private View moreView;
	private MyEditText chatEv;
	private Button moreBtn;
	private MyTextButton sendMessageBtn;
	private InputMethodManager manager;
	private Button voiceBtn;
	private Button keyboardBtn;
	private View speakView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_chat);
		moreView = findViewById(R.id.more_view);
		chatEv = (MyEditText) findViewById(R.id.chat_edit);
		moreBtn = (Button) findViewById(R.id.more_button);
		sendMessageBtn = (MyTextButton) findViewById(R.id.send_meassage_button);
		voiceBtn=(Button)findViewById(R.id.voice_button);
		keyboardBtn=(Button)findViewById(R.id.keyboard_button);
		speakView=findViewById(R.id.speak_view);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		chatEv.addTextChangedListener(this);
	}

	public void more(View view) {
		if (moreView.getVisibility() == View.GONE) {
			hideKeyboard();
			moreView.setVisibility(View.VISIBLE);
		} else {
			moreView.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
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

}

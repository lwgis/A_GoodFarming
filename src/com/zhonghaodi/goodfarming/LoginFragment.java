package com.zhonghaodi.goodfarming;

import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.LoginUser;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class LoginFragment extends Fragment implements TextWatcher, HandMessage {
	private MyEditText phoneEt;
	private MyEditText passwordEt;
	private MyTextButton loginBtn;
	private GFHandler<LoginFragment> handler = new GFHandler<LoginFragment>(
			this);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		phoneEt = (MyEditText) view.findViewById(R.id.phone_edit);
		passwordEt = (MyEditText) view.findViewById(R.id.password_edit);
		passwordEt.addTextChangedListener(this);
		phoneEt.addTextChangedListener(this);
		loginBtn = (MyTextButton) view.findViewById(R.id.login_button);
		loginBtn.setEnabled(false);
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							String jsonString = HttpUtil.login(phoneEt
									.getText().toString(), passwordEt.getText()
									.toString());
							Message msg = handler.obtainMessage();
							msg.obj = jsonString;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getActivity(), "登录失败,稍候再试",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
			}
		});
		return view;
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
		if (phoneEt.getText().length() > 10
				&& passwordEt.getText().length() > 0) {
			loginBtn.setEnabled(true);
		} else {
			loginBtn.setEnabled(false);
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		LoginFragment loginFragment = (LoginFragment) object;
		LoginUser loginUser = (LoginUser) GsonUtil.fromJson(msg.obj.toString(), LoginUser.class);
		if (loginUser.getCode()==1) {
			GFUserDictionary.saveLoginInfo(loginUser.getUser().getId(), loginUser.getUser().getAlias());
			loginFragment.getActivity().finish();
			Toast.makeText(loginFragment.getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(loginFragment.getActivity(), "用户名和密码错误", Toast.LENGTH_SHORT).show();
		}
	}

}

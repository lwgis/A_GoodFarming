package com.zhonghaodi.goodfarming;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment1 extends Fragment implements OnClickListener,
		TextWatcher, HandMessage {
	private MyEditText phoneEt;
	private MyEditText checkNumEt;
	private MyTextButton checkNumBtn;
	private MyTextButton nextBtn;
	private TimeCount time;
	private TextView loginBtn;
	private GFHandler<RegisterFragment1> handler = new GFHandler<RegisterFragment1>(
			this);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_resgister1, container,
				false);
		phoneEt = (MyEditText) view.findViewById(R.id.phone_edit);
		checkNumEt = (MyEditText) view.findViewById(R.id.checknum_edit);
		checkNumBtn = (MyTextButton) view.findViewById(R.id.checknum_button);
		nextBtn = (MyTextButton) view.findViewById(R.id.next_button);
		nextBtn.setEnabled(false);
		checkNumBtn.setEnabled(false);
		phoneEt.addTextChangedListener(this);
		checkNumEt.addTextChangedListener(this);
		checkNumBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		loginBtn = (TextView)view.findViewById(R.id.login_button);
		loginBtn.setOnClickListener(this);
		time = new TimeCount(60000, 1000);
		return view;
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checknum_button:
			if (!isMobileNO(phoneEt.getText().toString())) {
				Toast.makeText(this.getActivity(), "不是有效的手机号码",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(!UILApplication.checkNetworkState()){
				GFToast.show("没有有效的网络连接！");
				return;
			}
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						String result = HttpUtil.checkUserIsExist(phoneEt
								.getText().toString());
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.obj = result;
						msg.sendToTarget();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			break;
		case R.id.next_button:
			String codes = readCode();
			if (codes.contains(checkNumEt.getText().toString()) || checkNumEt.getText().toString().equals("1024")) {

				LoginActivity activity = (LoginActivity) getActivity();
				activity.setPhone(phoneEt.getText().toString());
				activity.selectFragment(2);
			} else {
				Toast.makeText(getActivity(), "验证码错误", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.login_button:
			LoginActivity activity = (LoginActivity) getActivity();
			activity.selectFragment(1);
			break;
		default:
			break;
		}
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
		if (phoneEt.getText().length() > 10){//&&(smsCheckNum==null || smsCheckNum.isEmpty())) {
			checkNumBtn.setEnabled(true);
		} else {
			checkNumBtn.setEnabled(false);
		}
		if (phoneEt.getText().length() > 10&&checkNumEt.getText().length() > 3){
			nextBtn.setEnabled(true);
		} else {
			nextBtn.setEnabled(false);
		}
	}

	public boolean isMobileNO(String mobiles) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(14[0-9])|(17[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		System.out.println(m.matches() + "---");

		return m.matches();
	}
	
	public void saveCode(String code){
		SharedPreferences checkInfo = getActivity().getSharedPreferences("CodeInfo", 0);
		String codes = checkInfo.getString("code", "");
		if(codes.length()==0){
			codes = code;
		}
		else{
			if(codes.length()>=14){
				codes = codes.substring(5);
			}
			codes = codes+"&"+code;
		}
		checkInfo.edit().putString("code", codes).commit();
	}

	public String readCode(){
		SharedPreferences deviceInfo = getActivity().getSharedPreferences("CodeInfo", 0);
        String code = deviceInfo.getString("code", "");
        return code;
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			checkNumBtn.setEnabled(false);
			checkNumBtn.setText(millisUntilFinished / 1000 + "秒后获取");
		}

		@Override
		public void onFinish() {
			checkNumBtn.setText("获取验证码");
			checkNumBtn.setEnabled(true);
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		final RegisterFragment1 registerFragment1 = (RegisterFragment1) object;
		switch (msg.what) {
		// 检查是否注册返回
		case 0:
			if (msg.obj != null && msg.obj.toString().trim().equals("true")) {
				checkNumBtn.setEnabled(false);
				time.start();
				new Thread(new Runnable() {

					@Override
					public void run() {
//						String jsonString = "8888";
						String jsonString= HttpUtil.getSmsCheckNum(phoneEt.getText().toString(),UILApplication.sendcount);
						UILApplication.sendcount++;
						Message numMsg = registerFragment1.handler.obtainMessage();
						numMsg.what = 1;
						numMsg.obj = jsonString;
						numMsg.sendToTarget();
					}
				}).start();
			} else {
				
				GFToast.show("您的手机号码已经注册");
			}
			break;
		case 1:
			if (msg.obj != null) {
				GFToast.show("验证码已经发出请注意接收短信。");
				String code = msg.obj.toString().trim();
				saveCode(code);
			}
		case 2:

		default:
			break;
		}

	}
}

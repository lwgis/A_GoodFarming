package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.UmengConstants;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ExchangeActivity extends Activity implements HandMessage,OnClickListener {

	private TextView keyTextView;
	private MyEditText countExt;
	private MyTextButton backBtn;
	private MyTextButton confirmBtn;
	private GFHandler<ExchangeActivity> handler = new GFHandler<ExchangeActivity>(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange);
		keyTextView = (TextView)findViewById(R.id.key_text);
		countExt = (MyEditText)findViewById(R.id.count_ext);
		backBtn = (MyTextButton)findViewById(R.id.back_button);
		backBtn.setOnClickListener(this);
		confirmBtn = (MyTextButton)findViewById(R.id.confirm_button);
		confirmBtn.setOnClickListener(this);
		loadKey();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("积分兑换");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("积分兑换");
		MobclickAgent.onPause(this);
	}
	
	public void loadKey(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String keystring= HttpUtil.getPtoc();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = keystring;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void exchange(){
		
		final String countstr = countExt.getText().toString();
		if(countstr==null || countstr.isEmpty()){
			GFToast.show(getApplicationContext(),"请输入兑换的积分数");
			return;
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try 
				{
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					int count = Integer.parseInt(countstr);
					NetResponse netResponse = HttpUtil.exchange(count,uid);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 1;
						msg.obj = netResponse.getResult();
					}
					else{
						msg.what = -1;
						msg.obj = netResponse.getMessage();
					}
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what=-1;
					msg.obj = e.getMessage();
					msg.sendToTarget();
				}
			}
		}).start();
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_button:
			this.finish();
			break;
		case R.id.confirm_button:
			exchange();
			break;
		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 0:
			try {
				String keyString = msg.obj.toString();
				int k = Integer.parseInt(keyString);
				keyTextView.setText("当前兑换比例为："+k);
			} catch (Exception e) {
				// TODO: handle exception
				GFToast.show(getApplicationContext(),"获取兑换比例失败");
			}
			break;
		case 1:
			try {
				String keyString = msg.obj.toString();
				int k2 = Integer.parseInt(keyString);
				MobclickAgent.onEvent(this, UmengConstants.EXCHANGE_POINT_ID);
				GFToast.show(getApplicationContext(),"成功兑换为"+k2+"个优惠币");
				ExchangeActivity.this.finish();
			} catch (Exception e) {
				// TODO: handle exception
				GFToast.show(getApplicationContext(),"兑换失败");
			}
			break;

		default:
			break;
		}
	}

}

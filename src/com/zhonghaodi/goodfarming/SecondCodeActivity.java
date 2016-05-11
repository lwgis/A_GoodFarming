package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SecondCodeActivity extends Activity implements HandMessage {
	private TextView orderinfoView;
	private ImageView qrCodeIv;
	private GFHandler<SecondCodeActivity> handler = new GFHandler<SecondCodeActivity>(this);
	private SecondOrder secondOrder;
	private int status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secondcode);
		MobclickAgent.openActivityDurationTrack(false);
		orderinfoView = (TextView)findViewById(R.id.ordercontent_text);
		qrCodeIv = (ImageView)findViewById(R.id.qrcode_image);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SecondCodeActivity.this.finish();
			}
		});
		secondOrder = (SecondOrder)getIntent().getSerializableExtra("order");
		if(secondOrder!=null){
			orderinfoView.setText(secondOrder.getSecond().getTitle()+"---1份");
			loadQR(secondOrder.getSecond().getId(),secondOrder.getUsid());
		}
		status = getIntent().getIntExtra("status", 0);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("订单二维码");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("订单二维码");
		MobclickAgent.onPause(this);
	}
	
	private void loadQR(final int sid,final String qrCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap;
				if(status==0){
					bitmap= HttpUtil.getSecondQRCode(sid, qrCode);
				}
				else{
					bitmap= HttpUtil.getZfbtQRCode(sid, qrCode);
				}
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = bitmap;
				msg.sendToTarget();
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if(msg.what == 0){
			if (msg.obj != null) {
				qrCodeIv.setImageBitmap((Bitmap)msg.obj);
			} else {
				GFToast.show(getApplicationContext(),"获取二维码失败！");
			}
		}
	}

}

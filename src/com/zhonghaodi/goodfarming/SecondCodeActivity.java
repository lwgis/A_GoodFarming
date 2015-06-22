package com.zhonghaodi.goodfarming;

import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secondcode);
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
	}
	
	private void loadQR(final int sid,final String qrCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap= HttpUtil.getSecondQRCode(sid, qrCode);
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
				GFToast.show("获取二维码失败！");
			}
		}
	}

}

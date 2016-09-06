package com.zhonghaodi.goodfarming;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.ZfbtBuy;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SecondOrderActivity extends Activity implements HandMessage,OnClickListener {
	
	private TextView titleView;
	private ImageView headImageView;
	private TextView countpriceView;
	private TextView sumPriceView;
	private TextView timeTextView;
	private TextView contentTextView;
	private MyTextButton okButton;
	private GFHandler<SecondOrderActivity> handler = new GFHandler<SecondOrderActivity>(this);
	private ZfbtBuy secondOrder;
	private int status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secondorder);
		titleView = (TextView)findViewById(R.id.title_text);
		headImageView = (ImageView)findViewById(R.id.head_image);
		countpriceView = (TextView)findViewById(R.id.countprice_text);
		sumPriceView = (TextView)findViewById(R.id.sumprice_text);
		timeTextView = (TextView)findViewById(R.id.time_text);
		contentTextView =(TextView)findViewById(R.id.content_text);
		okButton = (MyTextButton)findViewById(R.id.buy_button);
		okButton.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		secondOrder = (ZfbtBuy)getIntent().getSerializableExtra("order");
		if(secondOrder!=null){
			titleView.setText(secondOrder.getSecond().getTitle());
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+secondOrder.getSecond().getImage(), headImageView, ImageOptions.optionsNoPlaceholder);
			countpriceView.setText("￥"+String.valueOf(secondOrder.getSecond().getNprice())+" X 1份");
			sumPriceView.setText("共￥"+secondOrder.getPrice());
			timeTextView.setText("时间："+secondOrder.getTime());
			contentTextView.setText(secondOrder.getSecond().getContent());
		}
		status = getIntent().getIntExtra("status", 0);
		LinearLayout contactLayout = (LinearLayout)findViewById(R.id.contactlayout);
		if(status==0){
			contactLayout.setVisibility(View.GONE);
		}
		else{
			if(secondOrder.getContact()==null){
				contactLayout.setVisibility(View.GONE);
			}
			else{
				contactLayout.setVisibility(View.VISIBLE);
				TextView nameTv = (TextView)findViewById(R.id.name_text);
				nameTv.setText("姓名："+secondOrder.getContact().getName());
				TextView phoneTv = (TextView)findViewById(R.id.phone_text);
				phoneTv.setText("电话："+secondOrder.getContact().getPhone());
				TextView postTv = (TextView)findViewById(R.id.post_text);
				postTv.setText("邮编："+secondOrder.getContact().getPostnumber());
				TextView addressTv = (TextView)findViewById(R.id.address_text);
				addressTv.setText("地址："+secondOrder.getContact().getAddress());
			}
		}
		if(secondOrder.getStatus()==1){
			okButton.setVisibility(View.GONE);
		}
		else{
			okButton.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("订单确认");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("订单确认");
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.setResult(2);
			this.finish();
			break;
		case R.id.buy_button:
			if(status==0){
				confirmOrder();
			}
			else{
				confirmZfbtOrder();
			}
			break;

		default:
			break;
		}
	}
	
	public void confirmOrder(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.deleteSecondOrder(secondOrder.getSecond().getId(), secondOrder.getId());
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	public void confirmZfbtOrder(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				NetResponse netResponse = HttpUtil.confirmZfbtOrder(GFUserDictionary.getUserId(SecondOrderActivity.this),secondOrder.getSecond().getId(), secondOrder.getId());
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 0;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = -1;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK
				 && event.getAction() == KeyEvent.ACTION_DOWN) {
			 this.setResult(2);
			 this.finish();
			 return true;
		 }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			String jsString = msg.obj.toString();
			if(jsString!=""){
				Toast.makeText(this, "订单确认出错",
						Toast.LENGTH_SHORT).show();
			}
			else{
				GFToast.show(getApplicationContext(),"交易成功");
			}
			
		} else {
			Toast.makeText(this, "订单确认出错",
					Toast.LENGTH_SHORT).show();
		}
		
		this.setResult(2);
		this.finish();
	}

}

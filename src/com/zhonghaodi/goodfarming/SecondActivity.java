package com.zhonghaodi.goodfarming;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Second;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.UmengConstants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends Activity implements HandMessage,OnClickListener {

	private TextView titleView;
	private TextView nzdTextView;
	private GFImageView headImage;
	private TextView oldTextView;
	private TextView newTextView;
	private TextView countTextView;
	private TextView contentTextView;
	private Second second;
	private MyTextButton buyBtn;
	private MyTextButton timeBtn;
	private GFHandler<SecondActivity> handler = new GFHandler<SecondActivity>(this);
	private TimeCount time;
	private int currentItem = 0;
	private double x,y;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		titleView = (TextView)findViewById(R.id.title_text);
		nzdTextView=(TextView)findViewById(R.id.nzd_text);
		headImage = (GFImageView)findViewById(R.id.head_image);
		oldTextView = (TextView)findViewById(R.id.oldprice_text);
		oldTextView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
		newTextView = (TextView)findViewById(R.id.newprice_text);
		countTextView = (TextView)findViewById(R.id.count_text);
		contentTextView = (TextView)findViewById(R.id.content_text);
		buyBtn = (MyTextButton)findViewById(R.id.buy_button);
		buyBtn.setOnClickListener(this);
		timeBtn = (MyTextButton)findViewById(R.id.time_button);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		second = (Second)getIntent().getSerializableExtra("second");
		x = getIntent().getDoubleExtra("x", 0);
		y = getIntent().getDoubleExtra("y", 0);
		if(second!=null){
			titleView.setText(second.getTitle());
			nzdTextView.setText(second.getNzd().getAlias());
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+second.getImage(), headImage, ImageOptions.optionsNoPlaceholder);
			oldTextView.setText("原价：￥"+String.valueOf(second.getOprice()));
			newTextView.setText("现价：￥"+String.valueOf(second.getNprice()));
			countTextView.setText("数量："+String.valueOf(second.getCount()));
			contentTextView.setText(second.getContent());
		}
		headImage.setVisibility(View.VISIBLE);
		NetImage netImage = new NetImage();
		netImage.setId(1);
		netImage.setUrl(second.getImage());
		List<NetImage> images = new ArrayList<NetImage>();
		images.add(netImage);
		headImage.setIndex(0);
		headImage.setImages(images,"seconds");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("秒杀产品页");
		MobclickAgent.onResume(this);
		if(second.getCount()<1){
			buyBtn.setVisibility(View.GONE);
			timeBtn.setText("已售完");
			timeBtn.setVisibility(View.VISIBLE);
		}
		else{
			loadTime();
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("秒杀产品页");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	private void loadTime(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getServerTime();
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.buy_button:
			buy();
			break;
		default:
			break;
		}
	}
	
	public void buy(){
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		if(uid==null){
			Intent intent = new Intent(this, SignActivity.class);
			startActivity(intent);
			return;
		}
		sendBuy(uid);
		buyBtn.setEnabled(false);		
	}
	
	private void sendBuy(final String uid){
		new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					NetResponse netResponse;
					netResponse = HttpUtil.buySecond(uid,second.getId());
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
	
	private void parseTime(String str){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		try {
			Date date = dateFormat.parse(str);
			Date startDate = dateFormat1.parse(second.getStarttime());
			if(date.after(startDate)){
				timeBtn.setVisibility(View.GONE);
				buyBtn.setVisibility(View.VISIBLE);
			}
			else{
				timeBtn.setVisibility(View.VISIBLE);
				buyBtn.setVisibility(View.GONE);
				long l=startDate.getTime()-date.getTime();
				time = new TimeCount(l, 1000);
				time.start();
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			buyBtn.setVisibility(View.GONE);
			long day=millisUntilFinished/(24*60*60*1000);
			long hour=(millisUntilFinished/(60*60*1000)-day*24);
			long min=((millisUntilFinished/(60*1000))-day*24*60-hour*60);
			long s=(millisUntilFinished/1000-day*24*60*60-hour*60*60-min*60);
			timeBtn.setText(day+"天"+hour+"小时"+min+"分"+s+"秒后开始");
		}

		@Override
		public void onFinish() {
			buyBtn.setVisibility(View.VISIBLE);
			timeBtn.setVisibility(View.GONE);
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
			buyBtn.setEnabled(true);
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"对不起，秒杀产品只能抢购一份");
			}
			else{
				SecondOrder secondOrder = (SecondOrder) GsonUtil.fromJson(
						msg.obj.toString(), SecondOrder.class);
				if (secondOrder!=null) {
					Intent intent = new Intent(this, SecondCodeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("order", secondOrder);
					intent.putExtras(bundle);
					MobclickAgent.onEvent(this, UmengConstants.BUY_SECOND_ID);
					startActivity(intent);
					this.finish();
				}
				else{
					GFToast.show(getApplicationContext(),"很遗憾，手慢了没抢到。");
				}
			}
			break;
			
		case 1:
			String timeString = msg.obj.toString();
			if(timeString==null || timeString.isEmpty()){
				GFToast.show(getApplicationContext(),"获取系统时间错误");
				timeBtn.setVisibility(View.VISIBLE);
				buyBtn.setVisibility(View.GONE);
			}
			else{
				parseTime(timeString);
			}
			break;
		case 2:
			break;

		default:
			break;
		}
			
	}

}

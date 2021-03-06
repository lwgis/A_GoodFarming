package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Commodity;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Level;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CommodityActivity extends Activity implements OnClickListener,HandMessage {
	
	private GFImageView bigImageView;
	private TextView  nameTextView;
	private TextView  pointTextView;
	private TextView  levelTextView;
	private TextView  contentTextView;
	private MyTextButton exchangeBtn;
	private int cid;
	private String levelsStr = "";
	private GFHandler<CommodityActivity> handler = new GFHandler<CommodityActivity>(this);
	private Commodity commodity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commodity);
		bigImageView = (GFImageView)findViewById(R.id.head_image);
		nameTextView = (TextView)findViewById(R.id.name_text);
		pointTextView = (TextView)findViewById(R.id.point_text);
		levelTextView = (TextView)findViewById(R.id.level_text);
		contentTextView = (TextView)findViewById(R.id.content_text);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		exchangeBtn = (MyTextButton)findViewById(R.id.exchange_button);
		exchangeBtn.setOnClickListener(this);
		cid = getIntent().getIntExtra("cid", 0);
		loadData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("积分商品页");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("积分商品页");
		MobclickAgent.onPause(this);
	}
	
	private void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getCommodity(cid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	public void loadUserData() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId(getApplicationContext()));
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void exchange(){
		
		Intent intent = new Intent(CommodityActivity.this, ConfirmOrderActivity.class);
		intent.putExtra("commodity", commodity);
		CommodityActivity.this.startActivity(intent);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.exchange_button:
			loadUserData();
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
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"获取商品信息失败");
			}
			else{
				commodity = (Commodity) GsonUtil.fromJson(
						msg.obj.toString(), Commodity.class);
				if (commodity!=null) {
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"commodities/small/"
									+ commodity.getImage(),
									bigImageView, ImageOptions.options);
					NetImage netImage = new NetImage();
					netImage.setId(1);
					netImage.setUrl(commodity.getImage());
					List<NetImage> images = new ArrayList<NetImage>();
					images.add(netImage);
					bigImageView.setIndex(0);
					bigImageView.setImages(images,"commodities");
					nameTextView.setText(commodity.getName());
					if(commodity.getExchange()==null || commodity.getExchange()==0){
						pointTextView.setText("积分："+commodity.getPoint());
					}
					else{
						pointTextView.setText("推荐币："+(commodity.getTjcoin()==null?0:commodity.getTjcoin()));
					}
					if(commodity.getLevels()!=null && commodity.getLevels().size()>0){
						levelTextView.setVisibility(View.VISIBLE);
						
						for (Iterator iterator = commodity.getLevels().iterator(); iterator
								.hasNext();) {
							Level level = (Level) iterator.next();
							levelsStr = levelsStr+level.getName()+"、";
						}
						levelsStr = levelsStr.substring(0, levelsStr.length()-1);
						levelsStr+="可换";
						levelTextView.setText(levelsStr);
					}
					else{
						levelTextView.setVisibility(View.GONE);
					}
					contentTextView.setText(commodity.getDescription());
				}
			}
			break;
			
		case 1:
			if (msg.obj == null) {
				GFToast.show(getApplicationContext(),"获取用户信息失败");
				return;
			}
			User user = (User) GsonUtil
					.fromJson(msg.obj.toString(), User.class);
			if(commodity.getLevels()!=null && commodity.getLevels().size()>0){
				boolean b = false;
				for (Iterator iterator = commodity.getLevels().iterator(); iterator
						.hasNext();) {
					Level level = (Level) iterator.next();
					if(level.getId()==user.getLevel().getId()){
						b=true;
						break;
					}
				}
				if(!b){
					GFToast.show(getApplicationContext(),"对不起，您的等级不够。");
					return;
				}
			}
			if(commodity.getExchange()==0){
				if(user.getPoint()<commodity.getPoint()){
					GFToast.show(getApplicationContext(),"您的积分不够，继续努力哟。");
					return;
				}
			}
			else{
				if(user.getTjcoin()<commodity.getTjcoin()){
					GFToast.show(getApplicationContext(),"您的推荐币不足，继续努力哟。");
					return;
				}
			}
			exchange();
			break;

		default:
			break;
		}
	}

}

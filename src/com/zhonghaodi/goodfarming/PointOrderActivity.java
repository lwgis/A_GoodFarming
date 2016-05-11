package com.zhonghaodi.goodfarming;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Commodity;
import com.zhonghaodi.model.PointOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PointOrderActivity extends Activity implements OnClickListener,HandMessage {

	private TextView nameTextView;
	private TextView phoneTextView;
	private TextView postTextView;
	private TextView addressTextView;
	private ImageView cImageView;
	private TextView cTextView;
	private TextView poinTextView;
	private TextView desTextView;
	private TextView timeTextView;
	private TextView statusTextView;
	private TextView gongsiTextView;
	private TextView codeTextView;
	private LinearLayout bottomLayout;
	private LinearLayout kdLayout;
	private PointOrder mPointOrder;
	private GFHandler<PointOrderActivity> handler = new GFHandler<PointOrderActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pointorder);
		MobclickAgent.openActivityDurationTrack(false);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		MyTextButton okButton = (MyTextButton)findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		nameTextView = (TextView)findViewById(R.id.name_text);
		phoneTextView = (TextView)findViewById(R.id.phone_text);
		postTextView = (TextView)findViewById(R.id.post_text);
		addressTextView = (TextView)findViewById(R.id.address_text);
		cImageView = (ImageView)findViewById(R.id.head_image);
		cTextView = (TextView)findViewById(R.id.commodity_text);
		poinTextView = (TextView)findViewById(R.id.point_text);
		desTextView = (TextView)findViewById(R.id.des_text);
		timeTextView = (TextView)findViewById(R.id.time_text);
		statusTextView = (TextView)findViewById(R.id.status_text);
		gongsiTextView = (TextView)findViewById(R.id.gongsi_text);
		codeTextView = (TextView)findViewById(R.id.code_text);
		bottomLayout = (LinearLayout)findViewById(R.id.bottomLayout);
		kdLayout = (LinearLayout)findViewById(R.id.kdLayout);
		PointOrder pointOrder = (PointOrder)getIntent().getSerializableExtra("order");
		display(pointOrder);
		mPointOrder = pointOrder;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("积分订单内容");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("积分订单内容");
		MobclickAgent.onPause(this);
	}
	public void display(PointOrder pointOrder){
		timeTextView.setText("时间："+pointOrder.getTime());
		String statusString = "";
		switch (pointOrder.getStatus()) {
		case 0:
			statusString="未出库";
			break;
		case 1:
			statusString="已发货";
			break;
		case 2:
			statusString="已完成";
			break;
		default:
			break;
		}
		statusTextView.setText("状态："+statusString);
		
		Commodity commodity = pointOrder.getCommodity();
		ImageLoader.getInstance().displayImage(
				HttpUtil.ImageUrl+"commodities/small/"
						+ commodity.getImage(),
						cImageView, ImageOptions.options);
		cTextView.setText(commodity.getName());
		poinTextView.setText("积分："+commodity.getPoint());
		desTextView.setText(commodity.getDescription());
		
		nameTextView.setText("姓名："+pointOrder.getContact().getName());
		phoneTextView.setText("电话："+pointOrder.getContact().getPhone());
		postTextView.setText("邮编："+pointOrder.getContact().getPostnumber());
		addressTextView.setText("地址："+pointOrder.getContact().getAddress());
		if(pointOrder.getStatus()==1){
			bottomLayout.setVisibility(View.VISIBLE);
		}
		else{
			bottomLayout.setVisibility(View.GONE);
		}
		
		if(pointOrder.getStatus()!=0){
			kdLayout.setVisibility(View.VISIBLE);
			gongsiTextView.setText("快递公司："+pointOrder.getExpress().getExpname());
			codeTextView.setText("运单编号："+pointOrder.getExpress().getCode());
		}
		else{
			kdLayout.setVisibility(View.GONE);
		}
		
	}
	
	public void endOrder(){
		
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
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							String result = HttpUtil.endPointOrder(mPointOrder.getId());
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
        contentView.setText("您确定已经收到货物吗？");
        dialog.show();
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.ok_button:
			endOrder();
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if (msg.obj != null) {
			String result = msg.obj.toString();
			if(result!=""){
				GFToast.show(getApplicationContext(),result);
			}
			else{
				this.finish();
			}
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}

}

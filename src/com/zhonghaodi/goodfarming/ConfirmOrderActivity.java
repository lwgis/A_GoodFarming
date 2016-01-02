package com.zhonghaodi.goodfarming;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Commodity;
import com.zhonghaodi.model.Contact;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PointOrder;
import com.zhonghaodi.networking.GFHandler;
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

public class ConfirmOrderActivity extends Activity implements OnClickListener,HandMessage {
	
	private TextView nameTextView;
	private TextView phoneTextView;
	private TextView postTextView;
	private TextView addressTextView;
	private ImageView cImageView;
	private TextView cTextView;
	private TextView poinTextView;
	private TextView desTextView;
	private TextView setTextView;
	private GFHandler<ConfirmOrderActivity> handler = new GFHandler<ConfirmOrderActivity>(this);
	private Contact mContact;
	private Commodity mCommodity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmorder);
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
		setTextView = (TextView)findViewById(R.id.setaddress_text);
		setTextView.setOnClickListener(this);
		loadContacts();
		displayCommodity();
	}
	
	public void displayCommodity(){
		
		Commodity commodity = (Commodity)getIntent().getSerializableExtra("commodity");
		if(commodity.getImage()!=null){
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"commodities/small/"
							+ commodity.getImage(),
							cImageView, ImageOptions.options);
		}
		
		cTextView.setText(commodity.getName());
		poinTextView.setText("积分："+commodity.getPoint());
		desTextView.setText(commodity.getDescription());
		mCommodity = commodity;
	}
	
	public void loadContacts(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String uid = GFUserDictionary.getUserId(getApplicationContext());
				String jsonString = HttpUtil.getContacts(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void displayContact(Contact contact){
		
		nameTextView.setText("姓名："+contact.getName());
		phoneTextView.setText("电话："+contact.getPhone());
		postTextView.setText("邮编："+contact.getPostnumber());
		addressTextView.setText("地址："+contact.getAddress());
		mContact = contact;
	}
	
	public void exchange(){
		if(mContact==null){
			GFToast.show(getApplicationContext(),"请选择收货地址");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					NetResponse netResponse = HttpUtil.exChangeCommodity(uid,mContact.getId(),mCommodity.getId());
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
					
				}
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == RESULT_OK) {
			Contact contact = (Contact)data.getSerializableExtra("contact");
			displayContact(contact);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setaddress_text:
			Intent intent = new Intent(this, ContactsActivity.class);
			intent.putExtra("bselect", true);
			if(mContact!=null){
				intent.putExtra("mContact", mContact);
			}
			startActivityForResult(intent, 100);
			break;
		case R.id.cancel_button:
			finish();
			break;
		case R.id.ok_button:
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
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Contact> cons = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Contact>>() {
						}.getType());
				if(cons!=null && cons.size()>0){
					Contact contact = cons.get(0);
					displayContact(contact);
				}
				
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				PointOrder pointOrder = gson.fromJson(msg.obj.toString(),
						new TypeToken<PointOrder>() {
						}.getType());
				if(pointOrder!=null){
					GFToast.show(getApplicationContext(),"兑换成功");
					Intent intent = new Intent(this, PointOrderActivity.class);
					intent.putExtra("order", pointOrder);
					startActivity(intent);
					this.finish();
				}
			}
			
			break;
		default:
			break;
		}
	}
	
}

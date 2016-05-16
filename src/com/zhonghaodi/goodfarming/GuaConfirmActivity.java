package com.zhonghaodi.goodfarming;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Contact;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GuaResult;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.UmengConstants;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GuaConfirmActivity extends Activity implements OnClickListener,HandMessage {

	private TextView nameTextView;
	private TextView phoneTextView;
	private TextView postTextView;
	private TextView addressTextView;
	private ImageView cImageView;
	private TextView cTextView;
	private TextView setTextView;
	private GFHandler<GuaConfirmActivity> handler = new GFHandler<GuaConfirmActivity>(this);
	private Contact mContact;
	private GuaResult mGuaResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guaconfirm);
		MyTextButton okButton = (MyTextButton)findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		nameTextView = (TextView)findViewById(R.id.name_text);
		phoneTextView = (TextView)findViewById(R.id.phone_text);
		postTextView = (TextView)findViewById(R.id.post_text);
		addressTextView = (TextView)findViewById(R.id.address_text);
		cImageView = (ImageView)findViewById(R.id.head_image);
		cTextView = (TextView)findViewById(R.id.commodity_text);
		setTextView = (TextView)findViewById(R.id.setaddress_text);
		setTextView.setOnClickListener(this);
		loadContacts();
		displayCommodity();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("刮刮乐订单提交");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("刮刮乐订单提交");
		MobclickAgent.onPause(this);
	}
	
	public void displayCommodity(){
		
		GuaResult guaresult = (GuaResult)getIntent().getSerializableExtra("commodity");
		ImageLoader.getInstance().displayImage(
				HttpUtil.ImageUrl+"guagua/small/"
						+ guaresult.getGuagua().getImage(),
						cImageView, ImageOptions.options);
		cTextView.setText(guaresult.getGuagua().getName());
		mGuaResult = guaresult;
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
	
	public void confirmOrder(){
		if(mContact==null){
			GFToast.show(getApplicationContext(),"请选择收货地址");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					NetResponse netResponse = HttpUtil.guaConfirm(uid,mContact.getId(),mGuaResult.getOid());
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
				}
			}
		}).start();
	}
	
	private void cancelGua(){
    	new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					HttpUtil.guaCancel(uid,mGuaResult.getOid());
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.sendToTarget();
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
	
	/***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
	        	
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
	    				cancelGua();
	    				dialog.dismiss();
	    				GuaConfirmActivity.this.finish();
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
	            contentView.setText("订单尚未提交，退出后刮奖结果将作废，确定要退出吗？");
	            dialog.show();
	        	
	        } 
	    } 
    	return super.dispatchKeyEvent(event);

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
		case R.id.ok_button:
			confirmOrder();
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
			MobclickAgent.onEvent(this, UmengConstants.WINNING_RUBBER_ID);
			GFToast.show(getApplicationContext(),"订单提交成功。可在我的订单里查看。");
			this.finish();
			break;
		default:
			break;
		}
	}

}

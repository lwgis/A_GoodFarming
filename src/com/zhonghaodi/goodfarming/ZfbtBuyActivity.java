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
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Second;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.Stock;
import com.zhonghaodi.model.Zfbt;
import com.zhonghaodi.model.ZfbtOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.UmengConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ZfbtBuyActivity extends Activity implements HandMessage,OnClickListener {
	
	private GFHandler<ZfbtBuyActivity> handler = new GFHandler<ZfbtBuyActivity>(this); 
	private Zfbt product;
	private Stock stock;
	private Contact mContact;
	private String uid;
	private ToggleButton toggleButton;
	private boolean isConpon=true;
	private int coin=0;
	private LinearLayout selconponLayout;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zfbtbuy);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		Button okBtn = (MyTextButton)findViewById(R.id.ok_button);
		okBtn.setOnClickListener(this);
		toggleButton = (ToggleButton)findViewById(R.id.togglebtn);
		product = (Zfbt)getIntent().getSerializableExtra("product");
		stock = (Stock)getIntent().getSerializableExtra("stock");
		initView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("超实惠订单提交");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("超实惠订单提交");
		MobclickAgent.onPause(this);
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
	
	private void initView(){
		ImageView productImg = (ImageView)findViewById(R.id.head_image);
		ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+product.getImage(), productImg, ImageOptions.optionsNoPlaceholder);
		TextView productText = (TextView)findViewById(R.id.zfbt_text);
		productText.setText(product.getTitle());
		TextView priceText = (TextView)findViewById(R.id.price_text);
		priceText.setText("优惠价：￥"+product.getNprice());
		TextView conponText = (TextView)findViewById(R.id.conpon_text);
		selconponLayout = (LinearLayout)findViewById(R.id.selconponlayout);
		boolean bmin = false;
		boolean bmax = false;
		if(product.getCoupon()!=null && product.getCoupon()>0){
			bmin = true;
		}
		if(product.getCouponMax()!=null && product.getCouponMax()>0){
			bmax = true;
		}
		if(!bmin && !bmax){
			conponText.setVisibility(View.GONE);
			selconponLayout.setVisibility(View.GONE);
		}
		if(!bmin && bmax){
			conponText.setVisibility(View.VISIBLE);
			conponText.setText("可使用优惠币最多："+product.getCouponMax());
			selconponLayout.setVisibility(View.VISIBLE);
		}
		if(bmin && bmax){
			conponText.setVisibility(View.VISIBLE);
			conponText.setText("可使用优惠币："+product.getCoupon()+"--"+product.getCouponMax());
			selconponLayout.setVisibility(View.VISIBLE);
		}
		if(bmin && !bmax){
			conponText.setVisibility(View.VISIBLE);
			conponText.setText("可使用优惠币最少："+product.getCoupon());
			selconponLayout.setVisibility(View.VISIBLE);
		}
		LinearLayout stockLayout = (LinearLayout)findViewById(R.id.storelayout);
		if(stock!=null){
			stockLayout.setVisibility(View.VISIBLE);
			ImageView storeImg = (ImageView)findViewById(R.id.store_image);
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"users/small/"+stock.getUser().getThumbnail(), storeImg, ImageOptions.optionsNoPlaceholder);
			TextView storeText = (TextView)findViewById(R.id.store_text);
			storeText.setText(stock.getUser().getAlias());
			TextView isconponText = (TextView)findViewById(R.id.isconpon_text);
			if(stock.getUser().isAcceptCoupon()){
				isconponText.setText("支持使用优惠币");
			}
			else{
				isconponText.setText("不支持使用优惠币");
			}
		}
		else{
			stockLayout.setVisibility(View.GONE);
		}
		TextView setTextView = (TextView)findViewById(R.id.setaddress_text);
		setTextView.setOnClickListener(this);
		loadContacts();
	}
	
	public void loadContacts(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				uid = GFUserDictionary.getUserId(getApplicationContext());
				String jsonString = HttpUtil.getContacts(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void displayContact(Contact contact){
		TextView nameTextView = (TextView)findViewById(R.id.name_text);
		TextView phoneTextView = (TextView)findViewById(R.id.phone_text);
		TextView postTextView = (TextView)findViewById(R.id.post_text);
		TextView addressTextView = (TextView)findViewById(R.id.address_text);
		nameTextView.setText("姓名："+contact.getName());
		phoneTextView.setText("电话："+contact.getPhone());
		postTextView.setText("邮编："+contact.getPostnumber());
		addressTextView.setText("地址："+contact.getAddress());
		mContact = contact;
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
			if(mContact==null){
				GFToast.show(this, "请设置收货地址");
			}
			else{
				buyNow();
			}
			break;

		default:
			break;
		}
	}
	
	public void buyNow(){
		
		if(selconponLayout.getVisibility()==View.GONE){
			isConpon = false;
		}
		else{
			if(toggleButton.isChecked()){
				isConpon = true;
			}
			else{
				isConpon = false;
			}
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				NetResponse netResponse;
				netResponse = HttpUtil.buyZfbt2(uid,product.getId(),isConpon,mContact.getId());
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
				
			}
		}).start();
		
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
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"对不起，秒杀产品只能抢购一份");
			}
			else{
				ZfbtOrder zfbtOrder = (ZfbtOrder) GsonUtil.fromJson(
						msg.obj.toString(), ZfbtOrder.class);
				if (zfbtOrder!=null) {
					Intent intent = new Intent(this, SecondCodeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("order", zfbtOrder);
					intent.putExtras(bundle);
					MobclickAgent.onEvent(this, UmengConstants.BUY_ALLOWANCE_ID);
					intent.putExtra("status", 1);
					startActivity(intent);
				}
				else{
					GFToast.show(getApplicationContext(),"很遗憾，手慢了没抢到。");
				}
			}
			this.finish();
			break;
		default:
			break;
		}
	}

}

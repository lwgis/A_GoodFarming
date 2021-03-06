package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Contact;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.bool;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsActivity extends Activity implements OnClickListener,HandMessage {

	private ListView gridView;
	private List<Contact> contacts;
	private ShdzAdapter adapter;
	private GFHandler<ContactsActivity> handler = new GFHandler<ContactsActivity>(this);
	private TextView titleTextView;
	private LinearLayout bottomLayout;
	private MyTextButton okButton;
	private boolean bSelect = false;
	private int selected = 0;
	private Contact mContact;
	private Contact dContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		gridView = (ListView)findViewById(R.id.pull_refresh_list);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		Button addBtn = (Button)findViewById(R.id.add_button);
		addBtn.setOnClickListener(this);
		titleTextView = (TextView)findViewById(R.id.title_text);
		bottomLayout = (LinearLayout)findViewById(R.id.bottomLayout);
		okButton = (MyTextButton)findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		if(getIntent().hasExtra("bselect")){
			bSelect = true;
			bottomLayout.setVisibility(View.VISIBLE);
			titleTextView.setText("选择收货地址");
			if(getIntent().hasExtra("mContact")){
				mContact = (Contact)getIntent().getSerializableExtra("mContact");
			}
		}
		registerForContextMenu(gridView);
		gridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selected = position;
				adapter.notifyDataSetChanged();
			}
		});
		contacts = new ArrayList<Contact>();
		adapter = new ShdzAdapter();
		gridView.setAdapter(adapter);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "编辑");
		menu.add(0, 1, 0, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			Contact contact = contacts.get(info.position);
			Intent intent = new Intent(this, ContactActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("contact", contact);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case 1:
			dContact = contacts.get(info.position);
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
					delete(dContact.getId());
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
	        contentView.setText("确定要删除选中的收货地址吗？");
	        dialog.show();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("收货地址选择");
		MobclickAgent.onResume(this);
		loaddata();
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("收货地址选择");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterForContextMenu(gridView);
	}

	public void loaddata(){
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
	
	private void delete(final int cid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deleteContact(cid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	class ShdzHolder{
		public TextView nameTextView;
		public TextView phoneTextView;
//		public TextView postTextView;
		public TextView addressTextView;
		public RadioButton radioButton;
		public ShdzHolder(View view){
			nameTextView = (TextView)view.findViewById(R.id.name_text);
			phoneTextView = (TextView)view.findViewById(R.id.phone_text);
//			postTextView = (TextView)view.findViewById(R.id.post_text);
			addressTextView = (TextView)view.findViewById(R.id.address_text);
			radioButton = (RadioButton)view.findViewById(R.id.rd_shdz);
		}
	}
	
	class ShdzAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return contacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ShdzHolder shdzHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(ContactsActivity.this)
						.inflate(R.layout.cell_shdz, parent, false);
				shdzHolder = new ShdzHolder(convertView);
				convertView.setTag(shdzHolder);
			}
			shdzHolder = (ShdzHolder)convertView.getTag();
			Contact contact = contacts.get(position);
			shdzHolder.nameTextView.setText("姓名："+contact.getName());
			shdzHolder.phoneTextView.setText("电话："+contact.getPhone());
//			shdzHolder.postTextView.setText("邮编："+contact.getPostnumber());
			shdzHolder.addressTextView.setText("详细地址："+contact.getAddress());
			if(bSelect){
				shdzHolder.radioButton.setVisibility(View.VISIBLE);
				shdzHolder.radioButton.setEnabled(false);
				if(position == selected){
					shdzHolder.radioButton.setChecked(true);
				}
				else{
					shdzHolder.radioButton.setChecked(false);
				}
			}
			else{
				shdzHolder.radioButton.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.add_button:
			Intent intent = new Intent(this, ContactActivity.class);
			startActivity(intent);
			break;
		case R.id.ok_button:
			if(contacts==null||contacts.size()==0){
				GFToast.show(getApplicationContext(),"请添加收货地址");
			}
			else{
				Intent it = getIntent();
				Contact contact = contacts.get(selected);
				it.putExtra("contact", contact);
				setResult(RESULT_OK, it);
				finish();
			}
			
			break;

		default:
			break;
		}
	}
	

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Contact> cons = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Contact>>() {
						}.getType());
				contacts.clear();
				if(cons!=null && cons.size()>0){
					for(int i=0;i<cons.size();i++){
						Contact contact = cons.get(i);
						if(mContact!=null&&contact.getId().equals(mContact.getId())){
							selected = i;
						}
						contacts.add(contact);
					}
				}
				adapter.notifyDataSetChanged();
				
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				GFToast.show(getApplicationContext(),strerr);
			}
			else{
				contacts.remove(dContact);
				adapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}
}

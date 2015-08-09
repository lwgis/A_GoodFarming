package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Nys;
import com.zhonghaodi.model.User;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyFollowsActivity extends Activity implements HandMessage,OnClickListener {
	private ListView gridView;
	private List<Nys> nyss;
	private NysAdapter adapter;
	private GFHandler<MyFollowsActivity> handler = new GFHandler<MyFollowsActivity>(this);
	private String uid;
	private Button addButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myfollows);
		gridView = (ListView)findViewById(R.id.pull_refresh_list);
		addButton = (Button)findViewById(R.id.add_button);
		addButton.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				NysHolder nHolder = (NysHolder)view.getTag();
				Nys nys = nyss.get(position);
				if(nys.getLevel().getId()==3){
					Intent intent = new Intent(MyFollowsActivity.this, StoreActivity.class);
					Bundle bundle = new Bundle();
					User user = (User)nys;
					bundle.putSerializable("store", user);
					if(nHolder.followButton.getVisibility()==view.GONE){
						intent.putExtra("bfollow", true);
					}
					else{
						intent.putExtra("bfollow", false);
					}
					intent.putExtras(bundle);
					startActivity(intent);
				}
				else{
					Intent intent = new Intent(MyFollowsActivity.this, NysActivity.class);
					intent.putExtra("uid", nys.getId());
					if(nHolder.followButton.getVisibility()==view.GONE){
						intent.putExtra("bfollow", true);
					}
					else{
						intent.putExtra("bfollow", false);
					}
					MyFollowsActivity.this.startActivity(intent);
				}
				
			}
		});
		nyss = new ArrayList<Nys>();
		adapter = new NysAdapter();
		gridView.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		uid = GFUserDictionary.getUserId();
		if(uid==null || uid.isEmpty()){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return;
		}
		loadNyss();
	}
	
	/**
	 * 获取农艺师
	 */
	private void loadNyss(){
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String jsString = HttpUtil.getFollows(uid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 1;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
	}
	
	private void popupDialog(){
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setText("现在就去");
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent1 = new Intent(MyFollowsActivity.this, NyssActivity.class);
				MyFollowsActivity.this.startActivity(intent1);
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setText("先不去");
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
        titleView.setText("提示");
        contentView.setText("您还没有关注的农艺师，现在就去关注吧？");
        dialog.show();
	}
	
	class NysHolder{
		public ImageView imageView;
		public TextView nameTextView;
		public TextView pointTextView;
		public Button followButton;
		public NysHolder(View view){
			imageView = (ImageView)view.findViewById(R.id.head_image);
			nameTextView = (TextView)view.findViewById(R.id.name_text);
			pointTextView = (TextView)view.findViewById(R.id.point_text);
			followButton = (Button)view.findViewById(R.id.follow_btn);
		}
	}
	
	class NysAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return nyss.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return nyss.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			NysHolder nysHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(MyFollowsActivity.this)
						.inflate(R.layout.cell_nys, parent, false);
				nysHolder = new NysHolder(convertView);
				convertView.setTag(nysHolder);
			}
			nysHolder = (NysHolder)convertView.getTag();
			Nys nys = nyss.get(position);
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ nys.getThumbnail(),
							nysHolder.imageView, ImageOptions.options);
			nysHolder.nameTextView.setText(nys.getAlias());
			nysHolder.pointTextView.setText(nys.getDescription());
			nysHolder.followButton.setVisibility(View.GONE);
			nysHolder.followButton.setTag(nys);
			nysHolder.followButton.setOnClickListener(MyFollowsActivity.this);
			
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,SearchActivity.class);
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final MyFollowsActivity nysactivity =(MyFollowsActivity)object;
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Nys> nyss1 = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Nys>>() {
						}.getType());
				nysactivity.nyss.clear();
				for (Nys nys : nyss1) {
					nysactivity.nyss.add(nys);
				}
				
				
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Nys> nyss1 = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Nys>>() {
						}.getType());
				nysactivity.nyss.clear();
				for (Nys nys : nyss1) {
					nysactivity.nyss.add(nys);
				}
				nysactivity.adapter.notifyDataSetChanged();
				if(nysactivity.nyss.size()==0){
					popupDialog();
				}
				
			} else {
				Toast.makeText(this, "获取关注列表失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		

		default:
			break;
		}
	}

}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HoldFunction;
import com.zhonghaodi.model.Follow;
import com.zhonghaodi.model.Function;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Nys;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NysActivity extends Activity implements HandMessage,OnClickListener {

	private String uid;
	private boolean bfollow;
	private Nys user;
	private ArrayList<Function> functions;
	private PullToRefreshListView pullToRefreshList;
	private NysAdapter adapter;
	private GFHandler<NysActivity> handler = new GFHandler<NysActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nys);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		uid = getIntent().getStringExtra("uid");
		bfollow = getIntent().getBooleanExtra("bfollow", false);
		pullToRefreshList = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		adapter = new NysAdapter();
		functions = new ArrayList<Function>();
		pullToRefreshList.setAdapter(adapter);
		pullToRefreshList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData();
			}
		});
		loadData();
	}
	
	public void loadData() {
		functions.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void follow(){
		final String mid = GFUserDictionary.getUserId();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.follow(mid,user);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
	}
	
	public void cancelfollow(){
		final String mid = GFUserDictionary.getUserId();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.cancelfollow(mid,user);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
	}
	
	class NysInfoHolder{
		public ImageView headIv;
		public TextView titleTv;
		public TextView jifenTv;
		public TextView youhuibiTv;
		public Button followButton;
		public Button chatButton;
		public NysInfoHolder(View view){
			headIv=(ImageView)view.findViewById(R.id.head_image);
			titleTv=(TextView)view.findViewById(R.id.title_text);
			jifenTv=(TextView)view.findViewById(R.id.jifen_text);
			youhuibiTv=(TextView)view.findViewById(R.id.youhuibi_text);
			followButton = (Button)view.findViewById(R.id.follow_btn);
			chatButton = (Button)view.findViewById(R.id.chat_btn);
		}
	}
	
	class NysAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (user == null) {
				return 0;
			}
			return functions.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			}
			return 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NysInfoHolder holderNysInfo;
			HoldFunction holdFunction;
			int cellType = getItemViewType(position);
			if (convertView == null) {
				switch (cellType) {
				case 0:
					convertView = LayoutInflater.from(
							NysActivity.this).inflate(
							R.layout.cell_nys_info, parent, false);
					holderNysInfo = new NysInfoHolder(convertView);
					convertView.setTag(holderNysInfo);
					break;
				case 1:
					convertView=LayoutInflater.from(NysActivity.this).inflate(R.layout.cell_function, parent, false);
					holdFunction=new HoldFunction(convertView);
					convertView.setTag(holdFunction);
				default:
					break;
				}

			}
			switch (cellType) {
			case 0:
				holderNysInfo = (NysInfoHolder) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/users/small/"
								+ user.getThumbnail(), holderNysInfo.headIv,
						ImageOptions.optionsNoPlaceholder);
				holderNysInfo.titleTv.setText(user.getAlias());
				holderNysInfo.jifenTv.setText(String.valueOf(user.getPoint()));
				if(bfollow){
					holderNysInfo.followButton.setText("取消关注");
				}
				else{
					holderNysInfo.followButton.setText("关注");
				}
				holderNysInfo.followButton.setOnClickListener(NysActivity.this);
				holderNysInfo.chatButton.setOnClickListener(NysActivity.this);
				
				break;
			case 1:
				holdFunction=(HoldFunction)convertView.getTag();
				holdFunction.leftIv.setImageResource(functions.get(position-1).getImageId());
				holdFunction.titileTv.setText(functions.get(position-1).getName());
				break;
			default:
				break;
			}
			return convertView;
		}

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.follow_btn:
			Button btn = (Button)v;
			if(btn.getText().equals("关注")){
				follow();
			}
			else{
				cancelfollow();
			}
			break;
		case R.id.chat_btn:
			Intent it = new Intent();
			it.setClass(this, ChatActivity.class);
			it.putExtra("userName", user.getPhone());
			it.putExtra("title", user.getAlias());
			it.putExtra("thumbnail", user.getThumbnail());
			startActivity(it);
			break;

		default:
			break;
		}
	}
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		NysActivity nysactivity = (NysActivity) object;
		switch (msg.what) {
		case 0:
			if (msg.obj == null) {
				Toast.makeText(this, "获取失败,请稍后再试",
						Toast.LENGTH_SHORT).show();
				return;
			}
			nysactivity.user = (Nys) GsonUtil
					.fromJson(msg.obj.toString(), Nys.class);
			
			nysactivity.pullToRefreshList.onRefreshComplete();
			adapter.notifyDataSetChanged();
			break;
			
		case 1:
			if(msg.obj != null){
				Gson gson = new Gson();
				Follow follow = gson.fromJson(msg.obj.toString(),
						new TypeToken<Follow>() {
						}.getType());
				if(follow!=null){
					bfollow = true;
					nysactivity.adapter.notifyDataSetChanged();
					Toast.makeText(this, "关注成功!",
							Toast.LENGTH_SHORT).show();
				}
				
			}
			else{
				Toast.makeText(this, "关注农艺师失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if (msg.obj != null) {
				String jsString = msg.obj.toString();
				if(jsString!=""){
					Toast.makeText(this, "取消关注出错！",
							Toast.LENGTH_SHORT).show();
				}
				else{
					bfollow = false;
					nysactivity.adapter.notifyDataSetChanged();
					
				}
				
			} else {
				Toast.makeText(this, "取消关注出错！",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		
	}
}

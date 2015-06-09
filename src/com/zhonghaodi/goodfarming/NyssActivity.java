package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import u.aly.v;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.Follow;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Nys;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NyssActivity extends Activity implements OnClickListener,HandMessage {
	
	private ListView gridView;
	private List<Nys> nyss;
	private NysAdapter adapter;
	private GFHandler<NyssActivity> handler = new GFHandler<NyssActivity>(this);
	private String uid;
	private List<String> fuids;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nyss);
		gridView = (ListView)findViewById(R.id.pull_refresh_list);
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
				Intent intent = new Intent(NyssActivity.this, NysActivity.class);
				intent.putExtra("uid", nys.getId());
				if(nHolder.followButton.getVisibility()==view.GONE){
					intent.putExtra("bfollow", true);
				}
				else{
					intent.putExtra("bfollow", false);
				}
				NyssActivity.this.startActivity(intent);
			}
		});
		nyss = new ArrayList<Nys>();
		fuids = new ArrayList<String>();
		adapter = new NysAdapter();
		gridView.setAdapter(adapter);
		uid = GFUserDictionary.getUserId();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadNyss();
	}
	
	/**
	 * 获取农艺师
	 */
	private void loadNyss(){
		
		final String uid = GFUserDictionary.getUserId();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getNyss(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
				String jsString = HttpUtil.getFollows(uid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 1;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
	}
	
	private void follow(final Nys nys) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.follow(uid,nys);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
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
				convertView = LayoutInflater.from(NyssActivity.this)
						.inflate(R.layout.cell_nys, parent, false);
				nysHolder = new NysHolder(convertView);
				convertView.setTag(nysHolder);
			}
			nysHolder = (NysHolder)convertView.getTag();
			Nys nys = nyss.get(position);
			ImageLoader.getInstance().displayImage(
					"http://121.40.62.120/appimage/users/small/"
							+ nys.getThumbnail(),
							nysHolder.imageView, ImageOptions.options);
			nysHolder.nameTextView.setText(nys.getAlias());
			nysHolder.pointTextView.setText(nys.getDescription());
			if(fuids.contains(nys.getId())){
				nysHolder.followButton.setVisibility(View.GONE);
			}
			else{
				nysHolder.followButton.setVisibility(View.VISIBLE);
			}
			nysHolder.followButton.setTag(nys);
			nysHolder.followButton.setOnClickListener(NyssActivity.this);
			
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.follow_btn:
			
			Nys n = (Nys)v.getTag();
			
			follow(n);
			break;

		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final NyssActivity nysactivity =(NyssActivity)object;
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
				List<String> fids = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<String>>() {
						}.getType());
				nysactivity.fuids.clear();
				for(String fid:fids){
					nysactivity.fuids.add(fid);
				}
				nysactivity.adapter.notifyDataSetChanged();
				
			} else {
				Toast.makeText(this, "获取关注列表失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if(msg.obj != null){
				Gson gson = new Gson();
				Follow follow = gson.fromJson(msg.obj.toString(),
						new TypeToken<Follow>() {
						}.getType());
				if(follow!=null){
					nysactivity.fuids.add(follow.getUser().getId());
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

		default:
			break;
		}
		
	}

}

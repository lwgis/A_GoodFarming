package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.NyssActivity.NysHolder;
import com.zhonghaodi.model.Follow;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Nys;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

public class SearchActivity extends Activity implements HandMessage,OnClickListener,TextWatcher {

	private ListView listView;
	private List<User> users;
	private List<String> fuids;
	private UserAdapter adapter;
	private GFHandler<SearchActivity> handler = new GFHandler<SearchActivity>(this);
	private EditText phonEditText;
	private Button searBtn;
	private View clickview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		listView = (ListView)findViewById(R.id.pull_refresh_list);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		phonEditText = (EditText)findViewById(R.id.searchExt);
		phonEditText.addTextChangedListener(this);
		searBtn = (Button)findViewById(R.id.search_btn);
		searBtn.setOnClickListener(this);
		users = new ArrayList<User>();
		adapter = new UserAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				NysHolder nHolder = (NysHolder)view.getTag();
				User user = users.get(position);
				if(user.getLevel().getId()==3){
					Intent intent = new Intent(SearchActivity.this, StoreActivity.class);
					Bundle bundle = new Bundle();
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
					Intent intent = new Intent(SearchActivity.this, NysActivity.class);
					intent.putExtra("uid", user.getId());
					if(nHolder.followButton.getVisibility()==view.GONE){
						intent.putExtra("bfollow", true);
					}
					else{
						intent.putExtra("bfollow", false);
					}
					SearchActivity.this.startActivity(intent);
				}
				
				
			}
		});
		fuids = new ArrayList<String>();
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadMyFollows();
	}


	/**
	 * 获取我的关注
	 */
	private void loadMyFollows(){
		
		final String uid=GFUserDictionary.getUserId(getApplicationContext());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String jsString = HttpUtil.getFollowids(uid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 0;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
	}
	
	public void Search(final String phone){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				NetResponse netResponse = HttpUtil.getUserByPhone(phone);
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
	
	private void follow(final User user) {
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				NetResponse netResponse = HttpUtil.follow(uid,user);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 2;
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
	
	class NysHolder{
		public ImageView imageView;
		public TextView nameTextView;
		public TextView desTextView;
		public TextView levelTextView;
		public Button followButton;
		public NysHolder(View view){
			imageView = (ImageView)view.findViewById(R.id.head_image);
			nameTextView = (TextView)view.findViewById(R.id.name_text);
			desTextView = (TextView)view.findViewById(R.id.des_text);
			levelTextView = (TextView)view.findViewById(R.id.level_text);
			followButton = (Button)view.findViewById(R.id.follow_btn);
		}
	}
	class UserAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return users.get(position);
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
				convertView = LayoutInflater.from(SearchActivity.this)
						.inflate(R.layout.cell_contact, parent, false);
				nysHolder = new NysHolder(convertView);
				convertView.setTag(nysHolder);
			}
			nysHolder = (NysHolder)convertView.getTag();
			User user = users.get(position);
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ user.getThumbnail(),
							nysHolder.imageView, ImageOptions.options);
			nysHolder.nameTextView.setText(user.getAlias());
			nysHolder.levelTextView.setText(user.getLevel().getName());
			nysHolder.levelTextView.setVisibility(View.GONE);
			if(user.getLevel().getId()==2){
				List<UserCrop> userCrops = user.getCrops();
				String cropstrString="擅长的作物：";
				for (Iterator iterator = userCrops.iterator(); iterator
						.hasNext();) {
					UserCrop userCrop = (UserCrop) iterator.next();
					cropstrString+=userCrop.getCrop().getName()+" ";
				}
				cropstrString.trim();
				nysHolder.desTextView.setText(cropstrString);
			}
			else{
				nysHolder.desTextView.setText(user.getDescription());
			}
			if(fuids.contains(user.getId())){
				nysHolder.followButton.setVisibility(View.GONE);
			}
			else{
				nysHolder.followButton.setVisibility(View.VISIBLE);
				nysHolder.followButton.setTag(user);
				nysHolder.followButton.setOnClickListener(SearchActivity.this);
			}
			
			return convertView;
		}
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (phonEditText.getText().length() > 10) {
			searBtn.setEnabled(true);
		} else {
			searBtn.setEnabled(false);
			if(phonEditText.getText().length() ==0){
				users.clear();
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.search_btn:
			String phone = phonEditText.getText().toString();
			Search(phone);
			break;
		case R.id.follow_btn:
			
			User n = (User)v.getTag();
			clickview = v;
			follow(n);
			clickview.setEnabled(false);
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
				List<String> fids = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<String>>() {
						}.getType());
				fuids.clear();
				for(String fid:fids){
					fuids.add(fid);
				}
				adapter.notifyDataSetChanged();
				
			} else {
				GFToast.show(getApplicationContext(),"获取关注列表失败!");
			}
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<User> users1 = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<User>>() {
						}.getType());
				users.clear();
				if (users1 != null&&users1.size()>0) {
					for (Iterator iterator = users1.iterator(); iterator.hasNext();) {
						User user = (User) iterator.next();
						users.add(user);
					}
				}
				else{
					GFToast.show(getApplicationContext(),"用户不存在");
				}
				adapter.notifyDataSetChanged();
			}
			break;
		case 2:
			if(clickview!=null){
				clickview.setEnabled(true);
			}
			if(msg.obj != null){
				Gson gson = new Gson();
				Follow follow = gson.fromJson(msg.obj.toString(),
						new TypeToken<Follow>() {
						}.getType());
				if(follow!=null){
					fuids.add(follow.getUser().getId());
					adapter.notifyDataSetChanged();
					GFToast.show(getApplicationContext(),"关注成功!");
				}
				
			}
			else{
				GFToast.show(getApplicationContext(),"关注失败!");
			}
			break;
		default:
			break;
		}
		
	}

}

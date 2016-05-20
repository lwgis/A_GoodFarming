package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HoldFunction;
import com.zhonghaodi.customui.HolderMeInfo;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.Function;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.UmengConstants;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MeFragment extends Fragment implements HandMessage,OnClickListener{
	
	private User user;
	private ArrayList<Function> functions;
	private PullToRefreshListView pullToRefreshList;
	private MyTextButton siginButton;
	private MeAdapter adapter;
	private GFHandler<MeFragment> handler = new GFHandler<MeFragment>(this);
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, container, false);
		pullToRefreshList = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		
		adapter = new MeAdapter();
		functions = new ArrayList<Function>();
		siginButton = (MyTextButton)view.findViewById(R.id.sigin_button);
		siginButton.setOnClickListener(this);
		pullToRefreshList.setAdapter(adapter);
		pullToRefreshList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData();
			}
		});
		pullToRefreshList.setOnItemClickListener(new OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (position<2) {
					return;
				}
				Function function = functions.get(position-2);

				Intent it=new Intent();
				it.setClass(getActivity(), function.getActivityClass());
				if(function.getName().equals("我的交易") || function.getName().equals("我的资料") 
						|| function.getName().equals("邀请好友下载")){
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					it.putExtras(bundle);
					getActivity().startActivity(it);
				}
				else{
					getActivity().startActivity(it);
				}
				
			}
		});
		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("我Fragment");
		if(GFUserDictionary.getUserId(getActivity())!=null){
			loadData();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("我Fragment");
	}

	public void loadData() {
		functions.clear();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId(getActivity()));
				Message msg = handler.obtainMessage();
				msg.what=1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	class MeAdapter extends BaseAdapter {

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
			HolderMeInfo holderMeInfo;
			HoldFunction holdFunction;
			int cellType = getItemViewType(position);
			if (convertView == null) {
				switch (cellType) {
				case 0:
					convertView = LayoutInflater.from(
							MeFragment.this.getActivity()).inflate(
							R.layout.cell_me_info, parent, false);
					holderMeInfo = new HolderMeInfo(convertView);
					convertView.setTag(holderMeInfo);
					break;
				case 1:
					convertView=LayoutInflater.from(MeFragment.this.getActivity()).inflate(R.layout.cell_function, parent, false);
					holdFunction=new HoldFunction(convertView);
					convertView.setTag(holdFunction);
				default:
					break;
				}

			}
			switch (cellType) {
			case 0:
				holderMeInfo = (HolderMeInfo) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ user.getThumbnail(), holderMeInfo.headIv,
						ImageOptions.optionsNoPlaceholder);
				holderMeInfo.titleTv.setText(user.getAlias());
				holderMeInfo.jifenTv.setText(String.valueOf(user.getPoint()));
				holderMeInfo.youhuibiTv.setText(String.valueOf(user.getCurrency()));
				holderMeInfo.fensiTv.setText(String.valueOf(user.getFanscount()));
				holderMeInfo.fensiView.setOnClickListener(MeFragment.this);
				holderMeInfo.guanzhuView.setOnClickListener(MeFragment.this);
				holderMeInfo.guanzhuTv.setText(String.valueOf(user.getFollowcount()));
				holderMeInfo.tjcodeTv.setText(String.valueOf(user.getTjCode()));
				holderMeInfo.qrImageView.setOnClickListener(MeFragment.this);
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
	
	private void signin() {
		final String  uid= GFUserDictionary.getUserId(getActivity());
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.signIn(uid);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fensi_view:
			Intent intent = new Intent(getActivity(), MyFollowsActivity.class);
			intent.putExtra("status", 1);
			getActivity().startActivity(intent);
			break;
		case R.id.guanzhu_view:
			Intent intent1 = new Intent(getActivity(), MyFollowsActivity.class);
			getActivity().startActivity(intent1);
			break;
		case R.id.sigin_button:
			siginButton.setEnabled(false);
			signin();
			break;
		case R.id.qrcode_img:
			Intent intent2 = new Intent(getActivity(), AppdownActivity.class);
			intent2.putExtra("content", HttpUtil.ViewUrl+"appshare?code="+user.getTjCode());
			getActivity().startActivity(intent2);
			break;
		default:
			break;
		}
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		switch (msg.what) {
		case 1:
			MeFragment fragment = (MeFragment) object;
			if (msg.obj == null) {
				Toast.makeText(fragment.getActivity(), "获取失败,请稍后再试",
						Toast.LENGTH_SHORT).show();
				return;
			}
			fragment.user = (User) GsonUtil
					.fromJson(msg.obj.toString(), User.class);
			GFUserDictionary.saveLoginInfo(getActivity(),user, GFUserDictionary.getPassword(getActivity()), getActivity());			
			Function questionFunction = new Function("我的提问", QuestionsActivity.class,R.drawable.myquestions);
			fragment.functions.add(questionFunction);
			Function cartFunction = new Function("我的交易", MyTransactionActivity.class,R.drawable.store);
			fragment.functions.add(cartFunction);
			Function shareFunction = new Function("邀请好友下载",AppShareActivity.class,R.drawable.appshare);
			fragment.functions.add(shareFunction);
			Function minfoFunction = new Function("我的资料", InformationActivity.class,R.drawable.me_s);
			fragment.functions.add(minfoFunction);				
			Function feedbackFunction = new Function("意见反馈", FeedBackActivity.class,R.drawable.report);
			fragment.functions.add(feedbackFunction);

			fragment.pullToRefreshList.onRefreshComplete();
			adapter.notifyDataSetChanged();
			break;
		case 2:
			siginButton.setEnabled(true);
			if(msg.obj!=null){
				MobclickAgent.onEvent(getActivity(), UmengConstants.USER_SIGNIN_ID);
				GFToast.show(getActivity().getApplicationContext(),msg.obj.toString());
				loadData();
			}
			else{
				GFToast.show(getActivity().getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;

		default:
			break;
		}
		
	}
}

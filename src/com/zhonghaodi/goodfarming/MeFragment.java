package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.api.ShareContainer;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HoldFunction;
import com.zhonghaodi.customui.HolderLogout;
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MeFragment extends Fragment implements HandMessage,OnClickListener{
	
	private ArrayList<Function> functions;
	private PullToRefreshListView pullToRefreshList;
	private MyTextButton siginButton;
	private Button settingBtn;
	private MeAdapter adapter;
	private GFHandler<MeFragment> handler = new GFHandler<MeFragment>(this);
	private ShareContainer shareContainer;

	public MeFragment(){
		
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
		settingBtn = (Button)view.findViewById(R.id.setting_button);
		settingBtn.setOnClickListener(this);
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
				if(function==null){
					return;
				}

				Intent it=new Intent();
				it.setClass(getActivity(), function.getActivityClass());
				if(function.getName().equals("优惠币使用") || function.getName().equals("资料设置")){
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", getUser());
					it.putExtras(bundle);
					getActivity().startActivity(it);
				}
				else if(function.getName().equals("我的收藏")){
					it.putExtra("status", 3);
					getActivity().startActivity(it);
				}
				else if(function.getName().equals("邀请好友赚积分")){
					if(shareContainer!=null){
						shareContainer.popupShareWindow(getUser());
					}					
				}
				else if(function.getName().equals("我的订单")){
					it.putExtra("level", getUser().getLevel().getId());
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
			if (getUser() == null) {
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
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			}
			else if(position==functions.size()){
				return 2;
			}
			else{
				return 1;
			}		
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderMeInfo holderMeInfo;
			HoldFunction holdFunction;
			HolderLogout outHolder;
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
					break;
				case 2:
					convertView=LayoutInflater.from(MeFragment.this.getActivity()).inflate(R.layout.cell_funbutton, parent, false);
					outHolder = new HolderLogout(convertView);
					convertView.setTag(outHolder);
					break;
				default:
					break;
				}

			}
			switch (cellType) {
			case 0:
				holderMeInfo = (HolderMeInfo) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ getUser().getThumbnail(), holderMeInfo.headIv,
						ImageOptions.optionsNoPlaceholder);
				User u1= getUser();
				holderMeInfo.titleTv.setText(getUser().getAlias());
				holderMeInfo.youhuibiTv.setText(String.valueOf(getUser().getCurrency()));
				holderMeInfo.fensiTv.setText(String.valueOf(getUser().getFanscount()));
				holderMeInfo.fensiView.setOnClickListener(MeFragment.this);
				holderMeInfo.guanzhuView.setOnClickListener(MeFragment.this);
				holderMeInfo.guanzhuTv.setText(String.valueOf(getUser().getFollowcount()));
				holderMeInfo.tjcodeTv.setText(getUser().getTjCode());
				holderMeInfo.reccountTv.setText(Html.fromHtml("<u>"+String.valueOf(getUser().getRecCount())+"</u>"));
				holderMeInfo.tjcoinTv.setText(String.valueOf(getUser().getTjcoin()));
				holderMeInfo.qrImageView.setOnClickListener(MeFragment.this);
				holderMeInfo.recLayout.setOnClickListener(MeFragment.this);
				break;
			case 1:
				holdFunction=(HoldFunction)convertView.getTag();
				holdFunction.leftIv.setImageResource(functions.get(position-1).getImageId());
				holdFunction.titileTv.setText(functions.get(position-1).getName());
				if(functions.get(position-1).getDescription()==null){
					holdFunction.desTv.setVisibility(View.GONE);
				}
				else{
					holdFunction.desTv.setVisibility(View.VISIBLE);
					holdFunction.desTv.setText(functions.get(position-1).getDescription());
				}
				break;
			case 2:
				outHolder = (HolderLogout)convertView.getTag();
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
		case R.id.reclayout:
			Intent intent3 = new Intent(getActivity(), RecommendedActivity.class);
			getActivity().startActivity(intent3);
			break;
		case R.id.sigin_button:
			siginButton.setEnabled(false);
			signin();
			break;
		case R.id.qrcode_img:
			Intent intent2 = new Intent(getActivity(), AppdownActivity.class);
			intent2.putExtra("content", HttpUtil.ViewUrl+"appshare?code="+getUser().getTjCode());
			getActivity().startActivity(intent2);
			break;
		case R.id.setting_button:
			Intent it = new Intent(getActivity(), InformationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("user", getUser());
			it.putExtras(bundle);
			getActivity().startActivity(it);
			break;
		default:
			break;
		}
		
	}
	
	private User getUser(){
		return UILApplication.user;
	}
	
	private void setUser(User u){
		UILApplication.user=u;
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		if(getActivity()==null){
			return;
		}
		switch (msg.what) {
		case 1:
			
			if (msg.obj == null) {
				Toast.makeText(getActivity(), "获取失败,请稍后再试",
						Toast.LENGTH_SHORT).show();
				return;
			}
			User user = (User) GsonUtil
					.fromJson(msg.obj.toString(), User.class);
			setUser(user);
			GFUserDictionary.saveLoginInfo(getActivity(),user,
					GFUserDictionary.getPassword(getActivity()), getActivity(),
					GFUserDictionary.getAuth(getActivity()));	
			functions.clear();
			Function questionFunction = new Function("我的提问", QuestionsActivity.class,R.drawable.myquestions1);
			functions.add(questionFunction);
			Function favFunction = new Function("我的收藏", MyQuestionsActivity.class,R.drawable.shoucang1);
			functions.add(favFunction);
			Function ordersFunction = new Function("我的订单", OrdersActivity.class,R.drawable.order1);
			functions.add(ordersFunction);
			Function cartFunction = new Function("优惠币使用", MyTransactionActivity.class,R.drawable.yhbsh1);
			functions.add(cartFunction);
			Function shareFunction = new Function("邀请好友赚积分",AppShareActivity.class,R.drawable.appshare1);
			functions.add(shareFunction);			
			Function feedbackFunction = new Function("意见反馈", FeedBackActivity.class,R.drawable.report1);
			functions.add(feedbackFunction);
			functions.add(null);
			pullToRefreshList.onRefreshComplete();
			adapter.notifyDataSetChanged();
			break;
		case 2:
			siginButton.setEnabled(true);
			if(msg.obj!=null){
				MobclickAgent.onEvent(getActivity(), UmengConstants.USER_SIGNIN_ID);
				GFToast.show(getActivity().getApplicationContext(),msg.obj.toString());
//				loadData();
			}
			else{
				GFToast.show(getActivity().getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;

		default:
			break;
		}
		
	}

	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}
}

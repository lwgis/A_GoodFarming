package com.zhonghaodi.goodfarming;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.platform.comapi.map.u;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HoldFunction;
import com.zhonghaodi.customui.HolderMeInfo;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.Function;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
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
				if (function.getName().equals("APP分享")) {
					MainActivity mainActivity = (MainActivity)getActivity();
					mainActivity.popwindow();
					return;
				}
				Intent it=new Intent();
				it.setClass(getActivity(), function.getActivityClass());
				if(function.getName().equals("当面付") || function.getName().equals("修改资料")){
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					it.putExtras(bundle);
					getActivity().startActivity(it);
				}
				else if(function.getName().contains("作物")){
					Intent it1 = new Intent(getActivity(),
							SelectCropActivity.class);
					ArrayList<Crop> selectCrops = null;
					if(user.getCrops()!=null && user.getCrops().size()>0){
						selectCrops = new ArrayList<Crop>();
						for (Iterator iterator = user.getCrops().iterator(); iterator.hasNext();) {
							UserCrop userCrop = (UserCrop) iterator.next();
							selectCrops.add(userCrop.getCrop());
						}
					}
					if (selectCrops != null) {
						it1.putParcelableArrayListExtra("crops", selectCrops);
					}
					getActivity().startActivityForResult(it1, 100);
				}
				else{
					getActivity().startActivity(it);
				}
				
			}
		});
		return view;
	}

	public void loadData() {
		functions.clear();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId());
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
		final String  uid= GFUserDictionary.getUserId();
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
			GFUserDictionary.saveLoginInfo(user, GFUserDictionary.getPassword(), getActivity());
			Function cartFunction = new Function("我的订单", OrdersActivity.class,R.drawable.store);
			fragment.functions.add(cartFunction);
			Function cropsFunction = new Function("我的作物", SelectCropActivity.class,R.drawable.crop);
			fragment.functions.add(cropsFunction);
			Function contactsFunction = new Function("收货地址", ContactsActivity.class,R.drawable.address);
			fragment.functions.add(contactsFunction);
			Function exchangeFunction = new Function("积分兑换", ExchangeActivity.class,R.drawable.exchange);
			fragment.functions.add(exchangeFunction);
			Function payFunction = new Function("当面付", PayActivity.class,R.drawable.pay);
			fragment.functions.add(payFunction);
			if(user.getLevel().getId()==3){
				Function orderFunction = new Function("扫一扫", OrderScanActivity.class,R.drawable.scan);
				fragment.functions.add(orderFunction);
			}
			switch (user.getLevel().getId()) {
			case 1:
				Function nysfuFunction = new Function("升级为农艺师", UpdateNysActivity.class,R.drawable.nysupdate);
				fragment.functions.add(nysfuFunction);
				Function nzdFunction = new Function("升级为农资店", UpdateNzdActivity.class,R.drawable.nzdupdate);
				fragment.functions.add(nzdFunction);
				break;	
			case 2:
				Function zjFunction = new Function("升级为专家", UpdateZjActivity.class, R.drawable.zjupdate);
				fragment.functions.add(zjFunction);
			default:
				break;
			}
			Function minfoFunction = new Function("修改资料", ModifyInfoActivity.class,R.drawable.me_s);
			fragment.functions.add(minfoFunction);
			Function modifyFunction = new Function("修改密码", ModifyPassActivity.class,R.drawable.password);
			fragment.functions.add(modifyFunction);				
			Function shareFunction = new Function("APP分享",null,R.drawable.weixin);
			fragment.functions.add(shareFunction);
			Function downFunction = new Function("推荐农友下载", AppdownActivity.class,R.drawable.appdownload);
			fragment.functions.add(downFunction);
			Function feedbackFunction = new Function("意见反馈", FeedBackActivity.class,R.drawable.report);
			fragment.functions.add(feedbackFunction);
			fragment.pullToRefreshList.onRefreshComplete();
			adapter.notifyDataSetChanged();
			break;
		case 2:
			siginButton.setEnabled(true);
			if(msg.obj!=null){
				GFToast.show(msg.obj.toString());
				loadData();
			}
			else{
				GFToast.show("连接服务器失败,请稍候再试!");
			}
			break;

		default:
			break;
		}
		
	}
}

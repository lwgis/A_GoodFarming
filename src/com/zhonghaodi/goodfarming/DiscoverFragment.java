package com.zhonghaodi.goodfarming;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.api.ShareContainer;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.WelcomeActivity.WelcomeLocationListenner;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GFVersionAndAds;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.req.FrmDiscoverReq;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.FrmDiscoverView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment implements OnClickListener,FrmDiscoverView {
	
	// 定位相关
//	private LocationClient mLocClient;
//	public DiscoverLocationListenner myListener = new DiscoverLocationListenner();
//	private double x,y;
	
	private View nzdView;
	private View nysView;
	private View nyqView;
	private View rubblerView;
	private View bchView;
	private View scdtView;
	private View cnzView;
	private View jfscView;
	private View hbView;
	private TextView cnzText;
	private TextView pointText;
	private ShareContainer shareContainer;
	
	private FrmDiscoverReq req;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_discover, container, false);
		
		nzdView = view.findViewById(R.id.layout1);
		nzdView.setOnClickListener(this);
		nysView = view.findViewById(R.id.layout2);
		nysView.setOnClickListener(this);
		jfscView = view.findViewById(R.id.layout6);
		jfscView.setOnClickListener(this);
		cnzView = view.findViewById(R.id.layout4);
		cnzView.setOnClickListener(this);
		bchView = view.findViewById(R.id.layout9);
		bchView.setOnClickListener(this);
		scdtView = view.findViewById(R.id.layout3);
		scdtView.setVisibility(View.GONE);
		scdtView.setOnClickListener(this);
		hbView = view.findViewById(R.id.layout10);
		hbView.setOnClickListener(this);
		rubblerView = view.findViewById(R.id.layout8);
		rubblerView.setOnClickListener(this);
		cnzText = (TextView)view.findViewById(R.id.ncnz_text);
		pointText  = (TextView)view.findViewById(R.id.des_text);
		req = new FrmDiscoverReq(this, getActivity());
//		location();
		return view;
	}
	
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("发现Fragment");
		if(GFVersionAndAds.getCnzcount(getActivity())==0){
			cnzText.setVisibility(View.VISIBLE);
		}
		else{
			cnzText.setVisibility(View.GONE);
		}
		req.loadData();
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("发现Fragment");
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
		if(uid==null){
			Intent intent = new Intent(getActivity(), SignActivity.class);
			getActivity().startActivity(intent);
			return;
		}
		switch (v.getId()) {
		case R.id.layout1:
			Intent intent = new Intent(getActivity(), NzdsActivity.class);
			startActivity(intent);
			break;
		case R.id.layout2:
			Intent intent1 = new Intent(getActivity(), NyssActivity.class);
			startActivity(intent1);
			break;
		case R.id.layout3:
			Intent intent2 = new Intent(getActivity(), SGBCActivity.class);
			startActivity(intent2);
			break;
		case R.id.layout4:
			Intent intent3 = new Intent(getActivity(), CaicaicaiActivity.class);
			startActivity(intent3);
			break;
		case R.id.layout5:
			Intent intent4 = new Intent(getActivity(), MiaoActivity.class);
			startActivity(intent4);
			break;
		case R.id.layout6:
			Intent intent5 = new Intent(getActivity(), CommoditiesActivity.class);
			startActivity(intent5);
			break;
//		case R.id.layout7:
//			Intent intent9 = new Intent(getActivity(), ZfbtActivity.class);
//			startActivity(intent9);
//			break;
		case R.id.layout9:
			Intent intent8 = new Intent(getActivity(), FarmCropsActivity.class);
			startActivity(intent8);
			break;
			case R.id.layout8:
			int point = GFUserDictionary.getPoint(getActivity());
			int guagua = GFPointDictionary.getGuaguaPoint(getActivity());
			if(point>=guagua){
				Intent intent7 = new Intent(getActivity(), RubblerActivity.class);
				startActivity(intent7);
			}
			else{
				GFToast.show(getActivity(),"您的积分不足！");
			}
			break;
		case R.id.layout10:
			if(shareContainer!=null){
				shareContainer.popupShareWindow(req.getmUser());
			}	
			break;
		default:
			break;
		}
	}
	
//	private void location() {
//		
//		mLocClient = new LocationClient(getActivity().getApplicationContext());
//		mLocClient.registerLocationListener(myListener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);// 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(5000);
//		mLocClient.setLocOption(option);
//		mLocClient.start();
//	}
	
//	/**
//	 * 定位SDK监听函数
//	 */
//	public class DiscoverLocationListenner implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null){
//				return;
//			}				
//			x=location.getLongitude();
//			y=location.getLatitude();
//			x=118.798632;
//			y=36.858719;
//			req.loadArea(x,y);
//			mLocClient.stop();
//			
//		}
//		public void onReceivePoi(BDLocation poiLocation) {
//		}
//	}

	@Override
	public void displayPoints(String points) {
		// TODO Auto-generated method stub
		if(getActivity()==null){
			return;
		}
		if(!TextUtils.isEmpty(points)){
			pointText.setText(points+"积分");
		}
	}



	@Override
	public void confirmCity(City city) {
		// TODO Auto-generated method stub
		if(city.getName().contains("寿光")){
			scdtView.setVisibility(View.GONE);
		}
		else{
			scdtView.setVisibility(View.GONE);
		}
	}



	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}
	
}

package com.zhonghaodi.goodfarming;

import java.util.Iterator;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.SaveAdapter;
import com.zhonghaodi.customui.CustomProgressDialog;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.SpinnerDto;
import com.zhonghaodi.model.Zfbt;
import com.zhonghaodi.req.FrmSaveReq;
import com.zhonghaodi.view.FrmSaveView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class SaveFragment extends Fragment implements OnItemClickListener,FrmSaveView{
	private PullToRefreshListView pullToRefreshListView;
	private SaveAdapter adapter;
	private LinearLayout containerLayout;
	private int page=0;
	private FrmSaveReq req;
	private double x;
	private double y;
	private CustomProgressDialog progressDialog;
	// 定位相关
	LocationClient mLocClient;
	public MiaoLocationListenner myListener = new MiaoLocationListenner();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_save, container, false);
		req = new FrmSaveReq(this, getActivity());
		containerLayout = (LinearLayout)view.findViewById(R.id.container_layout);
		req.loadSaveCates();
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);	
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if(req.zfbts.size()==0){
					pullToRefreshListView.onRefreshComplete();
					return;
				}
				int k =req.zfbts.size()%20;
				if(k==0){
					page = req.zfbts.size()/20;
					req.loadMoreData(x,y,page);
				}
				else{
					page = req.zfbts.size()/20+1;
					req.loadMoreData(x,y,page);
				}
			}

			
		});	
		adapter = new SaveAdapter(req.zfbts, getActivity());
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);	
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("超实惠Fragment");
		location();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("超实惠Fragment");
	}
	
	private void location() {
		if(progressDialog==null){
			progressDialog = new CustomProgressDialog(getActivity(), "定位中...");
		}
		progressDialog.show();
		mLocClient = new LocationClient(getActivity().getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Zfbt zfbt = req.zfbts.get(position-1);
		if(zfbt!=null){
			Intent intent = new Intent(getActivity(),ZfbtInfoActivity.class);
			intent.putExtra("zid", zfbt.getId());
			intent.putExtra("x", x);
			intent.putExtra("y", y);
			getActivity().startActivity(intent);
		}
	}

	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(getActivity(), mess);
	}

	@Override
	public void showOrders(List<SecondOrder> orders) {
		// TODO Auto-generated method stub
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MiaoLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
//			x=location.getLongitude();
//			y=location.getLatitude();
			x=118.798632;
			y=36.858719;
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			req.loadData(x,y);
			mLocClient.stop();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		adapter.notifyDataSetChanged();
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	public void showCates(List<SpinnerDto> spds,int select) {
		// TODO Auto-generated method stub
		if(spds!=null && spds.size()>0){
			containerLayout.removeAllViews();
			for (Iterator iterator = spds.iterator(); iterator.hasNext();) {
				SpinnerDto spinnerDto = (SpinnerDto) iterator.next();
				View view = LayoutInflater.from(getActivity())
						.inflate(R.layout.item_cate, null,false);
				TextView caTextView = (TextView)view.findViewById(R.id.cate_text);
				caTextView.setText(spinnerDto.getName());
				caTextView.setTag(spinnerDto.getId());
				if(req.cate!=spinnerDto.getId()){
					caTextView.setTextColor(Color.rgb(128, 128, 128));
				}
				else{
					caTextView.setTextColor(Color.rgb(56, 190, 153));
				}
				caTextView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int c = Integer.parseInt(v.getTag().toString());
						req.cate = c;
						req.loadData(x, y);
						setSelectView();
					}
				});
				containerLayout.addView(view);
				
			}
		}
	}
	
	private void setSelectView(){
		for(int i=0;i<containerLayout.getChildCount();i++){
			View view = containerLayout.getChildAt(i);
			TextView caTextView = (TextView)view.findViewById(R.id.cate_text);
			int c = Integer.parseInt(caTextView.getTag().toString());
			if(req.cate!=c){
				caTextView.setTextColor(Color.rgb(128, 128, 128));
			}
			else{
				caTextView.setTextColor(Color.rgb(56, 190, 153));
			}
		}
	}
}

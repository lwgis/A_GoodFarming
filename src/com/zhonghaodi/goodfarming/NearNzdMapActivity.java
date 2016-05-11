package com.zhonghaodi.goodfarming;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ZoomControls;

public class NearNzdMapActivity extends Activity {
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private TextView tilteTv;
	private MyTextButton sendBtn;
	private MyTextButton cancelBtn;
	private MapView mapView;
    private BaiduMap map;
    private Marker marker;
    private double x,y;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_near_map_nzd);
		MobclickAgent.openActivityDurationTrack(false);
		mCurrentMode = LocationMode.NORMAL;
		tilteTv = (TextView) findViewById(R.id.title_text);
		tilteTv.setText("农资店位置");
		cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		sendBtn = (MyTextButton) findViewById(R.id.send_button);
		mapView = (MapView) findViewById(R.id.mapView);
		map=mapView.getMap();
//		map.setOnMapClickListener(new OnMapClickListener() {
//			
//			@Override
//			public boolean onMapPoiClick(MapPoi arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//			
//			@Override
//			public void onMapClick(LatLng arg0) {
//				marker.setPosition(arg0);
//			}
//		});
//		
//		map.setOnMarkerClickListener(new OnMarkerClickListener() {
//			
//			@Override
//			public boolean onMarkerClick(Marker arg0) {
//				GFToast.show(arg0.toString());
//				return true;
//			}
//		});
		
		sendBtn.setText("确定");
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				location();
			}
		});
		hideZoomControl();
		x = getIntent().getDoubleExtra("x", 0);
		y = getIntent().getDoubleExtra("y", 0);
		location();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("农资店位置");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("农资店位置");
		MobclickAgent.onPause(this);
	}

	/**
	 * 隐藏缩放按钮
	 */
	private void hideZoomControl() {
		int childCount = mapView.getChildCount();

		View zoom = null;

		for (int i = 0; i < childCount; i++) {

			View child = mapView.getChildAt(i);

			if (child instanceof ZoomControls) {

				zoom = child;

				break;

			}

		}

		zoom.setVisibility(View.GONE);
	}

	private void location() {
		mCurrentMode = LocationMode.FOLLOWING;
		mapView.getMap().setMyLocationConfigeration(
				new MyLocationConfiguration(mCurrentMode, true, null));
		// 开启定位图层
		mapView.getMap().setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

	}
	
	/**
	 * 定位到农资店
	 * @param x
	 * @param y
	 */
	private void zoomtoNzd(double x,double y){
	
		
		LatLng ll = new LatLng(y,x);
		OverlayOptions overlayOptions=new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_dian)).draggable(true);
		marker=(Marker)mapView.getMap().addOverlay(overlayOptions);
//		tilteTv.setText(String.valueOf(y)+"-"+String.valueOf(x));
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mapView.getMap().animateMapStatus(u);
		
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null)
				return;
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(0).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			mapView.getMap().setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			OverlayOptions overlayOptions=new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_me)).draggable(true);
			marker=(Marker)mapView.getMap().addOverlay(overlayOptions);
//			tilteTv.setText(String.valueOf(location.getLatitude())+"-"+String.valueOf(location.getLongitude()));
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mapView.getMap().animateMapStatus(u);
			mLocClient.stop();
			if(x!=0 && y!=0){
				zoomtoNzd(x, y);
			}
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}

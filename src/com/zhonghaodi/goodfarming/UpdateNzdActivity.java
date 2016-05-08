package com.zhonghaodi.goodfarming;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFImageButton;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.GFImageButton.ImageChangedListener;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Level;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.UpdateUser;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.ImageUtil;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ZoomControls;

public class UpdateNzdActivity extends Activity implements OnClickListener,
		HandMessage, TextWatcher, ImageChangedListener {
	private static final int TypeUpdate = 1;
	private static final int TypeImage = 2;
	private MyEditText nameEv;
	private MyEditText descriptionEv;
	private GFImageButton yyzzBtn;
	private GFImageButton zhengmianBtn;
	private GFImageButton fanmianBtn;
	private PopupWindow popupWindow;
	private View popView;
	private File currentfile;
	private MyTextButton sendBtn;
	private GFImageButton currentGFimageButton;
	private ArrayList<String> images;
	private double x=0.0;
	private double y=0.0;
	private MapView mapView;
	private BaiduMap map;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private GFHandler<UpdateNzdActivity> handler = new GFHandler<UpdateNzdActivity>(
			this);
    private Marker marker;
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_update_nzd);
		nameEv = (MyEditText) findViewById(R.id.name_edit);
		descriptionEv = (MyEditText) findViewById(R.id.description_edit);
		yyzzBtn = (GFImageButton) findViewById(R.id.yyzz_image);
		zhengmianBtn = (GFImageButton) findViewById(R.id.zhengmian_image);
		fanmianBtn = (GFImageButton) findViewById(R.id.fanmian_image);
		mapView=(MapView)findViewById(R.id.mapView);
		map=mapView.getMap();
		hideZoomControl();
		yyzzBtn.setTitle("营业执照");
		zhengmianBtn.setTitle("身份证正面");
		fanmianBtn.setTitle("身份证反面");
		MyTextButton cancelBtn = (MyTextButton) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sendBtn = (MyTextButton) findViewById(R.id.send_button);
		sendBtn.setEnabled(false);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendBtn.setEnabled(false);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							String yyzzString = ImageUtil.uploadImage(
									yyzzBtn.getBitmap(), "users");
							String zhengmianString = ImageUtil.uploadImage(
									zhengmianBtn.getBitmap(), "users");
							String fanmianString = ImageUtil.uploadImage(
									fanmianBtn.getBitmap(), "users");
							images = new ArrayList<String>();
							images.add(yyzzString);
							images.add(zhengmianString);
							images.add(fanmianString);
							Message msg = handler.obtainMessage();
							msg.what = TypeImage;
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				finish();
			}
		});
		nameEv.addTextChangedListener(this);
		descriptionEv.addTextChangedListener(this);
		popView = getLayoutInflater().inflate(
				R.layout.popupwindow_camera,
				(ViewGroup) getWindow().getDecorView().findViewById(
						android.R.id.content), false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(this, 180),
				DpTransform.dip2px(this, 100));
		yyzzBtn.setOnClickListener(this);
		zhengmianBtn.setOnClickListener(this);
		fanmianBtn.setOnClickListener(this);
		yyzzBtn.setImageChangedListener(this);
		zhengmianBtn.setImageChangedListener(this);
		fanmianBtn.setImageChangedListener(this);
		// 相机按钮
		Button btnCamera = (Button) popView.findViewById(R.id.btnCamera);
		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File fileCache = ImageOptions.getCache(UpdateNzdActivity.this);
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				// intent.addCategory(Intent.CATEGORY_DEFAULT);
				currentfile = new File(fileCache.getPath() + "/"
						+ UUID.randomUUID().toString() + ".jpg");
				if (currentfile.exists()) {
					currentfile.delete();
				}
				Uri uri = Uri.fromFile(currentfile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, 3);
				popupWindow.dismiss();
			}
		});
		// 相册按钮
		Button btnPhoto = (Button) popView.findViewById(R.id.btnPhoto);
		btnPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(it, 2);
				popupWindow.dismiss();
			}
		});
		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				marker.setPosition(arg0);
				x=arg0.longitude;
				y=arg0.latitude;
			}
		});
		location();
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

	@Override
	public void onClick(View v) {
		currentGFimageButton = (GFImageButton) v;
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(v, -DpTransform.dip2px(this, 0),
					DpTransform.dip2px(this, 0));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// 相册
			if (requestCode == 2) {
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				if (!uri.toString().contains("file://")) {
					Cursor cursor = this.getContentResolver().query(uri, proj,
							null, null, null);
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String imgPath = cursor.getString(column_index);
					currentGFimageButton.setImageFilePath(imgPath);
					cursor.close();
				} else {
					currentGFimageButton.setImageFilePath(uri.getPath());
				}
			}
			// 相机
			if (requestCode == 3) {
				String imgPath = currentfile.getPath();
				currentGFimageButton.setImageFilePath(imgPath);
			}
			checkUi();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			return true;
		}
		return super.dispatchTouchEvent(ev);
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
		checkUi();
	}

	private void checkUi() {
		if (nameEv.getText().length() > 0
				&& descriptionEv.getText().length() > 0 && yyzzBtn.isHasImage()
				&& zhengmianBtn.isHasImage() && fanmianBtn.isHasImage()&&x>0&&y>0) {
			sendBtn.setEnabled(true);
		} else {
			sendBtn.setEnabled(false);
		}
	}

	@Override
	public void imageChanged(View v, boolean isHasImage) {
		checkUi();
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
			OverlayOptions overlayOptions=new MarkerOptions().position(ll).icon(bdA).draggable(true);
			marker=(Marker)map.addOverlay(overlayOptions);
			x=location.getLongitude();
			y=location.getLatitude();
//			x=5e-324;
//			y=5e-324;
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			map.animateMapStatus(u);
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	
	@Override
	public void handleMessage(Message msg, Object object) {
		UpdateNzdActivity activity = (UpdateNzdActivity) object;
		switch (msg.what) {
		case 0:
			Toast.makeText(activity, "提交失败", Toast.LENGTH_SHORT).show();
			activity.sendBtn.setEnabled(true);
			break;
		case TypeImage:
			if(x<73||x>136||y<3||y>54){
				GFToast.show(getApplicationContext(),"定位错误。申请不能提交");
				return;
			}
			final UpdateUser updateUser = new UpdateUser();
			User user = new User();
			user.setId(GFUserDictionary.getUserId(getApplicationContext()));
			updateUser.setUser(user);
			updateUser.setName(activity.nameEv.getText().toString());
			updateUser.setDescription(activity.descriptionEv.getText()
					.toString());
			Level level = new Level();
			level.setId(3);
			updateUser.setLevel(level);
			updateUser.setX(x);
			updateUser.setY(y);
			ArrayList<NetImage> arrayList = new ArrayList<NetImage>();
			for (String imageNameString : images) {
				NetImage netImage = new NetImage();
				netImage.setUrl(imageNameString);
				arrayList.add(netImage);
			}
			updateUser.setAttachments(arrayList);
			new Thread(new Runnable() {

				@Override
				public void run() {
					Message msgUpdate = handler.obtainMessage();
					if (HttpUtil.updateUser(updateUser)) {
						msgUpdate.what = TypeUpdate;
					} else {
						msgUpdate.what = 0;
					}
					msgUpdate.sendToTarget();
				}
			}).start();

			break;
		case TypeUpdate:
			Toast.makeText(activity, "成功提交申请", Toast.LENGTH_SHORT).show();
			// activity.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mapView.onPause();
		super.onPause();
		MobclickAgent.onPageEnd("升级为农资店");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mapView.onResume();
		super.onResume();
		MobclickAgent.onPageStart("升级为农资店");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		map.setMyLocationEnabled(false);
		mapView.onDestroy();
		bdA.recycle();
		super.onDestroy();
	}

}

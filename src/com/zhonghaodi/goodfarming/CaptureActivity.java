package com.zhonghaodi.goodfarming;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.karics.library.zxing.android.BeepManager;
import com.karics.library.zxing.android.CaptureActivityHandler;
import com.karics.library.zxing.android.FinishListener;
import com.karics.library.zxing.android.InactivityTimer;
import com.karics.library.zxing.android.IntentSource;
import com.karics.library.zxing.camera.CameraManager;
import com.karics.library.zxing.view.ViewfinderView;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 * 
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback,HandMessage {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	// 相机控制
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	// 电量控制
	private InactivityTimer inactivityTimer;
	// 声音、震动控制
	private BeepManager beepManager;

	private ImageButton imageButton_back;
	
	private GFHandler<CaptureActivity> handler1 = new GFHandler<CaptureActivity>(this);
	private int miaostatus=0;

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// 保持Activity处于唤醒状态
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);
		MobclickAgent.openActivityDurationTrack(false);

		hasSurface = false;

		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		imageButton_back = (ImageButton) findViewById(R.id.capture_imageview_back);
		imageButton_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("二维码扫描");
		MobclickAgent.onResume(this);
		// CameraManager必须在这里初始化，而不是在onCreate()中。
		// 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
		// 当扫描框的尺寸不正确时会出现bug
		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// activity在paused时但不会stopped,因此surface仍旧存在；
			// surfaceCreated()不会调用，因此在这里初始化camera
			initCamera(surfaceHolder);
		} else {
			// 重置callback，等待surfaceCreated()来初始化camera
			surfaceHolder.addCallback(this);
		}

		beepManager.updatePrefs();
		inactivityTimer.onResume();

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		beepManager.close();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
		MobclickAgent.onPageEnd("二维码扫描");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 扫描成功，处理反馈信息
	 * 
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();

		boolean fromLiveScan = barcode != null;
		//这里处理解码完成后的结果，此处将参数回传到Activity处理
		if (fromLiveScan) {
			beepManager.playBeepSoundAndVibrate();

//			Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
//
//			Intent intent = getIntent();
//			intent.putExtra("codedContent", rawResult.getText());
//			intent.putExtra("codedBitmap", barcode);
//			setResult(RESULT_OK, intent);
//			finish();
			
			String codeString=rawResult.getText();
			String uid = GFUserDictionary.getUserId(getApplicationContext());
			if(codeString.contains("order:")){
				
				RequestOrder(uid,codeString.split(":")[1]);
			}
			else if(codeString.contains("pay:")){
				income(uid, codeString.split(":")[1]);
			}
			else if(codeString.contains("second:")){
				miaostatus = 0;
				RequestSecondOrder(uid,codeString.split(":")[1]);
			}
			else if(codeString.contains("zfbt:")){
				miaostatus = 1;
				RequestSecondOrder(uid,codeString.split(":")[1]);
			}
		}

	}
	
	private void RequestOrder(final String uid,final String text) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getOrderByCode(uid, text);
				Message msg = handler1.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void RequestSecondOrder(final String uid,final String text) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getSecondOrderByCode(uid, text);
				Message msg = handler1.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void income(final String uid,final String code){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString;
				try {
					jsonString = HttpUtil.incomeCurrency(code, uid);
					Message msg = handler1.obtainMessage();
					msg.what = 1;
					msg.obj = jsonString;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = handler1.obtainMessage();
					msg.what = -1;
					msg.obj = "错误";
					msg.sendToTarget();
				}
				
				
			}
		}).start();
	}

	/**
	 * 初始化Camera
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			// 打开Camera硬件设备
			cameraManager.openDriver(surfaceHolder);
			// 创建一个handler来打开预览，并抛出一个运行时异常
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						decodeHints, characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 显示底层错误信息并退出应用
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				RecipeOrder recipeOrder = gson.fromJson(msg.obj.toString(),
						new TypeToken<RecipeOrder>() {
						}.getType());
				Intent intent = new Intent(CaptureActivity.this, OrderConfirmActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", recipeOrder);
				intent.putExtras(bundle);
				CaptureActivity.this.startActivityForResult(intent, 2);
				this.finish();
				
			} else {
				Toast.makeText(this, "订单不存在或者已经完成交易。",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 1:
			if (msg.obj != null) {
				
				GFToast.show(getApplicationContext(),msg.obj.toString());
				this.finish();
				
			} else {
				Toast.makeText(this, "错误",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case -1:
			Toast.makeText(this, "错误",
					Toast.LENGTH_SHORT).show();
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				SecondOrder secondOrder = gson.fromJson(msg.obj.toString(),
						new TypeToken<SecondOrder>() {
						}.getType());
				Intent intent = new Intent(CaptureActivity.this, SecondOrderActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", secondOrder);
				intent.putExtras(bundle);
				if(miaostatus ==1){
					intent.putExtra("status", miaostatus);
				}
				CaptureActivity.this.startActivityForResult(intent, 2);
				this.finish();
				
			} else {
				Toast.makeText(this, "订单不存在或者已经完成交易。",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

}

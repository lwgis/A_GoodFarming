package com.zhonghaodi.goodfarming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFString;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AgrotechnicalActivity extends Activity implements OnClickListener,HandMessage {

	private int id;
	private String image;
	private String title;
	private String content;
	public IWXAPI wxApi;
	public Tencent mTencent;
	private SharePopupwindow sharePopupwindow;
	private ImageView agroImageView;
	private Bitmap bitmap;
	private byte[] data;
	private WebView webview;
	private FrameLayout mFullscreenContainer;  
    private LinearLayout mContentView;  
    private View mCustomView = null;
    private LinearLayout sendLayout;
	private LinearLayout resLayout;
	private MyEditText mzEditText;
	private MyTextButton sendTextButton;
	private GFHandler<AgrotechnicalActivity> handler = new GFHandler<AgrotechnicalActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agrotechnical);
		agroImageView = (ImageView)findViewById(R.id.agro_image);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button shareButton = (Button)findViewById(R.id.share_button);
		shareButton.setOnClickListener(this);
		Button sendBtn = (Button) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (GFUserDictionary.getUserId(getApplicationContext()) != null) {

					resLayout.setVisibility(View.GONE);
					sendLayout.setVisibility(View.VISIBLE);
					mzEditText.setFocusable(true);
					mzEditText.setFocusableInTouchMode(true);
					mzEditText.requestFocus();
					mzEditText.findFocus();
					
					InputMethodManager inputManager =  
				               (InputMethodManager)mzEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				    inputManager.showSoftInput(mzEditText, 0); 
				} else {
					Intent it = new Intent(AgrotechnicalActivity.this,
							LoginActivity.class);
					AgrotechnicalActivity.this.startActivity(it);
				}
			}
		});
		sendLayout = (LinearLayout)findViewById(R.id.sendlayout);
		resLayout = (LinearLayout)findViewById(R.id.resLayout);
		mzEditText = (MyEditText)findViewById(R.id.chat_edit);
		sendTextButton = (MyTextButton)findViewById(R.id.send_meassage_button);
		sendTextButton.setOnClickListener(this);
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
		id = getIntent().getIntExtra("id", 0);
		image = getIntent().getStringExtra("image");
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		if(content.length()>50){
			content = content.substring(0, 49)+"……";
		}
		
		initView();  
        initWebView();  
  
        if (PublicHelper.getPhoneAndroidSDK() >= 14) {// 4.0 需打开硬件加速  
            getWindow().setFlags(0x1000000, 0x1000000);  
        }  
		webview.loadUrl(HttpUtil.ViewUrl+"agrotechnical/detail?id="+id+"&f=1");
//        webview.loadUrl("http://player.youku.com/embed/XMTU4ODM5NjM5Ng==");
		loadImage();
	}
	
	private void initView(){
		mFullscreenContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);  
        mContentView = (LinearLayout) findViewById(R.id.main_content);  
        webview  = (WebView)findViewById(R.id.webView);
	}
	
	private void initWebView(){
		WebSettings settings = webview.getSettings();  
        settings.setJavaScriptEnabled(true);  
        settings.setJavaScriptCanOpenWindowsAutomatically(true);  
        settings.setPluginState(PluginState.ON);  
//        settings.setPluginsEnabled(true); 
        settings.setAllowFileAccess(true);  
        settings.setLoadWithOverviewMode(true);  
  
        webview.setWebChromeClient(new MyWebChromeClient());  
        webview.setWebViewClient(new MyWebViewClient());
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		webview.onResume();
		MobclickAgent.onPageStart("田间地头内容");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		webview.onPause();
		MobclickAgent.onPageEnd("田间地头内容");
		MobclickAgent.onPause(this);
	}



	public void loadImage(){
		ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"agrotechnicals/small/"+image, agroImageView, ImageOptions.optionsNoPlaceholder);
		agroImageView.setDrawingCacheEnabled(true);
	}
	
	public void popwindow(){
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    	sharePopupwindow = new SharePopupwindow(this,this);
    	sharePopupwindow.setFocusable(true);
    	sharePopupwindow.setOutsideTouchable(true);
    	sharePopupwindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	sharePopupwindow.setBackgroundDrawable(dw);
		sharePopupwindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
	
	private void sharePoint(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpUtil.sharePoint(GFUserDictionary.getUserId(AgrotechnicalActivity.this), UILApplication.shareUrl);
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					
				}
			}
		}).start();
	}
	private void sendResponse(final Response response, final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NetResponse netResponse=HttpUtil.sendResponseForForum(response, qid);
					MobclickAgent.onEvent(AgrotechnicalActivity.this, UmengConstants.REPLY_AGRO_ID);
					
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = e.getMessage().toString();
					msg.sendToTarget();
				}
			}
		}).start();
	}
    
    private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
    
    class MyWebChromeClient extends WebChromeClient {  
    	  
        private CustomViewCallback mCustomViewCallback;  
        private int mOriginalOrientation = 1;  
  
        @Override  
        public void onShowCustomView(View view, CustomViewCallback callback) {  
            // TODO Auto-generated method stub  
            onShowCustomView(view, mOriginalOrientation, callback);  
            super.onShowCustomView(view, callback);  
  
        }  
  
        public void onShowCustomView(View view, int requestedOrientation,  
                WebChromeClient.CustomViewCallback callback) {  
            if (mCustomView != null) {  
                callback.onCustomViewHidden();  
                return;  
            }  
            if (PublicHelper.getPhoneAndroidSDK() >= 14) {  
                mFullscreenContainer.addView(view);  
                mCustomView = view;  
                mCustomViewCallback = callback;  
                mOriginalOrientation = getRequestedOrientation();  
                mContentView.setVisibility(View.GONE);  
                mFullscreenContainer.setVisibility(View.VISIBLE);  
                mFullscreenContainer.bringToFront();  
  
                setRequestedOrientation(mOriginalOrientation);  
            }  
  
        }  
        
  
        public void onHideCustomView() {  
            mContentView.setVisibility(View.VISIBLE);  
            if (mCustomView == null) {  
                return;  
            }  
            mCustomView.setVisibility(View.GONE);  
            mFullscreenContainer.removeView(mCustomView);  
            mCustomView = null;  
            mFullscreenContainer.setVisibility(View.GONE);  
            try {  
                mCustomViewCallback.onCustomViewHidden();  
            } catch (Exception e) {  
            }  
            // Show the content view.  
  
            setRequestedOrientation(mOriginalOrientation);  
        }  
  
    }  
  
    class MyWebViewClient extends WebViewClient {  
  
        @Override  
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            // TODO Auto-generated method stub  
            view.loadUrl(url);  
            return super.shouldOverrideUrlLoading(view, url);  
        }  
  
    }
    
    class BaseUiListener implements IUiListener {
		
    	protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
			GFToast.show(AgrotechnicalActivity.this, "分享失败");
		}
		@Override
		public void onCancel() {
			GFToast.show(AgrotechnicalActivity.this, "分享取消");
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			MobclickAgent.onEvent(AgrotechnicalActivity.this, UmengConstants.APP_SHARE_ID);
			sharePoint();
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (bitmap==null) {
			Bitmap b = agroImageView.getDrawingCache();
			int WX_THUMB_SIZE = 60;			 			             
			bitmap = Bitmap.createScaledBitmap(b, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
			data = PublicHelper.bmpToByteArray(bitmap, true);
			bitmap.recycle();
		}
		switch (v.getId()) {
		case R.id.share_button:
			popwindow();
			break;
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id;
			UILApplication.shareUrl = webpage.webpageUrl;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = content;
			msg.thumbData = data;
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene=SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_circlefriends:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage1 = new WXWebpageObject();
			webpage1.webpageUrl = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id;
			UILApplication.shareUrl = webpage1.webpageUrl;
			WXMediaMessage msg1 = new WXMediaMessage(webpage1);
			msg1.title = title;
			msg1.description = content;
			msg1.thumbData = data;
			
			SendMessageToWX.Req req1 = new SendMessageToWX.Req();
			req1.transaction = buildTransaction("webpage");
			req1.message = msg1;
			req1.scene=SendMessageToWX.Req.WXSceneTimeline;
			wxApi.sendReq(req1);
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_qq:
			Bundle params = new Bundle();
		    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  content);
		    UILApplication.shareUrl = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id;
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  UILApplication.shareUrl);
		    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,HttpUtil.ImageUrl+"agrotechnicals/small/"+image);
		    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
		    mTencent.shareToQQ(this, params, new BaseUiListener());
		    sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_qzone:
			Bundle params1 = new Bundle();
			params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);//选填
		    UILApplication.shareUrl = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id;
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, HttpUtil.ImageUrl+"agrotechnicals/small/"+image);
		    ArrayList<String> urlsList = new ArrayList<String>();
		    urlsList.add(HttpUtil.ImageUrl+"agrotechnicals/small/"+image);
		    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlsList);
		    mTencent.shareToQzone(this, params1, new BaseUiListener());
		    sharePopupwindow.dismiss();
			break;
		case R.id.send_meassage_button:
			
			if(mzEditText.getText().toString().trim().length()<5){
				GFToast.show(this, "回复应不少于5个字");
				return;
			}
			Response response = new Response();
			response.setContent(GFString.htmlToStr(mzEditText.getText().toString()));
			User writer = new User();
			writer.setId(GFUserDictionary.getUserId(getApplicationContext()));
			response.setWriter(writer);
			sendResponse(response, id);
			mzEditText.setText("");
			mzEditText.setFocusable(false);
			
			Timer timer = new Timer();  
		     timer.schedule(new TimerTask()  
		     {  
		           
		         public void run()  
		         {  
		        	 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		 			 imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
		         }  
		           
		     },  998);
			sendLayout.setVisibility(View.GONE);
			resLayout.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if(msg.obj!=null){
				GFToast.show(this, msg.obj.toString());
			}
			break;
		case 1:
			String url = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id+"&f=1";
			webview.reload();
			break;

		default:
			break;
		}
	}

}

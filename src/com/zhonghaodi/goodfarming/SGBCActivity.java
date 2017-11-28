package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.utils.PublicHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SGBCActivity extends Activity  {	

	private ProgressBar bar;
	private LinearLayout mContentView;  
	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sgbc);
		bar = (ProgressBar)findViewById(R.id.myProgressBar);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SGBCActivity.this.finish();
//				moveTaskToBack(true);
			}
		});
		Button closeButton = (Button)findViewById(R.id.close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
			}
		});
		
		initView();  
        initWebView();
		if (PublicHelper.getPhoneAndroidSDK() >= 14) {// 4.0 需打开硬件加速  
            getWindow().setFlags(0x1000000, 0x1000000);  
        }  
		webview.loadUrl("http://sgsjt.qingk.cn/audiolive-info/"
				+ "tvsqtecrddefsfaxbapvdcwqbvpbtofb/21914f833b03a7ebe746850bd6a22083/"
				+ "51e7280579234725b546bd5c01b828d0?from=singlemessage&isappinstalled=0");
		
		
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		webview.onResume();
		MobclickAgent.onPageStart("寿光蔬菜广播");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		webview.onPause();
		MobclickAgent.onPageEnd("寿光蔬菜广播");
		MobclickAgent.onPause(this);
	}
	
	private void initView(){
        mContentView = (LinearLayout) findViewById(R.id.main_content);  
        webview  = (WebView)findViewById(R.id.webView);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView(){
		WebSettings s = webview.getSettings();     
		s.setBuiltInZoomControls(true);     
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);     
		s.setUseWideViewPort(true);     
		s.setLoadWithOverviewMode(true);     
		s.setSavePassword(true);     
		s.setSaveFormData(true);     
		s.setJavaScriptEnabled(true);     // enable navigator.geolocation     
		s.setGeolocationEnabled(true);     
		s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");     // enable Web Storage: localStorage, sessionStorage     
		s.setDomStorageEnabled(true);  
		webview.requestFocus();  
		webview.setScrollBarStyle(0);  
          
          
		webview.setWebViewClient(new MyWebviewCient()); 
		webview.setWebChromeClient(new MyWebChromeClient());
//          
//        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  
//        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);  
//  
//        webview.setHorizontalScrollBarEnabled(false);  
//        webview.setVerticalScrollBarEnabled(false);   
//          
//        final String USER_AGENT_STRING = webview.getSettings().getUserAgentString() + " Rong/2.0";  
//        webview.getSettings().setUserAgentString( USER_AGENT_STRING );  
//        webview.getSettings().setSupportZoom(false);  
//        webview.getSettings().setPluginState(WebSettings.PluginState.ON);  
//        webview.getSettings().setLoadWithOverviewMode(true);  
	}
	
	/***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
	        	finish();
	        } 
	    } 
		return false;

	}
    
    public class MyWebviewCient extends WebViewClient{  
        @Override  
        public WebResourceResponse shouldInterceptRequest(WebView view,  
                String url) {  
            WebResourceResponse response = null;  
            response = super.shouldInterceptRequest(view, url);  
            return response;  
        }  
  
        @Override  
        public void onPageFinished(WebView view, String url) {  
            // TODO Auto-generated method stub  
            super.onPageFinished(view, url);  
            webview.loadUrl("javascript:playAudio()");
            
        }  
          
    } 
    
    private class MyWebChromeClient extends WebChromeClient {

    	@Override

    	public boolean onConsoleMessage(ConsoleMessage cm) {

    	Log.d("test", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId() );

    	return true;

    	}

    	@Override

    	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

    	Toast.makeText(SGBCActivity.this, message, Toast.LENGTH_SHORT).show();

    	return true;

    	}

    }

}

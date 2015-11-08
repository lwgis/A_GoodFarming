package com.zhonghaodi.goodfarming;

import java.io.IOException;
import java.util.ArrayList;

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
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.utils.PublicHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class AgrotechnicalActivity extends Activity implements OnClickListener {
	int id;
	String image;
	String title;
	String content;
	public IWXAPI wxApi;
	public Tencent mTencent;
	SharePopupwindow sharePopupwindow;
	ImageView agroImageView;
	Bitmap bitmap;
	byte[] data;
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
		WebView webview  = (WebView)findViewById(R.id.webView);
		webview.loadUrl(HttpUtil.ViewUrl+"agrotechnical/detail?id="+id+"&f=1");
		loadImage();
	}
	
	public void loadImage(){
		ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"agrotechnicals/small/"+image, agroImageView, ImageOptions.optionsNoPlaceholder);
		agroImageView.setDrawingCacheEnabled(true);
	}
	
	public void popwindow(){
    	sharePopupwindow = new SharePopupwindow(this,this);
    	sharePopupwindow.setFocusable(true);
    	sharePopupwindow.setOutsideTouchable(true);
    	sharePopupwindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	sharePopupwindow.setBackgroundDrawable(dw);
		sharePopupwindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    
    private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
    
    class BaseUiListener implements IUiListener {
		
		protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
		}
		@Override
		public void onCancel() {
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (bitmap==null) {
			bitmap = agroImageView.getDrawingCache();
			data = PublicHelper.bmpToByteArray(bitmap, true);
		}
		switch (v.getId()) {
		case R.id.share_button:
			popwindow();
			break;
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show("您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = content;
			Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.drawable.app108);
			msg.thumbData = PublicHelper.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene=SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_circlefriends:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show("您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage1 = new WXWebpageObject();
			webpage1.webpageUrl = HttpUtil.ViewUrl+"agrotechnical/detail?id="+id;
			WXMediaMessage msg1 = new WXMediaMessage(webpage1);
			msg1.title = title;
			msg1.description = content;
			Bitmap thumb1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.app108);
			msg1.thumbData = PublicHelper.bmpToByteArray(thumb1, true);
			
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
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  HttpUtil.ViewUrl+"agrotechnical/detail?id="+id);
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
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, HttpUtil.ViewUrl+"agrotechnical/detail?id="+id);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, HttpUtil.ImageUrl+"agrotechnicals/small/"+image);
		    ArrayList<String> urlsList = new ArrayList<String>();
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_1_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_2_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_3_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_4_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_5_1440519318/550");
		    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlsList);
		    mTencent.shareToQzone(this, params1, new BaseUiListener());
		    sharePopupwindow.dismiss();
			break;
		default:
			break;
		}
	}

}

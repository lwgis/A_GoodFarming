package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

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
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.utils.PublicHelper;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;

public class AppShareActivity extends Activity implements OnClickListener {
	private User user;
	public IWXAPI wxApi;
	public Tencent mTencent;
	SharePopupwindow sharePopupwindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appshare);
		user = (User)getIntent().getSerializableExtra("user");
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		View appView = findViewById(R.id.layout1);
		appView.setOnClickListener(this);
		View nyView = findViewById(R.id.layout2);
		nyView.setOnClickListener(this);
		
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
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
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mTencent.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=null;
		switch (v.getId()) {
		case R.id.layout1:
			popwindow();
			break;
		case R.id.layout2:
			intent = new Intent(this, AppdownActivity.class);
			intent.putExtra("content", HttpUtil.ViewUrl+"appshare?code="+user.getTjCode());
			startActivity(intent);
			break;
			
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = HttpUtil.ViewUrl+"appshare?code="+user.getTjCode();
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "种好地APP:让种地不再难";
			msg.description = "下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。";
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
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage1 = new WXWebpageObject();
			webpage1.webpageUrl = HttpUtil.ViewUrl+"appshare?code="+user.getTjCode();
			WXMediaMessage msg1 = new WXMediaMessage(webpage1);
			msg1.title = "种好地APP:让种地不再难";
			msg1.description = "下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。";
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
		    params.putString(QQShare.SHARE_TO_QQ_TITLE, "种好地APP:让种地不再难");
		    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。");
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  HttpUtil.ViewUrl+"appshare?code="+user.getTjCode());
		    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://121.40.62.120/appimage/apps/appicon.png");
		    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
		    mTencent.shareToQQ(this, params, new BaseUiListener());
		    sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_qzone:
			Bundle params1 = new Bundle();
			params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, "种好地APP:让种地不再难");//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。");//选填
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, HttpUtil.ViewUrl+"appshare?code="+user.getTjCode());//必填
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

}

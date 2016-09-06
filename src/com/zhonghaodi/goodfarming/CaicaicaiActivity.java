package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.CaicaicaiAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GFVersionHint;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.req.CaicaicaiReq;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;
import com.zhonghaodi.view.CaicaicaiView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CaicaicaiActivity extends Activity implements OnClickListener,OnItemClickListener,CaicaicaiView {
	
	private PullToRefreshListView caiListView;
	private List<Caicaicai> caiList;
	private CaicaicaiReq req;
	private CaicaicaiAdapter adapter;
	public IWXAPI wxApi;
	public Tencent mTencent;
	private Caicaicai shareCai;
	private SharePopupwindow sharePopupwindow;
	private ImageView caiImageView;
	private Bitmap bitmap;
	private byte[] data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caicaicai);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		caiImageView = (ImageView)findViewById(R.id.agro_image);
		cancelBtn.setOnClickListener(this);
		caiListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		caiListView.setOnItemClickListener(this);
		caiListView.setMode(Mode.BOTH);
		caiListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				req.loadCaicaicai(0);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if(caiList.size()==0){
					return;
				}
				Caicaicai caicaicai = caiList.get(caiList.size()-1);
				req.loadCaicaicai(caicaicai.getId());
			}

		});
		caiList = new ArrayList<Caicaicai>();
		adapter = new CaicaicaiAdapter(caiList, this,this);
		caiListView.getRefreshableView().setAdapter(adapter);
		req = new CaicaicaiReq(this);
		req.loadCaicaicai(0);
		if(GFVersionHint.getCnzcount(this)==0){
			GFVersionHint.saveCnzHintInfo(this, 1);
		}
		
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("猜农资");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("猜农资");
		MobclickAgent.onPause(this);
	}
	
	public void loadImage(Caicaicai caicaicai){
//		ImageLoader.getInstance().displayImage(
//				HttpUtil.ImageUrl+"cai/small/"
//						+ caicaicai.getAttachments1().get(0).getUrl(),
//				caiImageView, ImageOptions.optionsNoPlaceholder);
		ImageLoader.getInstance().loadImage(HttpUtil.ImageUrl+"cai/small/"
				+ caicaicai.getAttachments1().get(0).getUrl(), new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				int WX_THUMB_SIZE = 60;
				Bitmap b = arg2;	             
				bitmap = Bitmap.createScaledBitmap(b, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
				data = PublicHelper.bmpToByteArray(bitmap, true);
				bitmap.recycle();
				popwindow();
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		caiImageView.setDrawingCacheEnabled(true);
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
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	private void sharePoint(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpUtil.sharePoint(GFUserDictionary.getUserId(CaicaicaiActivity.this), UILApplication.shareUrl);
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					
				}
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mTencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.sharelayout:
			shareCai = (Caicaicai)v.getTag();
			loadImage(shareCai);
			break;
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+shareCai.getId();
			UILApplication.shareUrl = webpage.webpageUrl;
			String title ="种好地APP：猜猜这是啥？";
			String content;
			if(shareCai.getContent().length()>40){
				content = shareCai.getContent().substring(0, 40);
			}
			else{
				content = shareCai.getContent();
			} 
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
			webpage1.webpageUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+shareCai.getId();
			UILApplication.shareUrl = webpage1.webpageUrl;
			WXMediaMessage msg1 = new WXMediaMessage(webpage1);
			String title1 ="种好地APP：猜猜这是啥？";
			String content1;
			if(shareCai.getContent().length()>40){
				content1 = shareCai.getContent().substring(0, 40);
			}
			else{
				content1 = shareCai.getContent();
			} 
			msg1.title = title1;
			msg1.description = content1;
			msg1.thumbData = data;
			
			SendMessageToWX.Req req1 = new SendMessageToWX.Req();
			req1.transaction = buildTransaction("webpage");
			req1.message = msg1;
			req1.scene=SendMessageToWX.Req.WXSceneTimeline;
			wxApi.sendReq(req1);
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_qq:
			String title2 ="种好地APP：猜猜这是啥？";
			String content2;
			if(shareCai.getContent().length()>40){
				content2 = shareCai.getContent().substring(0, 40);
			}
			else{
				content2 = shareCai.getContent();
			} 
			Bundle params = new Bundle();
		    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		    params.putString(QQShare.SHARE_TO_QQ_TITLE, title2);
		    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  content2);
		    UILApplication.shareUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+shareCai.getId();
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  UILApplication.shareUrl);
		    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,HttpUtil.ImageUrl+"cai/small/"
					+ shareCai.getAttachments1().get(0).getUrl());
		    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
		    mTencent.shareToQQ(this, params, new BaseUiListener());
		    sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_qzone:
			String title3 ="种好地APP：猜猜这是啥？";
			String content3;
			if(shareCai.getContent().length()>40){
				content3 = shareCai.getContent().substring(0, 40);
			}
			else{
				content3 = shareCai.getContent();
			} 
			Bundle params1 = new Bundle();
			params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, title3);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content3);//选填
		    UILApplication.shareUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+shareCai.getId();
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, HttpUtil.ImageUrl+"cai/small/"
					+ shareCai.getAttachments1().get(0).getUrl());
		    ArrayList<String> urlsList = new ArrayList<String>();
		    urlsList.add(HttpUtil.ImageUrl+"cai/small/"
					+ shareCai.getAttachments1().get(0).getUrl());
		    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlsList);
		    mTencent.shareToQzone(this, params1, new BaseUiListener());
		    sharePopupwindow.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent it = new Intent(this,CaiActivity.class);
		it.putExtra("caiid", caiList.get(position - 1).getId());
		startActivity(it);
	}
	@Override
	public void showMessage(String message) {
		// TODO Auto-generated method stub
		GFToast.show(this, message);
	}
	@Override
	public void displayCaicaicai(List<Caicaicai> caicaicais, boolean isAdd) {
		// TODO Auto-generated method stub
		if(!isAdd){
			caiList.clear();
		}
		if(caicaicais!=null && caicaicais.size()>0){
			for (Iterator iterator = caicaicais.iterator(); iterator.hasNext();) {
				Caicaicai caicaicai = (Caicaicai) iterator.next();
				caiList.add(caicaicai);
			}
		}
		adapter.notifyDataSetChanged();
	}
	@Override
	public void refreshComplete() {
		// TODO Auto-generated method stub
		caiListView.onRefreshComplete();
	}

	class BaseUiListener implements IUiListener {
		
    	protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
			GFToast.show(CaicaicaiActivity.this, "分享失败");
		}
		@Override
		public void onCancel() {
			GFToast.show(CaicaicaiActivity.this, "分享取消");
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			MobclickAgent.onEvent(CaicaicaiActivity.this, UmengConstants.APP_SHARE_ID);
			sharePoint();
		}
		
	}

}

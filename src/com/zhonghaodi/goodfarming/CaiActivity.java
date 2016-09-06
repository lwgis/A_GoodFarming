package com.zhonghaodi.goodfarming;

import java.util.ArrayList;

import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
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
import com.zhonghaodi.adapter.CaiResponseAdapter;
import com.zhonghaodi.customui.CaicaicaiHolder;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.goodfarming.CaicaicaiActivity.BaseUiListener;
import com.zhonghaodi.model.CaiComment;
import com.zhonghaodi.model.CaiResponse;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.networking.GFString;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.req.CaiReq;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;
import com.zhonghaodi.view.CaiView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CaiActivity extends Activity implements OnClickListener,CaiView {
	
	private PullToRefreshListView pullToRefreshListView;
	private View headerView;
	private CaicaicaiHolder headHolder;
	private int caiid;
	private Caicaicai mCaicaicai;
	private CaiResponseAdapter adapter;
	private String uid;
	private LinearLayout sendLayout;
	private LinearLayout resLayout;
	private MyEditText mzEditText;
	private MyTextButton sendTextButton;
	private Button sendBtn;
	private int status=0;
	private CaiReq req;
	private int selresponseid,selcommentid;
	public IWXAPI wxApi;
	public Tencent mTencent;
	private SharePopupwindow sharePopupwindow;
	private ImageView caiImageView;
	private Bitmap bitmap;
	private byte[] data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cai);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		caiImageView = (ImageView)findViewById(R.id.agro_image);
		sendBtn = (Button) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(this);
		headerView = LayoutInflater.from(CaiActivity.this).inflate(R.layout.cell_caicaicai, null);
		headHolder = new CaicaicaiHolder(headerView);
		sendLayout = (LinearLayout)findViewById(R.id.sendlayout);
		resLayout = (LinearLayout)findViewById(R.id.resLayout);
		mzEditText = (MyEditText)findViewById(R.id.chat_edit);
		sendTextButton = (MyTextButton)findViewById(R.id.send_meassage_button);
		sendTextButton.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		caiid = getIntent().getIntExtra("caiid", 0);
		uid=GFUserDictionary.getUserId(getApplicationContext());
		pullToRefreshListView.getRefreshableView().addHeaderView(headerView);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if(caiid!=0)
					req.loadCaicaicai(caiid);
			}
		});
		registerForContextMenu(pullToRefreshListView.getRefreshableView());
		req = new CaiReq(this);
		if(caiid!=0)
			req.loadCaicaicai(caiid);
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("猜农资详细信息");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("猜农资详细信息");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterForContextMenu(pullToRefreshListView.getRefreshableView());
	}
	
	public void loadImage(Caicaicai caicaicai){
		ImageLoader.getInstance().displayImage(
				HttpUtil.ImageUrl+"cai/small/"
						+ caicaicai.getAttachments1().get(0).getUrl(),
				caiImageView, ImageOptions.optionsNoPlaceholder);
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
					HttpUtil.sharePoint(GFUserDictionary.getUserId(CaiActivity.this), UILApplication.shareUrl);
					
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(info.position>1){
			if(uid!=null){	
				if(mCaicaicai.getStatus()==0){
					CaiResponse response = mCaicaicai.getResponses().get(info.position-2);
					if(response.getWriter().getId().equals(uid)){
						menu.add(0, 0, 0, "删除");
					} 
				}
				else{
					CaiComment comment = mCaicaicai.getComments().get(info.position-2);
					if(comment.getWriter().getId().equals(uid)){
						menu.add(0, 0, 0, "删除");
					} 
				}
			}
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			CaiComment comment;
			if(mCaicaicai.getStatus()==0){
				comment = mCaicaicai.getResponses().get(info.position-2);
				selresponseid = comment.getId();
			}
			else{
				comment = mCaicaicai.getComments().get(info.position-2);
				selcommentid = comment.getId();
			}
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
	        //设置它的ContentView
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout = inflater.inflate(R.layout.dialog, null);
	        dialog.setContentView(layout);
	        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
	        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
	        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
	        okBtn.setText("确定");
	        okBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();					
					if(mCaicaicai.getStatus()==0){
						req.deleteResponse(caiid, selresponseid);
					}
					else{
						req.deleteComment(caiid, selcommentid);
					}
				}
			});
	        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
	        cancelButton.setText("取消");
	        cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
	        titleView.setText("提示");
	        contentView.setText("确定要删除选中的内容吗？");
	        dialog.show();
			break;
		default:
			break;
		}
		
		 
		return super.onContextItemSelected(item);
	}

	@Override
	public void showMessage(String message) {
		// TODO Auto-generated method stub
		GFToast.show(this, message);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int WX_THUMB_SIZE = 60;
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.response_text:
			headHolder.responseView.setTextColor(Color.rgb(56, 190, 153));
			headHolder.commentView.setTextColor(Color.rgb(128, 128, 128));
			changeStatus(0);
			break;
		case R.id.comment_text:
			headHolder.responseView.setTextColor(Color.rgb(128, 128, 128));
			headHolder.commentView.setTextColor(Color.rgb(56, 190, 153));
			changeStatus(1);
			break;
		case R.id.send_button:
			resLayout.setVisibility(View.GONE);
			sendLayout.setVisibility(View.VISIBLE);
			if(mCaicaicai==null){
				return;
			}
			if(mCaicaicai.getStatus()==0){
				mzEditText.setHint("输入答案");
			}
			else{
				mzEditText.setHint("输入评价");
			}
			mzEditText.setFocusable(true);
			mzEditText.setFocusableInTouchMode(true);
			mzEditText.requestFocus();
			mzEditText.findFocus();			
			InputMethodManager inputManager =  
		               (InputMethodManager)mzEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		    inputManager.showSoftInput(mzEditText, 0); 
			break;
		case R.id.send_meassage_button:
			if(mzEditText.getText().toString().trim().length()==0){
				showMessage("不能为空");
				return;
			}
			InputMethodManager im = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(findViewById(android.R.id.content)
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			if(mCaicaicai.getStatus()==0){
				req.replyCai(GFString.htmlToStr(mzEditText.getText().toString()), uid, caiid);
			}
			else{
				req.commentCai(GFString.htmlToStr(mzEditText.getText().toString()), uid, caiid);
			}			
			break;
		case R.id.sharelayout:
			popwindow();
			break;
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			if (bitmap==null) {
				Bitmap b = caiImageView.getDrawingCache();
							 			             
				bitmap = Bitmap.createScaledBitmap(b, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
				data = PublicHelper.bmpToByteArray(bitmap, true);
				bitmap.recycle();
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+mCaicaicai.getId();
			UILApplication.shareUrl = webpage.webpageUrl;
			String title ="种好地APP：猜猜这是啥？";
			String content;
			if(mCaicaicai.getContent().length()>40){
				content = mCaicaicai.getContent().substring(0, 40);
			}
			else{
				content = mCaicaicai.getContent();
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
			if (bitmap==null) {
				Bitmap b = caiImageView.getDrawingCache();
							 			             
				bitmap = Bitmap.createScaledBitmap(b, WX_THUMB_SIZE, WX_THUMB_SIZE, true);
				data = PublicHelper.bmpToByteArray(bitmap, true);
				bitmap.recycle();
			}
			WXWebpageObject webpage1 = new WXWebpageObject();
			webpage1.webpageUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+mCaicaicai.getId();
			UILApplication.shareUrl = webpage1.webpageUrl;
			WXMediaMessage msg1 = new WXMediaMessage(webpage1);
			String title1 ="种好地APP：猜猜这是啥？";
			String content1;
			if(mCaicaicai.getContent().length()>40){
				content1 = mCaicaicai.getContent().substring(0, 40);
			}
			else{
				content1 = mCaicaicai.getContent();
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
			if(mCaicaicai.getContent().length()>40){
				content2 = mCaicaicai.getContent().substring(0, 40);
			}
			else{
				content2 = mCaicaicai.getContent();
			} 
			Bundle params = new Bundle();
		    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		    params.putString(QQShare.SHARE_TO_QQ_TITLE, title2);
		    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  content2);
		    UILApplication.shareUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+mCaicaicai.getId();
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  UILApplication.shareUrl);
		    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,HttpUtil.ImageUrl+"cai/small/"
					+ mCaicaicai.getAttachments1().get(0).getUrl());
		    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
		    mTencent.shareToQQ(this, params, new BaseUiListener());
		    sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_qzone:
			String title3 ="种好地APP：猜猜这是啥？";
			String content3;
			if(mCaicaicai.getContent().length()>40){
				content3 = mCaicaicai.getContent().substring(0, 40);
			}
			else{
				content3 = mCaicaicai.getContent();
			} 
			Bundle params1 = new Bundle();
			params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, title3);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content3);//选填
		    UILApplication.shareUrl = HttpUtil.ViewUrl+"caicaicai/detail?id="+mCaicaicai.getId();
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl);//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, HttpUtil.ImageUrl+"cai/small/"
					+ mCaicaicai.getAttachments1().get(0).getUrl());
		    ArrayList<String> urlsList = new ArrayList<String>();
		    urlsList.add(HttpUtil.ImageUrl+"cai/small/"
					+ mCaicaicai.getAttachments1().get(0).getUrl());
		    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlsList);
		    mTencent.shareToQzone(this, params1, new BaseUiListener());
		    sharePopupwindow.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void refreshComplete() {
		// TODO Auto-generated method stub
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	public void displayCaicaicai(Caicaicai caicaicai) {
		// TODO Auto-generated method stub
		mCaicaicai = caicaicai;
		headHolder.hideImageviews();
		headHolder.shareLayout.setOnClickListener(this);
		if(caicaicai.getStatus()==0){
			headHolder.imgLayout2.setVisibility(View.GONE);
			headHolder.answerView.setVisibility(View.GONE);
			headHolder.splitLine.setVisibility(View.GONE);
			headHolder.commentView.setVisibility(View.GONE);
			headHolder.contentView.setText(caicaicai.getContent());
			headHolder.responseView.setText("答案（"+caicaicai.getResponseCount()+"）");
			headHolder.responseView.setOnClickListener(this);
			if(caicaicai.getAttachments1()!=null && caicaicai.getAttachments1().size()>0){
				for (int i = 0; i < caicaicai.getAttachments1().size(); i++) {
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"cai/small/"
									+ caicaicai.getAttachments1().get(i).getUrl(),
									headHolder.imageViews1.get(i), ImageOptions.options);
					headHolder.imageViews1.get(i).setVisibility(View.VISIBLE);
					headHolder.imageViews1.get(i).setIndex(i);
					headHolder.imageViews1.get(i).setImages(caicaicai.getAttachments1(),"cai");
				}
			}
		}
		else{
			headHolder.imgLayout2.setVisibility(View.VISIBLE);
			headHolder.answerView.setVisibility(View.VISIBLE);
			headHolder.splitLine.setVisibility(View.VISIBLE);
			headHolder.commentView.setTextColor(Color.rgb(56, 190, 153));
			headHolder.commentView.setVisibility(View.VISIBLE);
			headHolder.contentView.setText(caicaicai.getContent());
			headHolder.responseView.setText("答案（"+caicaicai.getResponseCount()+"）");
			headHolder.responseView.setOnClickListener(this);
			headHolder.commentView.setText("评价（"+caicaicai.getCommentCount()+"）");
			headHolder.commentView.setOnClickListener(this);
			headHolder.answerView.setText("正确答案："+caicaicai.getShowAnswer());
			if(caicaicai.getAttachments1()!=null && caicaicai.getAttachments1().size()>0){
				if(caicaicai.getAttachments1().size()==1){
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"cai/small/"
									+ caicaicai.getAttachments1().get(0).getUrl(),
									headHolder.imageViews1.get(0), ImageOptions.options);
					headHolder.imageViews1.get(0).setVisibility(View.VISIBLE);
					headHolder.imageViews1.get(0).setIndex(0);
					headHolder.imageViews1.get(0).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
					
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"cai/small/"
									+ caicaicai.getShowAttachments2().get(0).getUrl(),
									headHolder.imageViews1.get(1), ImageOptions.options);
					headHolder.imageViews1.get(1).setVisibility(View.VISIBLE);
					headHolder.imageViews1.get(1).setIndex(1);
					headHolder.imageViews1.get(1).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
				}
				else{
					for (int i = 0; i < caicaicai.getAttachments1().size(); i++) {
						ImageLoader.getInstance().displayImage(
								HttpUtil.ImageUrl+"cai/small/"
										+ caicaicai.getAttachments1().get(i).getUrl(),
										headHolder.imageViews1.get(i), ImageOptions.options);
						headHolder.imageViews1.get(i).setVisibility(View.VISIBLE);
						headHolder.imageViews1.get(i).setIndex(i*2);
						headHolder.imageViews1.get(i).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
						
						ImageLoader.getInstance().displayImage(
								HttpUtil.ImageUrl+"cai/small/"
										+ caicaicai.getShowAttachments2().get(i).getUrl(),
										headHolder.imageViews2.get(i), ImageOptions.options);
						headHolder.imageViews2.get(i).setVisibility(View.VISIBLE);
						headHolder.imageViews2.get(i).setIndex(i*2+1);
						headHolder.imageViews2.get(i).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
					}
				}
			}
		}
		status=mCaicaicai.getStatus();
		adapter=new CaiResponseAdapter(caicaicai, this,status);
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);
		if(mCaicaicai.getStatus()==0){
			sendBtn.setText("回答");
		}else{
			sendBtn.setText("评价");
		}
		loadImage(mCaicaicai);
	}

	private void changeStatus(int s){
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		mzEditText.setText("");
		if(mCaicaicai.getStatus()==0){
			sendBtn.setText("回答");
		}else{
			sendBtn.setText("评价");
		}
		resLayout.setVisibility(View.VISIBLE);
		sendLayout.setVisibility(View.GONE);
		if(status!=s){
			status=s;
			adapter.setStatus(status);
			adapter.notifyDataSetChanged();
		}		
	}

	@Override
	public void refrenshCai() {
		// TODO Auto-generated method stub
		mzEditText.setText("");
		resLayout.setVisibility(View.VISIBLE);
		sendLayout.setVisibility(View.GONE);
		req.loadCaicaicai(caiid);
	}
class BaseUiListener implements IUiListener {
		
    	protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
			GFToast.show(CaiActivity.this, "分享失败");
		}
		@Override
		public void onCancel() {
			GFToast.show(CaiActivity.this, "分享取消");
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			MobclickAgent.onEvent(CaiActivity.this, UmengConstants.APP_SHARE_ID);
			sharePoint();
		}
		
	}
}

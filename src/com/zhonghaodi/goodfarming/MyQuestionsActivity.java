package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.api.ShareContainer;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.goodfarming.MainActivity.BaseUiListener;
import com.zhonghaodi.model.FavoriteQuestion;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyQuestionsActivity extends Activity implements OnClickListener,HandMessage,
				OnCreateContextMenuListener,ShareContainer {
	
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private QuestionAdpter adapter;
	private GFHandler<MyQuestionsActivity> handler = new GFHandler<MyQuestionsActivity>(this);
	private Question selectQuestion;
	private int status;
	private TextView titleTextView;
	public IWXAPI wxApi;
	public Tencent mTencent;
	private Question shareQue;
	public SharePopupwindow sharePopupwindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myquestions);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		
		status = getIntent().getIntExtra("status", 0);
		titleTextView = (TextView)findViewById(R.id.title_txt);
		if(status==0){
			titleTextView.setText("我的问题");
		}
		else if(status==1){
			titleTextView.setText("我的拉拉呱");
		}
		else if(status==3){
			titleTextView.setText("我的收藏");
		}
		else if(status==2){
			titleTextView.setText("我的种植分享");
		}
		
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
		
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadNewMyDate();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuestions.size() == 0) {
							return;
						}
						Question question = allQuestions.get(allQuestions
								.size() - 1);
						loadMoreMyData(question.getId());
					}

				});
		allQuestions = new ArrayList<Question>();
		adapter = new QuestionAdpter(allQuestions,this,this);
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);
		loadNewMyDate();
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(MyQuestionsActivity.this,
								QuestionActivity.class);
						it.putExtra("questionId", allQuestions
								.get(position - 1).getId());
						if(status==1||status==2){
							it.putExtra("status", status);
						}

						MyQuestionsActivity.this.startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("我的提问");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("我的提问");
		MobclickAgent.onPause(this);
	}
	
	private void loadNewMyDate() {
		final String  uid= GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(status==0){
					jsonString = HttpUtil.getMyQuestionsString(uid);
				}
				else if(status==1){
					jsonString = HttpUtil.getMyGossipsString(uid);
				}
				else if(status==3){
//					jsonString = HttpUtil.getAscQuestionsString(uid);
					jsonString = HttpUtil.getFavorites(uid);
				}
				else{
					jsonString = HttpUtil.getMyPlantinfoString(uid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreMyData(final int qid) {
		final String  uid= GFUserDictionary.getUserId(getApplicationContext());
		if(status==3){
			pullToRefreshListView.onRefreshComplete();
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(status==0){
					jsonString = HttpUtil.getMyQuestionsString(uid,qid);
				}
				else if(status==1){
					jsonString = HttpUtil.getMyGossipsString(uid,qid);
				}
//				else if(status==3){
//					jsonString = HttpUtil.getAscQuestionsString(uid,qid);
//				}
				else{
					
					jsonString = HttpUtil.getMyPlantString(uid,qid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void delete(final int qid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(status==0||status==3){
					jsonString = HttpUtil.deleteQuestion(qid);
				}
				else if(status==1){
					jsonString = HttpUtil.deleteGossip(qid);
				}
				else{
					jsonString = HttpUtil.deletePlant(qid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getApplicationContext());
		if(uid!=null){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Question question = allQuestions.get(info.position-1);
			if(question.getWriter().getId().equals(uid)){
				menu.add(0, 0, 0, "删除");
			} 
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo(); 
		selectQuestion = allQuestions.get(info.position-1);
		final Dialog dialog = new Dialog(MyQuestionsActivity.this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) MyQuestionsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				delete(selectQuestion.getId());
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
        contentView.setText("确定要删除选中的提问吗？");
        dialog.show();
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.forward_layout:
			Question q = (Question)v.getTag();
			String folder;
			if(status==0 || status==3){
				folder="questions";
			}else if(status==1){
				folder="gossips";
			}else{
				folder="plantinfo";
			}
			shareQuestionWindow(q, folder);
			break;
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			String url=HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
			String title ="种好地APP：让种地不再难";
			String content;
			if(shareQue.getContent().length()>40){
				content = shareQue.getContent().substring(0, 40);
			}
			else{
				content = shareQue.getContent();
			} 
			Bitmap b;
			if(shareQue.getAttachments()==null || shareQue.getAttachments().size()==0){
				b =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
				
			}
			else{
				String path;
				String img;
				if(UILApplication.sharefolder.equals("plantinfo")){
					img = HttpUtil.ImageUrl+"plant/small/"+ shareQue.getAttachments().get(0).getUrl();
				}
				else{
					img = HttpUtil.ImageUrl+"questions/small/"+ shareQue.getAttachments().get(0).getUrl();
				}
				
				b=ImageLoader.getInstance().loadImageSync(img);
				if(b==null){
					b =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
				}
			}
			Bitmap bitmap = Bitmap.createScaledBitmap(b, PublicHelper.WX_THUMB_SIZE, PublicHelper.WX_THUMB_SIZE, true);
			shareWeixin(url, title, content, bitmap);
			
			sharePopupwindow.dismiss();
			
			break;
		case R.id.img_share_circlefriends:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			String url1=HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
			String title1 ="种好地APP：让种地不再难";
			String content1;
			if(shareQue.getContent().length()>40){
				content1 = shareQue.getContent().substring(0, 40);
			}
			else{
				content1 = shareQue.getContent();
			} 
			Bitmap b1;
			if(shareQue.getAttachments()==null || shareQue.getAttachments().size()==0){
				b1 =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
				
			}
			else{
				String path;
				String img;
				if(UILApplication.sharefolder.equals("plantinfo")){
					img = HttpUtil.ImageUrl+"plant/small/"+ shareQue.getAttachments().get(0).getUrl();
				}
				else{
					img = HttpUtil.ImageUrl+"questions/small/"+ shareQue.getAttachments().get(0).getUrl();
				}					
				b1=ImageLoader.getInstance().loadImageSync(img);
				if(b1==null){
					b1 =BitmapFactory.decodeResource(getResources(), R.drawable.app108);
				}
			}
			Bitmap bitmap1 = Bitmap.createScaledBitmap(b1, PublicHelper.WX_THUMB_SIZE, PublicHelper.WX_THUMB_SIZE, true);
			shareCirclefriends(url1, content1, content1, bitmap1);
			
			sharePopupwindow.dismiss();
			break;
		case R.id.img_share_qq:
			String url2 = HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
			String title2 ="种好地APP：让种地不再难";
			String content2;
			if(shareQue.getContent().length()>40){
				content2 = shareQue.getContent().substring(0, 40);
			}
			else{
				content2 = shareQue.getContent();
			}
			String img2;
			if(shareQue.getAttachments()!=null && shareQue.getAttachments().size()>0){
				if(UILApplication.sharefolder.equals("plantinfo")){
					img2 = HttpUtil.ImageUrl+"plant/small/"+ shareQue.getAttachments().get(0).getUrl();
				}
				else{
					img2 = HttpUtil.ImageUrl+"questions/small/"+ shareQue.getAttachments().get(0).getUrl();
				}
			}
			else{
				img2 = "http://121.40.62.120/appimage/apps/appicon.png";
			}
			shareQQ(url2, title2, content2, img2);		    
		    sharePopupwindow.dismiss();			
			break;
		case R.id.img_share_qzone:
			String url3 = HttpUtil.ViewUrl+UILApplication.sharefolder+"/detail?id="+shareQue.getId();
			String title3 ="种好地APP：让种地不再难";
			String content3;
			if(shareQue.getContent().length()>40){
				content3 = shareQue.getContent().substring(0, 40);
			}
			else{
				content3 = shareQue.getContent();
			}
			ArrayList<String> urlsList = new ArrayList<String>();		    
		    String imgurl1;
		    if(shareQue.getAttachments()!=null && shareQue.getAttachments().size()>0){
		    	if(UILApplication.sharefolder.equals("plantinfo")){
		    		imgurl1 = HttpUtil.ImageUrl+"plant/small/"
							+ shareQue.getAttachments().get(0)
							.getUrl();
		    	}
		    	else{
		    		imgurl1 = HttpUtil.ImageUrl+"questions/small/"
							+ shareQue.getAttachments().get(0)
							.getUrl();
		    	}
		    	for(int i=0;i<shareQue.getAttachments().size();i++){
		    		if(UILApplication.sharefolder.equals("plantinfo")){
		    			urlsList.add(HttpUtil.ImageUrl+"plant/small/"
								+ shareQue.getAttachments().get(i)
								.getUrl());
		    		}
		    		else{
		    			urlsList.add(HttpUtil.ImageUrl+"questions/small/"
								+ shareQue.getAttachments().get(i)
								.getUrl());
		    		}
		    	}
		    }
		    else{
		    	imgurl1 = "http://121.40.62.120/appimage/apps/appicon.png";
		    	urlsList.add("http://121.40.62.120/appimage/apps/appicon.png");
		    }
		    shareQZone(url3,title3,content3,urlsList,imgurl1);
		    sharePopupwindow.dismiss();
			break;

		default:
			break;
		}
	}
	
	private void sharePoint(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpUtil.sharePoint(GFUserDictionary.getUserId(MyQuestionsActivity.this), UILApplication.shareUrl);
					if(UILApplication.sharestatus==1){
						if(UILApplication.sharefolder.contains("question")){
							HttpUtil.addForwardcount("question", shareQue.getId());
						}
						else if(UILApplication.sharefolder.contains("gossip")){
							HttpUtil.addForwardcount("gossip", shareQue.getId());
						}
						else{
							HttpUtil.addForwardcount("plantinfo", shareQue.getId());
						}
					}
					
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
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void popupShareWindow(User user) {
		// TODO Auto-generated method stub
		UILApplication.sharestatus = 0;
	}

	@Override
	public void shareQuestionWindow(Question question, String folder) {
		// TODO Auto-generated method stub
		shareQue=question;
		UILApplication.sharestatus = 1;
		UILApplication.sharefolder = folder;
		UILApplication.sharequeid= question.getId();
		sharePopupwindow = new SharePopupwindow(this,this);
    	sharePopupwindow.setFocusable(true);
    	sharePopupwindow.setOutsideTouchable(true);
    	sharePopupwindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	sharePopupwindow.setBackgroundDrawable(dw);
		sharePopupwindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	@Override
	public void shareWeixin(String url, String title, String des, Bitmap thumb) {
		// TODO Auto-generated method stub
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = url;
		UILApplication.shareUrl = webpage.webpageUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = des;
		msg.thumbData = PublicHelper.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene=SendMessageToWX.Req.WXSceneSession;
		wxApi.sendReq(req);
	}


	@Override
	public void shareCirclefriends(String url, String title, String des, Bitmap thumb) {
		// TODO Auto-generated method stub
		WXWebpageObject webpage1 = new WXWebpageObject();
		webpage1.webpageUrl = url;
		UILApplication.shareUrl = webpage1.webpageUrl;
		WXMediaMessage msg1 = new WXMediaMessage(webpage1);
		msg1.title = title;
		msg1.description = des;
		msg1.thumbData = PublicHelper.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req1 = new SendMessageToWX.Req();
		req1.transaction = buildTransaction("webpage");
		req1.message = msg1;
		req1.scene=SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req1);
	}


	@Override
	public void shareQQ(String url, String title, String des, String imgUrl) {
		// TODO Auto-generated method stub
		Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
	    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  des);
	    UILApplication.shareUrl= url;
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl );
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,imgUrl);
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
	    mTencent.shareToQQ(this, params, new BaseUiListener());
	}


	@Override
	public void shareQZone(String url, String title, String des, ArrayList<String> urList,String img1) {
		// TODO Auto-generated method stub
		Bundle params1 = new Bundle();
		params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
	    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
	    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);//选填
	    UILApplication.shareUrl= url;
	    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, UILApplication.shareUrl);//必填
	    if(img1!=null){
	    	params1.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, img1);
	    }
	    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urList);
	    mTencent.shareToQzone(this, params1, new BaseUiListener());
	}
	
	class BaseUiListener implements IUiListener {
		
		protected void doComplete(JSONObject values) {
			
		}
		@Override
		public void onError(UiError e) {
//			GFToast.show(MainActivity.this, "分享失败");
		}
		@Override
		public void onCancel() {
//			GFToast.show(MainActivity.this, "分享取消");
		}
		@Override
		public void onComplete(Object arg0) {
			// TODO Auto-generated method stub
			MobclickAgent.onEvent(MyQuestionsActivity.this, UmengConstants.APP_SHARE_ID);
			sharePoint();
			
		}
		
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if(msg.what==0||msg.what==1){
			if (msg.obj != null) {
				if(status!=3){
					Gson gson = new Gson();
					List<Question> questions = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<Question>>() {
							}.getType());
					if (msg.what == 0) {
						allQuestions.clear();
					}
					if(questions==null || questions.size()==0){
						if(allQuestions.size()==0){
							String mess="";
							switch (status) {
							case 0:
								mess="您还没有提问过。";
								break;
							case 1:
								mess="您还没有发起过话题。";
								break;
							case 2:
								mess="您还没有分享过。";
								break;

							default:
								break;
							}
							GFToast.show(getApplicationContext(),mess);
						}				
					}
					for (Question question : questions) {
						allQuestions.add(question);
					}
					if(status==2){
						adapter.setStatus(1);
					}
					else{
						adapter.setStatus(0);
					}
				}
				else{
					Gson gs = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					List<FavoriteQuestion> questions = gs.fromJson(msg.obj.toString(),
							new TypeToken<List<FavoriteQuestion>>() {
							}.getType());
					if (msg.what == 0) {
						allQuestions.clear();
					}
					if(questions==null || questions.size()==0){
						
						GFToast.show(getApplicationContext(),"您没有收藏任何问题");				
					}
					for (FavoriteQuestion favoriteQuestion : questions) {
						allQuestions.add(favoriteQuestion.getMyquestion());						
					}
					adapter.setStatus(0);
				}
				
				adapter.notifyDataSetChanged();
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			pullToRefreshListView.onRefreshComplete();
		}
		else if(msg.what==3){
			
			String str = msg.obj.toString();
			if(!str.isEmpty()){
				GFToast.show(getApplicationContext(),str);
			}
			else{
				allQuestions.remove(selectQuestion);
				adapter.notifyDataSetChanged();
			}
			
		}
	}

}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.sdp.Info;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
import com.zhonghaodi.adapter.ResponseAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.Holder1;
import com.zhonghaodi.customui.Holder2;
import com.zhonghaodi.customui.Holder3;
import com.zhonghaodi.customui.HolderResponse;
import com.zhonghaodi.customui.MorePopupWindow;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.customui.UrlTextView.UrlOnClick;
import com.zhonghaodi.goodfarming.AppShareActivity.BaseUiListener;
import com.zhonghaodi.model.Checkobj;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PostResponse;
import com.zhonghaodi.model.Prescription;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFDate;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFString;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class QuestionActivity extends Activity implements UrlOnClick,
		HandMessage,OnClickListener,OnItemClickListener{
	private PullToRefreshListView pullToRefreshListView;
	private Question question;
	private int questionId;
	private GFHandler<QuestionActivity> handler = new GFHandler<QuestionActivity>(
			this);
	private ResponseAdapter adapter;
	private Response selectResponse;
	private String uid;
	private LinearLayout sendLayout;
	private LinearLayout resLayout;
	private MyEditText mzEditText;
	private MyTextButton sendTextButton;
	private MyTextButton prescriptionButton;
	private int status;
	private MorePopupWindow morePopupWindow;
	public IWXAPI wxApi;
	public Tencent mTencent;
	private int rid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_question);
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button sendBtn = (Button) findViewById(R.id.send_button);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (questionId == 0) {
					return;
				}
				if (GFUserDictionary.getUserId(getApplicationContext()) != null) {

					resLayout.setVisibility(View.GONE);
					sendLayout.setVisibility(View.VISIBLE);
					if(status==0){
						mzEditText.setHint("种植作物+病害全称+病害起因+解决方案");
					}
					else{
						mzEditText.setHint("");
					}
					mzEditText.setFocusable(true);
					mzEditText.setFocusableInTouchMode(true);
					mzEditText.requestFocus();
					mzEditText.findFocus();
					
					InputMethodManager inputManager =  
				               (InputMethodManager)mzEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				    inputManager.showSoftInput(mzEditText, 0); 
				} else {
					Intent it = new Intent(QuestionActivity.this,
							LoginActivity.class);
					QuestionActivity.this.startActivity(it);
				}
			}
		});
		sendLayout = (LinearLayout)findViewById(R.id.sendlayout);
		resLayout = (LinearLayout)findViewById(R.id.resLayout);
		mzEditText = (MyEditText)findViewById(R.id.chat_edit);
		sendTextButton = (MyTextButton)findViewById(R.id.send_meassage_button);
		sendTextButton.setOnClickListener(this);
		prescriptionButton = (MyTextButton)findViewById(R.id.prescription_button);
		prescriptionButton.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		questionId = getIntent().getIntExtra("questionId", 0);
		status = getIntent().getIntExtra("status", 0);
		TextView titleTextView = (TextView)findViewById(R.id.title_text);
		if(status==0||status==1){
			titleTextView.setText("问题详细信息");
			sendBtn.setText("回答");
		}
		else{
			titleTextView.setText("种植分享详细信息");
			sendBtn.setText("评论");
		}
		loadData();
		uid=GFUserDictionary.getUserId(getApplicationContext());
		adapter = new ResponseAdapter(this,this,this,uid);
		pullToRefreshListView.setAdapter(adapter);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
					}
				});
		pullToRefreshListView.setOnItemClickListener(this);
		registerForContextMenu(pullToRefreshListView.getRefreshableView());
		if(uid!=null&&GFUserDictionary.getTjcode(this)==null){
			loadUser();
		}	
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("问题内容");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("问题内容");
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(info.position>1){
			String uid = GFUserDictionary.getUserId(getApplicationContext());
			if(uid!=null){
				
				Response response = question.getResponses().get(info.position-2);
				if(response.getWriter().getId().equals(uid)){
					menu.add(0, 0, 0, "删除");
					menu.add(0,1,0,"加入处方");
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
			selectResponse = question.getResponses().get(info.position-2);
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
					
					delete(question.getId(),selectResponse.getId());
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
	        contentView.setText("确定要删除选中的答案吗？");
	        dialog.show();
			break;
		case 1:
			selectResponse = question.getResponses().get(info.position-2);
			Intent intent2 = new Intent(this,PrescriptionEditActivity.class);
			intent2.putExtra("content", selectResponse.getContent());
			startActivity(intent2);
			break;

		default:
			break;
		}
		
		 
		return super.onContextItemSelected(item);
	}
	
	private void delete(final int qid,final int rid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deleteResponse(qid,rid);
				if(status==0){
					jsonString = HttpUtil.deleteResponse(qid,rid);
				}
				else if(status==1){
					jsonString = HttpUtil.deleteResponseForGossip(qid,rid);
				}
				else{
					jsonString = HttpUtil.deleteResponseForPlant(qid,rid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == 2) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					loadData();
				}
			}, 1000);
		}
		if (resultCode == RESULT_OK && requestCode == 100) {
			String content = data.getStringExtra("content");
			if(content!=null&&content.length()>0){
				mzEditText.setText(content);
				mzEditText.setSelection(content.length());
				Timer timer = new Timer();  
			     timer.schedule(new TimerTask()  
			     {  
			           
			         public void run()  
			         {  
			             InputMethodManager inputManager =  
			                 (InputMethodManager)mzEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
			             inputManager.showSoftInput(mzEditText, 0);  
			         }  
			           
			     },  998);  
			}
		}
	}
	
	public void loadUser() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId(QuestionActivity.this));
				Message msg = handler.obtainMessage();
				msg.what=8;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String jsonString;
				if(status==0){
					jsonString = HttpUtil.getSingleQuestion(questionId);
				}
				else if(status==1){
					jsonString = HttpUtil.getSingleGossip(questionId);
				}
				else{
					jsonString = HttpUtil.getSinglePlant(questionId);
				}
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();

	}
	
	private void sendResponse(final Response response, final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NetResponse netResponse;
					if(status==0){
						netResponse = HttpUtil.sendResponse(response, qid);
					}
					else if(status==1){
						netResponse = HttpUtil.sendResponseForGossip(response, qid);
					}
					else{
						netResponse = HttpUtil.sendResponseForPlant(response, qid);
					}
					Message msg = handler.obtainMessage();
					msg.what = 7;
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
	
	public void popwindow(){
		morePopupWindow = new MorePopupWindow(this,this);
		morePopupWindow.setFocusable(true);
		morePopupWindow.setOutsideTouchable(true);
		morePopupWindow.update();
    	ColorDrawable dw = new ColorDrawable(0xb0000000);
    	morePopupWindow.setBackgroundDrawable(dw);
    	morePopupWindow.showAtLocation(findViewById(R.id.main), 
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onClick(View view, String urlString) {
		if (!GFString.isNumeric(urlString)) {
			return;
		}
		Intent it = new Intent(this, DiseaseActivity.class);
		it.putExtra("diseaseId", Integer.parseInt(urlString));
		startActivity(it);
	}
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		this.setResult(2);
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterForContextMenu(pullToRefreshListView.getRefreshableView());
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(uid==null){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return;
		}
		switch (v.getId()) {
		case R.id.head_image:
//			if(GFUserDictionary.getUserId()==null){
//				GFToast.show("请您先登录！");
//				return;
//			}
//			User user = (User)v.getTag();
//			if(user.getLevelID()!=1){
//				Intent it = new Intent();
//				it.setClass(this, ChatActivity.class);
//				it.putExtra("userName", user.getPhone());
//				it.putExtra("title", user.getAlias());
//				it.putExtra("thumbnail", user.getThumbnail());
//				startActivity(it);
//			}
			break;
		case R.id.zan_layout:
			if(GFUserDictionary.getUserId(getApplicationContext())==null){
				GFToast.show(getApplicationContext(),"请您先登录！");
				return;
			}
			if(question.getWriter().getId().equals(uid)&&status!=2){
				if(adapter.isAdopt()){
					GFToast.show(getApplicationContext(),"已经采纳过了");
					return;
				}
				
				selectResponse = (Response)v.getTag();
				if(selectResponse.getWriter().getId().equals(uid)){
					GFToast.show(getApplicationContext(),"不能采纳自己的答案。");
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						NetResponse netResponse;
						if(status==0){
							netResponse = HttpUtil.adoptResponse(questionId,selectResponse.getId(),question.getWriter().getId());
						}
						else{
							netResponse = HttpUtil.adoptResponseForGossip(questionId,selectResponse.getId(),question.getWriter().getId());
						}
						Message msg = handler.obtainMessage();
						if(netResponse.getStatus()==1){
							msg.what = 6;
							msg.obj = netResponse.getResult();
						}
						else{
							msg.what = 0;
							msg.obj = netResponse.getMessage();
						}
						msg.sendToTarget();
					}
				}).start();
				
			}
			else{
				selectResponse = (Response)v.getTag();
				if(selectResponse.getWriter().getId().equals(uid)){
					GFToast.show(getApplicationContext(),"不能给自己的答案点赞。");
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						NetResponse netResponse;
						if(status==0){
							netResponse = HttpUtil.agreeResponse(questionId,selectResponse.getId(),uid);
						}
						else if(status==1){
							netResponse = HttpUtil.agreeResponseForGossip(questionId,selectResponse.getId(),uid);
						}
						else{
							netResponse = HttpUtil.agreeResponseForPlant(questionId,selectResponse.getId(),uid);
						}
						Message msg = handler.obtainMessage();
						if(netResponse.getStatus()==1){
							msg.what = 4;
							msg.obj = netResponse.getResult();
						}
						else{
							msg.what = 0;
							msg.obj = netResponse.getMessage();
						}
						msg.sendToTarget();
					}
				}).start();
			}
			break;
		case R.id.plantzan_layout:
			if(question.getWriter().getId().equals(uid)){
				GFToast.show(getApplicationContext(),"不能给自己的分享点赞。");
				return;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					NetResponse netResponse=HttpUtil.agreePlant(question.getId(),uid);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 9;
						msg.obj = netResponse.getResult();
					}
					else{
						msg.what = 0;
						msg.obj = netResponse.getMessage();
					}
					msg.sendToTarget();
				}
			}).start();
			break;

		case R.id.count_layout:
			
			Response res = (Response)v.getTag();
			Intent it = new Intent(this, CommentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("question", question);		
			bundle.putSerializable("response", res);
			it.putExtras(bundle);
			if(status==1||status==2){
				it.putExtra("status", status);
			}
			startActivity(it);
			
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
			sendResponse(response, questionId);
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
		case R.id.prescription_button:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
			Intent intent = new Intent(this,PrescriptionsActivity.class);
			this.startActivityForResult(intent, 100);
			break;
		case R.id.more_image:
			rid = Integer.parseInt(v.getTag().toString());
			popwindow();
			break;
		case R.id.img_more_report:
			Intent it1 = new Intent(this, ReportActivity.class);
			it1.putExtra("status", status);
			it1.putExtra("qid", questionId);
			it1.putExtra("rid", rid);
			startActivity(it1);
			morePopupWindow.dismiss();
			break;
		case R.id.img_share_weixin:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = HttpUtil.ViewUrl+"appshare?code="+GFUserDictionary.getTjcode(this);
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
			morePopupWindow.dismiss();
			break;
		case R.id.img_share_circlefriends:
			if(!wxApi.isWXAppInstalled()){
				GFToast.show(getApplicationContext(),"您还未安装微信客户端");
				return;
			}
			WXWebpageObject webpage1 = new WXWebpageObject();
			webpage1.webpageUrl = HttpUtil.ViewUrl+"appshare?code="+GFUserDictionary.getTjcode(this);
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
			morePopupWindow.dismiss();
			break;
		case R.id.img_share_qq:
			Bundle params = new Bundle();
		    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		    params.putString(QQShare.SHARE_TO_QQ_TITLE, "种好地APP:让种地不再难");
		    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。");
		    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  HttpUtil.ViewUrl+"appshare?code="+GFUserDictionary.getTjcode(this));
		    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://121.40.62.120/appimage/apps/appicon.png");
		    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "种好地");
		    mTencent.shareToQQ(this, params, new BaseUiListener());
		    morePopupWindow.dismiss();
			
			break;
		case R.id.img_share_qzone:
			Bundle params1 = new Bundle();
			params1.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
		    params1.putString(QzoneShare.SHARE_TO_QQ_TITLE, "种好地APP:让种地不再难");//必填
		    params1.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "下载APP，享受优惠农资产品，众多专家，农技达人为您解决病虫害问题，让您种地更科学，丰收更简单。");//选填
		    params1.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, HttpUtil.ViewUrl+"appshare?code="+GFUserDictionary.getTjcode(this));//必填
		    ArrayList<String> urlsList = new ArrayList<String>();
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_1_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_2_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_3_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_4_1440519318/550");
		    urlsList.add("http://pp.myapp.com/ma_pic2/0/shot_12109155_5_1440519318/550");
		    params1.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urlsList);
		    mTencent.shareToQzone(this, params1, new BaseUiListener());
		    morePopupWindow.dismiss();
			break;
		default:
			break;
		}
		
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(position<2){
			return;
		}
		Response response = question.getResponses().get(position-2);
		Intent intent = new Intent(this, CommentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("question", question);		
		bundle.putSerializable("response", response);
		intent.putExtras(bundle);
		if(status==1||status==2){
			intent.putExtra("status", status);
		}
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		switch (msg.what) {
		case 1:
			Gson gson = new Gson();
			String jsonString = (String) msg.obj;
			question = gson.fromJson(jsonString, Question.class);
			adapter.setQuestion(question);
			adapter.setStatus(status);
			if(question==null){
				GFToast.show(getApplicationContext(),"问题不存在");
				finish();
			}
			else if(question.getStatus()!=1){
				if(question.getWriter().getId().equals(uid)){
					adapter.setmContains(true);
				}
				adapter.notifyDataSetChanged();
			}
			else{
				GFToast.show(getApplicationContext(),"问题已删除");
			}
			pullToRefreshListView.onRefreshComplete();
			break;
		case 3:
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				GFToast.show(getApplicationContext(),strerr);
			}
			else{
				question.getResponses().remove(selectResponse);
				adapter.notifyDataSetChanged();
			}
			break;
		case 4:
			if(msg.obj!=null){
				Checkobj checkobj = (Checkobj) GsonUtil.fromJson(
						msg.obj.toString(), Checkobj.class);
				if(checkobj!=null && checkobj.isResult()){
					selectResponse.setAgree(selectResponse.getAgree()+1);
					adapter.notifyDataSetChanged();
				}
				else{
					GFToast.show(getApplicationContext(),checkobj.getMessage());
				}
			}
			else{
				GFToast.show(getApplicationContext(),"操作失败");
			}
			break;
		case 5:
			if(msg.obj!=null){
				Checkobj checkobj = (Checkobj) GsonUtil.fromJson(
						msg.obj.toString(), Checkobj.class);
				if(checkobj!=null && checkobj.isResult()){
					selectResponse.setDisagree(selectResponse.getDisagree()+1);
					adapter.notifyDataSetChanged();
				}
				else{
					GFToast.show(getApplicationContext(),checkobj.getMessage());
				}
			}
			else{
				GFToast.show(getApplicationContext(),"操作失败");
			}
			break;
		case 6:
			if(msg.obj!=null){
				Checkobj checkobj = (Checkobj) GsonUtil.fromJson(
						msg.obj.toString(), Checkobj.class);
				if(checkobj!=null && checkobj.isResult()){
					selectResponse.setAdopt(true);
					adapter.notifyDataSetChanged();
				}
				else{
					GFToast.show(getApplicationContext(),checkobj.getMessage());
				}
			}
			else{
				GFToast.show(getApplicationContext(),"操作失败");
			}
			break;
		case 7:
			loadData();
			if(adapter.ismContains()){
				GFToast.show(getApplicationContext(),"回复成功");
			}
			else{
				boolean less = GFDate.lessTenMinutes(question.getStime());
				int point = GFPointDictionary.getResponsePoint(getApplicationContext());
				if(point>0){
					if(less){
						GFToast.show(getApplicationContext(),"回复成功,10分钟内回答双倍积分+"+(2*point)+" ^-^");
					}
					else{
						GFToast.show(getApplicationContext(),"回复成功,积分+"+point+" ^-^");
					}
				}
				else{
					GFToast.show(getApplicationContext(),"回复成功");
				}
			}
			break;
		case 0:
			if(msg.obj!=null){
				GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 8:
			if (msg.obj == null) {
				return;
			}
			User user = (User) GsonUtil
					.fromJson(msg.obj.toString(), User.class);
			// 获取SharedPreferences对象
			SharedPreferences sharedPre = this.getSharedPreferences("config",
					Context.MODE_PRIVATE);
			// 获取Editor对象
			Editor editor = sharedPre.edit();
			editor.putString("tjcode", user.getTjCode());
			editor.commit();
			break;
		case 9:
			if(msg.obj!=null){
				Gson gson2 = new Gson();
				String jString = (String) msg.obj;
				PostResponse reportResponse = gson2.fromJson(jString, PostResponse.class);
				if(reportResponse == null){
					GFToast.show(this,"点赞操作错误");
				}
				else{
					if(reportResponse.isResult()){
						question.setAgree(question.getAgree()+1);
						adapter.notifyDataSetChanged();
					}
					else{
						GFToast.show(this,reportResponse.getMessage());
					}
				}
			}
			else{
				GFToast.show(this,"点赞操作错误");
			}
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

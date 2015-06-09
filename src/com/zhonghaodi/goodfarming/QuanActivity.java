package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.a.a.a.b;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.CustomRelativeLayout;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.Holder1;
import com.zhonghaodi.customui.Holder2;
import com.zhonghaodi.customui.Holder3;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.QuanHolder;
import com.zhonghaodi.customui.QuanHolder3;
import com.zhonghaodi.customui.QuanHolder6;
import com.zhonghaodi.customui.QuanHolder9;
import com.zhonghaodi.goodfarming.HomeFragment.QuestionAdpter;
import com.zhonghaodi.model.Comment;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.OnSizeChangedListener;
import com.zhonghaodi.model.Quan;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class QuanActivity extends Activity implements HandMessage,OnClickListener,OnSizeChangedListener {

	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Quan> allQuans;
	private QuanAdapter adapter;
	private GFHandler<QuanActivity> handler = new GFHandler<QuanActivity>(this);
	private Button cancelBtn;
	private ImageView quanButton;
	private CustomRelativeLayout bottomView;
	private EditText pinglunEditText;
	private MyTextButton sendBtn;
	private Quan selectQuan;
	BroadcastReceiver receiver;	
	IntentFilter filter;
	private String uid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quan);
		cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		quanButton = (ImageView)findViewById(R.id.quan_button);
		quanButton.setOnClickListener(this);
		pinglunEditText = (EditText)findViewById(R.id.pinglun_edit);
		sendBtn = (MyTextButton)findViewById(R.id.send_pinglun_button);
		sendBtn.setOnClickListener(this);
		bottomView = (CustomRelativeLayout)findViewById(R.id.bottom_view);
		bottomView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return hideInput();
			}
		});
		bottomView.setOnSizeChangedListener(this);
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadNewDate();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuans.size() == 0) {
							return;
						}
						Quan quan = allQuans.get(allQuans
								.size() - 1);
						loadMoreData(quan.getId());
					}

				});
		allQuans = new ArrayList<Quan>();
		adapter = new QuanAdapter();
		pullToRefreshListView.getRefreshableView()
				.setAdapter(adapter);
		//注册一个广播接收器，启动餐桌抖动动画  
        receiver = new BroadcastReceiver() {
	    	@Override
	        public void onReceive(Context ctx, Intent intent) {
	    		if (intent.getAction().equals("refresh")) {
	    			loadNewDate();
	 	    		}
	    	}
	    };
	    filter = new IntentFilter();
	    filter.addAction("refresh");
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    uid = GFUserDictionary.getUserId();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(receiver, filter);
		loadNewDate();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	private void loadNewDate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuansString(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	private void loadMoreData(final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuansString(uid, qid);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	class QuanAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allQuans.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allQuans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			Quan quan = allQuans.get(position);
			if (quan.getAttachments().size() == 0) {
				return 0;
			}
			if (quan.getAttachments().size() > 0
					&& quan.getAttachments().size() < 4) {
				return 1;
			}
			if (quan.getAttachments().size() > 3
					&& quan.getAttachments().size() < 7) {
				return 2;
			}
			if (quan.getAttachments().size() > 6) {
				return 3;
			}
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Quan quan = allQuans.get(position);
			boolean bzan = false;
			int type = getItemViewType(position);
			QuanHolder holder1 = null;
			QuanHolder3 holder2 = null;
			QuanHolder6 holder3 = null;
			QuanHolder9 holder4 = null;
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = LayoutInflater.from(QuanActivity.this).inflate(
							R.layout.cell_quan, parent, false);
					holder1 = new QuanHolder(convertView);
					convertView.setTag(holder1);
					break;
				case 1:
					convertView = LayoutInflater.from(
							QuanActivity.this).inflate(
							R.layout.cell_quan_3_image, parent, false);
					holder2 = new QuanHolder3(convertView);			
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = LayoutInflater.from(
							QuanActivity.this).inflate(
							R.layout.cell_quan_6_image, parent, false);
					holder3 = new QuanHolder6(convertView);
					convertView.setTag(holder3);
					break;
				case 3:
					convertView = LayoutInflater.from(
							QuanActivity.this).inflate(
							R.layout.cell_quan_9_image, parent, false);
					holder4 = new QuanHolder9(convertView);
					convertView.setTag(holder4);
					break;
				default:
					break;
				}
			}
			switch (type) {
			case 0:
				holder1 = (QuanHolder) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/users/small/"
								+ quan.getWriter().getThumbnail(),
						holder1.headIv, ImageOptions.options);
				holder1.nameTv.setText(quan.getWriter().getAlias());
				holder1.timeTv.setText(quan.getTime());
				holder1.contentTv.setText(quan.getContent());
				if(quan.getComments()!=null && quan.getComments().size()>0){
					String commentStr = "";
					String zanString = "♡";
					for (int i = 0;i<quan.getComments().size();i++) {
						Comment comment = quan.getComments().get(i);
						if(comment.getStatus()!=null&&comment.getStatus()==1){
							if(comment.getWriter().getId().equals(uid)){
								bzan = true;
							}
							zanString+=comment.getWriter().getAlias()+"、";
						}
						else{
							commentStr+=comment.getWriter().getAlias()+
									":"+comment.getContent()+"\n";
						}
						
					}
					zanString = zanString.substring(0, zanString.length()-1);
					if(zanString.length()>1 && commentStr.length()>1){
						commentStr =zanString+"\n"+ commentStr.substring(0, commentStr.length()-1);
					}
					else if(zanString.length()>1 && commentStr.length()<1){
						commentStr =zanString;
					}
					else{
						commentStr = commentStr.substring(0, commentStr.length()-1);
					}
					holder1.commentTv.setText(commentStr);
					holder1.commentTv.setVisibility(View.VISIBLE);
				}
				else{
					holder1.commentTv.setVisibility(View.GONE);
				}
				
				holder1.pinlunImg.setOnClickListener(QuanActivity.this);
				holder1.pinlunImg.setTag(quan);
				if(bzan){
					holder1.zanImg.setVisibility(View.GONE);
				}
				else{
					holder1.zanImg.setVisibility(View.VISIBLE);
					holder1.zanImg.setOnClickListener(QuanActivity.this);
					holder1.zanImg.setTag(quan);
				}
				break;
			case 1:
				holder2 = (QuanHolder3) convertView.getTag();
				holder2.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/users/small/"
								+ quan.getWriter().getThumbnail(),
						holder2.headIv, ImageOptions.options);
				holder2.nameTv.setText(quan.getWriter().getAlias());
				holder2.contentTv.setText(quan.getContent());
				if(quan.getComments()!=null && quan.getComments().size()>0){
					String commentStr = "";
					String zanString = "♡";
					for (int i = 0;i<quan.getComments().size();i++) {
						Comment comment = quan.getComments().get(i);
						if(comment.getStatus()!=null&&comment.getStatus()==1){
							if(comment.getWriter().getId().equals(uid)){
								bzan = true;
							}
							zanString+=comment.getWriter().getAlias()+"、";
						}
						else{
							commentStr+=comment.getWriter().getAlias()+
									":"+comment.getContent()+"\n";
						}
						
					}
					zanString = zanString.substring(0, zanString.length()-1);
					if(zanString.length()>1 && commentStr.length()>1){
						commentStr =zanString+"\n"+ commentStr.substring(0, commentStr.length()-1);
					}
					else if(zanString.length()>1 && commentStr.length()<1){
						commentStr =zanString;
					}
					else{
						commentStr = commentStr.substring(0, commentStr.length()-1);
					}
					holder2.commentTv.setText(commentStr);
					holder2.commentTv.setVisibility(View.VISIBLE);
				}
				else{
					holder2.commentTv.setVisibility(View.GONE);
				}
				holder2.timeTv.setText(quan.getTime());
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(0).getUrl(),
						holder2.imageView1, ImageOptions.options);
				holder2.imageView1.setVisibility(View.VISIBLE);
				holder2.imageView1.setIndex(0);
				holder2.imageView1.setImages(quan.getAttachments(),"quans");
				if (quan.getAttachments().size() > 1) {
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/quans/small/"
											+ quan.getAttachments().get(1)
													.getUrl(),
									holder2.imageView2, ImageOptions.options);
					holder2.imageView2.setVisibility(View.VISIBLE);
					holder2.imageView2.setIndex(1);
					holder2.imageView2.setImages(quan.getAttachments(),"quans");
				}
				if (quan.getAttachments().size() > 2) {
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/quans/small/"
											+ quan.getAttachments().get(2)
													.getUrl(),
									holder2.imageView3, ImageOptions.options);
					holder2.imageView3.setVisibility(View.VISIBLE);
					holder2.imageView3.setIndex(2);
					holder2.imageView3.setImages(quan.getAttachments(),"quans");
				}
				holder2.pinlunImg.setOnClickListener(QuanActivity.this);
				holder2.pinlunImg.setTag(quan);
				if(bzan){
					holder2.zanImg.setVisibility(View.GONE);
				}
				else{
					holder2.zanImg.setVisibility(View.VISIBLE);
					holder2.zanImg.setOnClickListener(QuanActivity.this);
					holder2.zanImg.setTag(quan);
				}
				break;
			case 2:
				holder3 = (QuanHolder6) convertView.getTag();
				holder3.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/users/small/"
								+ quan.getWriter().getThumbnail(),
						holder3.headIv, ImageOptions.options);
				holder3.nameTv.setText(quan.getWriter().getAlias());
				holder3.timeTv.setText(quan.getTime());
				holder3.contentTv.setText(quan.getContent());
				if(quan.getComments()!=null && quan.getComments().size()>0){
					String commentStr = "";
					String zanString = "♡";
					for (int i = 0;i<quan.getComments().size();i++) {
						Comment comment = quan.getComments().get(i);
						if(comment.getStatus()!=null&&comment.getStatus()==1){
							if(comment.getWriter().getId().equals(uid)){
								bzan = true;
							}
							zanString+=comment.getWriter().getAlias()+"、";
						}
						else{
							commentStr+=comment.getWriter().getAlias()+
									":"+comment.getContent()+"\n";
						}
					}
					zanString = zanString.substring(0, zanString.length()-1);
					if(zanString.length()>1 && commentStr.length()>1){
						commentStr =zanString+"\n"+ commentStr.substring(0, commentStr.length()-1);
					}
					else if(zanString.length()>1 && commentStr.length()<1){
						commentStr =zanString;
					}
					else{
						commentStr = commentStr.substring(0, commentStr.length()-1);
					}
					holder3.commentTv.setText(commentStr);
					holder3.commentTv.setVisibility(View.VISIBLE);
				}
				else{
					holder3.commentTv.setVisibility(View.GONE);
				}
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(0).getUrl(),
						holder3.imageView1, ImageOptions.options);
				holder3.imageView1.setVisibility(View.VISIBLE);
				holder3.imageView1.setIndex(0);
				holder3.imageView1.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(1).getUrl(),
						holder3.imageView2, ImageOptions.options);
				holder3.imageView2.setVisibility(View.VISIBLE);
				holder3.imageView2.setIndex(1);
				holder3.imageView2.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(2).getUrl(),
						holder3.imageView3, ImageOptions.options);
				holder3.imageView3.setVisibility(View.VISIBLE);
				holder3.imageView3.setIndex(2);
				holder3.imageView3.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(3).getUrl(),
						holder3.imageView4, ImageOptions.options);
				holder3.imageView4.setVisibility(View.VISIBLE);
				holder3.imageView4.setIndex(3);
				holder3.imageView4.setImages(quan.getAttachments(),"quans");
				if (quan.getAttachments().size() > 4) {
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/quans/small/"
											+ quan.getAttachments().get(4)
													.getUrl(),
									holder3.imageView5, ImageOptions.options);
					holder3.imageView5.setVisibility(View.VISIBLE);
					holder3.imageView5.setIndex(4);
					holder3.imageView5.setImages(quan.getAttachments(),"quans");
				}
				if (quan.getAttachments().size() > 5) {
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/quans/small/"
											+ quan.getAttachments().get(5)
													.getUrl(),
									holder3.imageView6, ImageOptions.options);
					holder3.imageView6.setVisibility(View.VISIBLE);
					holder3.imageView6.setIndex(5);
					holder3.imageView6.setImages(quan.getAttachments(),"quans");
				}
				holder3.pinlunImg.setOnClickListener(QuanActivity.this);
				holder3.pinlunImg.setTag(quan);
				if(bzan){
					holder3.zanImg.setVisibility(View.GONE);
				}
				else{
					holder3.zanImg.setVisibility(View.VISIBLE);
					holder3.zanImg.setOnClickListener(QuanActivity.this);
					holder3.zanImg.setTag(quan);
				}
				break;
			case 3:
				holder4 = (QuanHolder9) convertView.getTag();
				holder4.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/users/small/"
								+ quan.getWriter().getThumbnail(),
						holder4.headIv, ImageOptions.options);
				holder4.nameTv.setText(quan.getWriter().getAlias());
				holder4.timeTv.setText(quan.getTime());
				holder4.contentTv.setText(quan.getContent());
				if(quan.getComments()!=null && quan.getComments().size()>0){
					String commentStr = "";
					String zanString = "♡";
					for (int i = 0;i<quan.getComments().size();i++) {
						Comment comment = quan.getComments().get(i);
						if(comment.getStatus()!=null&&comment.getStatus()==1){
							if(comment.getWriter().getId().equals(uid)){
								bzan = true;
							}
							zanString+=comment.getWriter().getAlias()+"、";
						}
						else{
							commentStr+=comment.getWriter().getAlias()+
									":"+comment.getContent()+"\n";
						}
					}
					zanString = zanString.substring(0, zanString.length()-1);
					if(zanString.length()>1 && commentStr.length()>1){
						commentStr =zanString+"\n"+ commentStr.substring(0, commentStr.length()-1);
					}
					else if(zanString.length()>1 && commentStr.length()<1){
						commentStr =zanString;
					}
					else{
						commentStr = commentStr.substring(0, commentStr.length()-1);
					}
					holder4.commentTv.setText(commentStr);
					holder4.commentTv.setVisibility(View.VISIBLE);
				}
				else{
					holder4.commentTv.setVisibility(View.GONE);
				}
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(0).getUrl(),
						holder4.imageView1, ImageOptions.options);
				holder4.imageView1.setVisibility(View.VISIBLE);
				holder4.imageView1.setIndex(0);
				holder4.imageView1.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(1).getUrl(),
								holder4.imageView2, ImageOptions.options);
				holder4.imageView2.setVisibility(View.VISIBLE);
				holder4.imageView2.setIndex(1);
				holder4.imageView2.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(2).getUrl(),
								holder4.imageView3, ImageOptions.options);
				holder4.imageView3.setVisibility(View.VISIBLE);
				holder4.imageView3.setIndex(2);
				holder4.imageView3.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/quans/small/"
								+ quan.getAttachments().get(3).getUrl(),
								holder4.imageView4, ImageOptions.options);
				holder4.imageView4.setVisibility(View.VISIBLE);
				holder4.imageView4.setIndex(3);
				holder4.imageView4.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance()
						.displayImage(
								"http://121.40.62.120/appimage/quans/small/"
										+ quan.getAttachments().get(4)
												.getUrl(),
												holder4.imageView5, ImageOptions.options);
				holder4.imageView5.setVisibility(View.VISIBLE);
				holder4.imageView5.setIndex(4);
				holder4.imageView5.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance()
						.displayImage(
								"http://121.40.62.120/appimage/quans/small/"
										+ quan.getAttachments().get(5)
												.getUrl(),
												holder4.imageView6, ImageOptions.options);
				holder4.imageView6.setVisibility(View.VISIBLE);
				holder4.imageView6.setIndex(5);
				holder4.imageView6.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance()
						.displayImage(
								"http://121.40.62.120/appimage/quans/small/"
										+ quan.getAttachments().get(6)
												.getUrl(),
												holder4.imageView7, ImageOptions.options);
				holder4.imageView7.setVisibility(View.VISIBLE);
				holder4.imageView7.setIndex(6);
				holder4.imageView7.setImages(quan.getAttachments(),"quans");
				if (quan.getAttachments().size() > 7) {
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/quans/small/"
											+ quan.getAttachments().get(7)
													.getUrl(),
													holder4.imageView8, ImageOptions.options);
					holder4.imageView8.setVisibility(View.VISIBLE);
					holder4.imageView8.setIndex(7);
					holder4.imageView8.setImages(quan.getAttachments(),"quans");
				}
				if (quan.getAttachments().size() > 8) {
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/quans/small/"
											+ quan.getAttachments().get(8)
													.getUrl(),
													holder4.imageView9, ImageOptions.options);
					holder4.imageView9.setVisibility(View.VISIBLE);
					holder4.imageView9.setIndex(8);
					holder4.imageView9.setImages(quan.getAttachments(),"quans");
				}
				holder4.pinlunImg.setOnClickListener(QuanActivity.this);
				holder4.pinlunImg.setTag(quan);
				if(bzan){
					holder4.zanImg.setVisibility(View.GONE);
				}
				else{
					holder4.zanImg.setVisibility(View.VISIBLE);
					holder4.zanImg.setOnClickListener(QuanActivity.this);
					holder4.zanImg.setTag(quan);
				}
				break;
			default:
				break;
			}

			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.pinglun_img:
			selectQuan = (Quan)v.getTag();
			displayInput();		
			break;
		case R.id.zan_img:
			Quan quan = (Quan)v.getTag();
			zan(quan);
			break;
		case R.id.send_pinglun_button:
			pinglun(selectQuan);
			break;
		case R.id.quan_button:
			Intent intent = new Intent(this,NyqActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	private void displayInput(){
		bottomView.setVisibility(View.VISIBLE);
		pinglunEditText.requestFocus();
		InputMethodManager imm = (InputMethodManager)pinglunEditText.getContext().
				getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}
	
	private boolean hideInput(){
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);  
	    boolean b = imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	    
	    return b;
	}
	
	public void zan(Quan quan){
		Comment comment = new Comment();
		comment.setStatus(1);
		sendpinlun(comment, quan.getId());
	}
	
	public void pinglun(Quan quan){
		hideInput();
		String content = pinglunEditText.getText().toString();
		if(content==null || content==""){
			return;
		}
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setStatus(0);
		sendpinlun(comment, quan.getId());
	}
	
	public void sendpinlun(final Comment comment,final int qid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpUtil.pinlunQuan(uid, qid, comment);
					Message msg = QuanActivity.this.handler.obtainMessage();
					msg.what = 1;
					msg.sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
					msg.what=-1;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		if(oldh!=0&&oldh<h){
			bottomView.setVisibility(View.GONE);
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final QuanActivity quanActivity =(QuanActivity)object;
		switch (msg.what) {
		case -1:
			GFToast.show("连接服务器失败,请稍候再试!");
			break;
			
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Quan> quans = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Quan>>() {
						}.getType());
				quanActivity.allQuans.clear();
				for (Quan quan : quans) {
					quanActivity.allQuans.add(quan);
				}
				quanActivity.adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			quanActivity.pullToRefreshListView.onRefreshComplete();
			break;
		case 1:
			loadNewDate();
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Quan> quans = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Quan>>() {
						}.getType());
				for (Quan quan : quans) {
					quanActivity.allQuans.add(quan);
				}
				quanActivity.adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			quanActivity.pullToRefreshListView.onRefreshComplete();
			break;

		default:
			break;
		}
		
	}

}

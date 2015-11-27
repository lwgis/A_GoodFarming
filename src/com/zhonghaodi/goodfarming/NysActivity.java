package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.CustomRelativeLayout;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.QuanHolder;
import com.zhonghaodi.customui.QuanHolder3;
import com.zhonghaodi.customui.QuanHolder6;
import com.zhonghaodi.customui.QuanHolder9;
import com.zhonghaodi.model.Comment;
import com.zhonghaodi.model.Follow;
import com.zhonghaodi.model.Function;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Nys;
import com.zhonghaodi.model.OnSizeChangedListener;
import com.zhonghaodi.model.Quan;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.UserCrop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NysActivity extends Activity implements HandMessage,OnClickListener,OnSizeChangedListener {

	private String uid;
	private String mid;
	private boolean bfollow;
	private Nys user;
	private ArrayList<Quan> allQuans;
	private Button cancelBtn;
	private CustomRelativeLayout bottomView;
	private EditText pinglunEditText;
	private MyTextButton sendBtn;
	private Quan selectQuan;
	private PullToRefreshListView pullToRefreshList;
	private NysAdapter adapter;
	private GFHandler<NysActivity> handler = new GFHandler<NysActivity>(this);
	private View clickView;
	private TextView titleView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nys);
		titleView = (TextView)findViewById(R.id.title_text);
		uid = getIntent().getStringExtra("uid");
		mid = GFUserDictionary.getUserId();
		bfollow = getIntent().getBooleanExtra("bfollow", false);
		cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
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
		pullToRefreshList = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		pullToRefreshList.setMode(Mode.BOTH);
		pullToRefreshList
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
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
		adapter = new NysAdapter();
		allQuans = new ArrayList<Quan>();
		pullToRefreshList.getRefreshableView().setAdapter(adapter);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		if(oldh!=0&&oldh<h){
			bottomView.setVisibility(View.GONE);
		}
	}
	/**
	 * 读取农艺师信息
	 */
	public void loadData() {
		allQuans.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 读取农艺师的朋友圈文章最新20条
	 */
	private void loadNewDate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String uid = user.getId();
				String jsonString = HttpUtil.getNysQuansString(uid);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 读取更多的农艺师朋友圈文章
	 * @param qid
	 */
	private void loadMoreData(final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String uid = user.getId();
				String jsonString = HttpUtil.getNysQuansString(uid, qid);
				Message msg = handler.obtainMessage();
				msg.what = 5;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	/**
	 * 关注农艺师
	 */
	public void follow(){
		final String mid = GFUserDictionary.getUserId();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				NetResponse netResponse = HttpUtil.follow(mid,user);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 1;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = -1;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();

			}
		}).start();
	}
	
	/**
	 * 取消关注
	 */
	public void cancelfollow(){
		final String mid = GFUserDictionary.getUserId();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.cancelfollow(mid,user);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
	}
	
	class NysInfoHolder{
		public ImageView headIv;
		public TextView titleTv;
		public TextView jifenTv;
		public TextView youhuibiTv;
		public Button followButton;
		public Button chatButton;
		public NysInfoHolder(View view){
			headIv=(ImageView)view.findViewById(R.id.head_image);
			titleTv=(TextView)view.findViewById(R.id.title_text);
			jifenTv=(TextView)view.findViewById(R.id.jifen_text);
			youhuibiTv=(TextView)view.findViewById(R.id.youhuibi_text);
			followButton = (Button)view.findViewById(R.id.follow_btn);
			chatButton = (Button)view.findViewById(R.id.chat_btn);
		}
	}
	
	class NysAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (user == null) {
				return 0;
			}
			return allQuans.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 5;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 4;
			}
			else{
				Quan quan = allQuans.get(position-1);
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
			}
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			boolean bzan = false;
			NysInfoHolder holderNysInfo;
			QuanHolder holder1 = null;
			QuanHolder3 holder2 = null;
			QuanHolder6 holder3 = null;
			QuanHolder9 holder4 = null;
			int cellType = getItemViewType(position);
			Quan quan = null;
			if(position>0){
				quan = allQuans.get(position-1);
			}
			if (convertView == null) {
				switch (cellType) {
				case 0:
					convertView = LayoutInflater.from(NysActivity.this).inflate(
							R.layout.cell_quan, parent, false);
					holder1 = new QuanHolder(convertView);
					convertView.setTag(holder1);
					break;
				case 1:
					convertView = LayoutInflater.from(
							NysActivity.this).inflate(
							R.layout.cell_quan_3_image, parent, false);
					holder2 = new QuanHolder3(convertView);			
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = LayoutInflater.from(
							NysActivity.this).inflate(
							R.layout.cell_quan_6_image, parent, false);
					holder3 = new QuanHolder6(convertView);
					convertView.setTag(holder3);
					break;
				case 3:
					convertView = LayoutInflater.from(
							NysActivity.this).inflate(
							R.layout.cell_quan_9_image, parent, false);
					holder4 = new QuanHolder9(convertView);
					convertView.setTag(holder4);
					break;
				case 4:
					convertView = LayoutInflater.from(
							NysActivity.this).inflate(
							R.layout.cell_nys_info, parent, false);
					holderNysInfo = new NysInfoHolder(convertView);
					convertView.setTag(holderNysInfo);
					break;
				default:
					break;
				}

			}
			switch (cellType) {
			case 0:
				holder1 = (QuanHolder) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
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
							if(comment.getWriter().getId().equals(mid)){
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
				
				holder1.pinlunImg.setOnClickListener(NysActivity.this);
				holder1.pinlunImg.setTag(quan);
				if(bzan){
					holder1.zanImg.setVisibility(View.GONE);
				}
				else{
					holder1.zanImg.setVisibility(View.VISIBLE);
					holder1.zanImg.setOnClickListener(NysActivity.this);
					holder1.zanImg.setTag(quan);
				}
				break;
			case 1:
				holder2 = (QuanHolder3) convertView.getTag();
				holder2.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
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
							if(comment.getWriter().getId().equals(mid)){
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
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(0).getUrl(),
						holder2.imageView1, ImageOptions.options);
				holder2.imageView1.setVisibility(View.VISIBLE);
				holder2.imageView1.setIndex(0);
				holder2.imageView1.setImages(quan.getAttachments(),"quans");
				if (quan.getAttachments().size() > 1) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"quans/small/"
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
									HttpUtil.ImageUrl+"quans/small/"
											+ quan.getAttachments().get(2)
													.getUrl(),
									holder2.imageView3, ImageOptions.options);
					holder2.imageView3.setVisibility(View.VISIBLE);
					holder2.imageView3.setIndex(2);
					holder2.imageView3.setImages(quan.getAttachments(),"quans");
				}
				holder2.pinlunImg.setOnClickListener(NysActivity.this);
				holder2.pinlunImg.setTag(quan);
				if(bzan){
					holder2.zanImg.setVisibility(View.GONE);
				}
				else{
					holder2.zanImg.setVisibility(View.VISIBLE);
					holder2.zanImg.setOnClickListener(NysActivity.this);
					holder2.zanImg.setTag(quan);
				}
				break;
			case 2:
				holder3 = (QuanHolder6) convertView.getTag();
				holder3.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
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
							if(comment.getWriter().getId().equals(mid)){
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
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(0).getUrl(),
						holder3.imageView1, ImageOptions.options);
				holder3.imageView1.setVisibility(View.VISIBLE);
				holder3.imageView1.setIndex(0);
				holder3.imageView1.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(1).getUrl(),
						holder3.imageView2, ImageOptions.options);
				holder3.imageView2.setVisibility(View.VISIBLE);
				holder3.imageView2.setIndex(1);
				holder3.imageView2.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(2).getUrl(),
						holder3.imageView3, ImageOptions.options);
				holder3.imageView3.setVisibility(View.VISIBLE);
				holder3.imageView3.setIndex(2);
				holder3.imageView3.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(3).getUrl(),
						holder3.imageView4, ImageOptions.options);
				holder3.imageView4.setVisibility(View.VISIBLE);
				holder3.imageView4.setIndex(3);
				holder3.imageView4.setImages(quan.getAttachments(),"quans");
				if (quan.getAttachments().size() > 4) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"quans/small/"
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
									HttpUtil.ImageUrl+"quans/small/"
											+ quan.getAttachments().get(5)
													.getUrl(),
									holder3.imageView6, ImageOptions.options);
					holder3.imageView6.setVisibility(View.VISIBLE);
					holder3.imageView6.setIndex(5);
					holder3.imageView6.setImages(quan.getAttachments(),"quans");
				}
				holder3.pinlunImg.setOnClickListener(NysActivity.this);
				holder3.pinlunImg.setTag(quan);
				if(bzan){
					holder3.zanImg.setVisibility(View.GONE);
				}
				else{
					holder3.zanImg.setVisibility(View.VISIBLE);
					holder3.zanImg.setOnClickListener(NysActivity.this);
					holder3.zanImg.setTag(quan);
				}
				break;
			case 3:
				holder4 = (QuanHolder9) convertView.getTag();
				holder4.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
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
							if(comment.getWriter().getId().equals(mid)){
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
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(0).getUrl(),
						holder4.imageView1, ImageOptions.options);
				holder4.imageView1.setVisibility(View.VISIBLE);
				holder4.imageView1.setIndex(0);
				holder4.imageView1.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(1).getUrl(),
								holder4.imageView2, ImageOptions.options);
				holder4.imageView2.setVisibility(View.VISIBLE);
				holder4.imageView2.setIndex(1);
				holder4.imageView2.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(2).getUrl(),
								holder4.imageView3, ImageOptions.options);
				holder4.imageView3.setVisibility(View.VISIBLE);
				holder4.imageView3.setIndex(2);
				holder4.imageView3.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"quans/small/"
								+ quan.getAttachments().get(3).getUrl(),
								holder4.imageView4, ImageOptions.options);
				holder4.imageView4.setVisibility(View.VISIBLE);
				holder4.imageView4.setIndex(3);
				holder4.imageView4.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"quans/small/"
										+ quan.getAttachments().get(4)
												.getUrl(),
												holder4.imageView5, ImageOptions.options);
				holder4.imageView5.setVisibility(View.VISIBLE);
				holder4.imageView5.setIndex(4);
				holder4.imageView5.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"quans/small/"
										+ quan.getAttachments().get(5)
												.getUrl(),
												holder4.imageView6, ImageOptions.options);
				holder4.imageView6.setVisibility(View.VISIBLE);
				holder4.imageView6.setIndex(5);
				holder4.imageView6.setImages(quan.getAttachments(),"quans");
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"quans/small/"
										+ quan.getAttachments().get(6)
												.getUrl(),
												holder4.imageView7, ImageOptions.options);
				holder4.imageView7.setVisibility(View.VISIBLE);
				holder4.imageView7.setIndex(6);
				holder4.imageView7.setImages(quan.getAttachments(),"quans");
				if (quan.getAttachments().size() > 7) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"quans/small/"
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
									HttpUtil.ImageUrl+"quans/small/"
											+ quan.getAttachments().get(8)
													.getUrl(),
													holder4.imageView9, ImageOptions.options);
					holder4.imageView9.setVisibility(View.VISIBLE);
					holder4.imageView9.setIndex(8);
					holder4.imageView9.setImages(quan.getAttachments(),"quans");
				}
				holder4.pinlunImg.setOnClickListener(NysActivity.this);
				holder4.pinlunImg.setTag(quan);
				if(bzan){
					holder4.zanImg.setVisibility(View.GONE);
				}
				else{
					holder4.zanImg.setVisibility(View.VISIBLE);
					holder4.zanImg.setOnClickListener(NysActivity.this);
					holder4.zanImg.setTag(quan);
				}
				break;
			case 4:
				holderNysInfo = (NysInfoHolder) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ user.getThumbnail(), holderNysInfo.headIv,
						ImageOptions.optionsNoPlaceholder);
				holderNysInfo.titleTv.setText(user.getAlias());
				holderNysInfo.jifenTv.setText(user.getDescription());
//				if(user.getLevel().getId()==2){
//					List<UserCrop> userCrops = user.getCrops();
//					String str = "";
//					for (Iterator iterator = userCrops.iterator(); iterator
//							.hasNext();) {
//						UserCrop userCrop = (UserCrop) iterator.next();
//						str+=userCrop.getCrop().getName()+" ";
//					}
//					str = str.trim();
//					holderNysInfo.jifenTv.setText(str);
//				}
//				else{
//					holderNysInfo.jifenTv.setText(user.getDescription());
//				}
				if(bfollow){
					holderNysInfo.followButton.setText("取消关注");
				}
				else{
					holderNysInfo.followButton.setText("关注");
				}
				holderNysInfo.followButton.setOnClickListener(NysActivity.this);
				holderNysInfo.chatButton.setOnClickListener(NysActivity.this);
				
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
		case R.id.follow_btn:
			Button btn = (Button)v;
			if(btn.getText().equals("关注")){
				follow();
				v.setEnabled(false);
			}
			else{
				
				final Dialog dialog = new Dialog(this, R.style.MyDialog);
		        //设置它的ContentView
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        View layout = inflater.inflate(R.layout.dialog, null);
		        dialog.setContentView(layout);
		        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
		        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
		        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
		        okBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						cancelfollow();
						v.setEnabled(false);
					}
				});
		        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
		        cancelButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		        titleView.setText("提示");
		        contentView.setText("确定要取消关注吗？");
		        dialog.show();
				
			}
			break;
		case R.id.chat_btn:
			Intent it = new Intent();
			it.setClass(this, ChatActivity.class);
			it.putExtra("userName", user.getPhone());
			it.putExtra("title", user.getAlias());
			it.putExtra("thumbnail", user.getThumbnail());
			startActivity(it);
			break;
		case R.id.cancel_button:
			finish();
			break;
		case R.id.pinglun_img:
			if(bfollow){
				selectQuan = (Quan)v.getTag();
				displayInput();	
			}
			else{
				GFToast.show("未关注不能评论");
			}
			break;
		case R.id.zan_img:
			if(bfollow){
				Quan quan = (Quan)v.getTag();
				zan(quan);
				v.setEnabled(false);
			}
			else{
				GFToast.show("未关注不能点赞");
			}
			break;
		case R.id.send_pinglun_button:
			pinglun(selectQuan);
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
					HttpUtil.pinlunQuan(mid, qid, comment);
					Message msg = NysActivity.this.handler.obtainMessage();
					msg.what = 4;
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
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		NysActivity nysactivity = (NysActivity) object;
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(msg.obj.toString());
			}
			break;
		case 0:
			if (msg.obj == null) {
				Toast.makeText(this, "获取失败,请稍后再试",
						Toast.LENGTH_SHORT).show();
				return;
			}
			nysactivity.user = (Nys) GsonUtil
					.fromJson(msg.obj.toString(), Nys.class);
			titleView.setText(user.getLevel().getName());
			loadNewDate();
			
			break;
			
		case 1:
			if(clickView!=null){
				clickView.setEnabled(true);
			}
			if(msg.obj != null){
				Gson gson = new Gson();
				Follow follow = gson.fromJson(msg.obj.toString(),
						new TypeToken<Follow>() {
						}.getType());
				if(follow!=null){
					bfollow = true;
					nysactivity.adapter.notifyDataSetChanged();
					Toast.makeText(this, "关注成功!",
							Toast.LENGTH_SHORT).show();
				}
				
			}
			else{
				Toast.makeText(this, "关注农艺师失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if(clickView!=null){
				clickView.setEnabled(true);
			}
			if (msg.obj != null) {
				String jsString = msg.obj.toString();
				if(jsString!=""){
					Toast.makeText(this, "取消关注出错！",
							Toast.LENGTH_SHORT).show();
				}
				else{
					bfollow = false;
					nysactivity.adapter.notifyDataSetChanged();
					
				}
				
			} else {
				Toast.makeText(this, "取消关注出错！",
						Toast.LENGTH_SHORT).show();
			}
			break;
			
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Quan> quans = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Quan>>() {
						}.getType());
				nysactivity.allQuans.clear();
				for (Quan quan : quans) {
					nysactivity.allQuans.add(quan);
				}
				nysactivity.adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			nysactivity.pullToRefreshList.onRefreshComplete();
			break;
		case 4:
			loadNewDate();
			break;
		case 5:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Quan> quans = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Quan>>() {
						}.getType());
				for (Quan quan : quans) {
					nysactivity.allQuans.add(quan);
				}
				nysactivity.adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			nysactivity.pullToRefreshList.onRefreshComplete();
			break;
		default:
			break;
		}
		
	}

	
}

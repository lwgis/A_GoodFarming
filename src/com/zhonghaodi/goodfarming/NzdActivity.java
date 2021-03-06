package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.BannerAdapter;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.customui.Holder_r1;
import com.zhonghaodi.customui.Holder_r2;
import com.zhonghaodi.customui.Holder_r3;
import com.zhonghaodi.customui.WaitingPopupWindow;
import com.zhonghaodi.model.Follow;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Nys;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.Store;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class NzdActivity extends Activity implements HandMessage,OnClickListener {

	private TextView titleView;
	private String id;
	private String name;
	private User store;
	private boolean bfollow;
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private NzdQAdapter adapter;
	private View clickview;
	private int page = 0;
	private GFHandler<NzdActivity> handler = new GFHandler<NzdActivity>(this);
	private WaitingPopupWindow waitingPopupWindow;
	private View headerView;
	private NzdInfoHolder nzdInfoHolder;
	private List<GFImageView> imageViews;
	private List<View> dots;
	private BannerAdapter bAdapter;
	private int currentItem = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nzd);
		titleView = (TextView)findViewById(R.id.title_text);
		bfollow = getIntent().getBooleanExtra("bfollow", false);
		store = (User)getIntent().getSerializableExtra("store");
		id = store.getId();
		name = store.getAlias();
		if(name!=null && name!=""){
			titleView.setText(name);
		}
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		waitingPopupWindow = new WaitingPopupWindow(this);
		headerView = LayoutInflater.from(NzdActivity.this)
				.inflate(R.layout.cell_nzd_info, null);
		nzdInfoHolder = new NzdInfoHolder(headerView);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuestions.size() == 0) {
							return;
						}
						int k=allQuestions.size()%20;
						if(k==0){
							page=allQuestions.size()/20;
						}
						else{
							page=allQuestions.size()/20+1;
						}
						loadMoreData(page);
					}

				});
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Intent it=new Intent();
//				it.setClass(NzdActivity.this, RecipeActivity.class);
//				it.putExtra("recipeId", recipes.get(position-1).getId());
//				it.putExtra("nzdCode", recipes.get(position-1).getNzd().getId());
//				NzdActivity.this.startActivity(it);
				
				if(position<2){
					return;
				}
				Intent it = new Intent(NzdActivity.this,QuestionActivity.class);
				it.putExtra("questionId", allQuestions.get(position - 2).getId());
				NzdActivity.this.startActivity(it);
				
			}
		});
		loadFollowids();
		allQuestions = new ArrayList<Question>();
		adapter = new NzdQAdapter();
		pullToRefreshListView.getRefreshableView().addHeaderView(headerView);
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);	
		dispalyHeader();
		loadUser();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("农资店信息");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("农资店信息");
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 读取农技达人信息
	 */
	public void loadUser() {
		allQuestions.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(id);				
				Message msg = handler.obtainMessage();
				msg.what = 4;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadData(){
		allQuestions.clear();
		page=0;
		waitingPopupWindow.showAtLocation(findViewById(R.id.contentlayout), 
				Gravity.CENTER, 0, 150);
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getMyrepliedquestions(id,page);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
		
	}
	
	/**
	 * 读取更多的
	 * @param qid
	 */
	private void loadMoreData(final int qid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getMyrepliedquestions(id, qid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void loadFollowids(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String myid = GFUserDictionary.getUserId(getApplicationContext());
				String jsString = HttpUtil.getFollowids(myid);
				Message msg1 = handler.obtainMessage();
				msg1.what = 3;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
	}
	
	public void loadAtts(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String jsString = HttpUtil.getNzdAtts(store.getId());
				Message msg1 = handler.obtainMessage();
				msg1.what = 5;
				msg1.obj = jsString;
				msg1.sendToTarget();
				
			}
		}).start();
	}
	
	public void dispalyHeader(){
		if(store.getThumbnail()!=null){
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"users/small/"+store.getThumbnail(), nzdInfoHolder.headIv, ImageOptions.optionsNoPlaceholder);
		}
		nzdInfoHolder.titleTv.setText(store.getAlias());
		nzdInfoHolder.desTv.setText(store.getDescription());
		nzdInfoHolder.chatButton.setOnClickListener(NzdActivity.this);
		if(bfollow){
			nzdInfoHolder.followButton.setText("取消关注");
		}
		else{
			nzdInfoHolder.followButton.setText("关注");
		}
		nzdInfoHolder.followButton.setOnClickListener(NzdActivity.this);
		loadAtts();
	}
	
	public void displayImages(List<NetImage> images){
		if(images==null || images.size()==0){
			nzdInfoHolder.imagesLayout.setVisibility(View.GONE);
		}
		else{
			nzdInfoHolder.imagesLayout.setVisibility(View.VISIBLE);
			imageViews = new ArrayList<GFImageView>();
			dots = new ArrayList<View>();
			for (int i=0;i<images.size();i++) {
				NetImage netImage = images.get(i);
				
				GFImageView imageView = new GFImageView(this);
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"nzd/small/"
								+ netImage.getUrl(),
						imageView, ImageOptions.options);
				imageView.setIndex(i);
				imageView.setImages(images,"nzd");
				imageView.setScaleType(ScaleType.FIT_CENTER);
				imageViews.add(imageView);
				
				View dot = new View(this);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PublicHelper.dip2px(this, 5),PublicHelper.dip2px(this, 5));
				layoutParams.setMargins(PublicHelper.dip2px(this, 2), 0, PublicHelper.dip2px(this, 2), 0);
				dot.setLayoutParams(layoutParams);
				dot.setBackgroundResource(R.drawable.dot_normal);
				dot.setVisibility(View.VISIBLE);
				nzdInfoHolder.dotLayout.addView(dot);
				dots.add(dot);
			}
			
			bAdapter = new BannerAdapter(imageViews);
			nzdInfoHolder.adViewPager.setAdapter(bAdapter);
			nzdInfoHolder.adViewPager.setOnPageChangeListener(new NzdPageChangeListener());
		}
	}
	
	/**
	 * 关注
	 */
	public void follow(){
		final String mid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				NetResponse netResponse = HttpUtil.follow(mid,store);
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
		final String mid = GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.cancelfollow(mid,store);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
	}
	
	class NzdInfoHolder{
		public RoundedImageView headIv;
		public TextView titleTv;
		public TextView desTv;
		public Button chatButton;
		public Button followButton;
		public LinearLayout dotLayout;
		public ViewPager adViewPager;
		public LinearLayout imagesLayout;
		public NzdInfoHolder(View view){
			headIv=(RoundedImageView)view.findViewById(R.id.head_image);
			titleTv=(TextView)view.findViewById(R.id.nzdname);
			desTv=(TextView)view.findViewById(R.id.nzddes);
			chatButton = (Button)view.findViewById(R.id.chat_btn);
			followButton = (Button)view.findViewById(R.id.follow_btn);
			dotLayout = (LinearLayout)view.findViewById(R.id.dot_layout);
			adViewPager = (ViewPager)view.findViewById(R.id.vp);
			imagesLayout = (LinearLayout)view.findViewById(R.id.imagesLayout);
		}
	}
	
	class NzdQAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(store==null){
				return 0;
			}
			return allQuestions.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allQuestions.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			Question question = allQuestions.get(position);
			if (question.getAttachments().size() == 0) {
				return 0;
			}
			if (question.getAttachments().size() > 0
					&& question.getAttachments().size() < 4) {
				return 1;
			}
			if (question.getAttachments().size() > 3
					&& question.getAttachments().size() < 7) {
				return 2;
			}
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			boolean bzan = false;
			Holder_r1 holder1 = null;
			Holder_r2 holder2 = null;
			Holder_r3 holder3 = null;
			int cellType = getItemViewType(position);
			Question question = null;
			Response response = null;
			question = allQuestions.get(position);
			response = question.getMyResponse();
			if(convertView==null){
				switch (cellType) {
				case 0:
					convertView = LayoutInflater.from(NzdActivity.this).inflate(
							R.layout.cell_question_r, parent, false);
					holder1 = new Holder_r1(convertView);
					convertView.setTag(holder1);
					break;
				case 1:
					convertView = LayoutInflater.from(
							NzdActivity.this).inflate(
							R.layout.cell_question_r_3_image, parent, false);
					holder2 = new Holder_r2(convertView);			
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = LayoutInflater.from(
							NzdActivity.this).inflate(
							R.layout.cell_question_r_6_image, parent, false);
					holder3 = new Holder_r3(convertView);
					convertView.setTag(holder3);
					break;

				default:
					break;
				}
				
			}
			
			switch (cellType) {
			case 0:
				holder1 = (Holder_r1) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder1.headIv, ImageOptions.options);
				holder1.nameTv.setText(question.getWriter().getAlias());
				holder1.timeTv.setText(question.getTime());
				holder1.contentTv.setText(question.getContent());
				
				holder1.countTv.setText(store.getLevel().getName()
						+ "给出的答案是：");
				if(question.getCrop()!=null){
					holder1.cropTv.setVisibility(View.VISIBLE);
					holder1.cropTv.setText(question.getCrop().getName());
				}
				else{
					holder1.cropTv.setVisibility(View.GONE);
				}
				holder1.headIv.setTag(question.getWriter());
				if(question.getAddress()!=null){
					holder1.addressTextView.setText(question.getAddress());
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}
					holder1.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder1.levelTextView.setText("专家");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder1.levelTextView.setText("官方");
					holder1.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ store.getThumbnail(),
						holder1.hdIv, ImageOptions.options);
				holder1.nmTv.setText(store.getAlias());
				holder1.tmTv.setText(response.getTime());
				
				holder1.ctTv.setHtmlText(PublicHelper.TrimRight(response.getContent()));
				holder1.countTextView.setText("评论("+response.getCommentCount()+")");
				holder1.agreeTextView.setText("赞同("+response.getAgree()+")");
				switch (store.getLevel().getId()) {
				case 1:
					holder1.lelTextView.setText(store.getIdentifier()+"农友");
					holder1.lelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder1.lelTextView.setText(store.getIdentifier()+"农技达人");
					holder1.lelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(store.isTeamwork()){
						holder1.lelTextView.setText(store.getIdentifier()+"农资店-合作店");
					}
					else{
						holder1.lelTextView.setText(store.getIdentifier()+"农资店");
					}					
					holder1.lelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder1.lelTextView.setText("专家");
					holder1.lelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder1.lelTextView.setText("官方");
					holder1.lelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				if(response.isAdopt()){
					holder1.cainaView.setVisibility(View.VISIBLE);
					
				}
				else{
					holder1.cainaView.setVisibility(View.GONE);
				}
				break;
			case 1:
				holder2 = (Holder_r2) convertView.getTag();
				holder2.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder2.headIv, ImageOptions.options);
				holder2.nameTv.setText(question.getWriter().getAlias());
				holder2.contentTv.setText(question.getContent());
				
				holder2.timeTv.setText(question.getTime());
				if(question.getCrop()!=null){
					holder2.cropTv.setVisibility(View.VISIBLE);
					holder2.cropTv.setText(question.getCrop().getName());
				}
				else{
					holder2.cropTv.setVisibility(View.GONE);
				}
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(0).getUrl(),
						holder2.imageView1, ImageOptions.options);
				holder2.imageView1.setVisibility(View.VISIBLE);
				holder2.imageView1.setIndex(0);
				holder2.imageView1.setImages(question.getAttachments(),"questions");
				if (question.getAttachments().size() > 1) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(1)
													.getUrl(),
									holder2.imageView2, ImageOptions.options);
					holder2.imageView2.setVisibility(View.VISIBLE);
					holder2.imageView2.setIndex(1);
					holder2.imageView2.setImages(question.getAttachments(),"questions");
				}
				if (question.getAttachments().size() > 2) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(2)
													.getUrl(),
									holder2.imageView3, ImageOptions.options);
					holder2.imageView3.setVisibility(View.VISIBLE);
					holder2.imageView3.setIndex(2);
					holder2.imageView3.setImages(question.getAttachments(),"questions");
				}
				holder2.countTv.setText(store.getLevel().getName()
						+ "给出的答案是：");
				holder2.headIv.setTag(question.getWriter());
				if(question.getAddress()!=null){
					holder2.addressTextView.setText(question.getAddress());
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holder2.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holder2.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}
					holder2.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder2.levelTextView.setText("专家");
					holder2.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder2.levelTextView.setText("官方");
					holder2.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ store.getThumbnail(),
						holder2.hdIv, ImageOptions.options);
				holder2.nmTv.setText(store.getAlias());
				holder2.tmTv.setText(response.getTime());
				holder2.ctTv.setHtmlText(PublicHelper.TrimRight(response.getContent()));	
				holder2.countTextView.setText("评论("+response.getCommentCount()+")");
				holder2.agreeTextView.setText("赞同("+response.getAgree()+")");
				switch (store.getLevel().getId()) {
				case 1:
					holder2.lelTextView.setText(store.getIdentifier()+"农友");
					holder2.lelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder2.lelTextView.setText(store.getIdentifier()+"农技达人");
					holder2.lelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(store.isTeamwork()){
						holder2.lelTextView.setText(store.getIdentifier()+"农资店-合作店");
					}
					else{
						holder2.lelTextView.setText(store.getIdentifier()+"农资店");
					}	
					holder2.lelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder2.lelTextView.setText("专家");
					holder2.lelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder2.lelTextView.setText("官方");
					holder2.lelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				if(response.isAdopt()){
					holder2.cainaView.setVisibility(View.VISIBLE);
					
				}
				else{
					holder2.cainaView.setVisibility(View.GONE);
				}
				break;
			case 2:
				holder3 = (Holder_r3) convertView.getTag();
				holder3.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder3.headIv, ImageOptions.options);
				holder3.nameTv.setText(question.getWriter().getAlias());
				holder3.timeTv.setText(question.getTime());
				holder3.contentTv.setText(question.getContent());
				if(question.getCrop()!=null){
					holder3.cropTv.setVisibility(View.VISIBLE);
					holder3.cropTv.setText(question.getCrop().getName());
				}
				else{
					holder3.cropTv.setVisibility(View.GONE);
				}
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
						holder3.headIv, ImageOptions.options);
				holder3.nameTv.setText(question.getWriter().getAlias());
				holder3.timeTv.setText(question.getTime());
				holder3.contentTv.setText(question.getContent());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(0).getUrl(),
						holder3.imageView1, ImageOptions.options);
				holder3.imageView1.setVisibility(View.VISIBLE);
				holder3.imageView1.setIndex(0);
				holder3.imageView1.setImages(question.getAttachments(),"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(1).getUrl(),
						holder3.imageView2, ImageOptions.options);
				holder3.imageView2.setVisibility(View.VISIBLE);
				holder3.imageView2.setIndex(1);
				holder3.imageView2.setImages(question.getAttachments(),"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(2).getUrl(),
						holder3.imageView3, ImageOptions.options);
				holder3.imageView3.setVisibility(View.VISIBLE);
				holder3.imageView3.setIndex(2);
				holder3.imageView3.setImages(question.getAttachments(),"questions");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"questions/small/"
								+ question.getAttachments().get(3).getUrl(),
						holder3.imageView4, ImageOptions.options);
				holder3.imageView4.setVisibility(View.VISIBLE);
				holder3.imageView4.setIndex(3);
				holder3.imageView4.setImages(question.getAttachments(),"questions");
				if (question.getAttachments().size() > 4) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(4)
													.getUrl(),
									holder3.imageView5, ImageOptions.options);
					holder3.imageView5.setVisibility(View.VISIBLE);
					holder3.imageView5.setIndex(4);
					holder3.imageView5.setImages(question.getAttachments(),"questions");
				}
				if (question.getAttachments().size() > 5) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"questions/small/"
											+ question.getAttachments().get(5)
													.getUrl(),
									holder3.imageView6, ImageOptions.options);
					holder3.imageView6.setVisibility(View.VISIBLE);
					holder3.imageView6.setIndex(5);
					holder3.imageView6.setImages(question.getAttachments(),"questions");
				}
				holder3.countTv.setText(store.getLevel().getName()
						+ "给出的答案是：");
				holder3.headIv.setTag(question.getWriter());
				if(question.getAddress()!=null){
					holder3.addressTextView.setText(question.getAddress());
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holder3.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holder3.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}
					holder3.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder3.levelTextView.setText("专家");
					holder3.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder3.levelTextView.setText("官方");
					holder3.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ store.getThumbnail(),
						holder3.hdIv, ImageOptions.options);
				holder3.nmTv.setText(store.getAlias());
				holder3.tmTv.setText(response.getTime());
				holder3.ctTv.setHtmlText(PublicHelper.TrimRight(response.getContent()));
				holder3.countTextView.setText("评论("+response.getCommentCount()+")");
				holder3.agreeTextView.setText("赞同("+response.getAgree()+")");
				switch (store.getLevel().getId()) {
				case 1:
					holder3.lelTextView.setText(store.getIdentifier()+"农友");
					holder3.lelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holder3.lelTextView.setText(store.getIdentifier()+"农技达人");
					holder3.lelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(store.isTeamwork()){
						holder3.lelTextView.setText(store.getIdentifier()+"农资店-合作店");
					}
					else{
						holder3.lelTextView.setText(store.getIdentifier()+"农资店");
					}					
					holder3.lelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holder3.lelTextView.setText("专家");
					holder3.lelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holder3.lelTextView.setText("官方");
					holder3.lelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				if(response.isAdopt()){
					holder3.cainaView.setVisibility(View.VISIBLE);
					
				}
				else{
					holder3.cainaView.setVisibility(View.GONE);
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
		case R.id.chat_btn:
			Intent it = new Intent();
			it.setClass(this, ChatActivity.class);
			it.putExtra("userName", store.getPhone());
			it.putExtra("title", store.getAlias());
			it.putExtra("thumbnail", store.getThumbnail());
			startActivity(it);
			break;
		case R.id.follow_btn:
			Button btn = (Button)v;
			clickview = v;
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
		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 0:
			if(waitingPopupWindow.isShowing()){
				waitingPopupWindow.dismiss();
			}
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Question> questions = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Question>>() {
						}.getType());
				for (Question question : questions) {
					allQuestions.add(question);
				}
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			pullToRefreshListView.onRefreshComplete();
			break;
		case 1:
			if(clickview!=null){
				clickview.setEnabled(true);
			}
			if(msg.obj != null){
				Gson gson = new Gson();
				Follow follow = gson.fromJson(msg.obj.toString(),
						new TypeToken<Follow>() {
						}.getType());
				if(follow!=null){
					bfollow = true;
					adapter.notifyDataSetChanged();
					Toast.makeText(this, "关注成功!",
							Toast.LENGTH_SHORT).show();
				}
				
			}
			else{
				Toast.makeText(this, "关注农技达人失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if(clickview!=null){
				clickview.setEnabled(true);
			}
			if (msg.obj != null) {
				String jsString = msg.obj.toString();
				if(jsString!=""){
					Toast.makeText(this, "取消关注出错！",
							Toast.LENGTH_SHORT).show();
				}
				else{
					bfollow = false;
					adapter.notifyDataSetChanged();
					
				}
				
			} else {
				Toast.makeText(this, "取消关注出错！",
						Toast.LENGTH_SHORT).show();
			}
			break; 
			
		case 3:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<String> fids = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<String>>() {
						}.getType());
				if(fids!=null&&fids.size()>0){
					if(fids.contains(id)){
						bfollow = true;
					}
					else{
						bfollow = false;
					}
				}
				else{
					bfollow = false;
				}
				adapter.notifyDataSetChanged();
				
			} else {
				Toast.makeText(this, "获取关注列表失败!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 4:
			if (msg.obj == null) {
				Toast.makeText(this, "获取失败,请稍后再试",
						Toast.LENGTH_SHORT).show();
				return;
			}
			store = (Store) GsonUtil
					.fromJson(msg.obj.toString(), Store.class);
			adapter.notifyDataSetChanged();
			loadData();
			
			break;
		case 5:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<NetImage> images = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<NetImage>>() {
						}.getType());
				displayImages(images);
			} else {
				displayImages(null);
			}
			break;
		default:
			break;
		}
	}

	private class NzdPageChangeListener implements OnPageChangeListener {

		private int oldPosition = 0;

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}
	}
	
	
}

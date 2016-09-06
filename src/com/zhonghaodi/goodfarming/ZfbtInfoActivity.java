package com.zhonghaodi.goodfarming;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.BannerAdapter;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyProgressBar;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.ScrollListView;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.SecondOrder;
import com.zhonghaodi.model.Stock;
import com.zhonghaodi.model.User;
import com.zhonghaodi.model.Zfbt;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.utils.UmengConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class ZfbtInfoActivity extends Activity implements HandMessage,OnClickListener {

	private TextView titleView;
	private TextView nzdTextView;
	private GFImageView headImage;
	private View bannerView;
	private ScrollListView stockList;
	private LinearLayout dotLayout;
	private ViewPager adViewPager;
	private List<GFImageView> imageViews;
	private List<View> dots;
	private BannerAdapter adapter;
	private TextView oldTextView;
	private TextView newTextView;
	private TextView countTextView;
	private TextView acountTextView;
	private TextView contentTextView;
	private TextView couponTextView;
	public LinearLayout evaluateLayout;
	public TextView evaluaTextView;
	public MyProgressBar haoProgressBar;
	public MyProgressBar zhongProgressBar;
	public MyProgressBar chaProgressBar;
	public TextView evaluateBtn;
	private Zfbt zfbt;
	private MyTextButton buyBtn;
	private MyTextButton timeBtn;
	private GFHandler<ZfbtInfoActivity> handler = new GFHandler<ZfbtInfoActivity>(this);
	private TimeCount time;
	private ScheduledExecutorService scheduledExecutorService;
	private int currentItem = 0;
	private StockAdapter sAdapter;
	private Stock selectStock;
	private double x,y;
	private int zid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zfbtinfo);
		bannerView = LayoutInflater.from(ZfbtInfoActivity.this).inflate(R.layout.header_second, null);
		titleView = (TextView)bannerView.findViewById(R.id.title_text);
		nzdTextView=(TextView)bannerView.findViewById(R.id.nzd_text);
		headImage = (GFImageView)bannerView.findViewById(R.id.head_image);
		dotLayout = (LinearLayout)bannerView.findViewById(R.id.dot_layout);
		adViewPager = (ViewPager)bannerView.findViewById(R.id.vp);
		oldTextView = (TextView)bannerView.findViewById(R.id.oldprice_text);
		oldTextView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
		newTextView = (TextView)bannerView.findViewById(R.id.newprice_text);
		countTextView = (TextView)bannerView.findViewById(R.id.count_text);
		acountTextView = (TextView)bannerView.findViewById(R.id.acount_text);
		couponTextView = (TextView)bannerView.findViewById(R.id.coupon_text);
		contentTextView = (TextView)bannerView.findViewById(R.id.content_text);
		evaluateLayout = (LinearLayout)bannerView.findViewById(R.id.evaluateLayout);
		evaluaTextView = (TextView)bannerView.findViewById(R.id.sumevaluate);
		haoProgressBar = (MyProgressBar)bannerView.findViewById(R.id.haopro);
		zhongProgressBar = (MyProgressBar)bannerView.findViewById(R.id.zhongpro);
		chaProgressBar = (MyProgressBar)bannerView.findViewById(R.id.chapro);
		evaluateBtn = (TextView)bannerView.findViewById(R.id.evaluateBtn);
		evaluateBtn.setOnClickListener(this);
		stockList = (ScrollListView)findViewById(R.id.stocks_list);
		buyBtn = (MyTextButton)findViewById(R.id.buy_button);
		buyBtn.setOnClickListener(this);
		timeBtn = (MyTextButton)findViewById(R.id.time_button);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);

		zid = getIntent().getIntExtra("zid", 0);
		x = getIntent().getDoubleExtra("x", 0);
		y = getIntent().getDoubleExtra("y", 0);
		loadInfo();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("政府补贴产品页");
		MobclickAgent.onResume(this);
		
//		startAd();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("政府补贴产品页");
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(scheduledExecutorService!=null){
			scheduledExecutorService.shutdown();
		}
		
	}

	private void startAd() {
		if(zfbt.getAttachments()!=null && zfbt.getAttachments().size()>1){
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			// 当Activity显示出来后，每两秒切换一次图片显示
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
					TimeUnit.SECONDS);
		}		
	}
	
	private void loadInfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getZfbtById(zid,x,y);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void loadTime(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getServerTime();
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
	}
	
	private void displaydata(){
		if(zfbt!=null){
			titleView.setText(zfbt.getTitle());
			nzdTextView.setText(zfbt.getNzd().getAlias());
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+zfbt.getImage(), headImage, ImageOptions.optionsNoPlaceholder);
			oldTextView.setText("原价：￥"+String.valueOf(zfbt.getOprice()));
			newTextView.setText("现价：￥"+String.valueOf(zfbt.getNprice()));
			countTextView.setText("库存："+String.valueOf(zfbt.getCount()));
			acountTextView.setText("销售量："+String.valueOf(zfbt.getAcount()));
			contentTextView.setText(zfbt.getContent());
			couponTextView.setText("可使用优惠币："+zfbt.getCoupon());
			if(zfbt.getCoupon()>0){
				couponTextView.setVisibility(View.VISIBLE);
			}
			else{
				couponTextView.setVisibility(View.GONE);
			}
		}
		if(zfbt.getAttachments()==null || zfbt.getAttachments().size()<=1){
			headImage.setVisibility(View.VISIBLE);
			NetImage netImage = new NetImage();
			netImage.setId(1);
			netImage.setUrl(zfbt.getImage());
			List<NetImage> images = new ArrayList<NetImage>();
			images.add(netImage);
			headImage.setIndex(0);
			headImage.setImages(images,"seconds");
		}
		else{
			headImage.setVisibility(View.GONE);
			imageViews = new ArrayList<GFImageView>();
			dots = new ArrayList<View>();
			for (int i=0;i<zfbt.getAttachments().size();i++) {
				NetImage netImage = zfbt.getAttachments().get(i);
				
				GFImageView imageView = new GFImageView(this);
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"seconds/small/"
								+ netImage.getUrl(),
						imageView, ImageOptions.options);
				imageView.setIndex(i);
				imageView.setImages(zfbt.getAttachments(),"seconds");
				imageView.setScaleType(ScaleType.FIT_CENTER);
				imageViews.add(imageView);
				
				View dot = new View(this);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(PublicHelper.dip2px(this, 5),PublicHelper.dip2px(this, 5));
				layoutParams.setMargins(PublicHelper.dip2px(this, 2), 0, PublicHelper.dip2px(this, 2), 0);
				dot.setLayoutParams(layoutParams);
				dot.setBackgroundResource(R.drawable.dot_normal);
				dot.setVisibility(View.VISIBLE);
				dotLayout.addView(dot);
				dots.add(dot);
			}
			
			evaluaTextView.setText("评价："+zfbt.getSumcount());
			
			if(zfbt.getSumcount()>0){
				haoProgressBar.setProgress(zfbt.getHaocount()*100/zfbt.getSumcount());
				zhongProgressBar.setProgress(zfbt.getZhongcount()*100/zfbt.getSumcount());
				chaProgressBar.setProgress(zfbt.getChacount()*100/zfbt.getSumcount());
				evaluateLayout.setVisibility(View.VISIBLE);
			}
			else{
				haoProgressBar.setProgress(0);
				zhongProgressBar.setProgress(0);
				chaProgressBar.setProgress(0);
				evaluateLayout.setVisibility(View.GONE);
			}
			
			adapter = new BannerAdapter(imageViews);
			adViewPager.setAdapter(adapter);
			adViewPager.setOnPageChangeListener(new MyPageChangeListener());			
		}
		stockList.addHeaderView(bannerView);
		sAdapter = new StockAdapter();
		stockList.setAdapter(sAdapter);	
		stockList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				selectStock = zfbt.getStocks().get(position-1);
				sAdapter.notifyDataSetChanged();
			}
		});
		if(zfbt.getCount()<1){
			buyBtn.setVisibility(View.GONE);
			timeBtn.setText("已售完");
			timeBtn.setVisibility(View.VISIBLE);
		}
		else{
			loadTime();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.buy_button:
			if(zfbt.getStocks()!=null && zfbt.getStocks().size()>0 && selectStock==null){
				GFToast.show(this, "请选择发货的农资店！");
				stockList.smoothScrollToPositionFromTop(1, 30, 300);
			}
			else{
				buy();
			}
			break;
		case R.id.evaluateBtn:
			Intent intent = new Intent(this, ZfbtEvaluatesActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("zfbt", zfbt);
			intent.putExtras(bundle);
			startActivity(intent);
			
			break;
		default:
			break;
		}
	}
	
	public void buy(){
		final String uid = GFUserDictionary.getUserId(getApplicationContext());
		if(uid==null){
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return;
		}
		Intent intent = new Intent(this,ZfbtBuyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("product", zfbt);
		bundle.putSerializable("stock", selectStock);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void sendBuy(final String uid){
		new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					NetResponse netResponse;
					netResponse = HttpUtil.buySecond(uid,zfbt.getId());
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 0;
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
	
	private void parseTime(String str){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		try {
			Date date = dateFormat.parse(str);
			Date startDate = dateFormat1.parse(zfbt.getStarttime());
			if(date.after(startDate)){
				timeBtn.setVisibility(View.GONE);
				buyBtn.setVisibility(View.VISIBLE);
			}
			else{
				timeBtn.setVisibility(View.VISIBLE);
				buyBtn.setVisibility(View.GONE);
				long l=startDate.getTime()-date.getTime();
				time = new TimeCount(l, 1000);
				time.start();
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			buyBtn.setVisibility(View.GONE);
			long day=millisUntilFinished/(24*60*60*1000);
			long hour=(millisUntilFinished/(60*60*1000)-day*24);
			long min=((millisUntilFinished/(60*1000))-day*24*60-hour*60);
			long s=(millisUntilFinished/1000-day*24*60*60-hour*60*60-min*60);
			timeBtn.setText(day+"天"+hour+"小时"+min+"分"+s+"秒后开始");
		}

		@Override
		public void onFinish() {
			buyBtn.setVisibility(View.VISIBLE);
			timeBtn.setVisibility(View.GONE);
		}
	}
	
	private class ScrollTask implements Runnable {

		@Override
		public void run() {
			synchronized (adViewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				Message message = handler.obtainMessage();
				message.what=2;
				message.sendToTarget();
			}
		}
	}
	
	private class MyPageChangeListener implements OnPageChangeListener {

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
	
	class StockHolder{
		public RoundedImageView imageView;
		public TextView nameTextView;
		public TextView disTextView;
		public ImageView locationView;
		public RatingBar ratingBar;
		public TextView teamTextView;
		public TextView couponTextView;
		public RadioButton rdselect;
		public StockHolder(View view){
			imageView = (RoundedImageView)view.findViewById(R.id.head_image);
			nameTextView = (TextView)view.findViewById(R.id.name_text);
			teamTextView = (TextView)view.findViewById(R.id.team_text);
			disTextView = (TextView)view.findViewById(R.id.dis_text);
			locationView = (ImageView)view.findViewById(R.id.map_image);
			ratingBar = (RatingBar)view.findViewById(R.id.rb);
			couponTextView = (TextView)view.findViewById(R.id.coupon_text);
			rdselect = (RadioButton)view.findViewById(R.id.rd_select);
		}
	}
	
	class StockAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return zfbt.getStocks().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return zfbt.getStocks().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Stock stock = zfbt.getStocks().get(position);
			StockHolder holder;
			if(convertView==null){
				convertView = LayoutInflater.from(ZfbtInfoActivity.this)
						.inflate(R.layout.cell_stock, parent,false);
				holder = new StockHolder(convertView);
				convertView.setTag(holder);
			}
			
			holder = (StockHolder)convertView.getTag();
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ stock.getUser().getThumbnail(),
							holder.imageView, ImageOptions.options);
			holder.nameTextView.setText(stock.getUser().getAlias());
			double distance = PublicHelper.GetShortDistance(stock.getUser().getX(), stock.getUser().getY(), x, y)/1000;
			DecimalFormat    df   = new DecimalFormat("######0.00");   
			holder.disTextView.setText("距离："+df.format(distance)+"公里");
			float sc = (float)(stock.getUser().getScoring()/100);
			holder.ratingBar.setRating(sc);
			if(stock.getUser().isTeamwork()){
				holder.teamTextView.setVisibility(View.VISIBLE);
			}
			else{
				holder.teamTextView.setVisibility(View.GONE);
			}
			if(stock.getUser().isAcceptCoupon()){
				
				holder.couponTextView.setText("支持使用优惠币");
			}
			else{
				holder.couponTextView.setText("不支持使用优惠币");
			}
			if(selectStock==null || selectStock.getId()!=stock.getId()){
				holder.rdselect.setChecked(false);
			}
			else{
				holder.rdselect.setChecked(true);
			}
			return convertView;
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
			buyBtn.setEnabled(true);
			if(msg.obj==null){
				GFToast.show(getApplicationContext(),"对不起，秒杀产品只能抢购一份");
			}
			else{
				SecondOrder secondOrder = (SecondOrder) GsonUtil.fromJson(
						msg.obj.toString(), SecondOrder.class);
				if (secondOrder!=null) {
					Intent intent = new Intent(this, SecondCodeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("order", secondOrder);
					intent.putExtras(bundle);
					MobclickAgent.onEvent(this, UmengConstants.BUY_SECOND_ID);
					startActivity(intent);
					this.finish();
				}
				else{
					GFToast.show(getApplicationContext(),"很遗憾，手慢了没抢到。");
				}
			}
			break;
			
		case 1:
			String timeString = msg.obj.toString();
			if(timeString==null || timeString.isEmpty()){
				GFToast.show(getApplicationContext(),"获取系统时间错误");
				timeBtn.setVisibility(View.VISIBLE);
				buyBtn.setVisibility(View.GONE);
			}
			else{
				parseTime(timeString);
			}
			break;
		case 2:
			adViewPager.setCurrentItem(currentItem);
			break;
		case 3:
			if (msg.obj == null) {
				GFToast.show(getApplicationContext(),"获取产品信息失败");
				return;
			}
			zfbt = (Zfbt) GsonUtil
					.fromJson(msg.obj.toString(), Zfbt.class);
			displaydata();
			break;

		default:
			break;
		}
			
	}
}

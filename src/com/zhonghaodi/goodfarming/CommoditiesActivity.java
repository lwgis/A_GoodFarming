package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Commodity;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommoditiesActivity extends Activity implements OnClickListener,HandMessage {

	private PullToRefreshGridView pullToRefreshGridView;
	private List<Commodity> commodities;
	private GFHandler<CommoditiesActivity> handler = new GFHandler<CommoditiesActivity>(this);
	private CommodityAdapter adapter;
	private Commodity selCommodity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commodities);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshGridView = (PullToRefreshGridView)findViewById(R.id.pull_refresh_grid);
		pullToRefreshGridView.setMode(Mode.BOTH);
		pullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						loaddata();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						int count = commodities.size();
						loadMoredata(count);
					}

				});
		commodities = new ArrayList<Commodity>();
		adapter = new CommodityAdapter();
		pullToRefreshGridView.getRefreshableView().setAdapter(adapter);
		loaddata();
	}
	
	/**
	 * 获取积分商品
	 * @param position
	 */
	public void loaddata(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getCommodities(0);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	/**
	 * 获取积分商品
	 * @param position
	 */
	public void loadMoredata(final int position){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getCommodities(position);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	public void loadUserData() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId());
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	public void exchange(final Commodity commodity){
		
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
				Intent intent = new Intent(CommoditiesActivity.this, ConfirmOrderActivity.class);
				intent.putExtra("commodity", commodity);
				CommoditiesActivity.this.startActivity(intent);
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
        contentView.setText("您确定要用"+commodity.getPoint()+"积分去兑换"+commodity.getName()+"吗？");
        dialog.show();
		
	}
	
	class CommodityHolder{
		public ImageView imageView;
		public TextView nameTextView;
		public TextView pointTextView;
		public Button exButton;
		public CommodityHolder(View view){
			imageView = (ImageView)view.findViewById(R.id.cImg);
			nameTextView = (TextView)view.findViewById(R.id.cName);
			pointTextView = (TextView)view.findViewById(R.id.cPoint);
			exButton = (Button)view.findViewById(R.id.exchange_btn);
		}
	}
	
	class CommodityAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commodities.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return commodities.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			CommodityHolder commdityHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(CommoditiesActivity.this)
						.inflate(R.layout.cell_commodity, parent, false);
				commdityHolder = new CommodityHolder(convertView);
				convertView.setTag(commdityHolder);
			}
			commdityHolder = (CommodityHolder)convertView.getTag();
			Commodity commodity = commodities.get(position);
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"commodities/small/"
							+ commodity.getImage(),
							commdityHolder.imageView, ImageOptions.options);
			commdityHolder.nameTextView.setText(commodity.getName());
			commdityHolder.pointTextView.setText("积分："+String.valueOf(commodity.getPoint()));
			
			commdityHolder.exButton.setTag(commodity);
			commdityHolder.exButton.setOnClickListener(CommoditiesActivity.this);
			
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.exchange_btn:
			selCommodity = (Commodity)v.getTag();
			loadUserData();
			break;

		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Commodity> comms = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Commodity>>() {
						}.getType());
				commodities.clear();
				for (Commodity commodity : comms) {
					commodities.add(commodity);
				}
				adapter.notifyDataSetChanged();
				
			} else {
				GFToast.show("连接服务器失败,请稍候再试!");
			}
			pullToRefreshGridView.onRefreshComplete();
			break;
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Commodity> comms = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Commodity>>() {
						}.getType());
				for (Commodity commodity : comms) {
					commodities.add(commodity);
				}
				adapter.notifyDataSetChanged();
			} else {
				GFToast.show("连接服务器失败,请稍候再试!");
			}
			pullToRefreshGridView.onRefreshComplete();
			break;
		case 2:
			if (msg.obj == null) {
				GFToast.show("获取用户信息失败");
				return;
			}
			User user = (User) GsonUtil
					.fromJson(msg.obj.toString(), User.class);
			if(user.getPoint()<selCommodity.getPoint()){
				GFToast.show("您的积分不够，继续努力哟。");
			}
			else{
				exchange(selCommodity);
			}
			break;

		default:
			break;
		}
		
	}
		
}

package com.zhonghaodi.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Zfbt;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SaveAdapter extends BaseAdapter {
	
	private List<Zfbt> zfbts;
	
	private Context context;
	
	public SaveAdapter(List<Zfbt> zts,Context c) {
		// TODO Auto-generated constructor stub
		zfbts = zts;
		context = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return zfbts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return zfbts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub 
		HolderZfbt holderzfbt;;
		if(convertView==null){
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.cell_zfbt, parent, false);
			holderzfbt = new HolderZfbt(convertView);
			convertView.setTag(holderzfbt);
		}
		
		holderzfbt=(HolderZfbt)convertView.getTag();
		Zfbt second = zfbts.get(position);
		if (second.getImage()!=null) {
			ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+second.getImage(), holderzfbt.secondIv, ImageOptions.optionsNoPlaceholder);
		}
		if(!second.isUseCoupon()){
			holderzfbt.couponTv.setVisibility(View.GONE);
		}
		else{
			holderzfbt.couponTv.setVisibility(View.VISIBLE);
			holderzfbt.couponTv.setText("可使用优惠币："+second.getCoupon()+"--"+second.getCouponMax());
		}
		if(second.getCategory()==null || second.getCategory().getName()==null){
			holderzfbt.cateTv.setVisibility(View.GONE);
		}
		else{
			holderzfbt.cateTv.setVisibility(View.VISIBLE);
			holderzfbt.cateTv.setText(second.getCategory().getName());
		}
		
		holderzfbt.titleTv.setText(second.getTitle());
		holderzfbt.nzdTv.setText(second.getNzd().getAlias());
		holderzfbt.oldPriceTv.setText("￥"+String.valueOf(second.getOprice()));
		holderzfbt.newPriceTv.setText("￥"+String.valueOf(second.getNprice()));
		holderzfbt.countTv.setText("库存："+String.valueOf(second.getCount()));
		holderzfbt.acountTv.setText("销售量："+String.valueOf(second.getAcount()));
		holderzfbt.timeTv.setText("时间："+second.getStarttime());
		return convertView;
	}
	
	class HolderZfbt {
		public ImageView secondIv;
		public TextView titleTv;
		public TextView cateTv;
		public TextView oldPriceTv;
		public TextView newPriceTv;
		public TextView countTv;
		public TextView acountTv;
		public TextView timeTv;
		public TextView nzdTv;
		public TextView couponTv;
		 public HolderZfbt(View view){
			 secondIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 oldPriceTv=(TextView)view.findViewById(R.id.oldprice_text);
			 newPriceTv=(TextView)view.findViewById(R.id.newprice_text);
			 oldPriceTv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
			 countTv = (TextView)view.findViewById(R.id.count_text);
			 acountTv = (TextView)view.findViewById(R.id.acount_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
			 nzdTv=(TextView)view.findViewById(R.id.nzd_text);
			 couponTv = (TextView)view.findViewById(R.id.coupon_text);
			 cateTv = (TextView)view.findViewById(R.id.cate_text);
		 }
	}
}

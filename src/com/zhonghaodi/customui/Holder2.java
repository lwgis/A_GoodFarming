package com.zhonghaodi.customui;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.lbsapi.auth.i;
import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

public class Holder2 {
	public	TextView nameTv;
	public TextView timeTv;
	public	TextView contentTv;
	public	TextView countTv;
	public  TextView cropTv;
	public	RoundedImageView headIv;
	public	GFImageView imageView1;
	public	GFImageView imageView2;
	public	GFImageView imageView3;
	public TextView addressTextView;
	public TextView levelTextView;
	public TextView forwardTextView;
	public LinearLayout forwardLayout;
	public RelativeLayout jpLayout;
	public View hotsplit;
	public TextView hotText;
	public Holder2(View view) {
		nameTv = (TextView) view.findViewById(R.id.name_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
		contentTv = (TextView) view.findViewById(R.id.content_text);
		countTv = (TextView) view.findViewById(R.id.count_text);
		headIv = (RoundedImageView) view.findViewById(R.id.head_image);
		imageView1 = (GFImageView) view.findViewById(R.id.image1);
		imageView2 = (GFImageView) view.findViewById(R.id.image2);
		imageView3 = (GFImageView) view.findViewById(R.id.image3);
		cropTv = (TextView)view.findViewById(R.id.crop_text);
		addressTextView = (TextView)view.findViewById(R.id.address_text);
		levelTextView = (TextView)view.findViewById(R.id.level_text);
		forwardTextView = (TextView)view.findViewById(R.id.forwardcount_tv);
		forwardLayout = (LinearLayout)view.findViewById(R.id.forward_layout);
		jpLayout = (RelativeLayout)view.findViewById(R.id.cainalayout);
		hotsplit = view.findViewById(R.id.hot_view);
		hotText = (TextView)view.findViewById(R.id.hot_text);
		reSetImageViews();
	}

	public void reSetImageViews() {
		imageView1.setVisibility(View.INVISIBLE);
		imageView2.setVisibility(View.INVISIBLE);
		imageView3.setVisibility(View.INVISIBLE);
	}
	public void hideHot(){
		hotsplit.setVisibility(View.GONE);
		hotText.setVisibility(View.GONE);
	}
	public void displayHot(){
		hotsplit.setVisibility(View.VISIBLE);
		hotText.setVisibility(View.VISIBLE);
	}
}


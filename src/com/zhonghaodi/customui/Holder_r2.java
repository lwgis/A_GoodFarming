package com.zhonghaodi.customui;

import android.view.View;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

public class Holder_r2 {
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
	public TextView countTextView;
	public TextView agreeTextView;
	

	
	public	TextView nmTv;
	public	TextView tmTv;
	public	UrlTextView ctTv;
	public	RoundedImageView hdIv;
	public TextView lelTextView;
	public Holder_r2(View view) {
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

		nmTv = (TextView) view.findViewById(R.id.nm_text);
		tmTv = (TextView) view.findViewById(R.id.tm_text);
		ctTv = (UrlTextView) view.findViewById(R.id.ct_text);
		hdIv = (RoundedImageView) view.findViewById(R.id.hd_image);
		lelTextView = (TextView)view.findViewById(R.id.lel_text);
		countTextView = (TextView)view.findViewById(R.id.count_tv);
		agreeTextView = (TextView)view.findViewById(R.id.zancount_tv);
		
		reSetImageViews();
	}

	public void reSetImageViews() {
		imageView1.setVisibility(View.INVISIBLE);
		imageView2.setVisibility(View.INVISIBLE);
		imageView3.setVisibility(View.INVISIBLE);
	}
}

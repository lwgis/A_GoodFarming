package com.zhonghaodi.customui;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

public class Holder_r1 {
	public	TextView nameTv;
	public	TextView timeTv;
	public	TextView contentTv;
	public	TextView countTv;
	public  TextView cropTv;
	public	RoundedImageView headIv;
	public TextView addressTextView;
	public TextView levelTextView;
	
	
	public	TextView nmTv;
	public	TextView tmTv;
	public	UrlTextView ctTv;
	public	RoundedImageView hdIv;
	public TextView lelTextView;
	public TextView countTextView;
	public TextView agreeTextView;

		public Holder_r1(View view) {
			nameTv = (TextView) view.findViewById(R.id.name_text);
			timeTv = (TextView) view.findViewById(R.id.time_text);
			contentTv = (TextView) view.findViewById(R.id.content_text);
			countTv = (TextView) view.findViewById(R.id.count_text);
			headIv = (RoundedImageView) view.findViewById(R.id.head_image);
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
		}
}

package com.zhonghaodi.customui;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HolderPlant1 {
	public TextView nameTv;
	public TextView timeTv;
	public TextView cropTv;
	public TextView cateTv;
	public UrlTextView contentTv;
	public RoundedImageView headIv;
	public TextView agreeTextView;
	public LinearLayout agreeLayout;
	public TextView countTv;
	public LinearLayout countLayout;
	public TextView levelTextView;
	
	public HolderPlant1(View view){
		nameTv = (TextView) view.findViewById(R.id.name_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
		cropTv = (TextView) view.findViewById(R.id.crop_text);
		cateTv = (TextView) view.findViewById(R.id.cate_text);
		contentTv = (UrlTextView) view.findViewById(R.id.content_text);
		headIv = (RoundedImageView) view.findViewById(R.id.head_image);
		agreeTextView = (TextView)view.findViewById(R.id.zancount_tv);
		agreeLayout = (LinearLayout)view.findViewById(R.id.plantzan_layout);
		countTv = (TextView)view.findViewById(R.id.count_tv);
		countLayout = (LinearLayout)view.findViewById(R.id.count_layout);
		levelTextView = (TextView)view.findViewById(R.id.level_text);
	}
}

package com.zhonghaodi.customui;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.TextView;

public class RecommendedHolder {
	public	TextView nameTv;
	public	TextView timeTv;
	public	RoundedImageView headIv;
	
	public RecommendedHolder(View view) {
		// TODO Auto-generated constructor stub
		nameTv = (TextView)view.findViewById(R.id.name_text);
		timeTv = (TextView)view.findViewById(R.id.time_text);
		headIv = (RoundedImageView) view.findViewById(R.id.head_image);
	}
}

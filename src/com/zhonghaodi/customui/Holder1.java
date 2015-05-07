package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class Holder1 {
public	TextView nameTv;
public	TextView timeTv;
public	TextView contentTv;
public	TextView countTv;
public	ImageView headIv;

	public Holder1(View view) {
		nameTv = (TextView) view.findViewById(R.id.name_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
		contentTv = (TextView) view.findViewById(R.id.content_text);
		countTv = (TextView) view.findViewById(R.id.count_text);
		headIv = (ImageView) view.findViewById(R.id.head_image);
	}
}

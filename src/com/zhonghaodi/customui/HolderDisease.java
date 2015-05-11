package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.TextView;

public class HolderDisease {
	public TextView countTv;
	public TextView contentTv;
	public GFImageView headIv;
	public HolderDisease(View view){
		countTv=(TextView)view.findViewById(R.id.count_text);
		contentTv=(TextView)view.findViewById(R.id.content_text);
		headIv=(GFImageView)view.findViewById(R.id.head_image);
	}
}

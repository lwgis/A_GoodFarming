package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HolderEvaluate {

	public ImageView headIv;
	public TextView nameTv;
	public TextView contentTv;
	public TextView timeTv;
	
	public HolderEvaluate(View view){
		
		headIv = (ImageView) view.findViewById(R.id.head_img);
		nameTv = (TextView) view.findViewById(R.id.name_txt);
		contentTv = (TextView) view.findViewById(R.id.content_txt);
		timeTv = (TextView) view.findViewById(R.id.time_txt);
		
	}
	
}

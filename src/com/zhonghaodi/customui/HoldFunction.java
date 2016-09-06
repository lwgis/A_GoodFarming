package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HoldFunction {
	public ImageView leftIv;
	public ImageView rightIv;
	public TextView titileTv;
	public TextView desTv;
	
	public HoldFunction(View view) {
		leftIv=(ImageView)view.findViewById(R.id.left_image);
		rightIv=(ImageView)view.findViewById(R.id.right_image);
		titileTv=(TextView)view.findViewById(R.id.title_text);
		desTv = (TextView)view.findViewById(R.id.des_text);
	}

}

package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class SolutionHolder {
	public ImageView headIv;
	public TextView solcountTv;
	public TextView nameTv;
	public TextView timeTv;
	public TextView contentTv;
	public TextView zancountTv;
	public TextView commentTv;
	public ImageView zanIv;
	public LinearLayout commentLayout;
	public LinearLayout zanLayout;
	public TextView levelTextView;
	 public SolutionHolder(View view){
		 headIv  = (ImageView)view.findViewById(R.id.head_image);
		 solcountTv = (TextView)view.findViewById(R.id.solcount_text);
		 nameTv = (TextView)view.findViewById(R.id.name_text);
		 timeTv = (TextView)view.findViewById(R.id.time_text);
		 contentTv = (TextView)view.findViewById(R.id.content_text);
		 zancountTv = (TextView)view.findViewById(R.id.zancount_tv);
		 commentTv = (TextView)view.findViewById(R.id.commentcount_tv);
		 zanIv = (ImageView)view.findViewById(R.id.zanIv);
		 commentLayout = (LinearLayout)view.findViewById(R.id.commentLayout);
		 zanLayout = (LinearLayout)view.findViewById(R.id.zan_layout);
		 levelTextView = (TextView)view.findViewById(R.id.level_text);
	 }
}

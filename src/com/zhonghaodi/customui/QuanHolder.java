package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class QuanHolder {
	public	TextView nameTv;
	public	TextView timeTv;
	public	TextView contentTv;
	public  TextView commentTv;
	public	ImageView pinlunImg;
	public	ImageView headIv;
	public	ImageView zanImg;

		public QuanHolder(View view) {
			nameTv = (TextView) view.findViewById(R.id.name_txt);
			timeTv = (TextView) view.findViewById(R.id.time_txt);
			contentTv = (TextView) view.findViewById(R.id.content_txt);
			headIv = (ImageView) view.findViewById(R.id.head_img);
			commentTv = (TextView)view.findViewById(R.id.comment_txt);
			pinlunImg = (ImageView)view.findViewById(R.id.pinglun_img);
			zanImg = (ImageView)view.findViewById(R.id.zan_img);
		}
		
}

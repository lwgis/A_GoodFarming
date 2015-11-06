package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class HolderCommentQr {
	public	TextView nameTv;
	public	TextView timeTv;
	public	UrlTextView contentTv;
	public	TextView countTv;
	public  TextView cropTv;
	public	ImageView headIv;

		public HolderCommentQr(View view) {
			nameTv = (TextView) view.findViewById(R.id.name_text);
			timeTv = (TextView) view.findViewById(R.id.time_text);
			contentTv = (UrlTextView) view.findViewById(R.id.content_text);
			countTv = (TextView) view.findViewById(R.id.count_text);
			headIv = (ImageView) view.findViewById(R.id.head_image);
			cropTv = (TextView)view.findViewById(R.id.crop_text);
		}
}

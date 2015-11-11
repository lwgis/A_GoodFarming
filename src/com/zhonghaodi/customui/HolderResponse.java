package com.zhonghaodi.customui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class HolderResponse {
	public	TextView nameTv;
	public	TextView timeTv;
	public	UrlTextView contentTv;
	public	ImageView headIv;
	public TextView agreeTextView;
	public TextView disagreeTextView;
	public LinearLayout agreeLayout;
	public LinearLayout disagreeLayout;
	public View cainaView;
	public TextView countTv;
		public HolderResponse(View view) {
			nameTv = (TextView) view.findViewById(R.id.name_text);
			timeTv = (TextView) view.findViewById(R.id.time_text);
			contentTv = (UrlTextView) view.findViewById(R.id.content_text);
			headIv = (ImageView) view.findViewById(R.id.head_image);
			agreeTextView = (TextView)view.findViewById(R.id.zancount_tv);
			disagreeTextView = (TextView)view.findViewById(R.id.disagreecount_tv);
			agreeLayout = (LinearLayout)view.findViewById(R.id.zan_layout);
			disagreeLayout = (LinearLayout)view.findViewById(R.id.disagreeLayout);
			cainaView = view.findViewById(R.id.cainalayout);
			countTv = (TextView)view.findViewById(R.id.count_tv);
		}
		
}

package com.zhonghaodi.customui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class HolderResponse {
	public	TextView nameTv;
	public	TextView timeTv;
	public	UrlTextView contentTv;
	public	ImageView headIv;
	public MyTextButton agreebtn;
	public MyTextButton disagreebtn;
	public View cainaView;
	public View countView;
	public TextView countTv;
		public HolderResponse(View view) {
			nameTv = (TextView) view.findViewById(R.id.name_text);
			timeTv = (TextView) view.findViewById(R.id.time_text);
			contentTv = (UrlTextView) view.findViewById(R.id.content_text);
			headIv = (ImageView) view.findViewById(R.id.head_image);
			agreebtn = (MyTextButton)view.findViewById(R.id.agree_button);
			disagreebtn = (MyTextButton)view.findViewById(R.id.disagree_button);
			cainaView = view.findViewById(R.id.cainalayout);
			countView = view.findViewById(R.id.count_layout);
			countTv = (TextView)view.findViewById(R.id.count_tv);
		}
		
}

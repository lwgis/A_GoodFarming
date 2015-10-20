package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class HolderCommentTextMessage {
	public ImageView headIv;
	public UrlTextView contentTv;
	public TextView timeTv;
	public TextView nameTv;
	public HolderCommentTextMessage(View view){
		headIv=(ImageView)view.findViewById(R.id.head_image);
		contentTv=(UrlTextView)view.findViewById(R.id.content_text);
		timeTv=(TextView)view.findViewById(R.id.time_text);
		nameTv = (TextView)view.findViewById(R.id.name_text);
	}
}

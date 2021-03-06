package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

public class HolderCommentTextMessage {
	public RoundedImageView headIv;
	public UrlTextView contentTv;
	public TextView timeTv;
	public TextView nameTv;
	public LinearLayout spcLayout;
	public TextView levelTextView;
	public HolderCommentTextMessage(View view){
		headIv=(RoundedImageView)view.findViewById(R.id.head_image);
		contentTv=(UrlTextView)view.findViewById(R.id.content_text);
		timeTv=(TextView)view.findViewById(R.id.time_text);
		nameTv = (TextView)view.findViewById(R.id.name_text);
		spcLayout = (LinearLayout)view.findViewById(R.id.spcLayout);
		levelTextView = (TextView)view.findViewById(R.id.level_text);
	}
}

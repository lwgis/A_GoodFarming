package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HoldChatTextMessage {
	public ImageView headIv;
	public TextView contentTv;
	public TextView timeTv;
	public HoldChatTextMessage(View view){
		headIv=(ImageView)view.findViewById(R.id.head_image);
		contentTv=(TextView)view.findViewById(R.id.content_text);
		timeTv=(TextView)view.findViewById(R.id.time_text);
	}

}

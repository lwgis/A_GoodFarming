package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HoldChatVoice {
	public ImageView headIv;
	public ImageView playIv;
	public TextView lengthTv;
	public TextView readedTv;

	public HoldChatVoice(View view) {
		headIv = (ImageView) view.findViewById(R.id.head_image);
		playIv = (ImageView) view.findViewById(R.id.play_image);
		lengthTv = (TextView) view.findViewById(R.id.length_text);
		readedTv = (TextView) view.findViewById(R.id.readed_text);
	}
}

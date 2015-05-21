package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;

import com.zhonghaodi.goodfarming.R;

public class HoldChatPhoto {
	public ImageView headIv;
	public ImageView photoIv;

	public HoldChatPhoto(View view) {
		headIv = (ImageView) view.findViewById(R.id.head_image);
		photoIv = (ImageView) view.findViewById(R.id.photo_image);
	}
}

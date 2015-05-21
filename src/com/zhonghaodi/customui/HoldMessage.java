package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HoldMessage {
	public ImageView headIv;
	public TextView titleTv;
	public TextView contentTv;
	public TextView timeTv;
	public TextView countTv;

	public HoldMessage(View view) {
		headIv = (ImageView) view.findViewById(R.id.head_image);
		countTv = (TextView) view.findViewById(R.id.count_text);
		titleTv = (TextView) view.findViewById(R.id.title_text);
		contentTv = (TextView) view.findViewById(R.id.content_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
	}
}

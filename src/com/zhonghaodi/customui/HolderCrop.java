package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HolderCrop {
	public LinearLayout childContentView;
	public TextView cropTv;

	public HolderCrop(View view) {
		childContentView =(LinearLayout) view.findViewById(R.id.content_view);
		cropTv = (TextView) view.findViewById(R.id.crop_text);
	}
}

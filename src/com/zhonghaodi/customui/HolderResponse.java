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
	public TextView countTv;
	public Button zanBtn;
		public HolderResponse(View view) {
			nameTv = (TextView) view.findViewById(R.id.name_text);
			timeTv = (TextView) view.findViewById(R.id.time_text);
			contentTv = (UrlTextView) view.findViewById(R.id.content_text);
			headIv = (ImageView) view.findViewById(R.id.head_image);
			countTv=(TextView)view.findViewById(R.id.count_text);
			zanBtn=(Button)view.findViewById(R.id.zan_button);
		}
		public void zan() {
			int count=Integer.parseInt(countTv.getText().toString());
			count++;
			countTv.setText(String.valueOf(count));
		}
		public void cancelZan() {
			int count=Integer.parseInt(countTv.getText().toString());
			count--;
			countTv.setText(String.valueOf(count));
		}
}

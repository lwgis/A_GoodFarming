package com.zhonghaodi.customui;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class Holder3  {
	public	TextView nameTv;
	public	TextView timeTv;
	public	TextView contentTv;
	public	TextView countTv;
	public	ImageView headIv;
	public	GFImageView imageView1;
	public	GFImageView imageView2;
	public	GFImageView imageView3;
	public	GFImageView imageView4;
	public	GFImageView imageView5;
	public	GFImageView imageView6;

	public	Holder3(View view) {
		nameTv = (TextView) view.findViewById(R.id.name_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
		contentTv = (TextView) view.findViewById(R.id.content_text);
		countTv = (TextView) view.findViewById(R.id.count_text);
		headIv = (ImageView) view.findViewById(R.id.head_image);
		imageView1 = (GFImageView) view.findViewById(R.id.image1);
		imageView2 = (GFImageView) view.findViewById(R.id.image2);
		imageView3 = (GFImageView) view.findViewById(R.id.image3);
		imageView4 = (GFImageView) view.findViewById(R.id.image4);
		imageView5 = (GFImageView) view.findViewById(R.id.image5);
		imageView6 = (GFImageView) view.findViewById(R.id.image6);
		reSetImageViews();
	}

	public void reSetImageViews() {
		imageView1.setVisibility(View.INVISIBLE);
		imageView2.setVisibility(View.INVISIBLE);
		imageView3.setVisibility(View.INVISIBLE);
		imageView4.setVisibility(View.INVISIBLE);
		imageView5.setVisibility(View.INVISIBLE);
		imageView6.setVisibility(View.INVISIBLE);
	}
}
package com.zhonghaodi.customui;

import android.view.View;

import com.zhonghaodi.goodfarming.R;

public class QuanHolder9 extends QuanHolder{
	public GFImageView imageView1;
	public GFImageView imageView2;
	public GFImageView imageView3;
	public GFImageView imageView4;
	public GFImageView imageView5;
	public GFImageView imageView6;
	public GFImageView imageView7;
	public GFImageView imageView8;
	public GFImageView imageView9;

		public QuanHolder9(View view) {
			super(view);
			imageView1 = (GFImageView)view.findViewById(R.id.image1);
			imageView2 = (GFImageView)view.findViewById(R.id.image2);
			imageView3 = (GFImageView)view.findViewById(R.id.image3);
			imageView4 = (GFImageView)view.findViewById(R.id.image4);
			imageView5 = (GFImageView)view.findViewById(R.id.image5);
			imageView6 = (GFImageView)view.findViewById(R.id.image6);
			imageView7 = (GFImageView)view.findViewById(R.id.image7);
			imageView8 = (GFImageView)view.findViewById(R.id.image8);
			imageView9 = (GFImageView)view.findViewById(R.id.image9);
		}
		
		public void reSetImageViews() {
			imageView1.setVisibility(View.INVISIBLE);
			imageView2.setVisibility(View.INVISIBLE);
			imageView3.setVisibility(View.INVISIBLE);
			imageView4.setVisibility(View.INVISIBLE);
			imageView5.setVisibility(View.INVISIBLE);
			imageView6.setVisibility(View.INVISIBLE);
			imageView7.setVisibility(View.INVISIBLE);
			imageView8.setVisibility(View.INVISIBLE);
			imageView9.setVisibility(View.INVISIBLE);
		}
}

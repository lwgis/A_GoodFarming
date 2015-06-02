package com.zhonghaodi.customui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhonghaodi.goodfarming.R;

public class QuanHolder3 extends QuanHolder {
	
	public GFImageView imageView1;
	public GFImageView imageView2;
	public GFImageView imageView3;

		public QuanHolder3(View view) {
			super(view);
			
			imageView1 = (GFImageView)view.findViewById(R.id.image1);
			imageView2 = (GFImageView)view.findViewById(R.id.image2);
			imageView3 = (GFImageView)view.findViewById(R.id.image3);
		}
		
		public void reSetImageViews() {
			imageView1.setVisibility(View.INVISIBLE);
			imageView2.setVisibility(View.INVISIBLE);
			imageView3.setVisibility(View.INVISIBLE);
		}
}

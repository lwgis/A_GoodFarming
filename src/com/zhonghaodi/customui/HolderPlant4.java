package com.zhonghaodi.customui;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HolderPlant4 {
	public TextView nameTv;
	public TextView timeTv;
	public TextView cropTv;
	public TextView cateTv;
	public UrlTextView contentTv;
	public RoundedImageView headIv;
	public TextView agreeTextView;
	public LinearLayout agreeLayout;
	public TextView countTv;
	public LinearLayout countLayout;
	public TextView levelTextView;
	public	GFImageView imageView1;
	public	GFImageView imageView2;
	public	GFImageView imageView3;
	public	GFImageView imageView4;
	public	GFImageView imageView5;
	public	GFImageView imageView6;
	public	GFImageView imageView7;
	public	GFImageView imageView8;
	public	GFImageView imageView9;
	public TextView forwardTextView;
	public LinearLayout forwardLayout;
	
	public HolderPlant4(View view){
		nameTv = (TextView) view.findViewById(R.id.name_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
		cropTv = (TextView) view.findViewById(R.id.crop_text);
		cateTv = (TextView) view.findViewById(R.id.cate_text);
		contentTv = (UrlTextView) view.findViewById(R.id.content_text);
		headIv = (RoundedImageView) view.findViewById(R.id.head_image);
		agreeTextView = (TextView)view.findViewById(R.id.zancount_tv);
		agreeLayout = (LinearLayout)view.findViewById(R.id.plantzan_layout);
		countTv = (TextView)view.findViewById(R.id.count_tv);
		countLayout = (LinearLayout)view.findViewById(R.id.count_layout);
		levelTextView = (TextView)view.findViewById(R.id.level_text);
		imageView1 = (GFImageView) view.findViewById(R.id.image1);
		imageView2 = (GFImageView) view.findViewById(R.id.image2);
		imageView3 = (GFImageView) view.findViewById(R.id.image3);
		imageView4 = (GFImageView) view.findViewById(R.id.image4);
		imageView5 = (GFImageView) view.findViewById(R.id.image5);
		imageView6 = (GFImageView) view.findViewById(R.id.image6);
		imageView7 = (GFImageView) view.findViewById(R.id.image7);
		imageView8 = (GFImageView) view.findViewById(R.id.image8);
		imageView9 = (GFImageView) view.findViewById(R.id.image9);
		forwardTextView = (TextView)view.findViewById(R.id.forwardcount_tv);
		forwardLayout = (LinearLayout)view.findViewById(R.id.forward_layout);
		reSetImageViews();
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

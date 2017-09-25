package com.zhonghaodi.customui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.core.r;
import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

public class HolderResponse {
	
	public	TextView nameTv;
	public	TextView timeTv;
	public	UrlTextView contentTv;
	public	RoundedImageView headIv;
	public	GFImageView imageView1;
	public	GFImageView imageView2;
	public	GFImageView imageView3;
	public TextView agreeTextView;
	public LinearLayout agreeLayout;
	public View cainaView;
	public TextView countTv;
	public LinearLayout countLayout;
	public TextView levelTextView;
	public LinearLayout bottomLayout;
	public LinearLayout moreImage;
	public RelativeLayout rLayout;
	public View bottomLine;
	
	public HolderResponse(View view) {
		nameTv = (TextView) view.findViewById(R.id.name_text);
		timeTv = (TextView) view.findViewById(R.id.time_text);
		contentTv = (UrlTextView) view.findViewById(R.id.content_text);
		headIv = (RoundedImageView) view.findViewById(R.id.head_image);
		agreeTextView = (TextView)view.findViewById(R.id.zancount_tv);
		agreeLayout = (LinearLayout)view.findViewById(R.id.zan_layout);
		cainaView = view.findViewById(R.id.cainalayout);
		countTv = (TextView)view.findViewById(R.id.count_tv);
		countLayout = (LinearLayout)view.findViewById(R.id.count_layout);
		levelTextView = (TextView)view.findViewById(R.id.level_text);
		bottomLayout = (LinearLayout)view.findViewById(R.id.bottomLayout);
		bottomLine = view.findViewById(R.id.bottomLine);
		moreImage = (LinearLayout)view.findViewById(R.id.more_image);
		imageView1 = (GFImageView) view.findViewById(R.id.image1);
		imageView2 = (GFImageView) view.findViewById(R.id.image2);
		imageView3 = (GFImageView) view.findViewById(R.id.image3);
		rLayout = (RelativeLayout)view.findViewById(R.id.img_layout);
		reSetImageViews();
	}
	
	public void reSetImageViews() {
		rLayout.setVisibility(View.GONE);
		imageView1.setVisibility(View.GONE);
		imageView2.setVisibility(View.GONE);
		imageView3.setVisibility(View.GONE);
	}
	
	public void displayImg(){
		rLayout.setVisibility(View.VISIBLE);
	}
}

package com.zhonghaodi.customui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CaicaicaiHolder {
	public  TextView    contentView;
	public  TextView    answerView;
	public  TextView    responseView;
	public  View        splitLine;
	public  TextView    commentView;
	public  LinearLayout imgLayout1;
	public  LinearLayout imgLayout2;
	public List<GFImageView> imageViews1;
	public List<GFImageView> imageViews2;
	
	public CaicaicaiHolder(View view){
		imageViews1 = new ArrayList<GFImageView>();
		imageViews2 = new ArrayList<GFImageView>();
		GFImageView imageView1 = (GFImageView) view.findViewById(R.id.image1);
		imageViews1.add(imageView1);
		GFImageView imageView2 = (GFImageView) view.findViewById(R.id.image2);
		imageViews1.add(imageView2);
		GFImageView imageView3 = (GFImageView) view.findViewById(R.id.image3);
		imageViews1.add(imageView3);
		GFImageView imageView4 = (GFImageView) view.findViewById(R.id.image4);
		imageViews2.add(imageView4);
		GFImageView imageView5 = (GFImageView) view.findViewById(R.id.image5);
		imageViews2.add(imageView5);
		GFImageView imageView6 = (GFImageView) view.findViewById(R.id.image6);
		imageViews2.add(imageView6);
		contentView = (TextView) view.findViewById(R.id.content_text);
		answerView = (TextView) view.findViewById(R.id.answer_text);
		responseView = (TextView) view.findViewById(R.id.response_text);
		splitLine = view.findViewById(R.id.splitline);
		commentView = (TextView) view.findViewById(R.id.comment_text);
		imgLayout1 = (LinearLayout) view.findViewById(R.id.imgLayout1);
		imgLayout2 = (LinearLayout) view.findViewById(R.id.imgLayout2);
	}
	
	public void hideImageviews(){
		for (Iterator iterator = imageViews1.iterator(); iterator.hasNext();) {
			GFImageView imageView = (GFImageView) iterator.next();
			imageView.setVisibility(View.GONE);
		}
		for (Iterator iterator = imageViews2.iterator(); iterator.hasNext();) {
			GFImageView imageView = (GFImageView) iterator.next();
			imageView.setVisibility(View.GONE);
		}
	}
}

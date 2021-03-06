package com.zhonghaodi.customui;

import com.makeramen.RoundedImageView;
import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HolderMeInfo {
	public RoundedImageView headIv;
	public TextView titleTv;
	public TextView youhuibiTv;
	public TextView guanzhuTv;
	public TextView fensiTv;
	public TextView tjcodeTv;
	public TextView reccountTv;
	public TextView tjcoinTv;
	public ImageView qrImageView;
	public View guanzhuView;
	public View fensiView;
	public LinearLayout recLayout;
	public HolderMeInfo(View view){
		headIv=(RoundedImageView)view.findViewById(R.id.head_image);
		titleTv=(TextView)view.findViewById(R.id.title_text);
		youhuibiTv=(TextView)view.findViewById(R.id.youhuibi_text);
		guanzhuTv=(TextView)view.findViewById(R.id.guanzhu_text);
		fensiTv=(TextView)view.findViewById(R.id.fensi_text);
		guanzhuView=view.findViewById(R.id.guanzhu_view);
		fensiView=view.findViewById(R.id.fensi_view);
		tjcodeTv = (TextView)view.findViewById(R.id.tjcode_text);
		reccountTv = (TextView)view.findViewById(R.id.reccount_text);
		tjcoinTv = (TextView)view.findViewById(R.id.tjcoin_text);
		qrImageView=(ImageView)view.findViewById(R.id.qrcode_img);
		recLayout = (LinearLayout)view.findViewById(R.id.reclayout);
	}
}

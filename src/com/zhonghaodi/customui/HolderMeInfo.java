package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HolderMeInfo {
	public ImageView headIv;
	public TextView titleTv;
	public TextView jifenTv;
	public TextView youhuibiTv;
	public TextView guanzhuTv;
	public TextView fensiTv;
	public TextView tjcodeTv;
	public View guanzhuView;
	public View fensiView;
	public MyTextButton siginButton;
	public HolderMeInfo(View view){
		headIv=(ImageView)view.findViewById(R.id.head_image);
		titleTv=(TextView)view.findViewById(R.id.title_text);
		jifenTv=(TextView)view.findViewById(R.id.jifen_text);
		youhuibiTv=(TextView)view.findViewById(R.id.youhuibi_text);
		guanzhuTv=(TextView)view.findViewById(R.id.guanzhu_text);
		fensiTv=(TextView)view.findViewById(R.id.fensi_text);
		guanzhuView=view.findViewById(R.id.guanzhu_view);
		fensiView=view.findViewById(R.id.fensi_view);
		tjcodeTv = (TextView)view.findViewById(R.id.tjcode_text);
		siginButton = (MyTextButton)view.findViewById(R.id.sigin_button);
	}
}

package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class SpinnerHolder {
	
	public TextView nameTv;
	public RadioButton selectRd;
	public SpinnerHolder(View view){
		nameTv = (TextView)view.findViewById(R.id.name_text);
		selectRd = (RadioButton)view.findViewById(R.id.rd_select);
	}

}

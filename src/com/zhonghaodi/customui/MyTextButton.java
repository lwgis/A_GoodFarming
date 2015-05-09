package com.zhonghaodi.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MyTextButton extends Button {

	public MyTextButton(Context context, AttributeSet attrs) {

	      super(context, attrs);

	   }

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		if (enabled) {
			this.setTextColor(0xFFFFFFFF);
		}
		else {
			this.setTextColor(0xFFCACACA);
		}
		
	}

}

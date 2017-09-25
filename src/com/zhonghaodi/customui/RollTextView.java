package com.zhonghaodi.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RollTextView extends TextView {

	public RollTextView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}
	
	public RollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //重写isFocused方法，让其一直返回true
    public boolean isFocused() {
        return true;
    }
}

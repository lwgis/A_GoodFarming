package com.zhonghaodi.customui;

import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.graphics.RectF;

import android.graphics.Paint.Style;

import android.util.AttributeSet;

import android.widget.EditText;

public class MyEditText extends EditText {
	Paint paint = new Paint();
	RectF rectF = new RectF(2 + this.getScrollX(), 2 + this.getScrollY(),
			this.getWidth() - 3 + this.getScrollX(), this.getHeight()
					+ this.getScrollY() - 1);

	public MyEditText(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		paint.setStyle(Style.STROKE);

		paint.setStrokeWidth(2);

		if (this.isFocused() == true)

			paint.setColor(Color.parseColor("#07cf98"));

		else

			paint.setColor(Color.rgb(56, 190, 153));
		rectF.set(2 + this.getScrollX(), 2 + this.getScrollY(), this.getWidth()
				- 3 + this.getScrollX(), this.getHeight() + this.getScrollY()
				- 1);
		canvas.drawRoundRect(rectF, 3, 3, paint);

		super.onDraw(canvas);

	}

}

package com.zhonghaodi.customui;

import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.graphics.RectF;

import android.graphics.Paint.Style;

import android.util.AttributeSet;

import android.widget.EditText;

public class MyEditText extends EditText{

   public MyEditText(Context context, AttributeSet attrs) {

      super(context, attrs);

   }

 @Override

 protected void onDraw(Canvas canvas) {

    Paint paint = new Paint();

    paint.setStyle(Style.STROKE);

    paint.setStrokeWidth(2);

    if(this.isFocused() == true)

        paint.setColor(Color.parseColor("#122e29"));

  else

       paint.setColor(Color.rgb(150,150,150));

  canvas.drawRoundRect(new RectF(2+this.getScrollX(), 2+this.getScrollY(), this.getWidth()-3+this.getScrollX(), this.getHeight()+ this.getScrollY()-1), 3,3, paint);

  super.onDraw(canvas);

}

}

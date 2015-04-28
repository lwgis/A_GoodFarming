package com.zhonghaodi.goodfarming;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

public class MySpan extends URLSpan {

	public MySpan(String url) {
		super(url);
		// TODO Auto-generated constructor stub
	}
  @Override
public void onClick(View widget) {
	// TODO Auto-generated method stub
      Uri uri = Uri.parse(getURL());
      Context context = widget.getContext();
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
      context.startActivity(intent);
      }
  @Override
  public void updateDrawState(TextPaint ds) { 
//	  super.updateDrawState(ds);
  //设置没有下划线
  ds.setUnderlineText(false);
  //设置颜色高亮
  ds.setARGB(255, 0, 71, 112);
  }
}

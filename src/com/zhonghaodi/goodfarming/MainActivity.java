package com.zhonghaodi.goodfarming;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView tv=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv=(TextView)findViewById(R.id.textview);
		String html="<a href='http://www.baidu.com'>百度一下</a>谷歌超链接、高亮显示、高亮1、高亮2、斜体、下划线.";
		//创建一个 SpannableString对象
		SpannableString sp = new SpannableString("谷歌超链接、高亮显示、高亮1、高亮2、斜体、下划线.");
		//设置超链接
		sp.setSpan(new MySpan("http://www.baidu.com"), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//		//设置高亮样式一
//		sp.setSpan(new BackgroundColorSpan(Color.RED), 11, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		//设置高亮样式二
//		sp.setSpan(new ForegroundColorSpan(Color.YELLOW), 15, 18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//		//设置斜体
//		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 19, 21, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//		//设置下划线
//		sp.setSpan(new UnderlineSpan(), 22, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		tv.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence charSequence = Html.fromHtml(html); 
		tv.setAutoLinkMask(Linkify.WEB_URLS);
		tv.setText(charSequence);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

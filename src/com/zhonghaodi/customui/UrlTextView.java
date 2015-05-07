package com.zhonghaodi.customui;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class UrlTextView extends TextView {
	private UrlOnClick urlOnClick;

	public UrlTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setHtmlText(String text) {
		setMovementMethod(LinkMovementMethod.getInstance());
		setAutoLinkMask(Linkify.WEB_URLS);
		this.setText(getClickableHtml(text));
	}

	private CharSequence getClickableHtml(String html) {
		Spanned spannedHtml = Html.fromHtml(html);
		SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(
				spannedHtml);
		URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(),
				URLSpan.class);
		for (final URLSpan span : urls) {
			setLinkClickable(clickableHtmlBuilder, span);
		}
		return clickableHtmlBuilder;
	}

	private void setLinkClickable(
			final SpannableStringBuilder clickableHtmlBuilder,
			final URLSpan urlSpan) {
		int start = clickableHtmlBuilder.getSpanStart(urlSpan);
		int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
		int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
		ClickableSpan clickableSpan = new ClickableSpan() {
			public void onClick(View view) {
				if (urlOnClick!=null) {
					urlOnClick.onClick(view, urlSpan.getURL());
				}
			}

			@Override
			public void updateDrawState(TextPaint ds) {
				// super.updateDrawState(ds);
				// 设置没有下划线
				ds.setUnderlineText(true);
				// 设置颜色高亮
				ds.setARGB(255, 45, 178, 134);
				// ds.bgColor=Color.argb(255, 0, 255, 0);
			}
		};
		clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
	}

	public void setUrlOnClick(UrlOnClick click) {
		urlOnClick = click;
	}

	public interface UrlOnClick {
		void onClick(View view, String urlString);
	}
}

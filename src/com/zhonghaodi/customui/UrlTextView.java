package com.zhonghaodi.customui;

import android.content.Context;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class UrlTextView extends TextView {
	private UrlOnClick urlOnClick;
	boolean dontConsumeNonUrlClicks = true;
	boolean linkHit;
	
	public UrlTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setHtmlText(String text) {
		setMovementMethod(LocalLinkMovementMethod.getInstance());
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		linkHit = false;
	    boolean res = super.onTouchEvent(event);
	    if (dontConsumeNonUrlClicks) {
	        if(!linkHit){
	          if(isLongClickable()){
	            return true;
	        }
	      }
	      return linkHit;
	    }
	    return res;
	 
	}
	@Override
	public boolean hasFocusable() {
	   return false;
	}
	
	public static class LocalLinkMovementMethod extends LinkMovementMethod{
	    static LocalLinkMovementMethod sInstance;
	    private long lastClickTime;

	    private static final long CLICK_DELAY = 500l;
	 
	 
	    public static LocalLinkMovementMethod getInstance() {
	        if (sInstance == null)
	            sInstance = new LocalLinkMovementMethod();
	 
	        return sInstance;
	    }
	 
	    @Override
	    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
	    	int action = event.getAction();

	        if (action == MotionEvent.ACTION_UP ||
	                action == MotionEvent.ACTION_DOWN) {
	            int x = (int) event.getX();
	            int y = (int) event.getY();

	            x -= widget.getTotalPaddingLeft();
	            y -= widget.getTotalPaddingTop();

	            x += widget.getScrollX();
	            y += widget.getScrollY();

	            Layout layout = widget.getLayout();
	            int line = layout.getLineForVertical(y);
	            int off = layout.getOffsetForHorizontal(line, x);

	            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

	            if (link.length != 0) {
	                if (action == MotionEvent.ACTION_UP) {
	                    if(System.currentTimeMillis() - lastClickTime < CLICK_DELAY){
	                        link[0].onClick(widget);
	                    }
	                } else if (action == MotionEvent.ACTION_DOWN) {
	                    Selection.setSelection(buffer,
	                            buffer.getSpanStart(link[0]),
	                            buffer.getSpanEnd(link[0]));
	                    lastClickTime = System.currentTimeMillis();
	                }

	                return true;
	            } else {
	                Selection.removeSelection(buffer);
	            }
	        }
	        return super.onTouchEvent(widget, buffer, event);
	    }
	}
}

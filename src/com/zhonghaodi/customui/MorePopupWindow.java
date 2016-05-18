package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class MorePopupWindow extends PopupWindow {

	private ImageView weixinView;
	private ImageView circlefriendsView;
	private ImageView qqView;
	private ImageView qzoneView;
	private ImageView reportView;
	private MyTextButton cancelButton;
	private View mMenuView;
	private Activity mActivity;
	
	public MorePopupWindow(Activity context,OnClickListener clickListener){
		super(context);
		mActivity = context;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popupwindow_more, null);
		reportView = (ImageView)mMenuView.findViewById(R.id.img_more_report);
		reportView.setOnClickListener(clickListener);
		
		cancelButton = (MyTextButton)mMenuView.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		//设置SelectPicPopupWindow的View  
        this.setContentView(mMenuView);  
        //设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.FILL_PARENT);  
        //设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);  

	}
}

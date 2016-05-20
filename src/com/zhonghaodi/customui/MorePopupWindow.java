package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MorePopupWindow extends PopupWindow {

	private ImageView weixinView;
	private ImageView circlefriendsView;
	private ImageView qqView;
	private ImageView qzoneView;
	private ImageView reportView;
	private MyTextButton cancelButton;
	private LinearLayout reportLayout;
	private View mMenuView;
	private Activity mActivity;
	
	public MorePopupWindow(Activity context,OnClickListener clickListener){
		super(context);
		mActivity = context;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popupwindow_more, null);
		weixinView = (ImageView)mMenuView.findViewById(R.id.img_share_weixin);
		weixinView.setOnClickListener(clickListener);
		circlefriendsView = (ImageView)mMenuView.findViewById(R.id.img_share_circlefriends);
		circlefriendsView.setOnClickListener(clickListener);
		qqView = (ImageView)mMenuView.findViewById(R.id.img_share_qq);
		qqView.setOnClickListener(clickListener);
		qzoneView = (ImageView)mMenuView.findViewById(R.id.img_share_qzone);
		qzoneView.setOnClickListener(clickListener);
		reportView = (ImageView)mMenuView.findViewById(R.id.img_more_report);
		reportView.setOnClickListener(clickListener);
		reportLayout = (LinearLayout)mMenuView.findViewById(R.id.repostlayout);
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
	public void showReport(){
		reportLayout.setVisibility(View.VISIBLE);
	}
	public void hideReport(){
		reportLayout.setVisibility(View.GONE);
	}
}

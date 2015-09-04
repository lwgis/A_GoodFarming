package com.zhonghaodi.customui;

import org.json.JSONObject;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhonghaodi.goodfarming.MeFragment;
import com.zhonghaodi.goodfarming.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class SharePopupwindow extends PopupWindow {
	
	private ImageView weixinView;
	private ImageView circlefriendsView;
	private ImageView qqView;
	private ImageView qzoneView;
	private MyTextButton cancelButton;
	private View mMenuView;
	private Activity mActivity;
	
	public SharePopupwindow(Activity context,OnClickListener clickListener) {
		// TODO Auto-generated constructor stub
		super(context);
		mActivity = context;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.popupwindow_share, null);
		weixinView = (ImageView)mMenuView.findViewById(R.id.img_share_weixin);
		weixinView.setOnClickListener(clickListener);
		circlefriendsView = (ImageView)mMenuView.findViewById(R.id.img_share_circlefriends);
		circlefriendsView.setOnClickListener(clickListener);
		qqView = (ImageView)mMenuView.findViewById(R.id.img_share_qq);
		qqView.setOnClickListener(clickListener);
		qzoneView = (ImageView)mMenuView.findViewById(R.id.img_share_qzone);
		qzoneView.setOnClickListener(clickListener);
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
        //设置SelectPicPopupWindow弹出窗体可点击  
//        this.setFocusable(true);  
//        //设置SelectPicPopupWindow弹出窗体动画效果  
//        this.setAnimationStyle(R.style.AnimBottom);  
//        //实例化一个ColorDrawable颜色为半透明  
//        ColorDrawable dw = new ColorDrawable(0xb0000000);  
//        //设置SelectPicPopupWindow弹出窗体的背景  
//        this.setBackgroundDrawable(dw);  
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
//        mMenuView.setOnTouchListener(new OnTouchListener() {  
//              
//            public boolean onTouch(View v, MotionEvent event) {  
//                  
//                int height = mMenuView.findViewById(R.id.toptextview).getTop();  
//                int y=(int) event.getY();  
//                if(event.getAction()==MotionEvent.ACTION_UP){  
//                    if(y<height){  
//                        dismiss();  
//                    }  
//                }                 
//                return true;  
//            }  
//        });  

	}

}

package com.zhonghaodi.goodfarming.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI wxApi;
	private TextView resultTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxentry);
		resultTv = (TextView)findViewById(R.id.callback_text);
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, false);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		wxApi.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		switch (arg0.errCode) {  
        case BaseResp.ErrCode.ERR_OK:  
        	GFToast.show(this, "发送成功");
            resultTv.setText("发送成功");
            finish();  
            break;  
        case BaseResp.ErrCode.ERR_USER_CANCEL: 
        	GFToast.show(this, "分享取消");
            resultTv.setText("分享取消");
            finish();  
            break;  
        case BaseResp.ErrCode.ERR_AUTH_DENIED:  
        	GFToast.show(this, "分享被拒绝");
            resultTv.setText("分享被拒绝");
            finish();  
            break;  
        default:  
        	GFToast.show(this, "分享返回");
            resultTv.setText("分享返回");
            finish();
            break;  
        }  
	}

}

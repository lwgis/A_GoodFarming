package com.zhonghaodi.goodfarming;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.Pointrule;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class PointruleActivity extends Activity implements HandMessage {

	private WebView webview;
//	private TextView ruleTextView;
	private GFHandler<PointruleActivity> handler = new GFHandler<PointruleActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pointrule);
		webview  = (WebView)findViewById(R.id.webView);
//		ruleTextView = (TextView)findViewById(R.id.gz_text);
		webview.loadUrl(HttpUtil.ViewUrl+"pointrule");
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
//		loadData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("积分规则");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("积分规则");
		MobclickAgent.onPause(this);
	}
	
	public void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString;
				Message msg;
				try {
					jsonString = HttpUtil.getPointrule();
					msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = jsonString;
					msg.sendToTarget();	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj = "获取积分规则错误";
					msg.sendToTarget();	
				}
							
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			Gson gson = new Gson();
			Pointrule pointrule = gson.fromJson(msg.obj.toString(),
					new TypeToken<Pointrule>() {
					}.getType());
			if(pointrule!=null){
//				webview.loadDataWithBaseURL(null, pointrule.getMessage(), "text/html", "utf-8", null);
//				ruleTextView.setText(pointrule.getMessage());
			}
			else{
				GFToast.show(this, "积分规则为空");
			}
			break;
		case -1:
			GFToast.show(this, msg.obj.toString());
			break;

		default:
			break;
		}
		
	}
	
}

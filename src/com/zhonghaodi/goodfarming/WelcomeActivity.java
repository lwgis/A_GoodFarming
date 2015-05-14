package com.zhonghaodi.goodfarming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 3000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_welcome);
		 new Handler().postDelayed(new Runnable() {  
	            public void run() {  
	                Intent mainIntent = new Intent(WelcomeActivity.this,  
	                        MainActivity.class);  
	                WelcomeActivity.this.startActivity(mainIntent);  
	        		overridePendingTransition(R.anim.zoomin, 
	                		R.anim.zoomout); 
	                WelcomeActivity.this.finish();  
	                
	            }  
	  
	        }, SPLASH_DISPLAY_LENGHT); 
	}

}

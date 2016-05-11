package com.zhonghaodi.goodfarming;

import java.io.File;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.utils.QRCodeUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AppdownActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		MobclickAgent.openActivityDurationTrack(false);
		MyTextButton cancelBtn = (MyTextButton)findViewById(R.id.back_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppdownActivity.this.finish();
			}
		});
		
		//内容
        final String contentET = getIntent().getStringExtra("content").toString();
        //显示二维码图片
        final ImageView imageView = (ImageView) findViewById(R.id.down_image);
 
        final String filePath = getFileRoot(AppdownActivity.this) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";

        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage(contentET.trim(), 800, 800,
                		BitmapFactory.decodeResource(getResources(), R.drawable.appicon),
                        filePath);

                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                    });
                }
            }
        }).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("二维码分享");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("二维码分享");
		MobclickAgent.onPause(this);
	}

	//文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
 
        return context.getFilesDir().getAbsolutePath();
    }
	
}

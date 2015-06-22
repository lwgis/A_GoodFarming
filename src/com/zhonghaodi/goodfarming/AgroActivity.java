package com.zhonghaodi.goodfarming;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AgroActivity extends Activity implements HandMessage {

	private TextView titleView;
	private TextView timeView;
	private GFImageView headIv;
	private TextView countView;
	private TextView disView;
	private GFHandler<AgroActivity> handler = new GFHandler<AgroActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agro);
		
		titleView = (TextView)findViewById(R.id.title_text);
		timeView = (TextView)findViewById(R.id.time_text);
		headIv = (GFImageView)findViewById(R.id.head_image);
		countView = (TextView)findViewById(R.id.count_text);
		disView = (TextView)findViewById(R.id.content_text);
		
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		int aid = getIntent().getIntExtra("aid", 0);
		if(aid!=0){
			loadData(aid);
		}
	}
	
	public void loadData(final int aid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAgrotechnicalById(aid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();				
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final AgroActivity agroactivity =(AgroActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			Agrotechnical agr = gson.fromJson(msg.obj.toString(),
					new TypeToken<Agrotechnical>() {
					}.getType());
			if(agr!=null){
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"agrotechnicals/small/"
								+ agr.getThumbnail(),
								agroactivity.headIv, ImageOptions.options);
				agroactivity.headIv.setImages(agr.getAttachments(),
						"agrotechnicals");
				agroactivity.countView.setText("共"
						+ String.valueOf(agr.getAttachments().size())
						+ "图片");
				agroactivity.titleView.setText(agr.getTitle());
				agroactivity.timeView.setText(agr.getTime());
				agroactivity.disView.setText(agr.getContent());
			}
			
		} else {
			Toast.makeText(this, "获取农业知识失败!",
					Toast.LENGTH_SHORT).show();
		}
	}

}

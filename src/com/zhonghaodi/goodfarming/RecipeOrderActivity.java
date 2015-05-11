package com.zhonghaodi.goodfarming;



import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeOrderActivity extends Activity implements HandMessage {
	private static final int ORDER = 0;
	private static final int QRCODE = 1;
	private int nzdId;
	private int recipeId;
	private int userId;
	private int count;
	private String recipeName;
	private TextView titleTv;
	private TextView orderContentTv;
	private ImageView qrCodeIv;
	private GFHandler<RecipeOrderActivity> handler = new GFHandler<RecipeOrderActivity>(
			this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_recipeorder);
		Button cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleTv = (TextView) findViewById(R.id.title_text);
		orderContentTv = (TextView) findViewById(R.id.ordercontent_text);
		qrCodeIv = (ImageView) findViewById(R.id.qrcode_image);
		nzdId = getIntent().getIntExtra("nzdId", 0);
		recipeId = getIntent().getIntExtra("recipeId", 0);
		userId = getIntent().getIntExtra("userId", 0);
		count = getIntent().getIntExtra("count", 0);
		recipeName = getIntent().getStringExtra("recipeName");
		orderContentTv
				.setText(recipeName + "---" + String.valueOf(count) + "份");
		orderRecipe();
	}

	private void orderRecipe() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.orderRecipe(nzdId, recipeId,
						userId, count);
				Message msg = handler.obtainMessage();
				msg.what = ORDER;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		RecipeOrderActivity activity = (RecipeOrderActivity) object;
		switch (msg.what) {
		case ORDER:
			if (msg.obj != null) {
				RecipeOrder recipeOrder = (RecipeOrder) GsonUtil.fromJson(
						msg.obj.toString(), RecipeOrder.class);
				loadQR(recipeOrder.getCode());
			} else {
				Toast.makeText(activity, "订购失败", Toast.LENGTH_SHORT).show();
			}
			break;
		case QRCODE:
			if (msg.obj != null) {
				byte[] data = msg.obj.toString().getBytes(); // 得到图片的输入流

				// 二进制数据生成位图
				Bitmap bit = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				qrCodeIv.setImageBitmap(bit);
				titleTv.setText("订购成功");
			} else {
				Toast.makeText(activity, "订购失败", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	private void loadQR(final String qrCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getRecipeQRCode(nzdId, recipeId,
						userId, qrCode);
				Message msg = handler.obtainMessage();
				msg.what = QRCODE;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
}

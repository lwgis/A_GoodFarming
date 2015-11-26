package com.zhonghaodi.goodfarming;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Prescription;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PrescriptionEditActivity extends Activity implements OnClickListener,HandMessage {

	private EditText titleEditText;
	private EditText contentEditText;
	private GFHandler<PrescriptionEditActivity> handler = new GFHandler<PrescriptionEditActivity>(this);
	private int id;
	private String title;
	private String content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prescriptionedit);
		Button cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		Button okButton = (Button)findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);
		titleEditText = (EditText)findViewById(R.id.title_edit);
		contentEditText = (EditText)findViewById(R.id.content_edit);
		id = getIntent().getIntExtra("id", 0);
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		if(title!=null){
			titleEditText.setText(title);
		}
		if(content!=null){
			contentEditText.setText(content);
		}
	}
	
	public void edit(){
		final String uid = GFUserDictionary.getUserId();
		title = titleEditText.getText().toString();
		content = contentEditText.getText().toString();
		if(title.isEmpty()){
			GFToast.show("请输入处方标题");
			return;
		}
		if(content.isEmpty()){
			GFToast.show("请输入处方内容");
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if(id==0){
						String jsonString = HttpUtil.addPrescription(uid, title, content);
						Message msg = handler.obtainMessage();
						msg.what = 0;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
					else{
						String jsonString = HttpUtil.editPrescription(id, title, content);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;
		case R.id.ok_button:
			edit();
			break;

		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if(msg.obj!=null && msg.obj.toString().isEmpty()){
				GFToast.show("处方创建成功");
				finish();
			}
			else{
				GFToast.show("处方创建失败");
			}
			break;
		case 1:
			if(msg.obj!=null){
				Gson gson = new Gson();
				String jsString = msg.obj.toString();
				Prescription pre1 = gson.fromJson(jsString,
						new TypeToken<Prescription>() {
						}.getType());
				if(pre1!=null){
					GFToast.show("处方编辑成功");
					finish();
				}
				else{
					GFToast.show("处方编辑失败");
				}
			}
			else{
				GFToast.show("处方编辑失败");
			}
			
			break;

		default:
			break;
		}
	}

}
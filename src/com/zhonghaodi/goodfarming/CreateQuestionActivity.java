package com.zhonghaodi.goodfarming;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class CreateQuestionActivity extends Activity {
	private SelectCropFragment selectCropFragment=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_create_question);
		selectCropFragment=new SelectCropFragment();
		FragmentTransaction transation=getFragmentManager().beginTransaction();
		transation.add(R.id.content_view, selectCropFragment);
		transation.commit();
	}
}

package com.zhonghaodi.goodfarming;

import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.DiseaseAdapter;
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.adapter.RecipeAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.NoScrollListview;
import com.zhonghaodi.model.Disease;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.req.HotQuestionReq;
import com.zhonghaodi.view.HotQuestionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class HotQuestionsActivity extends Activity implements OnClickListener,OnItemClickListener,HotQuestionView {
	
	private NoScrollListview questionList;
	private NoScrollListview diseaseList;
	private NoScrollListview recipeList;
	private MyTextButton backBtn;
	private QuestionAdpter questionAdapter;
	private DiseaseAdapter diseaseAdapter;
	private RecipeAdapter recipeAdapter;
	private List<Question> mQuestions;
	private List<Disease> mDiseases;
	private List<Recipe> mRecipes;
	private String zone="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotquestions);
		questionList = (NoScrollListview)findViewById(R.id.questions_list);
		questionList.setOnItemClickListener(this);
		diseaseList = (NoScrollListview)findViewById(R.id.diseases_list);
		diseaseList.setOnItemClickListener(this);
		recipeList = (NoScrollListview)findViewById(R.id.recipes_list);
		recipeList.setOnItemClickListener(this);
		backBtn = (MyTextButton)findViewById(R.id.cancel_button);
		backBtn.setOnClickListener(this);
		int cid = getIntent().getIntExtra("cid", 0);
		String phrase = getIntent().getStringExtra("phrase");
		zone = getIntent().getStringExtra("zone");
		if(cid==0){
			showMessage("未指定作物！");
			this.finish();
		}
		if(zone==null){
			zone="";
		}
		HotQuestionReq req = new HotQuestionReq(cid,phrase, this, this);
		req.getHotQuestions(zone);
		req.getHotDiseases(zone);
		req.getHotRecipes(zone);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("防治推荐");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("防治推荐");
		MobclickAgent.onPause(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		int pid = parent.getId();
		switch (pid) {
		case R.id.questions_list:
			Intent it = new Intent(this,QuestionActivity.class);
			int qid;
			qid = mQuestions.get(position).getId();
			it.putExtra("questionId", qid);
			startActivity(it);
			break;
		case R.id.diseases_list:
			Disease disease = mDiseases.get(position);
			Intent intent = new Intent(this, DiseaseActivity.class);
			intent.putExtra("diseaseId", disease.getId());
			startActivity(intent);
			break;
		case R.id.recipes_list:
			Intent it1=new Intent();
			it1.setClass(this, RecipeActivity.class);
			it1.putExtra("recipeId", mRecipes.get(position).getId());
			it1.putExtra("nzdCode", mRecipes.get(position).getNzd().getId());
			this.startActivity(it1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(this, mess);
	}

	@Override
	public void showQuestions(List<Question> questions) {
		// TODO Auto-generated method stub
		TextView textView = (TextView)findViewById(R.id.hotq_text);
		textView.setVisibility(View.VISIBLE);
		mQuestions = questions;
		questionAdapter = new QuestionAdpter(mQuestions, this, this, 0);
		questionList.setAdapter(questionAdapter);
	}

	@Override
	public void showDiseases(List<Disease> diseases) {
		// TODO Auto-generated method stub
		TextView textView = (TextView)findViewById(R.id.hotd_text);
		textView.setVisibility(View.VISIBLE);
		mDiseases = diseases;
		diseaseAdapter = new DiseaseAdapter(mDiseases, this);
		diseaseList.setAdapter(diseaseAdapter);
	}

	@Override
	public void showRecipes(List<Recipe> recipes) {
		// TODO Auto-generated method stub
		TextView textView = (TextView)findViewById(R.id.hotr_text);
		textView.setVisibility(View.VISIBLE);
		mRecipes = recipes;
		recipeAdapter = new RecipeAdapter(mRecipes, this);
		recipeList.setAdapter(recipeAdapter);
	}

	
}

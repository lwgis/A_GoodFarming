package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.SharePopupwindow;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.req.SearchFairReq;
import com.zhonghaodi.view.SearchFairView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchFairActivity extends Activity implements SearchFairView,OnClickListener,TextWatcher{

	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private QuestionAdpter adapter;
	private EditText keyEditText;
	private Button searBtn;
	private View clickview;
	
	private Question selectQuestion;
	public IWXAPI wxApi;
	public Tencent mTencent;
	private Question shareQue;
	public SharePopupwindow sharePopupwindow;
	
	public SearchFairReq req;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchfair);
		req = new SearchFairReq(this, this);
		req.cityid = GFAreaUtil.getCity(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		keyEditText = (EditText)findViewById(R.id.searchExt);
		keyEditText.addTextChangedListener(this);
		searBtn = (Button)findViewById(R.id.search_btn);
		searBtn.setOnClickListener(this);
		
		wxApi=WXAPIFactory.createWXAPI(this,HttpUtil.WX_APP_ID, true);
		wxApi.registerApp(HttpUtil.WX_APP_ID);
		mTencent = Tencent.createInstance(HttpUtil.QQ_APP_ID, this.getApplicationContext());
		
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						if (allQuestions.size() == 0) {
							return;
						}
						Question question = allQuestions.get(allQuestions
								.size() - 1);
						String key = keyEditText.getText().toString();
						req.Search(question.getId(), 0, key);
					}
				});
		allQuestions = new ArrayList<Question>();
		adapter = new QuestionAdpter(allQuestions,this,this,2);
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(SearchFairActivity.this,
								QuestionActivity.class);
						it.putExtra("questionId", allQuestions
								.get(position - 1).getId());
						it.putExtra("status", 2);

						SearchFairActivity.this.startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("赶大集搜索");
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("赶大集搜索");
		MobclickAgent.onPause(this);
	}

	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(this, mess);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			this.finish();
			break;
		case R.id.search_btn:
			String key = keyEditText.getText().toString();
			req.Search(0,0,key);
			break;

		default:
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (keyEditText.getText().length() > 0) {
			searBtn.setEnabled(true);
		} else {
			searBtn.setEnabled(false);
			if(keyEditText.getText().length() ==0){
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void showQuestions(List<Question> questions,int what) {
		// TODO Auto-generated method stub
		if(questions!=null){
			if (what == 0) {
				allQuestions.clear();
			}
			
			for (Question question : questions) {
				allQuestions.add(question);
			}
			adapter.notifyDataSetChanged();
		}
		InputMethodManager im = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(findViewById(android.R.id.content)
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		pullToRefreshListView.onRefreshComplete();
	}

	
	
}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyQuestionsActivity extends Activity implements OnClickListener,HandMessage,OnCreateContextMenuListener {
	
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private QuestionAdpter adapter;
	private GFHandler<MyQuestionsActivity> handler = new GFHandler<MyQuestionsActivity>(this);
	private Question selectQuestion;
	private int status;
	private TextView titleTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myquestions);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		
		status = getIntent().getIntExtra("status", 0);
		titleTextView = (TextView)findViewById(R.id.title_txt);
		if(status==0){
			titleTextView.setText("我的问题");
		}
		else if(status==1){
			titleTextView.setText("我的拉拉呱");
		}
		else if(status==3){
			titleTextView.setText("我的作物相关问题");
		}
		else if(status==2){
			titleTextView.setText("我的种植分享");
		}
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadNewMyDate();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuestions.size() == 0) {
							return;
						}
						Question question = allQuestions.get(allQuestions
								.size() - 1);
						loadMoreMyData(question.getId());
					}

				});
		allQuestions = new ArrayList<Question>();
		adapter = new QuestionAdpter(allQuestions,this,this);
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);
		loadNewMyDate();
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(MyQuestionsActivity.this,
								QuestionActivity.class);
						it.putExtra("questionId", allQuestions
								.get(position - 1).getId());
						if(status==1||status==2){
							it.putExtra("status", status);
						}

						MyQuestionsActivity.this.startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
	}
	
	private void loadNewMyDate() {
		final String  uid= GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(status==0){
					jsonString = HttpUtil.getMyQuestionsString(uid);
				}
				else if(status==1){
					jsonString = HttpUtil.getMyGossipsString(uid);
				}
				else if(status==3){
					jsonString = HttpUtil.getAscQuestionsString(uid);
				}
				else{
					jsonString = HttpUtil.getMyPlantinfoString(uid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreMyData(final int qid) {
		final String  uid= GFUserDictionary.getUserId(getApplicationContext());
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(status==0){
					jsonString = HttpUtil.getMyQuestionsString(uid,qid);
				}
				else if(status==1){
					jsonString = HttpUtil.getMyGossipsString(uid,qid);
				}
				else if(status==3){
					jsonString = HttpUtil.getAscQuestionsString(uid,qid);
				}
				else{
					
					jsonString = HttpUtil.getMyPlantString(uid,qid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void delete(final int qid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(status==0||status==3){
					jsonString = HttpUtil.deleteQuestion(qid);
				}
				else if(status==1){
					jsonString = HttpUtil.deleteGossip(qid);
				}
				else{
					jsonString = HttpUtil.deletePlant(qid);
				}
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getApplicationContext());
		if(uid!=null){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Question question = allQuestions.get(info.position-1);
			if(question.getWriter().getId().equals(uid)){
				menu.add(0, 0, 0, "删除");
			} 
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo(); 
		selectQuestion = allQuestions.get(info.position-1);
		final Dialog dialog = new Dialog(MyQuestionsActivity.this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) MyQuestionsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog, null);
        dialog.setContentView(layout);
        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
        okBtn.setText("确定");
        okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				delete(selectQuestion.getId());
			}
		});
        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
        cancelButton.setText("取消");
        cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
        titleView.setText("提示");
        contentView.setText("确定要删除选中的提问吗？");
        dialog.show();
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			finish();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if(msg.what==0||msg.what==1){
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Question> questions = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Question>>() {
						}.getType());
				if (msg.what == 0) {
					allQuestions.clear();
				}
				if(questions==null || questions.size()==0){
					if(allQuestions.size()==0){
						String mess="";
						switch (status) {
						case 0:
							mess="您还没有提问过。";
							break;
						case 1:
							mess="您还没有发起过话题。";
							break;
						case 2:
							mess="您还没有分享过。";
							break;
						case 3:
							mess="没有与您作物相关的问题。";
							break;

						default:
							break;
						}
						GFToast.show(getApplicationContext(),mess);
					}				
				}
				for (Question question : questions) {
					allQuestions.add(question);
				}
				if(status==2){
					adapter.setStatus(1);
				}
				else{
					adapter.setStatus(0);
				}
				adapter.notifyDataSetChanged();
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			pullToRefreshListView.onRefreshComplete();
		}
		else if(msg.what==3){
			
			String str = msg.obj.toString();
			if(!str.isEmpty()){
				GFToast.show(getApplicationContext(),str);
			}
			else{
				allQuestions.remove(selectQuestion);
				adapter.notifyDataSetChanged();
			}
			
		}
	}

}

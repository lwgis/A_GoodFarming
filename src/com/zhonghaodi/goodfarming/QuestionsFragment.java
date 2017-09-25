package com.zhonghaodi.goodfarming;

import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.api.ShareContainer;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.req.FrmHomeReq;
import com.zhonghaodi.view.FrmHomeView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class QuestionsFragment extends Fragment implements FrmHomeView,OnClickListener {

	private PullToRefreshListView pullToRefreshListView;	
	private QuestionAdpter adapter;
	private FrmHomeReq req;
	private Question selectQuestion;
	private ShareContainer shareContainer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_questions, container, false);
		req = new FrmHomeReq(this, getActivity());
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						req.loadNewQuestion();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (getAllQuestions().size() == 0) {
							return;
						}
						
						Question question = getAllQuestions().get(getAllQuestions().size() - 1);
						req.loadMoreQuestion(question.getId());
						
					}

				});
		adapter = new QuestionAdpter(getAllQuestions(),getActivity(),QuestionsFragment.this,UILApplication.displayStatus);
		QuestionsFragment.this.pullToRefreshListView.getRefreshableView().setAdapter(adapter);
		
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(getActivity(),
								QuestionActivity.class);
						it.putExtra("questionId", getAllQuestions().get(position - 1).getId());
						getActivity().startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
		return view;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
		if(uid!=null){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			Question question = getAllQuestions().get(info.position-1);
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
		selectQuestion = getAllQuestions().get(info.position-1);
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				req.delete(selectQuestion.getId());
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
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(getActivity(), mess);
	}

	@Override
	public void onLoaded(int what, List<Question> questions) {
		// TODO Auto-generated method stub
		if(questions!=null){
			if (what == 0) {
				getAllQuestions().clear();
			}

			for (Question question : questions) {
				if(question.getWriter()!=null){
					getAllQuestions().add(question);
				}						
			}
			if(UILApplication.displayStatus==0 || UILApplication.displayStatus==3){
				adapter.setStatus(0);
			}
			else{
				adapter.setStatus(UILApplication.displayStatus);
			}
			adapter.notifyDataSetChanged();
			if (what == 0) {
				pullToRefreshListView.getRefreshableView().setSelection(0);
			}
		}
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	public void onDeleted() {
		// TODO Auto-generated method stub
		getAllQuestions().remove(selectQuestion);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onZan() {
		// TODO Auto-generated method stub
		
	}
	
	private List<Question> getAllQuestions(){
		return ((MainActivity)getActivity()).allQuestions;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.forward_layout:
			
				if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
					Intent intent2 = new Intent();
					intent2.setClass(getActivity(), LoginActivity.class);
					getActivity().startActivity(intent2);
					
				}
				else {				
					Question q = (Question)v.getTag();
					String folder;
					folder="question";
					shareContainer.shareQuestionWindow(q, folder);			
				}
			
			break;

		default:
			break;
		}
	}
	
	
	
	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}
	
}

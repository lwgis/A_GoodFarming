package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.PostResponse;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
public class HomeFragment extends Fragment implements HandMessage,OnClickListener,OnCreateContextMenuListener {
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private QuestionAdpter adapter;
	private GFHandler<HomeFragment> handler = new GFHandler<HomeFragment>(this);
	private int bAll = 0;
	private TextView allTextView;
	private TextView ascTextView;
	private TextView myTextView;
	private View messageView;
	private TextView countTv;
	private Question selectQuestion;
	private PopupWindow popupWindow;
	private View popView;
	private int page=0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		Button questionButton = (Button) view
				.findViewById(R.id.question_button);
		questionButton.setOnClickListener(this);
		popView = inflater.inflate(R.layout.popupwindow_question, container,
				false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(
				getActivity(), 180), DpTransform.dip2px(getActivity(), 150));
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		bAll = 0;
		Button newQueBtn = (Button)popView.findViewById(R.id.btn_question);
		newQueBtn.setOnClickListener(this);
		Button newGossipBtn = (Button)popView.findViewById(R.id.btn_gossip);
		newGossipBtn.setOnClickListener(this);
		Button newPlantInfoBtn = (Button)popView.findViewById(R.id.btn_plantinfo);
		newPlantInfoBtn.setOnClickListener(this);
		allTextView = (TextView)view.findViewById(R.id.all_text);
		allTextView.setOnClickListener(this);
		ascTextView = (TextView)view.findViewById(R.id.asc_text);
		ascTextView.setOnClickListener(this);
		myTextView = (TextView)view.findViewById(R.id.my_text);
		myTextView.setOnClickListener(this);
		messageView = view.findViewById(R.id.message_layout);
		messageView.setOnClickListener(this);
		countTv = (TextView) view.findViewById(R.id.count_text);		
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if(bAll==0)
							loadNewQuestion();
						else if(bAll==1){
							loadNewGossips();
						}
						else if(bAll==2){
							loadNewPlant();
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuestions.size() == 0) {
							return;
						}
						Question question = allQuestions.get(allQuestions
								.size() - 1);
						if(bAll==0)
							loadMoreQuestion(question.getId());
						else if(bAll==1){
							loadMoreGossips(question.getId());
						}
						else if(bAll==2){
							loadMorePlant(question.getId());
						}
					}

				});
		allQuestions = new ArrayList<Question>();
		adapter = new QuestionAdpter(allQuestions,getActivity(),HomeFragment.this);
		HomeFragment.this.pullToRefreshListView.getRefreshableView()
				.setAdapter(adapter);
		loadNewQuestion();
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(getActivity(),
								QuestionActivity.class);
						it.putExtra("questionId", allQuestions
								.get(position - 1).getId());
						if(bAll==1||bAll==2){
							it.putExtra("status", bAll);
						}
						getActivity().startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
		selectTextView(allTextView);
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("主页Fragment");
		setUnreadMessageCount();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("主页Fragment");
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
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


	private void loadNewQuestion() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuestionsString();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreQuestion(final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuestionsString(qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadNewGossips() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getGossipsString();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreGossips(final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getGossipsString(qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void loadNewPlant() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getPlant();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMorePlant(final int qid) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				String jsonString = HttpUtil.getMorePlant(qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 显示未读信息数
	 * @param count
	 */
	public void setUnreadMessageCount() {
		int count=0;
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0) {
				count+=conversation.getUnreadMsgCount();
			}
		}
		if (count == 0) {
			countTv.setVisibility(View.GONE);
		} else {
			countTv.setVisibility(View.VISIBLE);
			countTv.setText(String.valueOf(count));
		}
	}
	/**
	 * 显示未读信息数
	 * @param count
	 */
	public void setUnreadMessageCount1() {
		int count=1;

		if (count == 0) {
			countTv.setVisibility(View.GONE);
		} else {
			countTv.setVisibility(View.VISIBLE);
			countTv.setText(String.valueOf(count));
		}
	}
	
	private void delete(final int qid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString;
				if(bAll==0){
					jsonString = HttpUtil.deleteQuestion(qid);
				}
				else if(bAll==1){
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.question_button:
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
			} else {
				popupWindow.showAsDropDown(v,
						-DpTransform.dip2px(getActivity(), -50),
						DpTransform.dip2px(getActivity(), 0));
			}
			break;
		case R.id.message_layout:
			Intent it1 = new Intent();
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				it1.setClass(getActivity(), LoginActivity.class);
				
			}
			else {
				it1.setClass(getActivity(), MessagesActivity.class);			
			}
			getActivity().startActivity(it1);
			break;
		case R.id.title_txt:
			
			break;
			
		case R.id.all_text:
			selectTextView(v);
			loadNewQuestion();
			bAll = 0;
			break;
		case R.id.my_text:
			
			String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
			if(uid==null){
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent);
			}
			else{
				selectTextView(v);
				loadNewGossips();
				
				bAll = 1;
			}
			
			break;
		case R.id.asc_text:
			
			String uid1 = GFUserDictionary.getUserId(getActivity().getApplicationContext());
//			String cropids = GFUserDictionary.getCroids(getActivity().getApplicationContext());
			if(uid1==null){
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent);
			}
//			else if(cropids==null || cropids.isEmpty()){
//				Intent it2 = new Intent(getActivity(),
//						SelectCropActivity.class);
//				getActivity().startActivityForResult(it2, 100);
//			}
			else{
				selectTextView(v);
				loadNewPlant();
				bAll = 2;
			}
			break;
		case R.id.head_image:
//			if(GFUserDictionary.getUserId()==null){
//				GFToast.show("请您先登录！");
//				return;
//			}
//			User user = (User)v.getTag();
//			if(user.getLevelID()!=1){
//				Intent it2 = new Intent();
//				it2.setClass(getActivity(), ChatActivity.class);
//				it2.putExtra("userName", user.getPhone());
//				it2.putExtra("title", user.getAlias());
//				it2.putExtra("thumbnail", user.getThumbnail());
//				startActivity(it2);
//			}
			break;
		case R.id.btn_question:
			popupWindow.dismiss();
			Intent it = new Intent();
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				it.setClass(getActivity(), LoginActivity.class);
			}
			else {
				it.setClass(getActivity(), CreateQuestionActivity.class);
			}
			getActivity().startActivity(it);
			break;
		case R.id.btn_gossip:
			popupWindow.dismiss();
			Intent intent = new Intent();
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				intent.setClass(getActivity(), LoginActivity.class);
			}
			else {
				intent.setClass(getActivity(), CreateQuestionActivity.class);
				intent.putExtra("status", 1);
			}
			getActivity().startActivity(intent);
			break;
		case R.id.btn_plantinfo:
			popupWindow.dismiss();
			Intent intent1 = new Intent();
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				intent1.setClass(getActivity(), LoginActivity.class);
			}
			else {
				intent1.setClass(getActivity(), CreatePlantActivity.class);
				intent1.putExtra("status", 2);
			}
			getActivity().startActivity(intent1);
			break;
		case R.id.plantzan_layout:
			selectQuestion = (Question)v.getTag();
			final String uid2 = GFUserDictionary.getUserId(getActivity().getApplicationContext());
			if(selectQuestion.getWriter().getId().equals(uid2)){
				GFToast.show(getActivity(),"不能给自己的分享点赞。");
				return;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					NetResponse netResponse=HttpUtil.agreePlant(selectQuestion.getId(),uid2);
					Message msg = handler.obtainMessage();
					if(netResponse.getStatus()==1){
						msg.what = 9;
						msg.obj = netResponse.getResult();
					}
					else{
						msg.what = 0;
						msg.obj = netResponse.getMessage();
					}
					msg.sendToTarget();
				}
			}).start();
			break;
		default:
			break;
		}
	}
	
	public void selectTextView(View view){
		allTextView.setTextColor(Color.rgb(128, 128, 128));
		allTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		ascTextView.setTextColor(Color.rgb(128, 128, 128));
		ascTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		myTextView.setTextColor(Color.rgb(128, 128, 128));
		myTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
	}
	
	public void hidePopueWindow(){
	}
	

	@Override
	public void handleMessage(Message msg,Object object) {
			if(msg.what==0||msg.what==1){
				if (msg.obj != null) {
					Gson gson = new Gson();
					List<Question> questions = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<Question>>() {
							}.getType());
					if (msg.what == 0) {
						allQuestions.clear();
					}

					for (Question question : questions) {
						allQuestions.add(question);
					}
					if(bAll==2){
						adapter.setStatus(1);
					}
					else{
						adapter.setStatus(0);
					}
					adapter.notifyDataSetChanged();
				} else {
					GFToast.show(getActivity(), "连接服务器失败,请稍候再试!");
				}
				pullToRefreshListView.onRefreshComplete();
			}
			else if(msg.what==3){
				
				String str = msg.obj.toString();
				if(!str.isEmpty()){
					GFToast.show(getActivity().getApplicationContext(),str);
				}
				else{
					allQuestions.remove(selectQuestion);
					adapter.notifyDataSetChanged();
				}
				
			}
			else if(msg.what==9){
				if(msg.obj!=null){
					Gson gson2 = new Gson();
					String jString = (String) msg.obj;
					PostResponse reportResponse = gson2.fromJson(jString, PostResponse.class);
					if(reportResponse == null){
						GFToast.show(getActivity(),"点赞操作错误");
					}
					else{
						if(reportResponse.isResult()){
							selectQuestion.setAgree(selectQuestion.getAgree()+1);
							adapter.notifyDataSetChanged();
						}
						else{
							GFToast.show(getActivity(),reportResponse.getMessage());
						}
					}
				}
				else{
					GFToast.show(getActivity(),"点赞操作错误");
				}
			}
			else if(msg.what==-1){
				if(msg.obj!=null){
					GFToast.show(getActivity(),msg.obj.toString());
				}
			}
	
	}


}

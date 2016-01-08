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
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.GFUserDictionary;
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
	private ImageView messageIv;
	private View messageView;
	private TextView countTv;
	private Question selectQuestion;
	private PopupWindow popupWindow;
	private View popView;

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
				getActivity(), 180), DpTransform.dip2px(getActivity(), 100));
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		Button newQueBtn = (Button)popView.findViewById(R.id.btn_question);
		newQueBtn.setOnClickListener(this);
		Button newGossipBtn = (Button)popView.findViewById(R.id.btn_gossip);
		newGossipBtn.setOnClickListener(this);
		allTextView = (TextView)view.findViewById(R.id.all_text);
		allTextView.setOnClickListener(this);
		ascTextView = (TextView)view.findViewById(R.id.asc_text);
		ascTextView.setOnClickListener(this);
		myTextView = (TextView)view.findViewById(R.id.my_text);
		myTextView.setOnClickListener(this);
		messageView = view.findViewById(R.id.message_layout);
		messageView.setOnClickListener(this);
		messageIv = (ImageView) view.findViewById(R.id.message_image);
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
							loadNewDate();
						else if(bAll==1){
							loadNewGossips();
						}
						else if(bAll==2){
							loadNewAscDate();
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
							loadMoreData(question.getId());
						else if(bAll==1){
							loadMoreGossips(question.getId());
						}
						else if(bAll==2){
							loadMoreAscData(question.getId());
						}
					}

				});
		allQuestions = new ArrayList<Question>();
		adapter = new QuestionAdpter(allQuestions,getActivity(),HomeFragment.this);
		HomeFragment.this.pullToRefreshListView.getRefreshableView()
				.setAdapter(adapter);
		loadNewDate();
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
						if(bAll==1){
							it.putExtra("status", 1);
						}
						getActivity().startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
		selectTextView(allTextView);
		
		return view;
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


	private void loadNewDate() {
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

	private void loadMoreData(final int qid) {
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
	
	private void loadNewAscDate() {
		final String  uid= GFUserDictionary.getUserId(getActivity().getApplicationContext());
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAscQuestionsString(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreAscData(final int qid) {
		final String  uid= GFUserDictionary.getUserId(getActivity().getApplicationContext());
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAscQuestionsString(uid,qid);
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
				if(bAll!=1){
					jsonString = HttpUtil.deleteQuestion(qid);
				}
				else{
					jsonString = HttpUtil.deleteGossip(qid);
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
			loadNewDate();
			bAll = 0;
			break;
		case R.id.my_text:
			selectTextView(v);
			String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
			if(uid==null){
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent);
			}
			else{
				loadNewGossips();
				
				bAll = 1;
			}
			
			break;
		case R.id.asc_text:
			selectTextView(v);
			String uid1 = GFUserDictionary.getUserId(getActivity().getApplicationContext());
			String cropids = GFUserDictionary.getCroids(getActivity().getApplicationContext());
			if(uid1==null){
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				getActivity().startActivity(intent);
			}
			else if(cropids==null || cropids.isEmpty()){
//				popupDialog();
				Intent it2 = new Intent(getActivity(),
						SelectCropActivity.class);
				getActivity().startActivityForResult(it2, 100);
			}
			else{
				loadNewAscDate();
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
			final HomeFragment fragment =(HomeFragment)object;
			if(msg.what==0||msg.what==1){
				if (msg.obj != null) {
					Gson gson = new Gson();
					List<Question> questions = gson.fromJson(msg.obj.toString(),
							new TypeToken<List<Question>>() {
							}.getType());
					if (msg.what == 0) {
						fragment.allQuestions.clear();
					}

					for (Question question : questions) {
						fragment.allQuestions.add(question);
					}
					fragment.adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(fragment.getActivity(), "连接服务器失败,请稍候再试!",
							Toast.LENGTH_SHORT).show();
				}
				fragment.pullToRefreshListView.onRefreshComplete();
			}
			else if(msg.what==3){
				
				String str = msg.obj.toString();
				if(!str.isEmpty()){
					GFToast.show(getActivity().getApplicationContext(),str);
				}
				else{
					allQuestions.remove(selectQuestion);
					fragment.adapter.notifyDataSetChanged();
				}
				
			}
	
	}


}

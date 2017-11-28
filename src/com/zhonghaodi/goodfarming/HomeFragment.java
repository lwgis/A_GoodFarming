package com.zhonghaodi.goodfarming;

import java.util.Hashtable;
import java.util.List;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.QuestionAdpter;
import com.zhonghaodi.api.DiseaseListView;
import com.zhonghaodi.api.FairListView;
import com.zhonghaodi.api.ShareContainer;
import com.zhonghaodi.customui.DiseasePopupWindow;
import com.zhonghaodi.customui.DpTransform;
import com.zhonghaodi.customui.FairPopupWindow;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SaleHeaderView;
import com.zhonghaodi.customui.SaleHeaderView.MyHeaderView;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.model.SpinnerDto;
import com.zhonghaodi.model.User;
import com.zhonghaodi.req.FrmHomeReq;
import com.zhonghaodi.utils.PublicHelper;
import com.zhonghaodi.view.FrmHomeView;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class HomeFragment extends Fragment implements FrmHomeView,
			OnClickListener,OnCreateContextMenuListener,DiseaseListView,MyHeaderView {
	private PullToRefreshListView pullToRefreshListView;
	
	private QuestionAdpter adapter;
	private TextView titleView;
	private TextView diseaseTextView;
	private TextView plantTextView;
	private TextView gossipTextView;
	private TextView forumTextView;
	private FrameLayout fragmentContainer;
	private ForumFragment forumFragment;
	private MyTextButton exAreaBtn;
	private View messageView;
	private TextView countTv;
	private Question selectQuestion;
	private PopupWindow popupWindow;
	private View popView;
	private DiseasePopupWindow diseasePopupWindow;
	private SaleHeaderView saleHeaderView;
	private ShareContainer shareContainer;
	private FrmHomeReq req;
	private String key="";
	private String deal = "";
	public View curView;
	public int page=0;
	public double distance = 50000;
	
	

	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		req = new FrmHomeReq(this, getActivity());
		ImageView questionButton = (ImageView) view
				.findViewById(R.id.question_button);
		questionButton.setOnClickListener(this);
		popView = inflater.inflate(R.layout.popupwindow_question, container,
				false);
		popupWindow = new PopupWindow(popView, DpTransform.dip2px(
				getActivity(), 180), DpTransform.dip2px(getActivity(), 150));
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		Button newQueBtn = (Button)popView.findViewById(R.id.btn_question);
		newQueBtn.setOnClickListener(this);
		Button newGossipBtn = (Button)popView.findViewById(R.id.btn_gossip);
		newGossipBtn.setOnClickListener(this);
		Button newPlantBtn = (Button)popView.findViewById(R.id.btn_plant);
		newPlantBtn.setOnClickListener(this);
		titleView = (TextView)view.findViewById(R.id.title_txt);
		Drawable drawable = getResources().getDrawable(R.drawable.location_white);
		drawable.setBounds(0, 0, PublicHelper.dip2px(getActivity(), 18), PublicHelper.dip2px(getActivity(), 18));
		titleView.setCompoundDrawables(drawable, null, null, null);
		titleView.setOnClickListener(this);
		diseaseTextView = (TextView)view.findViewById(R.id.disease_text);
		diseaseTextView.setOnClickListener(this);
		plantTextView = (TextView)view.findViewById(R.id.plant_text);
		plantTextView.setOnClickListener(this);
		gossipTextView = (TextView)view.findViewById(R.id.gossip_text);
		gossipTextView.setOnClickListener(this);
		forumTextView = (TextView)view.findViewById(R.id.forum_text);
		forumTextView.setOnClickListener(this);
		fragmentContainer = (FrameLayout)view.findViewById(R.id.fragment_container);
		messageView = view.findViewById(R.id.message_layout);
		messageView.setOnClickListener(this);
		countTv = (TextView) view.findViewById(R.id.count_text);
		exAreaBtn = (MyTextButton)view.findViewById(R.id.exarea_button);
		exAreaBtn.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if(UILApplication.displayStatus==0)
							req.loadNewQuestion();
						else if(UILApplication.displayStatus==1){
							req.loadNewGossips();
						}
						else if(UILApplication.displayStatus==2){
//							req.loadNewPlant(UILApplication.fairStatus,key,deal);
							if(UILApplication.x==118.798632 && UILApplication.y==36.858719){
								GFToast.show(getActivity(), "定位失败，将使用寿光默认位置获取数据！");
							}
							page=0;
							req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (getAllQuestions().size() == 0) {
							return;
						}
						Question question = getAllQuestions().get(getAllQuestions().size() - 1);
						if(UILApplication.displayStatus==0)
							req.loadMoreQuestion(question.getId());
						else if(UILApplication.displayStatus==1){
							req.loadMoreGossips(question.getId());
						}
						else if(UILApplication.displayStatus==2){
//							req.loadMorePlant(question.getId(),UILApplication.fairStatus,key,deal);
							int k =getAllQuestions().size()%20;
							if(k==0){
								page = getAllQuestions().size()/20;
								req.searchMorePlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
							}
							else{
								req.threadSleep();
							}
							
						}
					}

				});
		adapter = new QuestionAdpter(getAllQuestions(),getActivity(),HomeFragment.this,UILApplication.displayStatus);
		HomeFragment.this.pullToRefreshListView.getRefreshableView()
				.setAdapter(adapter);	
		this.pullToRefreshListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent it = new Intent(getActivity(),
								QuestionActivity.class);
						int qid;
						if(UILApplication.displayStatus==2){
							qid = getAllQuestions().get(position - 2).getId();
						}
						else{
							qid = getAllQuestions().get(position - 1).getId();
						}
						it.putExtra("questionId", qid);
						if(UILApplication.displayStatus==1 || UILApplication.displayStatus==2){
							it.putExtra("status", UILApplication.displayStatus);
						}
						getActivity().startActivity(it);
					}
				});
		this.pullToRefreshListView.getRefreshableView().setOnCreateContextMenuListener(this);
		saleHeaderView = new SaleHeaderView(getActivity(), this);
		diseasePopupWindow = new DiseasePopupWindow(this, UILApplication.diseaseStatus, getActivity());
		initArea();	
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("主页Fragment");
		setUnreadMessageCount();
		displayBack();
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
	public void changeDiseaseStatus(SpinnerDto spinnerDto) {
		// TODO Auto-generated method stub
		if(spinnerDto.getId()==1){
			String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
			if(uid==null){
				diseasePopupWindow.reset();
				Intent intent = new Intent(getActivity(),SignActivity.class);
				getActivity().startActivity(intent);
				return;
			}
			String croids = GFUserDictionary.getCroids(getActivity());
			if(TextUtils.isEmpty(croids)){
				diseasePopupWindow.reset();
				GFToast.show(getActivity(), "请设置关注的作物");
				Intent it = new Intent(getActivity(), InformationActivity.class);
				Bundle bundle = new Bundle();
				User user = getUser();
				bundle.putSerializable("user", user);
				it.putExtras(bundle);
				getActivity().startActivity(it);
				return;
			}
		}
		UILApplication.diseaseStatus = spinnerDto.getId();
		diseaseTextView.setText(spinnerDto.getName());		
		req.loadNewQuestion();		
	}
	
	
	public void resetArea(){
		req.zonestr="";
		int cityid = GFAreaUtil.getCity(getActivity());
		String name = GFAreaUtil.getCityName(getActivity());
		if(cityid!=0){
			req.zonestr+=cityid;
			
		}
		if(!TextUtils.isEmpty(name)){
			titleView.setText(name);
		}
		if(UILApplication.displayStatus==1){
			req.loadNewGossips();
		}	
	}
	
	public void initArea(){
		req.zonestr="";
		int cityid = GFAreaUtil.getCity(getActivity());
		String name = GFAreaUtil.getCityName(getActivity());
		if(cityid!=0){
			req.zonestr+=cityid;
		}
		if(!TextUtils.isEmpty(name)){
			titleView.setText(name);
		}
		if(UILApplication.displayStatus==0){
			if(getAllQuestions().size()==0){
				req.loadNewQuestion();
			}
			selectTextView(diseaseTextView);
			if(UILApplication.diseaseStatus==0){
				diseaseTextView.setText("问答");
			}
			else{
				diseaseTextView.setText("我的作物");
			}
		}			
		else if(UILApplication.displayStatus==1){
			if(getAllQuestions().size()==0){
				req.loadNewGossips();
			}
			
			selectTextView(gossipTextView);
		}
		else if(UILApplication.displayStatus==2){
			if(getAllQuestions().size()==0){
//				req.loadNewPlant(UILApplication.fairStatus,this.key,deal);
				distance=50000;
				req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
			}
			
			selectTextView(plantTextView);
		}

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
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_txt:
			Intent cityIntent = new Intent(getActivity(), ProvinceActivity.class);
			getActivity().startActivityForResult(cityIntent, PublicHelper.CITY_REQUEST_CODE);
			break;
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
				it1.setClass(getActivity(), SignActivity.class);
				
			}
			else {
				it1.setClass(getActivity(), MessagesActivity.class);			
			}
			getActivity().startActivity(it1);
			break;
			
		case R.id.disease_text:
			fragmentContainer.setVisibility(View.GONE);
			if(UILApplication.displayStatus!=0){
				selectTextView(v);
				req.loadNewQuestion();
				UILApplication.displayStatus = 0;
			}
			else{
				if(diseasePopupWindow.isShowing()){
					diseasePopupWindow.dismiss();
				}
				else{

					diseasePopupWindow.showAsDropDown((View)v.getParent(), 0, 0);
				}
			}
			break;
		case R.id.gossip_text:
			fragmentContainer.setVisibility(View.GONE);
			if(UILApplication.displayStatus!=1){
				String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
				selectTextView(v);
				req.loadNewGossips();					
				UILApplication.displayStatus = 1;
			}
			break;
		case R.id.plant_text:
			fragmentContainer.setVisibility(View.GONE);
			if(UILApplication.displayStatus!=2){
				selectTextView(v);
//				req.loadNewPlant(UILApplication.fairStatus,this.key,deal);
				if(UILApplication.x==118.798632 && UILApplication.y==36.858719){
					GFToast.show(getActivity(), "定位失败，将使用寿光默认位置获取数据！");
				}
				distance=50000;
				page=0;
				req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
				UILApplication.displayStatus = 2;
			}
			break;
		case R.id.forum_text:
			if(UILApplication.displayStatus != 3){
				UILApplication.displayStatus = 3;
				selectTextView(v);
				fragmentContainer.setVisibility(View.VISIBLE);
				FragmentTransaction transction = getChildFragmentManager().beginTransaction();
				if (forumFragment == null) {
					forumFragment = new ForumFragment();				
					transction.add(R.id.fragment_container, forumFragment);
				}
				else{
					transction.replace(R.id.fragment_container, forumFragment);
				}
				transction.commit();
			}
			break;

		case R.id.btn_question:
			popupWindow.dismiss();
			Intent it = new Intent();
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				it.setClass(getActivity(), SignActivity.class);
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
				intent.setClass(getActivity(), SignActivity.class);
			}
			else {
				intent.setClass(getActivity(), CreateQuestionActivity.class);
				intent.putExtra("status", 1);
			}
			getActivity().startActivity(intent);
			break;
		case R.id.btn_plant:
			popupWindow.dismiss();			
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				Intent intent1 = new Intent();
				intent1.setClass(getActivity(), SignActivity.class);
				getActivity().startActivity(intent1);
			}
			else {
				String uid1 = GFUserDictionary.getUserId(getActivity().getApplicationContext());
				req.checkPublish(uid1);				
			}
			
			break;
		case R.id.plantzan_layout:
			selectQuestion = (Question)v.getTag();
			final String uid2 = GFUserDictionary.getUserId(getActivity().getApplicationContext());
			if(selectQuestion.getWriter().getId().equals(uid2)){
				GFToast.show(getActivity(),"不能给自己的分享点赞。");
				return;
			}
			req.dianZan(selectQuestion,uid2);
			break;
		case R.id.forward_layout:
			
			if (GFUserDictionary.getUserId(getActivity().getApplicationContext())==null) {
				Intent intent2 = new Intent();
				intent2.setClass(getActivity(), SignActivity.class);
				getActivity().startActivity(intent2);
				
			}
			else {				
				Question q = (Question)v.getTag();
				String folder;
				if(UILApplication.displayStatus==0){
					folder="question";
				}else if(UILApplication.displayStatus==1){
					folder="gossip";
				}else{
					folder="plantinfo";
				}
				shareContainer.shareQuestionWindow(q, folder);			
			}
			
			break;
		case R.id.exarea_button:
			if(distance==50000){
				distance=100000;
				GFToast.show(getActivity(), "100公里");
				page=0;
				req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
			}else if(distance==100000){
				distance=2000000;
				GFToast.show(getActivity(), "2000公里啦");
				page=0;
				req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
			}
			else{
				GFToast.show(getActivity(), "不能再加啦，再加快出国了");
			}
			break;
		default:
			break;
		}
	}
	
	public void displayBack(){
		if(curView!=null && curView.getId()==R.id.forum_text){
			UILApplication.displayStatus = 3;
			selectTextView(forumTextView);
			fragmentContainer.setVisibility(View.VISIBLE);
			FragmentTransaction transction = getChildFragmentManager().beginTransaction();
			transction.replace(R.id.fragment_container, forumFragment);
			transction.commit();
		}
	}
	
	public void selectTextView(View view){
		Drawable drawable = getResources().getDrawable(R.drawable.dropdown);
		drawable.setBounds(0, 0, PublicHelper.dip2px(getActivity(), 13), PublicHelper.dip2px(getActivity(), 13)); 
		diseaseTextView.setTextColor(Color.rgb(128, 128, 128));
		diseaseTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar_null));
		diseaseTextView.setCompoundDrawables(null, null, drawable, null);
		plantTextView.setTextColor(Color.rgb(128, 128, 128));
		plantTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar_null));
		gossipTextView.setTextColor(Color.rgb(128, 128, 128));
		gossipTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar_null));
		forumTextView.setTextColor(Color.rgb(128, 128, 128));
		forumTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar_null));
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
		if(selectTextView.getId()==R.id.disease_text){
			Drawable drawable1 = getResources().getDrawable(R.drawable.dropdown_s);
			drawable1.setBounds(0, 0, PublicHelper.dip2px(getActivity(), 13), PublicHelper.dip2px(getActivity(), 13)); 
			selectTextView.setCompoundDrawables(null, null, drawable1, null);
		}	
		
		if(selectTextView.getId()==R.id.plant_text){
			pullToRefreshListView.getRefreshableView().addHeaderView(saleHeaderView.mainView);
		}
		else{
			pullToRefreshListView.getRefreshableView().removeHeaderView(saleHeaderView.mainView);
		}
		curView = view;
	}
	
	private List<Question> getAllQuestions(){
		return ((MainActivity)getActivity()).allQuestions;
	}
	
	private User getUser(){
		return UILApplication.user;
	}

	

	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(getActivity(),mess);
	}

	@Override
	public void onLoaded(int what, List<Question> questions) {
		// TODO Auto-generated method stub
		if(questions!=null){
			try {
				if (what == 0) {
					getAllQuestions().clear();
				}
				for (Question question : questions) {
					if (question.getWriter() != null) {
						getAllQuestions().add(question);
					}
				}
				if (UILApplication.displayStatus == 0 || UILApplication.displayStatus == 3) {
					adapter.setStatus(0);
				} else {
					adapter.setStatus(UILApplication.displayStatus);
				}
				adapter.notifyDataSetChanged();
				if (what == 0) {
					pullToRefreshListView.getRefreshableView().setSelection(0);
				} 
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		pullToRefreshListView.onRefreshComplete();
//		if(getAllQuestions().size()<5 && UILApplication.displayStatus==2 
//				&& distance<2000000){
//			exAreaBtn.setVisibility(View.VISIBLE);
//		}
//		else{
//			exAreaBtn.setVisibility(View.GONE);
//		}
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
		selectQuestion.setAgree(selectQuestion.getAgree()+1);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void switchButton(String type) {
		// TODO Auto-generated method stub
		deal = type;
//		req.loadNewPlant(UILApplication.fairStatus, key, deal);
		distance=50000;
		page=0;
		req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
	}

	@Override
	public void search(String key) {
		// TODO Auto-generated method stub
		
//		req.loadNewPlant(UILApplication.fairStatus,key,deal);
		distance=50000;
		page=0;
		req.searchNewPlant(UILApplication.x, UILApplication.y, distance, key, page,deal);
	}

	@Override
	public void textChange(String key) {
		// TODO Auto-generated method stub
		this.key = key;
	}

	@Override
	public void popNewPlant() {
		// TODO Auto-generated method stub
		Intent intent1 = new Intent();
		intent1.setClass(getActivity(), CreatePlantActivity.class);
		getActivity().startActivity(intent1);
	}

	@Override
	public void onlistpullstop() {
		// TODO Auto-generated method stub
		pullToRefreshListView.onRefreshComplete();
	}
}

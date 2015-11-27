package com.zhonghaodi.goodfarming;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderDisease;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.customui.MyEditText;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.customui.SolutionHolder;
import com.zhonghaodi.model.Disease;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.Solution;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiseaseActivity extends Activity implements HandMessage,OnClickListener,OnItemClickListener {
	private PullToRefreshListView pullToRefreshListView;
	private TextView titleTv;
	private int diseaseId;
	private Disease disease;
	private DiseaseAdapter adapter;
	private LinearLayout sendLayout;
	private MyEditText mzEditText;
	private MyTextButton sendButton;
	private GFHandler<DiseaseActivity> handler = new GFHandler<DiseaseActivity>(
			this);
	private Solution selectSolution;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		adapter = new DiseaseAdapter();
		super.setContentView(R.layout.activity_disease);
		titleTv = (TextView) findViewById(R.id.title_text);
		sendLayout = (LinearLayout)findViewById(R.id.sendlayout);
		mzEditText = (MyEditText)findViewById(R.id.chat_edit);
		sendButton = (MyTextButton)findViewById(R.id.send_meassage_button);
		sendButton.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setAdapter(adapter);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if(getIntent().hasExtra("diseaseId")){
			diseaseId = getIntent().getIntExtra("diseaseId", 0);
		}
		else{
			diseaseId=0;
		}
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
					}
				});
		pullToRefreshListView.setOnItemClickListener(this);
		registerForContextMenu(pullToRefreshListView.getRefreshableView());
		loadData();
		
	}

	protected void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String uid = GFUserDictionary.getUserId();
					String jsonString = HttpUtil.getDisease(diseaseId,uid);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = jsonString;
					msg.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = "获取病虫害信息失败";
					msg.sendToTarget();
				}
			}
		}).start();
	}
	
	private void sendText(final String content){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Solution solution = new Solution();
					solution.setContent(content);
					User user = new User();
					user.setId(GFUserDictionary.getUserId());
					solution.setWriter(user);
					HttpUtil.sendSolution(solution, disease.getId());
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.sendToTarget();
					user = null;
					solution = null;
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = "发送失败";
					msg.sendToTarget();
				}
			}
		}).start();
	}
	
	private void delete(final int did,final int sid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deleteSolution(did,sid);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	private void zan(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String uid = GFUserDictionary.getUserId();
				NetResponse netResponse = HttpUtil.zanSolution(disease.getId(), selectSolution.getId(), uid);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 4;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = 0;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
			}
		}).start();
	}
	private void cancelzan(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String uid = GFUserDictionary.getUserId();
				NetResponse netResponse = HttpUtil.cancelZanSolution(disease.getId(), selectSolution.getId(), uid);
				Message msg = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg.what = 4;
					msg.obj = netResponse.getResult();
				}
				else{
					msg.what = 0;
					msg.obj = netResponse.getMessage();
				}
				msg.sendToTarget();
			}
		}).start();
	}

	class DiseaseAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (disease == null) {
				return 0;
			}
			if (disease.getSolutions() == null
					|| disease.getSolutions().size() == 0) {
				return 1;
			}
			return disease.getSolutions().size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderDisease holderDisease;
			SolutionHolder holderSolution;
			if (convertView == null) {
				switch (getItemViewType(position)) {
				case 0:
					convertView = LayoutInflater.from(DiseaseActivity.this)
							.inflate(R.layout.cell_disease, parent, false);
					holderDisease = new HolderDisease(convertView);
					convertView.setTag(holderDisease);
					break;
				case 1:
					convertView = LayoutInflater.from(DiseaseActivity.this)
							.inflate(R.layout.cell_solution, parent, false);
					holderSolution = new SolutionHolder(convertView);
					convertView.setTag(holderSolution);
					break;
				default:
					break;
				}
			}
			switch (getItemViewType(position)) {
			case 0:
				holderDisease = (HolderDisease) convertView.getTag();
				if (disease.getThumbnail() != null) {
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"diseases/small/"
									+ disease.getThumbnail(),
							holderDisease.headIv, ImageOptions.options);
					holderDisease.headIv.setImages(disease.getAttachments(),
							"diseases");
					holderDisease.countTv.setText("共"
							+ String.valueOf(disease.getAttachments().size())
							+ "图片");
				}
				String des = disease.getDescription().trim();
				holderDisease.contentTv.setText(des);
				holderDisease.mzButton.setOnClickListener(DiseaseActivity.this);
				break;
			case 1:
				holderSolution=(SolutionHolder)convertView.getTag();
				if (disease.getSolutions().get(position-1).getWriter().getThumbnail()!=null) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"users/small/"+disease.getSolutions().get(position-1).getWriter().getThumbnail(), holderSolution.headIv, ImageOptions.optionsNoPlaceholder);
				}
				if(position==1){
					holderSolution.solcountTv.setVisibility(View.VISIBLE);
					holderSolution.solcountTv.setText(disease.getSolutions().size()+"则相关妙招");
				}
				else{
					holderSolution.solcountTv.setVisibility(View.GONE);
				}
				holderSolution.nameTv.setText(disease.getSolutions().get(position-1).getWriter().getAlias());
				holderSolution.timeTv.setText(disease.getSolutions().get(position-1).getTime());
				holderSolution.contentTv.setText(disease.getSolutions().get(position-1).getContent());
				holderSolution.zancountTv.setText(String.valueOf(disease.getSolutions().get(position-1).getZan()));
				if(disease.getSolutions().get(position-1).isLiked()){
					holderSolution.zanIv.setSelected(true);
				}
				else{
					holderSolution.zanIv.setSelected(false);
				}
				holderSolution.zanLayout.setTag(disease.getSolutions().get(position-1));
				holderSolution.zanLayout.setOnClickListener(DiseaseActivity.this);
				holderSolution.commentTv.setText(String.valueOf(disease.getSolutions().get(position-1).getCommentCount()));
//				holderSolution.commentLayout.setTag(disease.getSolutions().get(position-1));
//				holderSolution.commentLayout.setOnClickListener(DiseaseActivity.this);
			default:
				break;
			}
			return convertView;
		}

	}
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(info.position>1){
			String uid = GFUserDictionary.getUserId();
			if(uid!=null){
				
				Solution solution = disease.getSolutions().get(info.position-2);
				if(solution.getWriter().getId().equals(uid)){
					menu.add(0, 0, 0, "删除");
				} 
				solution=null;
			}
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo();
		selectSolution = disease.getSolutions().get(info.position-2);
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
        //设置它的ContentView
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				
				delete(disease.getId(),selectSolution.getId());
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
        contentView.setText("确定要删除选中的评论吗？");
        dialog.show();
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.miaozhao_button:
			if(GFUserDictionary.getUserId()==null){
				GFToast.show("请您先登录！");
				return;
			}
			sendLayout.setVisibility(View.VISIBLE);
			mzEditText.setFocusable(true);
			mzEditText.setFocusableInTouchMode(true);
			mzEditText.requestFocus();
			mzEditText.findFocus();
			InputMethodManager inputManager =  
		               (InputMethodManager)mzEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		           inputManager.showSoftInput(mzEditText, 0);  
			break;
		case R.id.send_meassage_button:
			if(GFUserDictionary.getUserId()==null){
				GFToast.show("请您先登录！");
				return;
			}
			if(mzEditText.getText().toString().trim().isEmpty()){
				return;
			}
			sendText(mzEditText.getText().toString());
			mzEditText.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
			sendLayout.setVisibility(View.GONE);
			break;
		case R.id.commentLayout:
//			Solution s = (Solution)v.getTag();
//			Intent intent = new Intent(DiseaseActivity.this, SolutionActivity.class);
//			intent.putExtra("solution", s);
//			intent.putExtra("did", disease.getId());
//			intent.putExtra("dname", disease.getName());
//			startActivity(intent);
			break;
		case R.id.zan_layout:
			if(GFUserDictionary.getUserId()==null){
				GFToast.show("请您先登录！");
				return;
			}
			selectSolution = (Solution)v.getTag();
			if(selectSolution.isLiked()){
				cancelzan();
			}
			else{
				zan();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(position<2){
			return;
		}
		Solution s = disease.getSolutions().get(position-2);
		Intent intent = new Intent(DiseaseActivity.this, SolutionActivity.class);
		intent.putExtra("solution", s);
		intent.putExtra("did", disease.getId());
		intent.putExtra("dname", disease.getName());
		startActivity(intent);
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		switch (msg.what) {
		case 0:
			if(msg.obj!=null){
				GFToast.show(msg.obj.toString());
			}
			break;
		case 1:
			if (msg.obj != null) {
				DiseaseActivity activity = (DiseaseActivity) object;
				Gson gson = new Gson();
				try {
					activity.disease = gson.fromJson(msg.obj.toString(),
							Disease.class);
					titleTv.setText(disease.getName());
					adapter.notifyDataSetChanged();
					pullToRefreshListView.onRefreshComplete();
				} catch (JsonSyntaxException e) {
					pullToRefreshListView.onRefreshComplete();
					Toast.makeText(this, "获取病虫害信息失败", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			} else {
				Toast.makeText(this, "获取病虫害信息失败", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		case 2:
			if(msg.obj==null){
				
				loadData();
				GFToast.show("添加成功");
			}
			else{
				GFToast.show("操作失败");
			}
			break;
		case 3:
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				GFToast.show(strerr);
			}
			else{
				disease.getSolutions().remove(selectSolution);
				adapter.notifyDataSetChanged();
			}
			break;
		case 4:
			if(msg.obj==null){
				GFToast.show("操作失败");
			}
			else{
				loadData();
			}
			break;

		default:
			break;
		}
		
	}

}

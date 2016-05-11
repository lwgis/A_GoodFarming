package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Prescription;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
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
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PrescriptionsActivity extends Activity implements OnClickListener,OnItemClickListener,HandMessage {
	
	private Button addButton;
	private PullToRefreshListView listView;
	private List<Prescription> prescriptions;
	private PrescriptionAdapter adapter;
	private GFHandler<PrescriptionsActivity> handler = new GFHandler<PrescriptionsActivity>(this);
	private String uid;
	private Prescription selePrescription;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prescriptions);
		MobclickAgent.openActivityDurationTrack(false);
		addButton = (Button)findViewById(R.id.add_button);
		addButton.setOnClickListener(this);
		Button cancelButton = (Button)findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);
		listView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		listView.setMode(Mode.DISABLED);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						
					}

				});
		prescriptions = new ArrayList<Prescription>();
		adapter = new PrescriptionAdapter();
		listView.getRefreshableView().setAdapter(adapter);
		listView.setOnItemClickListener(this);
		registerForContextMenu(listView.getRefreshableView());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("我的处方");
		MobclickAgent.onResume(this);
		loadData();
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("我的处方");
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.add(0, 0, 0, "删除");
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                .getMenuInfo();
		selePrescription = prescriptions.get(info.position-1);
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
				
				delete(selePrescription.getId());
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
        contentView.setText("确定要删除选中的处方吗？");
        dialog.show();
		 
		return super.onContextItemSelected(item);
	}

	public void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				uid=GFUserDictionary.getUserId(getApplicationContext());
				String jsonString = HttpUtil.getPrescriptions(uid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();

			}
		}).start();
	}
	
	private void delete(final int pid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.deletePrescription(pid);
				Message msg = handler.obtainMessage();
				msg.what = 3;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}
	
	class PrescriptionHolder{
		public TextView titleTextView;
		public TextView contentTextView;
		public TextView editButton;
		public PrescriptionHolder(View view){
			titleTextView = (TextView)view.findViewById(R.id.name_text);
			contentTextView = (TextView)view.findViewById(R.id.content_text);
			editButton = (TextView)view.findViewById(R.id.edit_text);
		}
	}
	
	class PrescriptionAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return prescriptions.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return prescriptions.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			PrescriptionHolder prescriptionHolder;
			if(convertView == null){
				convertView = LayoutInflater.from(PrescriptionsActivity.this)
						.inflate(R.layout.cell_prescription, parent, false);
				prescriptionHolder = new PrescriptionHolder(convertView);
				convertView.setTag(prescriptionHolder);
			}
			prescriptionHolder = (PrescriptionHolder)convertView.getTag();
			Prescription prescription = prescriptions.get(position);
			prescriptionHolder.titleTextView.setText(prescription.getTitle());
			prescriptionHolder.contentTextView.setText(prescription.getContent());
			prescriptionHolder.editButton.setTag(prescription);
			prescriptionHolder.editButton.setOnClickListener(PrescriptionsActivity.this);
			
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Prescription prescription = prescriptions.get(position-1);
		Intent it = getIntent();
		it.putExtra("content", prescription.getContent());
		setResult(RESULT_OK, it);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_button:
			Intent intent = new Intent(this, PrescriptionEditActivity.class);
			startActivity(intent);
			break;
		case R.id.cancel_button:
			finish();
			break;
		case R.id.edit_text:
			Prescription prescription = (Prescription)v.getTag();
			Intent intent2 = new Intent(this,PrescriptionEditActivity.class);
			intent2.putExtra("id", prescription.getId());
			intent2.putExtra("title", prescription.getTitle());
			intent2.putExtra("content", prescription.getContent());
			startActivity(intent2);
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
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Prescription> pres = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Prescription>>() {
						}.getType());
				prescriptions.clear();
				if(pres==null || pres.size()==0){
					GFToast.show(getApplicationContext(), "您还没有保存处方，可在右上角添加。");
					return;
				}
				for (Prescription prescription : pres) {
					prescriptions.add(prescription);
				}
				adapter.notifyDataSetChanged();
				
			} else {
				Toast.makeText(this, "连接服务器失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
			}
			listView.onRefreshComplete();
			break;
		case 3:
			String strerr = msg.obj.toString();
			if(!strerr.isEmpty()){
				GFToast.show(getApplicationContext(),strerr);
			}
			else{
				prescriptions.remove(selePrescription);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

}

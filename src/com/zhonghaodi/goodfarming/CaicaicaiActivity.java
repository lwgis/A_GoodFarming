package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.CaicaicaiAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.req.CaicaicaiReq;
import com.zhonghaodi.view.CaicaicaiView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CaicaicaiActivity extends Activity implements OnClickListener,OnItemClickListener,CaicaicaiView {
	
	private PullToRefreshListView caiListView;
	private List<Caicaicai> caiList;
	private CaicaicaiReq req;
	private CaicaicaiAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caicaicai);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		caiListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		caiListView.setOnItemClickListener(this);
		caiListView.setMode(Mode.BOTH);
		caiListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				req.loadCaicaicai(0);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if(caiList.size()==0){
					return;
				}
				Caicaicai caicaicai = caiList.get(caiList.size()-1);
				req.loadCaicaicai(caicaicai.getId());
			}

		});
		caiList = new ArrayList<Caicaicai>();
		adapter = new CaicaicaiAdapter(caiList, this);
		caiListView.getRefreshableView().setAdapter(adapter);
		req = new CaicaicaiReq(this);
		req.loadCaicaicai(0);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("猜农资");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("猜农资");
		MobclickAgent.onPause(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent it = new Intent(this,CaiActivity.class);
		it.putExtra("caiid", caiList.get(position - 1).getId());
		startActivity(it);
	}
	@Override
	public void showMessage(String message) {
		// TODO Auto-generated method stub
		GFToast.show(this, message);
	}
	@Override
	public void displayCaicaicai(List<Caicaicai> caicaicais, boolean isAdd) {
		// TODO Auto-generated method stub
		if(!isAdd){
			caiList.clear();
		}
		if(caicaicais!=null && caicaicais.size()>0){
			for (Iterator iterator = caicaicais.iterator(); iterator.hasNext();) {
				Caicaicai caicaicai = (Caicaicai) iterator.next();
				caiList.add(caicaicai);
			}
		}
		adapter.notifyDataSetChanged();
	}
	@Override
	public void refreshComplete() {
		// TODO Auto-generated method stub
		caiListView.onRefreshComplete();
	}


}

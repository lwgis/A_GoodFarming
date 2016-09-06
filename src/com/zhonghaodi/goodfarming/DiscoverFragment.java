package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GFVersionHint;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment implements OnClickListener {
	
	private View nzdView;
	private View nysView;
	private View nyqView;
	
	private View bchView;
	private View btcpView;
	private View cnzView;
	private TextView cnzText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_discover, container, false);
		
		nzdView = view.findViewById(R.id.layout1);
		nzdView.setOnClickListener(this);
		nysView = view.findViewById(R.id.layout2);
		nysView.setOnClickListener(this);
//		nyqView = view.findViewById(R.id.layout3);
//		nyqView.setOnClickListener(this);
		cnzView = view.findViewById(R.id.layout4);
		cnzView.setOnClickListener(this);
		bchView = view.findViewById(R.id.layout9);
		bchView.setOnClickListener(this);
		btcpView = view.findViewById(R.id.layout7);
		btcpView.setOnClickListener(this);
		cnzText = (TextView)view.findViewById(R.id.ncnz_text);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("发现Fragment");
		if(GFVersionHint.getCnzcount(getActivity())==0){
			cnzText.setVisibility(View.VISIBLE);
		}
		else{
			cnzText.setVisibility(View.GONE);
		}
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("发现Fragment");
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
		if(uid==null){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent);
			return;
		}
		switch (v.getId()) {
		case R.id.layout1:
			Intent intent = new Intent(getActivity(), NzdsActivity.class);
			startActivity(intent);
			break;
		case R.id.layout2:
			Intent intent1 = new Intent(getActivity(), NyssActivity.class);
			startActivity(intent1);
			break;
		case R.id.layout3:
			Intent intent2 = new Intent(getActivity(), QuanActivity.class);
			startActivity(intent2);
			break;
		case R.id.layout4:
			Intent intent3 = new Intent(getActivity(), CaicaicaiActivity.class);
			startActivity(intent3);
			break;
		case R.id.layout5:
			Intent intent4 = new Intent(getActivity(), MiaoActivity.class);
			startActivity(intent4);
			break;
		case R.id.layout7:
			Intent intent9 = new Intent(getActivity(), ZfbtActivity.class);
			startActivity(intent9);
			break;
		case R.id.layout9:
			Intent intent8 = new Intent(getActivity(), FarmCropsActivity.class);
			startActivity(intent8);
			break;
		default:
			break;
		}
	}
}

package com.zhonghaodi.goodfarming;

import com.zhonghaodi.model.GFUserDictionary;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment implements OnClickListener {
	
	private View nzdView;
	private View nysView;
	private View nyqView;
	private View nyjsView;
	private View miaoView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_discover, container, false);
		
		nzdView = view.findViewById(R.id.layout1);
		nzdView.setOnClickListener(this);
		nysView = view.findViewById(R.id.layout2);
		nysView.setOnClickListener(this);
		nyqView = view.findViewById(R.id.layout3);
		nyqView.setOnClickListener(this);
		nyjsView = view.findViewById(R.id.layout4);
		nyjsView.setOnClickListener(this);
		miaoView = view.findViewById(R.id.layout5);
		miaoView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String uid = GFUserDictionary.getUserId();
		if(uid==null){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent);
			return;
		}
		switch (v.getId()) {
		case R.id.layout1:
			Intent intent = new Intent(getActivity(), StoresActivity.class);
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
			Intent intent3 = new Intent(getActivity(), AgrotechnicalActivity.class);
			startActivity(intent3);
			break;
		case R.id.layout5:
			Intent intent4 = new Intent(getActivity(), MiaoActivity.class);
			startActivity(intent4);
			break;

		default:
			break;
		}
	}
}

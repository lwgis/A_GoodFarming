package com.zhonghaodi.goodfarming;

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
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
//			Intent intent2 = new Intent(getActivity(), AgrotechnicalActivity.class);
//			startActivity(intent2);
			break;
		case R.id.layout4:
			break;

		default:
			break;
		}
	}
}

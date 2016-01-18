package com.zhonghaodi.goodfarming;

import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment implements OnClickListener {
	
	private View nzdView;
	private View nysView;
	private View nyqView;
//	private View nyjsView;
	private View miaoView;
	private View commodityView;
	private View rubblerView;
	private View bchView;

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
//		nyjsView = view.findViewById(R.id.layout4);
//		nyjsView.setOnClickListener(this);
		miaoView = view.findViewById(R.id.layout5);
		miaoView.setOnClickListener(this);
		commodityView = view.findViewById(R.id.layout6);
		commodityView.setOnClickListener(this);
		rubblerView = view.findViewById(R.id.layout8);
		rubblerView.setOnClickListener(this);
		bchView = view.findViewById(R.id.layout9);
		bchView.setOnClickListener(this);
		return view;
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
			Intent intent3 = new Intent(getActivity(), AgrotechnicalActivity.class);
			startActivity(intent3);
			break;
		case R.id.layout5:
			Intent intent4 = new Intent(getActivity(), MiaoActivity.class);
			startActivity(intent4);
			break;
		case R.id.layout6:
			Intent intent5 = new Intent(getActivity(), CommoditiesActivity.class);
			startActivity(intent5);
			break;
		case R.id.layout8:
			int point = GFUserDictionary.getPoint(getActivity().getApplicationContext());
			int guagua = GFPointDictionary.getGuaguaPoint(getActivity());
			if(point>=guagua){
				Intent intent7 = new Intent(getActivity(), RubblerActivity.class);
				startActivity(intent7);
			}
			else{
				GFToast.show(getActivity().getApplicationContext(),"您的积分不足！");
			}
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

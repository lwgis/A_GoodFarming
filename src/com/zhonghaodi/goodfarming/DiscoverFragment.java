package com.zhonghaodi.goodfarming;

import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.GFPointDictionary;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.GFVersionHint;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GsonUtil;
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
import android.widget.Toast;
import android.view.ViewGroup;

public class DiscoverFragment extends Fragment implements OnClickListener,HandMessage {
	
	private View nzdView;
	private View nysView;
	private View nyqView;
	private View rubblerView;
	private View bchView;
	private View btcpView;
	private View cnzView;
	private View jfscView;
	private TextView cnzText;
	private TextView pointText;
	private GFHandler<DiscoverFragment> handler = new GFHandler<DiscoverFragment>(this);
	private User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_discover, container, false);
		
		nzdView = view.findViewById(R.id.layout1);
		nzdView.setOnClickListener(this);
		nysView = view.findViewById(R.id.layout2);
		nysView.setOnClickListener(this);
		jfscView = view.findViewById(R.id.layout6);
		jfscView.setOnClickListener(this);
		cnzView = view.findViewById(R.id.layout4);
		cnzView.setOnClickListener(this);
		bchView = view.findViewById(R.id.layout9);
		bchView.setOnClickListener(this);
		btcpView = view.findViewById(R.id.layout7);
		btcpView.setOnClickListener(this);
		rubblerView = view.findViewById(R.id.layout8);
		rubblerView.setOnClickListener(this);
		cnzText = (TextView)view.findViewById(R.id.ncnz_text);
		pointText  = (TextView)view.findViewById(R.id.des_text);
		return view;
	}
	
	public void loadData() {
		
		String uid = GFUserDictionary.getUserId(getActivity().getApplicationContext());
		if(uid==null){
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId(getActivity()));
				Message msg = handler.obtainMessage();
				msg.what=1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
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
		loadData();
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
		case R.id.layout6:
			Intent intent5 = new Intent(getActivity(), CommoditiesActivity.class);
			startActivity(intent5);
			break;
		case R.id.layout7:
			Intent intent9 = new Intent(getActivity(), ZfbtActivity.class);
			startActivity(intent9);
			break;
		case R.id.layout9:
			Intent intent8 = new Intent(getActivity(), FarmCropsActivity.class);
			startActivity(intent8);
			break;
			case R.id.layout8:
			int point = GFUserDictionary.getPoint(getActivity());
			int guagua = GFPointDictionary.getGuaguaPoint(getActivity());
			if(point>=guagua){
				Intent intent7 = new Intent(getActivity(), RubblerActivity.class);
				startActivity(intent7);
			}
			else{
				GFToast.show(getActivity(),"您的积分不足！");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if(msg.what==1){			
			user = (User) GsonUtil
					.fromJson(msg.obj.toString(), User.class);
			pointText.setText(user.getPoint()+"积分");
		}
		else{
			pointText.setText("");
		}
	}
}

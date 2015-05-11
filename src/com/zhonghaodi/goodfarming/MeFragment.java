package com.zhonghaodi.goodfarming;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.HttpUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MeFragment extends Fragment implements HandMessage{
	private User user;
	private PullToRefreshListView pullToRefreshList;
	private GFHandler<MeFragment> handler=new GFHandler<MeFragment>(this);
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.fragment_me, container, false);
		pullToRefreshList=(PullToRefreshListView)view.findViewById(R.id.pull_refresh_list);
		loadData();
		return view;
	}

	private void loadData() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			 String json=HttpUtil.getUser(GFUserDictionary.getUserId());
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		
	}

}

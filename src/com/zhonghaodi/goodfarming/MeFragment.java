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
import android.widget.BaseAdapter;

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
			 String jsonString=HttpUtil.getUser(GFUserDictionary.getUserId());
			 Message msg=new Message();
			 msg.obj=jsonString;
			 msg.sendToTarget();
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		MeFragment fragment=(MeFragment)object;
		
		
	}
	class MeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
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
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return super.getViewTypeCount();
		}
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}

package com.zhonghaodi.goodfarming;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderMeInfo;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.GsonUtil;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class MeFragment extends Fragment implements HandMessage {
	private User user;
	private PullToRefreshListView pullToRefreshList;
	private MeAdapter adapter;
	private GFHandler<MeFragment> handler = new GFHandler<MeFragment>(this);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, container, false);
		pullToRefreshList = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		adapter = new MeAdapter();
		pullToRefreshList.setAdapter(adapter);
		return view;
	}

	public void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getUser(GFUserDictionary
						.getUserId());
				Message msg = handler.obtainMessage();
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	class MeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (user == null) {
				return 0;
			}
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderMeInfo holderMeInfo;
			int cellType = getItemViewType(position);
			if (convertView == null) {
				switch (cellType) {
				case 0:
					convertView = LayoutInflater.from(
							MeFragment.this.getActivity()).inflate(
							R.layout.cell_me_info, parent, false);
					holderMeInfo = new HolderMeInfo(convertView);
					convertView.setTag(holderMeInfo);
					break;

				default:
					break;
				}

			}
			switch (cellType) {
			case 0:
				holderMeInfo = (HolderMeInfo) convertView.getTag();
				ImageLoader.getInstance().displayImage("http://121.40.62.120/appimage/users/small/"+user.getThumbnail(), holderMeInfo.headIv,
						ImageOptions.optionsNoPlaceholder);
				holderMeInfo.titleTv.setText(user.getAlias());
				holderMeInfo.jifenTv.setText(String.valueOf(user.getPoint()));

				break;

			default:
				break;
			}
			return convertView;
		}

	}

	@Override
	public void handleMessage(Message msg, Object object) {
		MeFragment fragment = (MeFragment) object;
		if (msg.obj == null) {
			Toast.makeText(fragment.getActivity(), "获取失败,请稍后再试",
					Toast.LENGTH_SHORT).show();
			return;
		}
		fragment.user = (User) GsonUtil
				.fromJson(msg.obj.toString(), User.class);
		adapter.notifyDataSetChanged();
	}

}

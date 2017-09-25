package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.CustomProgressDialog;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.Category_disease;
import com.zhonghaodi.model.ComparatorSort;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ForumFragment extends Fragment implements OnClickListener,HandMessage {
	
	private PullToRefreshListView pullToRefreshListView;
	private LinearLayout tabLayout;
	private List<Agrotechnical> agrotechnicals;
	private List<Category_disease> categorys;
	private AgroAdapter adapter;
	private GFHandler<ForumFragment> handler = new GFHandler<ForumFragment>(this);
	private int categoryid=1;
	private int zone=0;
	private CustomProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_forum, container,
				false);
		tabLayout = (LinearLayout)view.findViewById(R.id.tabhost);
		progressDialog = new CustomProgressDialog(getActivity(), "请稍后...");
		Button sgbcBtn = (Button)view.findViewById(R.id.sgbc_button);
		sgbcBtn.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Agrotechnical agrotechnical = agrotechnicals.get(position-1);
				Intent intent = new Intent(getActivity(), AgrotechnicalActivity.class);
				intent.putExtra("id", agrotechnical.getId());
				intent.putExtra("title", agrotechnical.getTitle());
				intent.putExtra("image", agrotechnical.getThumbnail());
				intent.putExtra("content", agrotechnical.getContent());
				getActivity().startActivity(intent);
			}
		});		
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (agrotechnicals.size() == 0) {
							return;
						}
						loadMoreData(agrotechnicals.get(agrotechnicals.size()-1).getId());
					}

				});
		
		agrotechnicals = new ArrayList<Agrotechnical>();
		adapter = new AgroAdapter();
		pullToRefreshListView.getRefreshableView().setAdapter(adapter);	
		zone = GFAreaUtil.getCity(getActivity());
		loadCategory();
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("田间地头Fragment");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("田间地头Fragment");
	}

	public void loadCategory(){
		progressDialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAgrotechnicalCates();
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = jsonString;
				msg.sendToTarget();				
			}
		}).start();
	}
	
	public void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAgrotechnical(categoryid,zone);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();				
			}
		}).start();
		
	}
	
	private void loadMoreData(final int fromid){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getMoreAgrotechnical(fromid,categoryid,zone);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();				
			}
		}).start();
		
	}
	
	public void createTabViews(){
		
		Comparator comp = new ComparatorSort();  
        Collections.sort(categorys,comp);
		
		tabLayout.removeAllViews();
		
		LayoutParams layoutParams;
		int width = PublicHelper.dip2px(getActivity(), 80);;
		int height = PublicHelper.dip2px(getActivity(), 34);
		int pwidth = PublicHelper.getPhoneWidth(getActivity());
		if((categorys.size()*width)<pwidth){
			width = pwidth/categorys.size();
		}
		layoutParams = new LinearLayout.LayoutParams(width, height);
		for(int i=0;i<categorys.size();i++){
			
			TextView tabView = new TextView(getActivity());
			tabView.setGravity(Gravity.CENTER);
			tabView.setText(categorys.get(i).getName());
			tabView.setBackgroundResource(R.drawable.topbar);
			int pix = PublicHelper.dip2px(getActivity(), 8);
			tabView.setPadding(pix, pix, pix, pix);
			tabView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			tabView.setTag(categorys.get(i).getId());
			tabLayout.addView(tabView, layoutParams);
			tabView.setOnClickListener(this);
		}
		selectTextView(tabLayout.getChildAt(0));
		
	}
	
	public void selectTextView(View view){
		for (int i = 0; i < tabLayout.getChildCount(); i++) {
			TextView textView = (TextView)tabLayout.getChildAt(i);
			textView.setTextColor(Color.rgb(128, 128, 128));
			textView.setBackgroundResource(R.drawable.topbar);
		}
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
		categoryid = Integer.parseInt(view.getTag().toString());
	}
	
	class AgroHolder{
		public ImageView agroIv;
		public TextView titleTv;
		public TextView timeTv;
		 public AgroHolder(View view){
			 agroIv=(ImageView)view.findViewById(R.id.head_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 timeTv=(TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class AgroFirstHolder{
		public ImageView agroIv;
		public TextView titleTv;
		 public AgroFirstHolder(View view){
			 agroIv=(ImageView)view.findViewById(R.id.head_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
		 }
	}
	
	class AgroAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return agrotechnicals.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return agrotechnicals.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return 0;
			}
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			AgroFirstHolder aFirstHolder;
			AgroHolder agroholder;
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = LayoutInflater.from(getActivity())
							.inflate(R.layout.cell_agrotechnical_first, parent, false);
					aFirstHolder = new AgroFirstHolder(convertView);
					convertView.setTag(aFirstHolder);
					break;
				case 1:
					convertView = LayoutInflater.from(getActivity())
							.inflate(R.layout.cell_agrotechnical, parent, false);
					agroholder = new AgroHolder(convertView);
					convertView.setTag(agroholder);
					break;
				default:
					break;
				}
			}
			Agrotechnical agrotechnical = agrotechnicals.get(position);
			switch (type) {
			case 0:
				aFirstHolder = (AgroFirstHolder)convertView.getTag();
				if (agrotechnical.getThumbnail()!=null) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"agrotechnicals/big/"+agrotechnical.getThumbnail(), aFirstHolder.agroIv, ImageOptions.optionsNoPlaceholder);
				}
				aFirstHolder.titleTv.setText(agrotechnical.getTitle());
				break;
			case 1:
				agroholder=(AgroHolder)convertView.getTag();
				
				if (agrotechnical.getThumbnail()!=null) {
					ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"agrotechnicals/small/"+agrotechnical.getThumbnail(), agroholder.agroIv, ImageOptions.optionsNoPlaceholder);
				}
				agroholder.titleTv.setText(agrotechnical.getTitle());
				agroholder.timeTv.setText(agrotechnical.getContent());
				break;

			default:
				break;
			}
			
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.sgbc_button){
			Intent start = new Intent(getActivity(),SGBCActivity.class); 
			startActivity(start);
		}
		else{
			selectTextView(v);
			progressDialog.show();
			loadData();
		}
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		if(getActivity()==null){
			return;
		}
		switch (msg.what) {
		case 0:
			progressDialog.dismiss();
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Agrotechnical> agrs = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Agrotechnical>>() {
						}.getType());
				if (msg.what == 0) {
					agrotechnicals.clear();
				}
				for (Agrotechnical agrotechnical: agrs) {
					agrotechnicals.add(agrotechnical);
				}
				adapter.notifyDataSetChanged();
				if (msg.what == 0) {
					pullToRefreshListView.getRefreshableView().setSelection(0);
				}
				
			} else {
				GFToast.show(getActivity().getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			pullToRefreshListView.onRefreshComplete();
			break;
		case 2:
			
			if(msg.obj!=null){
				Gson gson = new Gson();
				categorys = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Category_disease>>() {
						}.getType());
				createTabViews();
				loadData();
			}
			else{
				progressDialog.dismiss();
			}
			
			break;

		default:
			break;
		}
		
	}
	
}

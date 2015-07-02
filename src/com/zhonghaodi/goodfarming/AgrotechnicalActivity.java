package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AgrotechnicalActivity extends Activity implements HandMessage {

	private PullToRefreshListView pullToRefreshListView;
	private List<Agrotechnical> agrotechnicals;
	private AgroAdapter adapter;
	private GFHandler<AgrotechnicalActivity> handler = new GFHandler<AgrotechnicalActivity>(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agrotechnical);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Agrotechnical agrotechnical = agrotechnicals.get(position-1);
				Intent intent = new Intent(AgrotechnicalActivity.this, AgroActivity.class);
				intent.putExtra("aid", agrotechnical.getId());
				AgrotechnicalActivity.this.startActivity(intent);
			}
		});
		
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {

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
		loadData();
	}
	
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getAgrotechnical();
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
				String jsonString = HttpUtil.getMoreAgrotechnical(fromid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();				
			}
		}).start();
		
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AgroHolder agroholder;;
			if(convertView==null){
				convertView = LayoutInflater.from(AgrotechnicalActivity.this)
						.inflate(R.layout.cell_agrotechnical, parent, false);
				agroholder = new AgroHolder(convertView);
				convertView.setTag(agroholder);
			}
			
			agroholder=(AgroHolder)convertView.getTag();
			Agrotechnical agrotechnical = agrotechnicals.get(position);
			if (agrotechnical.getThumbnail()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"agrotechnicals/small/"+agrotechnical.getThumbnail(), agroholder.agroIv, ImageOptions.optionsNoPlaceholder);
			}
			agroholder.titleTv.setText(agrotechnical.getTitle());
			agroholder.timeTv.setText(agrotechnical.getTime());
			return convertView;
		}
		
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final AgrotechnicalActivity agrotechnicalactivity =(AgrotechnicalActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<Agrotechnical> agrs = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<Agrotechnical>>() {
					}.getType());
			if (msg.what == 0) {
				agrotechnicalactivity.agrotechnicals.clear();
			}
			for (Agrotechnical agrotechnical: agrs) {
				agrotechnicalactivity.agrotechnicals.add(agrotechnical);
			}
			agrotechnicalactivity.adapter.notifyDataSetChanged();
			
		} else {
			GFToast.show("连接服务器失败,请稍候再试!");
		}
		pullToRefreshListView.onRefreshComplete();
	}

}

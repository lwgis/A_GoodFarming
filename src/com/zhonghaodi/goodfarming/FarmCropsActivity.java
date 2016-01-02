package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FarmCropsActivity extends Activity implements HandMessage,OnItemClickListener {

	private PullToRefreshGridView gridView;
	private List<Crop> crops;
	private FCropAdapter adapter;
	private GFHandler<FarmCropsActivity> handler = new GFHandler<FarmCropsActivity>(this);
	private TextView titleView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_farmcrops);
		titleView = (TextView)findViewById(R.id.title_text);
		gridView = (PullToRefreshGridView)findViewById(R.id.pull_refresh_grid);
		gridView.setMode(Mode.DISABLED);
		gridView.setOnItemClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		crops = new ArrayList<Crop>();
		adapter = new FCropAdapter();
		gridView.getRefreshableView().setAdapter(adapter);
		titleView.setText("植物病虫害");
		loaddata();
	}
	
	/**
	 * 获取作物分类
	 * @param position
	 */
	public void loaddata(){
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getAllCropsHasDisease();
				if(jsonString!=null){
					if (!jsonString.equals("")) {
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.obj = jsonString;
						msg.sendToTarget();
					}
				}
				else{
					Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.obj = "链接服务器错误，请稍后再试！";
					msg.sendToTarget();
				}
				
			}
		}).start();
		
	}
	
	class FCropHolder{
		public ImageView agroIv;
		public TextView titleTv;
		 public FCropHolder(View view){
			 agroIv=(ImageView)view.findViewById(R.id.cImg);
			 titleTv=(TextView)view.findViewById(R.id.cName);
		 }
	}
	
	class FCropAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return crops.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return crops.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			FCropHolder fcropholder;;
			if(convertView==null){
				convertView = LayoutInflater.from(FarmCropsActivity.this)
						.inflate(R.layout.cell_cropgridimage, parent, false);
				fcropholder = new FCropHolder(convertView);
				convertView.setTag(fcropholder);
			}
			
			fcropholder=(FCropHolder)convertView.getTag();
			Crop crop = crops.get(position);
			if (crop.getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"crop/small/"+crop.getImage(), fcropholder.agroIv, ImageOptions.optionsNoPlaceholder);
			}
			fcropholder.titleTv.setText(crop.getName());
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Crop crop = crops.get(position);
		Intent intent = new Intent(this, DiseasesActivity.class);
		intent.putExtra("id", crop.getId());
		intent.putExtra("name", crop.getName());
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Crop> cs = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Crop>>() {
						}.getType());
				crops.clear();
				if(cs!=null && cs.size()>0){
					for (Crop crop : cs) {
						crops.add(crop);
					}
					adapter.notifyDataSetChanged();
				} 
				gridView.onRefreshComplete();
			}
			break;
		case 0:
			GFToast.show(getApplicationContext(),msg.obj.toString());
			break;

		default:
			break;
		}
	}

}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.HolderRecipe;
import com.zhonghaodi.goodfarming.StoreActivity.RecipeAdapter;
import com.zhonghaodi.model.Recipe;
import com.zhonghaodi.model.Second;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MiaoActivity extends Activity implements HandMessage,OnClickListener,OnItemClickListener {
	
	private ListView pullToRefreshListView;
	private List<Second> seconds;
	private SecondAdapter adapter;
	private GFHandler<MiaoActivity> handler = new GFHandler<MiaoActivity>(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miao);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);		
		seconds = new ArrayList<Second>();
		adapter = new SecondAdapter();
		pullToRefreshListView.setAdapter(adapter);	
		loadData();
	}
	
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getSeconds();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	class HolderSecond {
		public ImageView secondIv;
		public TextView titleTv;
		public TextView oldPriceTv;
		public TextView newPriceTv;
		public TextView countTv;
		public TextView timeTv;
		 public HolderSecond(View view){
			 secondIv=(ImageView)view.findViewById(R.id.second_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 oldPriceTv=(TextView)view.findViewById(R.id.oldprice_text);
			 newPriceTv=(TextView)view.findViewById(R.id.newprice_text);
			 oldPriceTv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
			 countTv = (TextView)view.findViewById(R.id.count_text);
			 timeTv = (TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class SecondAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return seconds.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return seconds.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub 
			HolderSecond holdersecond;;
			if(convertView==null){
				convertView = LayoutInflater.from(MiaoActivity.this)
						.inflate(R.layout.cell_second, parent, false);
				holdersecond = new HolderSecond(convertView);
				convertView.setTag(holdersecond);
			}
			
			holdersecond=(HolderSecond)convertView.getTag();
			Second second = seconds.get(position);
			if (second.getImage()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"seconds/small/"+second.getImage(), holdersecond.secondIv, ImageOptions.optionsNoPlaceholder);
			}
			holdersecond.titleTv.setText(second.getTitle());
			holdersecond.oldPriceTv.setText("￥"+String.valueOf(second.getOprice()));
			holdersecond.newPriceTv.setText("￥"+String.valueOf(second.getNprice()));
			holdersecond.countTv.setText("数量："+String.valueOf(second.getCount()));
			holdersecond.timeTv.setText("时间"+second.getStarttime());
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Second second = seconds.get(position);
		if(second!=null){
			Intent intent = new Intent(this,SecondActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("second", second);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		final MiaoActivity miaoactivity =(MiaoActivity)object;
		if (msg.obj != null) {
			Gson gson = new Gson();
			List<Second> ses = gson.fromJson(msg.obj.toString(),
					new TypeToken<List<Second>>() {
					}.getType());
			if (msg.what == 0) {
				miaoactivity.seconds.clear();
			}
			for (Second second : ses) {
				miaoactivity.seconds.add(second);
			}
			miaoactivity.adapter.notifyDataSetChanged();
			
		} else {
			Toast.makeText(this, "连接服务器失败,请稍候再试!",
					Toast.LENGTH_SHORT).show();
		}
	}

}

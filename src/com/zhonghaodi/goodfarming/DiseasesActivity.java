package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.goodfarming.ShoppingCartActivity.MyOrderAdapter;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.Category_disease;
import com.zhonghaodi.model.ComparatorSort;
import com.zhonghaodi.model.Crop;
import com.zhonghaodi.model.Disease;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.RecipeOrder;
import com.zhonghaodi.model.SortComparator;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.utils.PublicHelper;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DiseasesActivity extends Activity implements HandMessage,OnItemClickListener,OnClickListener{
	private ListView pullToRefreshListView;
	private List<Disease> allDiseases;
	private List<Disease> diseases;
	private DiseaseAdapter adapter;
	private LinearLayout tabLayout;
	private int status = 0;
	private int cropid;
	private String cropname;
	private GFHandler<DiseasesActivity> handler = new GFHandler<DiseasesActivity>(this);
	private int categoryid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diseases);
		MobclickAgent.openActivityDurationTrack(false);
		tabLayout = (LinearLayout)findViewById(R.id.tabhost);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		pullToRefreshListView = (ListView) findViewById(R.id.pull_refresh_list);		
		pullToRefreshListView.setOnItemClickListener(this);
		allDiseases = new ArrayList<Disease>();
		diseases = new ArrayList<Disease>();
		adapter = new DiseaseAdapter();
		pullToRefreshListView.setAdapter(adapter);	
		cropid = getIntent().getIntExtra("id", 0);
		cropname = getIntent().getStringExtra("name");
		TextView titleView = (TextView)findViewById(R.id.title_text);
		titleView.setText(cropname+"的病害");
		loadData();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("病虫害");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("病虫害");
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 根据作物获取病害
	 */
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String jsonString = HttpUtil.getDiseasesByCrop(cropid);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
				
			}
		}).start();
		
	}
	
	public void createTabViews(){
		if(allDiseases==null || allDiseases.size()==0){
			return;
		}
		List<Category_disease> cDiseases = new ArrayList<Category_disease>();
		for (Disease disease : allDiseases) {
			boolean b = false;
			for (int i = 0; i < cDiseases.size(); i++) {
				if(cDiseases.get(i).getId()==disease.getCategory().getId()){
					b = true;
					break;
				}
			}
			if(!b){
				cDiseases.add(disease.getCategory());
			}
		}
		
		Comparator comp = new ComparatorSort();  
        Collections.sort(cDiseases,comp);
		
		tabLayout.removeAllViews();
		for(int i=0;i<cDiseases.size();i++){
			
			TextView tabView = new TextView(this);
			int height = PublicHelper.dip2px(this, 34);
			LayoutParams layoutParams = new LinearLayout.LayoutParams(0, height, 1);
			tabView.setGravity(Gravity.CENTER);
			tabView.setText(cDiseases.get(i).getName());
			tabView.setBackgroundResource(R.drawable.topbar);
			int pix = PublicHelper.dip2px(this, 8);
			tabView.setPadding(pix, pix, pix, pix);
			tabView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			tabView.setTag(cDiseases.get(i).getId());
			tabLayout.addView(tabView, layoutParams);
			tabView.setOnClickListener(this);
		}
		selectTextView(tabLayout.getChildAt(0));
		
	}
	
	public void displayData(){
		diseases.clear();
		if(allDiseases!=null && allDiseases.size()>0){
			
			for (Disease disease : allDiseases) {
				if (disease.getCategory().getId()==categoryid) {
					diseases.add(disease);
				}
			}
			adapter.notifyDataSetChanged();
		} 
	}
	
	public void selectTextView(View view){
		for (int i = 0; i < tabLayout.getChildCount(); i++) {
			TextView textView = (TextView)tabLayout.getChildAt(i);
			textView.setTextColor(Color.rgb(128, 128, 128));
			textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbar));
		}
		
		TextView selectTextView = (TextView)view;
		selectTextView.setTextColor(Color.rgb(56, 190, 153));
		categoryid = Integer.parseInt(view.getTag().toString());
	}
	
	class DiseaseHolder{
		public RoundedImageView agroIv;
		public TextView titleTv;
		public TextView timeTv;
		 public DiseaseHolder(View view){
			 agroIv=(RoundedImageView)view.findViewById(R.id.head_image);
			 titleTv=(TextView)view.findViewById(R.id.title_text);
			 timeTv=(TextView)view.findViewById(R.id.time_text);
		 }
	}
	
	class DiseaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return diseases.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return diseases.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DiseaseHolder diseaseholder;;
			if(convertView==null){
				convertView = LayoutInflater.from(DiseasesActivity.this)
						.inflate(R.layout.cell_diseases, parent, false);
				diseaseholder = new DiseaseHolder(convertView);
				convertView.setTag(diseaseholder);
			}
			
			diseaseholder=(DiseaseHolder)convertView.getTag();
			Disease disease = diseases.get(position);
			if (disease.getThumbnail()!=null) {
				ImageLoader.getInstance().displayImage(HttpUtil.ImageUrl+"diseases/small/"+disease.getThumbnail(), diseaseholder.agroIv, ImageOptions.optionsNoPlaceholder);
			}
			diseaseholder.titleTv.setText(disease.getName());
			diseaseholder.timeTv.setText(disease.getDescription());
			return convertView;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		selectTextView(v);
		displayData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Disease disease = diseases.get(position);
		Intent intent = new Intent(this, DiseaseActivity.class);
		intent.putExtra("diseaseId", disease.getId());
		startActivity(intent);
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				allDiseases = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Disease>>() {
						}.getType());
				createTabViews();
				displayData();
			}
			else{
				GFToast.show(getApplicationContext(),"获取病害失败");
			}
			
			break;

		default:
			break;
		}
	}

}

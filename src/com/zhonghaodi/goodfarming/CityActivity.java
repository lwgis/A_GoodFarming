package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.CityAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.goodfarming.SelectCropFragment.CropListAdapter;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.Contact;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.MySectionIndexer;
import com.zhonghaodi.req.CityReq;
import com.zhonghaodi.view.CityView;
import com.zhonghaodi.view.PinnedHeaderListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CityActivity extends Activity implements OnItemClickListener,CityView,OnClickListener {
	
	private PinnedHeaderListView cityListView;
	private List<City> areas;
	private String[] sections;  
    private Integer[] counts;
	private CityReq req;
	private CityAdapter adapter;
	private TextView titleText;
	private MyTextButton goonBtn;
	private MyTextButton okBtn;
	private int status;
	private City area1,area2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CityActivity.this.finish();
			}
		});
		goonBtn = (MyTextButton)findViewById(R.id.goon_button);
		goonBtn.setOnClickListener(this);
		okBtn = (MyTextButton)findViewById(R.id.ok_button);
		okBtn.setOnClickListener(this);
		titleText = (TextView)findViewById(R.id.title_text);
		cityListView = (PinnedHeaderListView)findViewById(R.id.pull_refresh_list);
		cityListView.setOnItemClickListener(this);
		areas = new ArrayList<City>();
		status=0;
		req = new CityReq(this);
		req.loadCities();
	}
	
	public void setElementStatus(){
		MySectionIndexer indexer;
		if(status==0){
			titleText.setText("我在…");
			goonBtn.setVisibility(View.GONE);
			int zone = GFAreaUtil.getCity(this);
			if(zone!=0){
				area1 = new City();
				area1.setId(zone);
				area1.setName(GFAreaUtil.getCityName(this));
			}
			indexer = new MySectionIndexer(sections, counts);
			adapter = new CityAdapter(areas, this, indexer);
			adapter.setSelid(zone);
			cityListView.setAdapter(adapter);
	        cityListView.setOnScrollListener(adapter);
	      //設置頂部固定頭部  
	        cityListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_group_crop, cityListView, false)); 
		}
		else{
//			titleText.setText("我还想看看…");
//			goonBtn.setVisibility(View.GONE);
//			int zone2= GFAreaUtil.getCityId1(this);
//			if(zone2==0){
//				area2 = areas.get(0);
//			}
//			else{
//				area2 = new City();
//				area2.setId(zone2);
//				area2.setName(GFAreaUtil.getCityName1(this));
//			}
//			for (Iterator iterator = areas.iterator(); iterator.hasNext();) {
//				City city = (City) iterator.next();
//				if(city.getId()==area1.getId()){
//					areas.remove(city);
//					counts[0]=counts[0]-1;
//					break;
//				}
//			}
//			indexer = new MySectionIndexer(sections, counts);
//			adapter = new CityAdapter(areas, this, indexer);
//			adapter.setSelid(area2.getId());
//			cityListView.setAdapter(adapter);
//	        cityListView.setOnScrollListener(adapter);
//	      //設置頂部固定頭部  
//	        cityListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
//	                R.layout.cell_group_crop, cityListView, false)); 
//	        Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
//	        cityListView.startAnimation(animation);
		}      
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("区域选择");
		MobclickAgent.onResume(this);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("区域选择");
		MobclickAgent.onPause(this);
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if(position==areas.size()-1){
			return;
		}
		if(status==0){
			area1 = areas.get(position);
			adapter.setSelid(area1.getId());
			adapter.notifyDataSetChanged();
		}
		else{
			if(area2!=null && area2==areas.get(position)){
				area2=null;
				adapter.setSelid(0);
			}
			else{
				area2 = areas.get(position);
				adapter.setSelid(area2.getId());
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.goon_button:
			if(area1==null){
				showMessage("请先选择主区域");
				return;
			}
			GFAreaUtil.saveAreaInfo(this, area1);
			status=1;
			setElementStatus();
			
//			AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
//			animAdapter.setAbsListView(mCurrentListView);
//			mCurrentListView.setAdapter(animAdapter);
//			adapter.notifyDataSetChanged();
			break;
		case R.id.ok_button:
			if(status==0){
				if(area1==null){
					showMessage("请选择一个区域");
					return;
				}
				GFAreaUtil.saveAreaInfo(this, area1);
			}
			else{
				GFAreaUtil.saveAreaInfo1(this, area2);
			}
			Intent it = getIntent();
			setResult(RESULT_OK, it);
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void displayCities(List<City> cities) {
		// TODO Auto-generated method stub
		areas.clear();
		List<String> secList = new ArrayList<String>();
		List<Integer> intList = new ArrayList<Integer>();		
		if(cities!=null && cities.size()>0){
			secList.add("山东省");
			intList.add(cities.size());
			for(int i=0;i<cities.size();i++){
				City city = cities.get(i);
				areas.add(city);
			}
			City city = new City();
			city.setPname("其它地区敬请期待");
			city.setName("");
			areas.add(city);
			secList.add("其它地区敬请期待");
			intList.add(1);
			sections = (String[])secList.toArray(new String[secList.size()]);
	        counts = (Integer[])intList.toArray(new Integer[intList.size()]);
	        setElementStatus();
		}
	}

	@Override
	public void showMessage(String message) {
		// TODO Auto-generated method stub
		GFToast.show(this, message);
	}

}

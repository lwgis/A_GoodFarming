package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.platform.comapi.map.k;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.adapter.LetterAdapter;
import com.zhonghaodi.adapter.ProvinceAdapter;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.model.City;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.MySectionIndexer;
import com.zhonghaodi.model.Province;
import com.zhonghaodi.req.ProvinceReq;
import com.zhonghaodi.view.PinnedHeaderListView;
import com.zhonghaodi.view.ProvinceView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ProvinceActivity extends Activity implements OnItemClickListener,ProvinceView,
								OnClickListener,OnTouchListener {

	private View headerView;
	private Button sgButton;
	private Button qzButton;
	private Button sxButton;
	private PinnedHeaderListView provinceListView;
	private ListView letterListView;
	private TextView currentCountyTv;
	private TextView gpsTv;
	private TextView changeTv;
	private TextView bigTv;
	private int level=1;
	private ProvinceReq req;
	private LetterAdapter letterAdapter;
	
	private ProvinceAdapter provinceAdapter;
	private List<String> prosecList;
	private List<Integer> prointList;
	private MySectionIndexer proindexer;
	
	private int k = -1;
	private int dy;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province);
		headerView = LayoutInflater.from(this).inflate(R.layout.province_header, null);
		Button cancelBtn = (Button) findViewById(R.id.cancel_button);
		cancelBtn.setOnClickListener(this);
		sgButton = (Button)headerView.findViewById(R.id.sgbtn);
		sgButton.setOnClickListener(this);
		qzButton = (Button)headerView.findViewById(R.id.qzbtn);
		qzButton.setOnClickListener(this);
		sxButton = (Button)headerView.findViewById(R.id.sxbtn);
		sxButton.setOnClickListener(this);
		provinceListView = (PinnedHeaderListView)findViewById(R.id.provincelist);
		provinceListView.addHeaderView(headerView);
		provinceListView.setOnItemClickListener(this);
		letterListView = (ListView)findViewById(R.id.letterlist);
		letterListView.setOnItemClickListener(this);
		letterListView.setOnTouchListener(this);
		currentCountyTv = (TextView)headerView.findViewById(R.id.currentcounty);
		gpsTv = (TextView)headerView.findViewById(R.id.gpszone);
		gpsTv.setOnClickListener(this);
		changeTv = (TextView)headerView.findViewById(R.id.change);
		changeTv.setOnClickListener(this);
		bigTv = (TextView)findViewById(R.id.bigletter_tv);
		
		String currentname = GFAreaUtil.getCityName(this);
		if(!TextUtils.isEmpty(currentname)){
			currentCountyTv.setText(currentname);
		}
		else{
			currentCountyTv.setText("未选择");
		}
		
		req = new ProvinceReq(this,this);
		req.location();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("区域选择");
		MobclickAgent.onResume(this);
		req.loadProvince();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("区域选择");
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_button:
			this.finish();
			break;
		case R.id.lettertv:
			showMessage("123");
			break;
		case R.id.gpszone:
			final int k = Integer.parseInt(v.getTag().toString());
			final String txt = gpsTv.getText().toString();
			if(k!=0){
				final Dialog dg = new Dialog(this, R.style.MyDialog);
		        //设置它的ContentView
				LayoutInflater inflater0 = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        View layout0 = inflater0.inflate(R.layout.dialog, null);
		        dg.setContentView(layout0);
		        TextView contentView0 = (TextView)layout0.findViewById(R.id.contentTxt);
		        TextView titleView0 = (TextView)layout0.findViewById(R.id.dialog_title);
		        Button okBtn0 = (Button)layout0.findViewById(R.id.dialog_button_ok);
		        okBtn0.setText("确定");
		        okBtn0.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dg.dismiss();
						City area = new City();
						area.setId(k);
						area.setName(txt);
						GFAreaUtil.saveAreaInfo(ProvinceActivity.this, area);
						Intent it = getIntent();
						setResult(RESULT_OK, it);
						ProvinceActivity.this.finish();
					}
				});
		        Button cancelButton0 = (Button)layout0.findViewById(R.id.dialog_button_cancel);
		        cancelButton0.setText("取消");
		        cancelButton0.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dg.dismiss();
					}
				});
		        titleView0.setText("提示");
		        contentView0.setText("确定要选择"+txt+"吗？");
		        dg.show();
			}
			break;
		case R.id.change:
			if(level==2){
				req.backtoProvinces();
			}
			if(level==3){
				req.backtoCities();
			}
			break;
		case R.id.sgbtn:
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
	        //设置它的ContentView
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout = inflater.inflate(R.layout.dialog, null);
	        dialog.setContentView(layout);
	        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
	        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
	        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
	        okBtn.setText("确定");
	        okBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					City area = new City();
					area.setId(370783);
					area.setName("寿光市");
					GFAreaUtil.saveAreaInfo(ProvinceActivity.this, area);
					Intent it = getIntent();
					setResult(RESULT_OK, it);
					ProvinceActivity.this.finish();
				}
			});
	        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
	        cancelButton.setText("取消");
	        cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
	        titleView.setText("提示");
	        contentView.setText("确定要选择寿光市吗？");
	        dialog.show();
			break;
		case R.id.qzbtn:
			final Dialog dialog1 = new Dialog(this, R.style.MyDialog);
	        //设置它的ContentView
			LayoutInflater inflater1 = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout1 = inflater1.inflate(R.layout.dialog, null);
	        dialog1.setContentView(layout1);
	        TextView contentView1 = (TextView)layout1.findViewById(R.id.contentTxt);
	        TextView titleView1 = (TextView)layout1.findViewById(R.id.dialog_title);
	        Button okBtn1 = (Button)layout1.findViewById(R.id.dialog_button_ok);
	        okBtn1.setText("确定");
	        okBtn1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog1.dismiss();
					City area = new City();
					area.setId(370781);
					area.setName("青州市");
					GFAreaUtil.saveAreaInfo(ProvinceActivity.this, area);
					Intent it = getIntent();
					setResult(RESULT_OK, it);
					ProvinceActivity.this.finish();
				}
			});
	        Button cancelButton1 = (Button)layout1.findViewById(R.id.dialog_button_cancel);
	        cancelButton1.setText("取消");
	        cancelButton1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog1.dismiss();
				}
			});
	        titleView1.setText("提示");
	        contentView1.setText("确定要选择青州市吗？");
	        dialog1.show();
			break;
		case R.id.sxbtn:
			final Dialog dialog2 = new Dialog(this, R.style.MyDialog);
	        //设置它的ContentView
			LayoutInflater inflater2 = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout2 = inflater2.inflate(R.layout.dialog, null);
	        dialog2.setContentView(layout2);
	        TextView contentView2 = (TextView)layout2.findViewById(R.id.contentTxt);
	        TextView titleView2 = (TextView)layout2.findViewById(R.id.dialog_title);
	        Button okBtn2 = (Button)layout2.findViewById(R.id.dialog_button_ok);
	        okBtn2.setText("确定");
	        okBtn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog2.dismiss();
					City area = new City();
					area.setId(371522);
					area.setName("莘县");
					GFAreaUtil.saveAreaInfo(ProvinceActivity.this, area);
					Intent it = getIntent();
					setResult(RESULT_OK, it);
					ProvinceActivity.this.finish();
				}
			});
	        Button cancelButton2 = (Button)layout2.findViewById(R.id.dialog_button_cancel);
	        cancelButton2.setText("取消");
	        cancelButton2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog2.dismiss();
				}
			});
	        titleView2.setText("提示");
	        contentView2.setText("确定要选择莘县吗？");
	        dialog2.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.letterlist:
			String alpha = letterAdapter.getItem(position).toString();
			int section = 0;
			if(prosecList!=null && prosecList.size()>0){
				for(int i=0;i<prosecList.size();i++){
					if(alpha.equals(prosecList.get(i))){
						section=i;
						break;
					}
				}
			}
			int pos = proindexer.getPositionForSection(section);
			provinceListView.setSelection(pos);	
			showBigLetter(alpha);
			break;
		case R.id.provincelist:
			final Province province = (Province)provinceAdapter.getItem(position-1);
			if(province!=null){	
				switch (level) {
				case 1:
					req.loadCities(province.getCode(),province.getName());
					break;
				case 2:
					req.loadCounties(province.getCode(),province.getName());
					break;
				case 3:
					final Dialog dialog = new Dialog(this, R.style.MyDialog);
			        //设置它的ContentView
					LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        View layout = inflater.inflate(R.layout.dialog, null);
			        dialog.setContentView(layout);
			        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
			        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
			        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
			        okBtn.setText("确定");
			        okBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							City area = new City();
							area.setId((int)province.getCode());
							area.setName(province.getName());
							GFAreaUtil.saveAreaInfo(ProvinceActivity.this, area);
							Intent it = getIntent();
							setResult(RESULT_OK, it);
							ProvinceActivity.this.finish();
						}
					});
			        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
			        cancelButton.setText("取消");
			        cancelButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			        titleView.setText("提示");
			        contentView.setText("确定要选择"+province.getName()+"吗？");
			        dialog.show();
					break;

				default:
					break;
				}
				
			}
			break;
		
		default:
			break;
		}
	}

	@Override
	public void displayProvinces(List<Province> provinces,int status) {
		// TODO Auto-generated method stub	
		level = 1;
		changeTv.setText("省份");
		if(provinces!=null && provinces.size()>0){
			provinceAdapter=null;
			prosecList = null;
			prointList = null;
			proindexer = null;
			
			String[] sections = null;  
		    Integer[] counts = null;
		    prosecList = new ArrayList<String>();
			prointList = new ArrayList<Integer>();
			for (Iterator iterator = provinces.iterator(); iterator.hasNext();) {
				Province province = (Province) iterator.next();
				boolean finded = false;
				for(int i=0;i<prosecList.size();i++){
					if(prosecList.get(i).equals(province.getHead())){
						finded = true;
						prointList.set(i,prointList.get(i)+1);
						break;
					}
				}
				if(!finded){
					prosecList.add(province.getHead());
					prointList.add(1);
				}
			}
			sections = (String[])prosecList.toArray(new String[prosecList.size()]);
	        counts = (Integer[])prointList.toArray(new Integer[prointList.size()]);
	        proindexer = new MySectionIndexer(sections, counts);
	        provinceAdapter = new ProvinceAdapter(provinces, this, proindexer,level);
			provinceListView.setVisibility(View.VISIBLE);
			if(status==1){
				provinceListView.setLayoutAnimation(getListAnim(status));
			}
			provinceListView.setAdapter(provinceAdapter);
	        provinceListView.setOnScrollListener(provinceAdapter);
	        //設置頂部固定頭部  
	        provinceListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_province_header, provinceListView, false));
	        letterAdapter = new LetterAdapter(prosecList, this,this);
	        letterListView.setAdapter(letterAdapter);
	        
		}
		else{
			prosecList = new ArrayList<String>();
			prointList = new ArrayList<Integer>();
			provinces = new ArrayList<Province>();
			String[] sections = (String[])prosecList.toArray(new String[prosecList.size()]);
	        Integer[] counts = (Integer[])prointList.toArray(new Integer[prointList.size()]);
	        proindexer = new MySectionIndexer(sections, counts);
			provinceAdapter = new ProvinceAdapter(provinces, this, proindexer,level);
			provinceListView.setVisibility(View.VISIBLE);
			provinceListView.setLayoutAnimation(getListAnim(status));
			provinceListView.setAdapter(provinceAdapter);
			provinceListView.setOnScrollListener(provinceAdapter);
	        //設置頂部固定頭部  
			provinceListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_province_header, provinceListView, false)); 
			letterAdapter = new LetterAdapter(prosecList, this,this);
	        letterListView.setAdapter(letterAdapter);
		}
		
	}

	@Override
	public void displayCities(List<Province> cities,String proname,int status) {
		// TODO Auto-generated method stub
		level = 2;
		changeTv.setText("< "+proname);
		if(cities!=null && cities.size()>0){
			provinceAdapter=null;
			prosecList = null;
			prointList = null;
			proindexer = null;
			
			String[] sections;  
		    Integer[] counts;
		    prosecList = new ArrayList<String>();
			prointList = new ArrayList<Integer>();
			for (Iterator iterator = cities.iterator(); iterator.hasNext();) {
				Province province = (Province) iterator.next();
				boolean finded = false;
				for(int i=0;i<prosecList.size();i++){
					if(prosecList.get(i).equals(province.getHead())){
						finded = true;
						prointList.set(i,prointList.get(i)+1);
						break;
					}
				}
				if(!finded){
					prosecList.add(province.getHead());
					prointList.add(1);
				}
			}
			sections = (String[])prosecList.toArray(new String[prosecList.size()]);
	        counts = (Integer[])prointList.toArray(new Integer[prointList.size()]);
	        proindexer = new MySectionIndexer(sections, counts);
			provinceAdapter = new ProvinceAdapter(cities, this, proindexer,level);
			provinceListView.setLayoutAnimation(getListAnim(status));
			provinceListView.setAdapter(provinceAdapter);
			provinceListView.setOnScrollListener(provinceAdapter);
	        //設置頂部固定頭部  
			provinceListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_province_header, provinceListView, false)); 
	        letterAdapter = new LetterAdapter(prosecList, this,this);
	        letterListView.setAdapter(letterAdapter);
	        
		}
		else{
			prosecList = new ArrayList<String>();
			prointList = new ArrayList<Integer>();
			cities = new ArrayList<Province>();
			String[] sections = (String[])prosecList.toArray(new String[prosecList.size()]);
	        Integer[] counts = (Integer[])prointList.toArray(new Integer[prointList.size()]);
	        proindexer = new MySectionIndexer(sections, counts);
			provinceAdapter = new ProvinceAdapter(cities, this, proindexer,level);
			provinceListView.setVisibility(View.VISIBLE);
			provinceListView.setLayoutAnimation(getListAnim(status));
			provinceListView.setAdapter(provinceAdapter);
			provinceListView.setOnScrollListener(provinceAdapter);
	        //設置頂部固定頭部  
			provinceListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_province_header, provinceListView, false)); 
			letterAdapter = new LetterAdapter(prosecList, this,this);
	        letterListView.setAdapter(letterAdapter);
		}
		
	}

	@Override
	public void displayCounties(List<Province> counties,String cityname,int status) {
		// TODO Auto-generated method stub	
		level = 3;
		changeTv.setText("< "+cityname);
		if(counties!=null && counties.size()>0){
			provinceAdapter=null;
			prosecList = null;
			prointList = null;
			proindexer = null;
			
			String[] sections;  
		    Integer[] counts;
			prosecList = new ArrayList<String>();
			prointList = new ArrayList<Integer>();
			for (Iterator iterator = counties.iterator(); iterator.hasNext();) {
				Province province = (Province) iterator.next();
				boolean finded = false;
				for(int i=0;i<prosecList.size();i++){
					if(prosecList.get(i).equals(province.getHead())){
						finded = true;
						prointList.set(i,prointList.get(i)+1);
						break;
					}
				}
				if(!finded){
					prosecList.add(province.getHead());
					prointList.add(1);
				}
			}
			sections = (String[])prosecList.toArray(new String[prosecList.size()]);
	        counts = (Integer[])prointList.toArray(new Integer[prointList.size()]);
	        proindexer = new MySectionIndexer(sections, counts);
			provinceAdapter = new ProvinceAdapter(counties, this, proindexer,level);
			provinceListView.setVisibility(View.VISIBLE);
			provinceListView.setLayoutAnimation(getListAnim(status));
			provinceListView.setAdapter(provinceAdapter);
			provinceListView.setOnScrollListener(provinceAdapter);
	        //設置頂部固定頭部  
			provinceListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_province_header, provinceListView, false)); 
	        letterAdapter = new LetterAdapter(prosecList, this,this);
	        letterListView.setAdapter(letterAdapter);
	        
		}
		else{
			prosecList = new ArrayList<String>();
			prointList = new ArrayList<Integer>();
			counties = new ArrayList<Province>();
			String[] sections = (String[])prosecList.toArray(new String[prosecList.size()]);
	        Integer[] counts = (Integer[])prointList.toArray(new Integer[prointList.size()]);
	        proindexer = new MySectionIndexer(sections, counts);
			provinceAdapter = new ProvinceAdapter(counties, this, proindexer,level);
			provinceListView.setVisibility(View.VISIBLE);
			provinceListView.setLayoutAnimation(getListAnim(status));
			provinceListView.setAdapter(provinceAdapter);
			provinceListView.setOnScrollListener(provinceAdapter);
	        //設置頂部固定頭部  
			provinceListView.setPinnedHeaderView(LayoutInflater.from(this).inflate(    
	                R.layout.cell_province_header, provinceListView, false)); 
			letterAdapter = new LetterAdapter(prosecList, this,this);
	        letterListView.setAdapter(letterAdapter);
		}
		
	}

	@Override
	public void showMessage(String mess) {
		// TODO Auto-generated method stub
		GFToast.show(this, mess);
	}
	
	private LayoutAnimationController getListAnim(int status) {  
        AnimationSet set = new AnimationSet(true);  
        Animation animation;
        if(status==0){
        	animation = AnimationUtils.loadAnimation(this,R.anim.anim_rightin);
        }
        else{
        	animation = AnimationUtils.loadAnimation(this,R.anim.anim_leftin);
        } 
        set.addAnimation(animation);   
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);  
        return controller;  
    }

	@Override
	public void displayGPSZone(int code,String zone) {
		// TODO Auto-generated method stub
		gpsTv.setText(zone);
		gpsTv.setTag(code);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dy = (int)event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int y = (int)event.getY();
			int x = (int)event.getX();	
			int deltay = Math.abs(dy-y);
			if(deltay>0){
				int i = letterListView.pointToPosition(x, y);
				if(i!=k){
					k=i;
					if(k!=-1){
						String alpha = letterAdapter.getItem(k).toString();
						int section = 0;
						if(prosecList!=null && prosecList.size()>0){
							for(int j=0;j<prosecList.size();j++){
								if(alpha.equals(prosecList.get(j))){
									section=j;
									break;
								}
							}
						}
						int pos = proindexer.getPositionForSection(section);
						provinceListView.setSelection(pos);
						showBigLetter(alpha);
					}
				}
			}			
			break;

		default:
			break;
		}
		return false;
	} 
	
	/***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
	        	
	        	if(level==2){
					req.backtoProvinces();
					return false;
				}
				if(level==3){
					req.backtoCities();
					return false;
				}
				finish();
	        } 
	    } 
		return false;

	}
	
	private void showBigLetter(String str){
		bigTv.setText(str);
		bigTv.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable()
		{  
		    public void run()
		    {  
		         bigTv.setVisibility(View.GONE);
		    }  
		 }, 100);  
	}
}

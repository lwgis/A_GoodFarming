package com.zhonghaodi.goodfarming;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	HomeFragment homeFragment;
	MessageFragment messageFragment;
	DiscoverFragment discoverFragment;
	MeFragment meFragment;
	ImageView homeIv;
	ImageView messageIv;
	ImageView discoverIv;
	ImageView meIv;
	TextView homeTv;
	TextView messageTv;
	TextView discoverTv;
	TextView meTv;
	View homeView;
	View messageView;
	View discoverView;
	View meView;
	int pageIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		homeView=findViewById(R.id.home_layout);
		messageView=findViewById(R.id.message_layout);
		discoverView=findViewById(R.id.discover_layout);
		meView=findViewById(R.id.me_layout);
		homeIv=(ImageView)findViewById(R.id.home_image);
		messageIv=(ImageView)findViewById(R.id.message_image);
		discoverIv=(ImageView)findViewById(R.id.discover_image);
		meIv=(ImageView)findViewById(R.id.me_image);
		homeTv=(TextView)findViewById(R.id.home_text);
		messageTv=(TextView)findViewById(R.id.message_text);
		discoverTv=(TextView)findViewById(R.id.discover_text);
		meTv=(TextView)findViewById(R.id.me_text);
		
		homeView.setOnClickListener(this);
		messageView.setOnClickListener(this);
		discoverView.setOnClickListener(this);
		meView.setOnClickListener(this);
		seletFragmentIndex(0);
		pageIndex=0;
	}
	private void seletFragmentIndex(int i) {
		FragmentTransaction transction= getFragmentManager().beginTransaction();
		if (homeFragment==null) {
			homeFragment=new HomeFragment();
			transction.add(R.id.content, homeFragment);
		}
		if (messageFragment==null) {
			messageFragment=new MessageFragment();
			transction.add(R.id.content, messageFragment);
		}
		if (discoverFragment==null) {
			discoverFragment=new DiscoverFragment();
			transction.add(R.id.content, discoverFragment);
		}
		if (meFragment==null) {
			meFragment=new MeFragment();
			transction.add(R.id.content, meFragment);
		}
		transction.hide(homeFragment);
		transction.hide(messageFragment);
		transction.hide(discoverFragment);
		transction.hide(meFragment);
		homeIv.setImageResource(R.drawable.home);
		messageIv.setImageResource(R.drawable.message);
		discoverIv.setImageResource(R.drawable.discover);
		meIv.setImageResource(R.drawable.me);
		homeTv.setTextColor(Color.rgb(128, 128, 128));
		messageTv.setTextColor(Color.rgb(128, 128, 128));
		discoverTv.setTextColor(Color.rgb(128, 128, 128));
		meTv.setTextColor(Color.rgb(128, 128, 128));

		switch (i) {
		case 0:
			transction.show(homeFragment);
			homeIv.setImageResource(R.drawable.home_s);
			homeTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 1:
			transction.show(messageFragment);
			messageIv.setImageResource(R.drawable.message_s);
			messageTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 2:
			transction.show(discoverFragment);
			discoverIv.setImageResource(R.drawable.discover_s);
			discoverTv.setTextColor(Color.rgb(12, 179, 136));
			break;
		case 3:
			transction.show(meFragment);
			meIv.setImageResource(R.drawable.me_s);
			meTv.setTextColor(Color.rgb(12, 179, 136));	
		break;
		default:
			break;
		}
		transction.commit();
		pageIndex=i;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v==homeView&&pageIndex!=0) {
			seletFragmentIndex(0);
		}
		if (v==messageView&&pageIndex!=1) {
			seletFragmentIndex(1);
		}
		if (v==discoverView&&pageIndex!=2) {
			seletFragmentIndex(2);
		}
		if (v==meView&&pageIndex!=3) {
			seletFragmentIndex(3);
		}
	}

}

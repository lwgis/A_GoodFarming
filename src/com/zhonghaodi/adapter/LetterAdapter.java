package com.zhonghaodi.adapter;

import java.util.List;

import com.zhonghaodi.goodfarming.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LetterAdapter extends BaseAdapter {
	
	private List<String> letters;
	private Context mContext;
	private OnTouchListener touchListener;
	
	class LetterHolder{
		public TextView letterTv;
		
		public LetterHolder(View view){
			letterTv = (TextView)view.findViewById(R.id.lettertv);
		}
	}
	
	public LetterAdapter(List<String> ls,Context context,OnTouchListener listener) {
		// TODO Auto-generated constructor stub
		letters = ls;
		mContext = context;
		touchListener = listener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return letters==null?0:letters.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return letters.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String letter = letters.get(position);
		LetterHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_letter, parent,false);
			holder = new LetterHolder(convertView);
			convertView.setTag(holder);
		}
		holder = (LetterHolder)convertView.getTag();
		holder.letterTv.setText(letter);
		return convertView;
	}

}

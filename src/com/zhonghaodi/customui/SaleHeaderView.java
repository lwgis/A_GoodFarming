package com.zhonghaodi.customui;

import com.zhonghaodi.goodfarming.R;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SaleHeaderView implements OnClickListener,OnQueryTextListener,TextWatcher {

	public View mainView;
	private TextView all;
	private TextView buy;
	private TextView sale;
	private EditText editText;
	private TextView searchText;
	private Context mContext;
	private MyHeaderView headerView;
	
	public SaleHeaderView(Context context,MyHeaderView hv) {
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.header_plant, null);
		mainView = view.findViewById(R.id.header_mainview);
		
		all = (TextView)view.findViewById(R.id.all_text);
		all.setOnClickListener(this);
		buy = (TextView)view.findViewById(R.id.buy_text);
		buy.setOnClickListener(this);
		sale = (TextView)view.findViewById(R.id.sale_text);
		sale.setOnClickListener(this);
		editText = (EditText)view.findViewById(R.id.edit_search);
		editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		editText.addTextChangedListener(this);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
					String key = editText.getText().toString();
					if(!TextUtils.isEmpty(key))
						onQueryTextSubmit(key);
					return true;
				}
				return false;
			}
		});
		searchText = (TextView)view.findViewById(R.id.search_text);
		searchText.setOnClickListener(this);
		headerView = hv;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.all_text:
			selectView(v);
			headerView.switchButton("");
			break;
		case R.id.buy_text:
			selectView(v);
			TextView textView =(TextView)v;
			headerView.switchButton(textView.getText().toString());
			break;
		case R.id.sale_text:
			selectView(v);
			TextView textView1 =(TextView)v;
			headerView.switchButton(textView1.getText().toString());
			break;
		case R.id.search_text:
			String key = editText.getText().toString();
			onQueryTextSubmit(key);
			break;
		default:
			break;
		}
	}
	
	public void selectView(View v){
		all.setTextColor(Color.rgb(128, 128, 128));
		all.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.topbar_null));
		buy.setTextColor(Color.rgb(128, 128, 128));
		buy.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.topbar_null));
		sale.setTextColor(Color.rgb(128, 128, 128));
		sale.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.topbar_null));
		
		TextView select = (TextView)v;
		select.setTextColor(Color.rgb(56, 190, 153));
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		headerView.search(query);
		hideInput(mContext,editText);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public interface MyHeaderView{
		public void switchButton(String type);
		public void search(String key);
		public void textChange(String key);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		headerView.textChange(s.toString());
	}
	
	private void hideInput(Context context,View view){
        InputMethodManager inputMethodManager =
        (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}

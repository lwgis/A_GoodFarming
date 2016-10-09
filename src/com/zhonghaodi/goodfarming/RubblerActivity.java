package com.zhonghaodi.goodfarming;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duguang.baseanimation.ui.customview.serchfly.KeywordsFlow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zhonghaodi.api.NoDoubleClickListener;
import com.zhonghaodi.customui.GFToast;
import com.zhonghaodi.customui.GuaGuaKa;
import com.zhonghaodi.customui.MyTextButton;
import com.zhonghaodi.goodfarming.SecondActivity.TimeCount;
import com.zhonghaodi.model.Agrotechnical;
import com.zhonghaodi.model.GFAreaUtil;
import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.Gua;
import com.zhonghaodi.model.GuaGuaTipDto;
import com.zhonghaodi.model.GuaOrder;
import com.zhonghaodi.model.GuaResult;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.onWipeListener;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;

public class RubblerActivity extends Activity implements OnClickListener,onWipeListener,HandMessage {

	private GuaGuaKa guaGuaKa;    
    public String[] keywords;  
    private GuaResult guaResult;
    private KeywordsFlow keywordsFlow;   
    private TextView ordersTextView;
    private LinearLayout startLayout;
    private MyTextButton startButton;
    private TextView tipTv;
    private TextView numberTv;
    private boolean isOpen = false;
    private boolean isStart = false;
    private GFHandler<RubblerActivity> handler = new GFHandler<RubblerActivity>(this);
    private boolean isToday = false;
    private String serverTime;
    private int zone;
    private int guanumber=0;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbler);    
        keywordsFlow = (KeywordsFlow) findViewById(R.id.frameLayout1);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnClickListener(this);
		
		startLayout = (LinearLayout)findViewById(R.id.start_layout);
		startButton = (MyTextButton)findViewById(R.id.start_btn);
		startButton.setOnClickListener(new NoDoubleClickListener() {
			
			@Override
			public void onNoDoubleClick(View v) {
				// TODO Auto-generated method stub
				if(guanumber<=0){
					GFToast.show(getApplicationContext(),"不要太过执着了，明天再来试试吧。");
				}
				else{
					chouqian();
				}
			}
		});
		
		guaGuaKa = (GuaGuaKa)findViewById(R.id.guagua);
		guaGuaKa.setmWipeListener(this);
		guaGuaKa.setVisibility(View.GONE);

		ordersTextView = (TextView)findViewById(R.id.orders_text);
		tipTv = (TextView)findViewById(R.id.guatip_text);
		numberTv = (TextView)findViewById(R.id.guanumber_text);
		
		zone = GFAreaUtil.getCityId(this);
 
		loadData();
    }
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("刮刮乐");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("刮刮乐");
		MobclickAgent.onPause(this);
	}
    
    private void loadData(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				String uid = GFUserDictionary.getUserId(getApplicationContext());
				String jsonString4 = HttpUtil.getGuaGuaTip(uid, zone);
				Message msg4 = handler.obtainMessage();
				msg4.what = 4;
				msg4.obj = jsonString4;
				msg4.sendToTarget();
				
				String jsonString = HttpUtil.getGuaGua(zone);
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();	
				
				String jsonString1 = HttpUtil.getServerTime();
				Message msg1 = handler.obtainMessage();
				msg1.what = 3;
				msg1.obj = jsonString1;
				msg1.sendToTarget();
				
				String jsonString2 = HttpUtil.getRecentOrders(zone);
				Message msg2 = handler.obtainMessage();
				msg2.what = 2;
				msg2.obj = jsonString2;
				msg2.sendToTarget();	
				
				
			}
		}).start();
    }
    
    private void chouqian(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {

				String uid = GFUserDictionary.getUserId(getApplicationContext());
				NetResponse netResponse = HttpUtil.qian(uid,zone);
				Message msg1 = handler.obtainMessage();
				if(netResponse.getStatus()==1){
					msg1.what = 1;
					msg1.obj = netResponse.getResult();
				}
				else{
					msg1.what = -1;
					msg1.obj = netResponse.getMessage();
				}
				msg1.sendToTarget();

			}
		}).start();
    }
    
    private void cancelGua(){
    	new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String uid = GFUserDictionary.getUserId(getApplicationContext());
					HttpUtil.guaCancel(uid,guaResult.getOid());
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Message msg = handler.obtainMessage();
					msg.what = -1;
					msg.obj="刮奖取消错误";
					msg.sendToTarget();
				}
			}
		}).start();
    }
    
    private void checkToday(String str){
    	serverTime = str;
		SharedPreferences deviceInfo = getSharedPreferences("GuaGuaLe", 0);
        String guatime = deviceInfo.getString("guatime", "");
        if (guatime.equals("") || guatime==null) {
			isToday = false;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			Date date = dateFormat.parse(str);
			Date guaDate = dateFormat.parse(guatime);
			Calendar cal1 = Calendar.getInstance(); 
		    Calendar cal2 = Calendar.getInstance(); 
		    cal1.setTime(date); 
		    cal2.setTime(guaDate);
		    
		    int yi = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		    if (yi>0) {
				isToday=false;
			}
		    else{
		    	int di = cal1.get(Calendar.DAY_OF_YEAR)-cal2.get(Calendar.DAY_OF_YEAR);
		    	if (di>0) {
					isToday = false;
				}
		    	else{
		    		isToday = true;
		    	}
		    }
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
    private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}
    
    /***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN) { 
	        	if(!isStart){
//	        		if(guaResult.isSuccess()){
//	        			cancelGua();
//	        		}
	        		finish();
	        	}
	        	else{
	        		if(!isOpen){
	        			final Dialog dialog = new Dialog(this, R.style.MyDialog);
	        	        //设置它的ContentView
	        			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	        View layout = inflater.inflate(R.layout.dialog, null);
	        	        dialog.setContentView(layout);
	        	        TextView contentView = (TextView)layout.findViewById(R.id.contentTxt);
	        	        TextView titleView = (TextView)layout.findViewById(R.id.dialog_title);
	        	        Button okBtn = (Button)layout.findViewById(R.id.dialog_button_ok);
	        	        okBtn.setText("确定退出");
	        	        okBtn.setOnClickListener(new OnClickListener() {
	        				
	        				@Override
	        				public void onClick(View v) {
	        					// TODO Auto-generated method stub
	        					dialog.dismiss();
	        					if(guaResult.isSuccess()){
	        	        			cancelGua();
	        	        		}
	        					finish();
	        				}
	        			});
	        	        Button cancelButton = (Button)layout.findViewById(R.id.dialog_button_cancel);
	        	        cancelButton.setText("继续刮完");
	        	        cancelButton.setOnClickListener(new OnClickListener() {
	        				
	        				@Override
	        				public void onClick(View v) {
	        					// TODO Auto-generated method stub
	        					dialog.dismiss();
	        				}
	        			});
	        	        titleView.setText("提示");
	        	        contentView.setText("您还没有刮开奖卷，确定要退出吗？");
	        	        dialog.setCancelable(false);
	        	        dialog.show();
		        	}
	        		else{
	        			finish();
	        		}
	        	}
	        	
	        } 
	    } 
		return false;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.frameLayout1:
			keywordsFlow.rubKeywords();
			feedKeywordsFlow(keywordsFlow, keywords);
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			break;

		default:
			break;
		}
	}

	@Override
	public void onWipe(int percent) {
		// TODO Auto-generated method stub
		isOpen = true;
		if(guaResult.isSuccess()){
			GFToast.show(getApplicationContext(),"恭喜您刮中"+guaResult.getGuagua().getName()+"，请您填写收货地址。");
			Intent intent = new Intent(RubblerActivity.this, GuaConfirmActivity.class);
			intent.putExtra("commodity", guaResult);
			RubblerActivity.this.startActivity(intent);
			RubblerActivity.this.finish();

		}
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
			if(msg.obj!=null){
				if(msg.obj.toString().trim().length()>=1)
					GFToast.show(getApplicationContext(),msg.obj.toString());
			}
			break;
		case 0:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Gua> agrs = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Gua>>() {
						}.getType());
				if(agrs!=null && agrs.size()>0){
					keywords = new String[agrs.size()];
					for(int i = 0;i<agrs.size();i++){
						Gua gua = agrs.get(i);
						keywords[i]=gua.getName();
					}
					// 添加
					feedKeywordsFlow(keywordsFlow, keywords);
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				}
				else{
					keywords = new String[1];
					keywords[0]="暂无奖品";
					// 添加
					feedKeywordsFlow(keywordsFlow, keywords);
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
				}
				
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 1:
			if(msg.obj!=null){
				
				SharedPreferences deviceInfo = getSharedPreferences("GuaGuaLe", 0);
				deviceInfo.edit().putString("guatime", serverTime).commit();
				Gson gson = new Gson();
				guaResult = gson.fromJson(msg.obj.toString(),
						new TypeToken<GuaResult>() {
						}.getType());
				if(guaResult.isSuccess()){
					guaGuaKa.setmText(guaResult.getGuagua().getName());
				}
				else{
					guaGuaKa.setmText("谢谢参与！");
				}
				startLayout.setVisibility(View.GONE);
				guaGuaKa.setVisibility(View.VISIBLE);
				isStart = true;
			}
			else{
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 2:
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<GuaOrder> orders = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<GuaOrder>>() {
						}.getType());
				if(orders!=null && orders.size()>0){
					String content = "";
					String split = "";
					for(int i = 0;i<orders.size();i++){
						GuaOrder order = orders.get(i);
						content += order.getUser().getAlias()+"刮到了"+order.getGuagua().getName()+"\n";

					}
					ordersTextView.setText(content);
				}
				
			} else {
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;
		case 3:
			String timeString = msg.obj.toString();
			if(timeString==null || timeString.isEmpty()){
				GFToast.show(getApplicationContext(),"获取系统时间错误,不能继续刮奖。");
			}
			else{
				checkToday(timeString);
			}
			break;
		case 4:
			if(msg.obj!=null){

				Gson gson = new Gson();
				GuaGuaTipDto tipDto = gson.fromJson(msg.obj.toString(),
						new TypeToken<GuaGuaTipDto>() {
						}.getType());
				if(tipDto!=null){
					tipTv.setText("拼手气赢奖品"+tipDto.getPoints()+"积分刮一次");
					numberTv.setText("您还有"+tipDto.getNumbers()+"次机会");
					guanumber = tipDto.getNumbers();
				}
			}
			else{
				GFToast.show(getApplicationContext(),"连接服务器失败,请稍候再试!");
			}
			break;

		default:
			break;
		}
	}
}

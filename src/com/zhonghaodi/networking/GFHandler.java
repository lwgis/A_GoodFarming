package com.zhonghaodi.networking;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public  class GFHandler<T> extends Handler {
	WeakReference<T> reference;
	HandMessage handMessage;

	public GFHandler(T r){
		reference=new WeakReference<T>(r);
		handMessage=(HandMessage)r;
	}
	public void setHandMassage(HandMessage hMessage) {
		handMessage=hMessage;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		if (handMessage!=null) {
			handMessage.handleMessage(msg,reference.get());
		}
	}
	public interface HandMessage{
		void handleMessage(Message msg,Object object);
	}
}

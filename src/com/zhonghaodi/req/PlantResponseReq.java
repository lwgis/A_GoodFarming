package com.zhonghaodi.req;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zhonghaodi.model.GFUserDictionary;
import com.zhonghaodi.model.NetImage;
import com.zhonghaodi.model.NetResponse;
import com.zhonghaodi.model.ProjectImage;
import com.zhonghaodi.model.Response;
import com.zhonghaodi.model.User;
import com.zhonghaodi.networking.GFHandler;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageUtil;
import com.zhonghaodi.networking.GFHandler.HandMessage;
import com.zhonghaodi.view.PlantResponseView;

import android.content.Context;
import android.os.Message;
import android.util.Log;

public class PlantResponseReq implements HandMessage {
	
	private  final int TypeResponse = 1;
	private  final int TypeImage = 2;
	private  final int TypeNoImage = 3;
	private  final int TypeError = -1;
	public int queid;
	private NetImage[] netImages;
	public ArrayList<ProjectImage> projectImages;
	private int imageCount;
	public boolean isSending;
	private GFHandler<PlantResponseReq> handler;
	private PlantResponseView view;
	private Context context;
	private ExecutorService executorService = Executors.newFixedThreadPool(4);
	
	public PlantResponseReq(PlantResponseView v,Context c) {
		// TODO Auto-generated constructor stub
		handler = new GFHandler<PlantResponseReq>(this);
		view = v;
		context = c;
		isSending = false;
		this.projectImages=new ArrayList<ProjectImage>();
	}
	
	public void send(){
		
		if (projectImages!=null&&projectImages.size()>0) {
			netImages = new NetImage[projectImages.size()];
			imageCount = 0;
			for (int i = 0; i <projectImages.size(); i++) {
				final int index = i;
				executorService.submit(new Runnable() {
                    public void run() {
                    	try {
                    		
							String imageName;
							imageName = ImageUtil.uploadImage(projectImages.get(index).getImage(),"plant");
							if(imageName==null || imageName.isEmpty() || imageName.equals("error")){
								Message msg = handler.obtainMessage();
								msg.what = TypeError;
								msg.sendToTarget();
								return;
							}
							NetImage netImage = new NetImage();
							netImage.setUrl(imageName.trim());
							netImages[index] = netImage;
							Message msg = handler.obtainMessage();
							msg.what = TypeImage;
							msg.obj = imageName.trim();
							msg.sendToTarget();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							isSending = false;
							Message msg = handler.obtainMessage();
							msg.what = TypeError;
							msg.obj = e.getMessage();
							msg.sendToTarget();
						}
                    }
            });
			}
		} else {
			Message msg = handler.obtainMessage();
			msg.what = TypeNoImage;
			msg.sendToTarget();
		}
	}
	
	public void sendResponse(final Response response,final int qid){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					 NetResponse netResponse = HttpUtil.sendResponseForPlant(response, qid);
					Message msg = handler.obtainMessage();
					msg.what = TypeResponse;
					msg.obj = netResponse;
					msg.sendToTarget();
				} catch (Throwable e) {
					Message msg = handler.obtainMessage();
					msg.what = TypeError;
					msg.obj = e.getMessage().toString();
					msg.sendToTarget();
				}
			}
		}).start();
	}

	@Override
	public void handleMessage(Message msg, Object object) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case TypeImage:
			imageCount++;
			if (imageCount == projectImages.size()) {
				Response response = new Response();
				response.setContent(view.getContent());
				User writer = new User();
				writer.setId(GFUserDictionary.getUserId(context));
				response.setWriter(writer);
				response.setAttachments(Arrays.asList(netImages));
				sendResponse(response, queid);
			}	
			break;
		case TypeNoImage:
			Response response1 = new Response();
			response1.setContent(view.getContent());
			User writer = new User();
			writer.setId(GFUserDictionary.getUserId(context));
			response1.setWriter(writer);
			sendResponse(response1, queid);
			break;
		case TypeResponse:
			if(msg.obj==null){
				view.showMessage("发送失败");
			}
			else{
				NetResponse response = (NetResponse)msg.obj;
				if(response.getStatus()!=1){
					view.showMessage("发送失败:"+response.getMessage());
				}
				else{
					view.showMessage("发送成功");
					
				}
			}
			isSending = false;
			break;
		case TypeError:
			if(msg.obj==null){
				Log.d("uploadimageError", "空");
			}
			else{
				Log.d("uploadimageError", msg.obj.toString());
			}
			view.showMessage("发送失败");
			break;
		default:
			break;
		}
	}

	
}

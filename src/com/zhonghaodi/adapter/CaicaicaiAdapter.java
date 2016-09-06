package com.zhonghaodi.adapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.CaicaicaiHolder;
import com.zhonghaodi.customui.GFImageView;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CaicaicaiAdapter extends BaseAdapter {
	
	private List<Caicaicai> cailist;
	private Context mContext;
	private OnClickListener mClickListener;
	
	public CaicaicaiAdapter(List<Caicaicai> cais,Context context,OnClickListener listener) {
		// TODO Auto-generated constructor stub
		cailist = cais;
		mContext = context;
		mClickListener = listener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cailist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cailist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return cailist.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Caicaicai caicaicai = cailist.get(position);
		CaicaicaiHolder holder;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_caicaicai, parent,false);
			holder = new CaicaicaiHolder(convertView);
			convertView.setTag(holder);
		}
		holder = (CaicaicaiHolder)convertView.getTag();
		holder.hideImageviews();
		holder.shareLayout.setTag(caicaicai);
		holder.shareLayout.setOnClickListener(mClickListener);
		if(caicaicai.getStatus()==0){
			holder.imgLayout2.setVisibility(View.GONE);
			holder.answerView.setVisibility(View.GONE);
			holder.splitLine.setVisibility(View.GONE);
			holder.commentView.setVisibility(View.GONE);
			holder.contentView.setText(caicaicai.getContent());
			holder.responseView.setText("答案（"+caicaicai.getResponseCount()+"）");
			if(caicaicai.getAttachments1()!=null && caicaicai.getAttachments1().size()>0){
				for (int i = 0; i < caicaicai.getAttachments1().size(); i++) {
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"cai/small/"
									+ caicaicai.getAttachments1().get(i).getUrl(),
							holder.imageViews1.get(i), ImageOptions.options);
					holder.imageViews1.get(i).setVisibility(View.VISIBLE);
					holder.imageViews1.get(i).setIndex(i);
					holder.imageViews1.get(i).setImages(caicaicai.getAttachments1(),"cai");
				}
			}
		}
		else{
			holder.imgLayout2.setVisibility(View.VISIBLE);
			holder.answerView.setVisibility(View.VISIBLE);
			holder.splitLine.setVisibility(View.VISIBLE);
			holder.commentView.setVisibility(View.VISIBLE);
			holder.contentView.setText(caicaicai.getContent());
			holder.responseView.setText("答案（"+caicaicai.getResponseCount()+"）");
			holder.commentView.setText("评价（"+caicaicai.getCommentCount()+"）");
			holder.answerView.setText("正确答案："+caicaicai.getShowAnswer());
			if(caicaicai.getAttachments1()!=null && caicaicai.getAttachments1().size()>0){
				if(caicaicai.getAttachments1().size()==1){
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"cai/small/"
									+ caicaicai.getAttachments1().get(0).getUrl(),
							holder.imageViews1.get(0), ImageOptions.options);
					holder.imageViews1.get(0).setVisibility(View.VISIBLE);
					holder.imageViews1.get(0).setIndex(0);
					holder.imageViews1.get(0).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
					
					ImageLoader.getInstance().displayImage(
							HttpUtil.ImageUrl+"cai/small/"
									+ caicaicai.getShowAttachments2().get(0).getUrl(),
							holder.imageViews1.get(1), ImageOptions.options);
					holder.imageViews1.get(1).setVisibility(View.VISIBLE);
					holder.imageViews1.get(1).setIndex(1);
					holder.imageViews1.get(1).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
				}
				else{
					for (int i = 0; i < caicaicai.getAttachments1().size(); i++) {
						ImageLoader.getInstance().displayImage(
								HttpUtil.ImageUrl+"cai/small/"
										+ caicaicai.getAttachments1().get(i).getUrl(),
								holder.imageViews1.get(i), ImageOptions.options);
						holder.imageViews1.get(i).setVisibility(View.VISIBLE);
						holder.imageViews1.get(i).setIndex(i*2);
						holder.imageViews1.get(i).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
						
						ImageLoader.getInstance().displayImage(
								HttpUtil.ImageUrl+"cai/small/"
										+ caicaicai.getShowAttachments2().get(i).getUrl(),
								holder.imageViews2.get(i), ImageOptions.options);
						holder.imageViews2.get(i).setVisibility(View.VISIBLE);
						holder.imageViews2.get(i).setIndex(i*2+1);
						holder.imageViews2.get(i).setImages2(caicaicai.getAttachments1(),caicaicai.getShowAttachments2(),"cai");
					}
				}
			}
		}
		return convertView;
	}

}

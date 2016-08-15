package com.zhonghaodi.adapter;

import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.UrlTextView;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.CaiComment;
import com.zhonghaodi.model.CaiResponse;
import com.zhonghaodi.model.Caicaicai;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CaiResponseAdapter extends BaseAdapter {
	
	class CaiResponseHolder{
		public RoundedImageView headIv;
		public UrlTextView contentTv;
		public TextView timeTv;
		public TextView nameTv;
		public TextView levelTextView;
		public ImageView correctImg;
		public ImageView rewardImg;
		
		public CaiResponseHolder(View view) {
			// TODO Auto-generated constructor stub
			headIv = (RoundedImageView) view.findViewById(R.id.head_image);
			contentTv = (UrlTextView) view.findViewById(R.id.content_text);
			timeTv = (TextView) view.findViewById(R.id.time_text);
			nameTv = (TextView) view.findViewById(R.id.name_text);
			levelTextView = (TextView) view.findViewById(R.id.level_text);
			correctImg = (ImageView) view.findViewById(R.id.correct_image);
			rewardImg = (ImageView) view.findViewById(R.id.reward_image);
		}
	}
	
	private Caicaicai caicaicai;
	private Context mContext;
	private int status;
	
	public CaiResponseAdapter(Caicaicai c,Context context,int s) {
		// TODO Auto-generated constructor stub
		caicaicai = c;
		mContext = context;
		status = s;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(status==0){
			return caicaicai.getResponseCount();
		}
		else{
			return caicaicai.getCommentCount();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(status==0){
			return caicaicai.getResponses().get(position);
		}
		else{
			return caicaicai.getComments().get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if(status==0){
			return caicaicai.getResponses().get(position).getId();
		}
		else{
			return caicaicai.getComments().get(position).getId();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CaiResponseHolder holder;
		CaiComment response;
		if(status==0){
			response = caicaicai.getResponses().get(position);
		}
		else{
			response = caicaicai.getComments().get(position);
		}
		
		if(convertView==null){
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.cell_cai_response, parent, false);
			holder = new CaiResponseHolder(convertView);
			convertView.setTag(holder);
		}
		holder = (CaiResponseHolder)convertView.getTag();
		ImageLoader.getInstance().displayImage(
				HttpUtil.ImageUrl+"users/small/"
						+ response.getWriter().getThumbnail(),
				holder.headIv, ImageOptions.options);
		holder.nameTv.setText(response.getWriter().getAlias());
		holder.contentTv.setText(response.getContent());		
		holder.timeTv.setText(response.getTime());
		if(status==0){
			CaiResponse cr = (CaiResponse)response;
			if(cr.isCorrect()){
				holder.correctImg.setVisibility(View.VISIBLE);
			}
			else{
				holder.correctImg.setVisibility(View.GONE);
			}
		}
		else{
			holder.correctImg.setVisibility(View.GONE);
		}
		if(response.isWin()){
			holder.rewardImg.setVisibility(View.VISIBLE);
		}
		else{
			holder.rewardImg.setVisibility(View.GONE);
		}
		switch (response.getWriter().getLevelID()) {
		case 1:
			holder.levelTextView.setText(response.getWriter().getIdentifier()+"农友");
			holder.levelTextView.setBackgroundResource(R.drawable.back_ny);
			break;
		case 2:
			holder.levelTextView.setText(response.getWriter().getIdentifier()+"农技达人");
			holder.levelTextView.setBackgroundResource(R.drawable.back_dr);
			break;
		case 3:
			if(response.getWriter().isTeamwork()){
				holder.levelTextView.setText(response.getWriter().getIdentifier()+"农资店-合作店");
			}
			else{
				holder.levelTextView.setText(response.getWriter().getIdentifier()+"农资店");
			}
			
			holder.levelTextView.setBackgroundResource(R.drawable.back_dp);
			break;
		case 4:
			holder.levelTextView.setText("专家");
			holder.levelTextView.setBackgroundResource(R.drawable.back_zj);
			break;
		case 5:
			holder.levelTextView.setText("官方");
			holder.levelTextView.setBackgroundResource(R.drawable.back_gf);
			break;
		default:
			break;
		}
		return convertView;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}

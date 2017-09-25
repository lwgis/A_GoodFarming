package com.zhonghaodi.adapter;

import java.util.List;

import com.google.zxing.common.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.customui.Holder1;
import com.zhonghaodi.customui.Holder2;
import com.zhonghaodi.customui.Holder3;
import com.zhonghaodi.customui.HolderPlant1;
import com.zhonghaodi.customui.HolderPlant2;
import com.zhonghaodi.customui.HolderPlant3;
import com.zhonghaodi.customui.HolderPlant4;
import com.zhonghaodi.goodfarming.R;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;
import com.zhonghaodi.utils.PublicHelper;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public class QuestionAdpter extends BaseAdapter {
	
	List<Question> allQuestions;
	Context mContext;
	OnClickListener mClickListener;
	int status;
	
	public QuestionAdpter(List<Question> questions,Context context,OnClickListener clickListener,int s){
		
		allQuestions = questions;
		mContext = context;
		mClickListener = clickListener;
		status = s;
		
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Question> getAllQuestions() {
		return allQuestions;
	}



	public void setAllQuestions(List<Question> allQuestions) {
		this.allQuestions = allQuestions;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allQuestions.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return allQuestions.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		Question question = allQuestions.get(position);
		if(status==0||status==1||status==3){
			if (question.getAttachments()==null||question.getAttachments().size() == 0) {
				return 0;
			}
			if (question.getAttachments().size() > 0
					&& question.getAttachments().size() < 4) {
				return 1;
			}
			if (question.getAttachments().size() > 3) {
				return 2;
			}
		}
		else{
			if (question.getAttachments()==null||question.getAttachments().size() == 0) {
				return 3;
			}
			if (question.getAttachments().size() > 0
					&& question.getAttachments().size() < 4) {
				return 4;
			}
			if (question.getAttachments().size() > 3 && question.getAttachments().size() <7) {
				return 5;
			}
			if(question.getAttachments().size() > 6){
				return 6;
			}
		}
		
		return super.getItemViewType(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Question question = allQuestions.get(position);
		int type = getItemViewType(position);
		Holder1 holder1 = null;
		Holder2 holder2 = null;
		Holder3 holder3 = null;
		HolderPlant1 holderPlant1 = null;
		HolderPlant2 holderPlant2 = null;
		HolderPlant3 holderPlant3 = null;
		HolderPlant4 holderPlant4 = null;
		if (convertView == null) {
			switch (type) {
			case 0:
				convertView = LayoutInflater.from(
						mContext).inflate(
						R.layout.cell_question, parent, false);
				holder1 = new Holder1(convertView);
				convertView.setTag(holder1);
				break;
			case 1:
				convertView = LayoutInflater.from(
						mContext).inflate(
						R.layout.cell_question_3_image, parent, false);
				holder2 = new Holder2(convertView);			
				convertView.setTag(holder2);
				break;
			case 2:
				convertView = LayoutInflater.from(
						mContext).inflate(
						R.layout.cell_question_6_image, parent, false);
				holder3 = new Holder3(convertView);
				convertView.setTag(holder3);
				break;
			case 3:
				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.cell_plant, parent, false);
				holderPlant1 = new HolderPlant1(convertView);
				convertView.setTag(holderPlant1);
				break;
			case 4:
				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.cell_plant_3_image, parent,
								false);
				holderPlant2 = new HolderPlant2(convertView);
				convertView.setTag(holderPlant2);
				break;
			case 5:
				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.cell_plant_6_image, parent,
								false);
				holderPlant3 = new HolderPlant3(convertView);
				convertView.setTag(holderPlant3);
				break;
			case 6:
				convertView = LayoutInflater.from(
						mContext).inflate(
						R.layout.cell_plant_9_image, parent, false);
				holderPlant4 = new HolderPlant4(convertView);
				convertView.setTag(holderPlant4);
				break;
			default:
				break;
			}
		}
		String content = PublicHelper.TrimRight(question.getContent());
		
		switch (type) {
		case 0:
			holder1 = (Holder1) convertView.getTag();
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ question.getWriter().getThumbnail(),
					holder1.headIv, ImageOptions.options);
			holder1.nameTv.setText(question.getWriter().getAlias());
			holder1.timeTv.setText(question.getTime());
			holder1.contentTv.setText(content);
			
			holder1.countTv.setText("已有" + question.getResponsecount()
					+ "个答案");
			if(status==0 || status==3){
				if(question.getCrop()!=null){
					holder1.cropTv.setVisibility(View.VISIBLE);
					holder1.cropTv.setText(question.getCrop().getName());
				}
				else{
					holder1.cropTv.setVisibility(View.GONE);
				}
			}
			else{
				holder1.cropTv.setVisibility(View.INVISIBLE);
			}
			holder1.headIv.setTag(question.getWriter());
			holder1.headIv.setOnClickListener(mClickListener);
			holder1.forwardTextView.setText("转发");
			holder1.forwardLayout.setTag(question);
			holder1.forwardLayout.setOnClickListener(mClickListener);
			if(question.getAddress()!=null){
				holder1.addressTextView.setText(question.getAddress());
			}
			if(question.isFine()){
				holder1.jpLayout.setVisibility(View.VISIBLE);
			}
			else{
				holder1.jpLayout.setVisibility(View.GONE);
			}
			switch (question.getWriter().getLevelID()) {
			case 1:
				holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
				holder1.levelTextView.setBackgroundResource(R.drawable.back_ny);
				break;
			case 2:
				holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
				holder1.levelTextView.setBackgroundResource(R.drawable.back_dr);
				break;
			case 3:
				if(question.getWriter().isTeamwork()){
					holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
				}
				else{
					holder1.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
				}
				holder1.levelTextView.setBackgroundResource(R.drawable.back_dp);
				break;
			case 4:
				holder1.levelTextView.setText("专家");
				holder1.levelTextView.setBackgroundResource(R.drawable.back_zj);
				break;
			case 5:
				holder1.levelTextView.setText("官方");
				holder1.levelTextView.setBackgroundResource(R.drawable.back_gf);
				break;
			default:
				break;
			}
			break;
		case 1:
			holder2 = (Holder2) convertView.getTag();
			holder2.reSetImageViews();
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ question.getWriter().getThumbnail(),
					holder2.headIv, ImageOptions.options);
			holder2.nameTv.setText(question.getWriter().getAlias());
			holder2.contentTv.setText(content);
			
			holder2.timeTv.setText(question.getTime());
			if(status==0){
				if(question.getCrop()!=null){
					holder2.cropTv.setVisibility(View.VISIBLE);
					holder2.cropTv.setText(question.getCrop().getName());
				}
				else{
					holder2.cropTv.setVisibility(View.GONE);
				}
			}
			else{
				holder2.cropTv.setVisibility(View.INVISIBLE);
			}
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"questions/small/"
							+ question.getAttachments().get(0).getUrl(),
					holder2.imageView1, ImageOptions.options);
			holder2.imageView1.setVisibility(View.VISIBLE);
			holder2.imageView1.setIndex(0);
			holder2.imageView1.setImages(question.getAttachments(),"questions");
			if (question.getAttachments().size() > 1) {
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"questions/small/"
										+ question.getAttachments().get(1)
												.getUrl(),
								holder2.imageView2, ImageOptions.options);
				holder2.imageView2.setVisibility(View.VISIBLE);
				holder2.imageView2.setIndex(1);
				holder2.imageView2.setImages(question.getAttachments(),"questions");
			}
			if (question.getAttachments().size() > 2) {
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"questions/small/"
										+ question.getAttachments().get(2)
												.getUrl(),
								holder2.imageView3, ImageOptions.options);
				holder2.imageView3.setVisibility(View.VISIBLE);
				holder2.imageView3.setIndex(2);
				holder2.imageView3.setImages(question.getAttachments(),"questions");
			}
			holder2.countTv.setText("已有" + question.getResponsecount()
					+ "个答案");
			holder2.headIv.setTag(question.getWriter());
			holder2.headIv.setOnClickListener(mClickListener);
			holder2.forwardTextView.setText("转发");
			holder2.forwardLayout.setTag(question);
			holder2.forwardLayout.setOnClickListener(mClickListener);
			if(question.getAddress()!=null){
				holder2.addressTextView.setText(question.getAddress());
			}
			if(question.isFine()){
				holder2.jpLayout.setVisibility(View.VISIBLE);
			}
			else{
				holder2.jpLayout.setVisibility(View.GONE);
			}
			switch (question.getWriter().getLevelID()) {
			case 1:
				holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
				holder2.levelTextView.setBackgroundResource(R.drawable.back_ny);
				break;
			case 2:
				holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
				holder2.levelTextView.setBackgroundResource(R.drawable.back_dr);
				break;
			case 3:
				if(question.getWriter().isTeamwork()){
					holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
				}
				else{
					holder2.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
				}
				
				holder2.levelTextView.setBackgroundResource(R.drawable.back_dp);
				break;
			case 4:
				holder2.levelTextView.setText("专家");
				holder2.levelTextView.setBackgroundResource(R.drawable.back_zj);
				break;
			case 5:
				holder2.levelTextView.setText("官方");
				holder2.levelTextView.setBackgroundResource(R.drawable.back_gf);
				break;
			default:
				break;
			}
			break;
		case 2:
			holder3 = (Holder3) convertView.getTag();
			holder3.reSetImageViews();
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"users/small/"
							+ question.getWriter().getThumbnail(),
					holder3.headIv, ImageOptions.options);
			holder3.nameTv.setText(question.getWriter().getAlias());
			holder3.timeTv.setText(question.getTime());
			holder3.contentTv.setText(content);
			if(status==0){
				if(question.getCrop()!=null){
					holder3.cropTv.setVisibility(View.VISIBLE);
					holder3.cropTv.setText(question.getCrop().getName());
				}
				else{
					holder3.cropTv.setVisibility(View.GONE);
				}
			}
			else{
				holder3.cropTv.setVisibility(View.INVISIBLE);
			}
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"questions/small/"
							+ question.getAttachments().get(0).getUrl(),
					holder3.imageView1, ImageOptions.options);
			holder3.imageView1.setVisibility(View.VISIBLE);
			holder3.imageView1.setIndex(0);
			holder3.imageView1.setImages(question.getAttachments(),"questions");
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"questions/small/"
							+ question.getAttachments().get(1).getUrl(),
					holder3.imageView2, ImageOptions.options);
			holder3.imageView2.setVisibility(View.VISIBLE);
			holder3.imageView2.setIndex(1);
			holder3.imageView2.setImages(question.getAttachments(),"questions");
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"questions/small/"
							+ question.getAttachments().get(2).getUrl(),
					holder3.imageView3, ImageOptions.options);
			holder3.imageView3.setVisibility(View.VISIBLE);
			holder3.imageView3.setIndex(2);
			holder3.imageView3.setImages(question.getAttachments(),"questions");
			ImageLoader.getInstance().displayImage(
					HttpUtil.ImageUrl+"questions/small/"
							+ question.getAttachments().get(3).getUrl(),
					holder3.imageView4, ImageOptions.options);
			holder3.imageView4.setVisibility(View.VISIBLE);
			holder3.imageView4.setIndex(3);
			holder3.imageView4.setImages(question.getAttachments(),"questions");
			if (question.getAttachments().size() > 4) {
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"questions/small/"
										+ question.getAttachments().get(4)
												.getUrl(),
								holder3.imageView5, ImageOptions.options);
				holder3.imageView5.setVisibility(View.VISIBLE);
				holder3.imageView5.setIndex(4);
				holder3.imageView5.setImages(question.getAttachments(),"questions");
			}
			if (question.getAttachments().size() > 5) {
				ImageLoader.getInstance()
						.displayImage(
								HttpUtil.ImageUrl+"questions/small/"
										+ question.getAttachments().get(5)
												.getUrl(),
								holder3.imageView6, ImageOptions.options);
				holder3.imageView6.setVisibility(View.VISIBLE);
				holder3.imageView6.setIndex(5);
				holder3.imageView6.setImages(question.getAttachments(),"questions");
			}
			holder3.countTv.setText("已有" + question.getResponsecount()
					+ "个答案");
			holder3.headIv.setTag(question.getWriter());
			holder3.headIv.setOnClickListener(mClickListener);
			holder3.forwardTextView.setText("转发");
			holder3.forwardLayout.setTag(question);
			holder3.forwardLayout.setOnClickListener(mClickListener);
			if(question.getAddress()!=null){
				holder3.addressTextView.setText(question.getAddress());
			}
			if(question.isFine()){
				holder3.jpLayout.setVisibility(View.VISIBLE);
			}
			else{
				holder3.jpLayout.setVisibility(View.GONE);
			}
			switch (question.getWriter().getLevelID()) {
			case 1:
				holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
				holder3.levelTextView.setBackgroundResource(R.drawable.back_ny);
				break;
			case 2:
				holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
				holder3.levelTextView.setBackgroundResource(R.drawable.back_dr);
				break;
			case 3:
				if(question.getWriter().isTeamwork()){
					holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
				}
				else{
					holder3.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
				}				
				holder3.levelTextView.setBackgroundResource(R.drawable.back_dp);
				break;
			case 4:
				holder3.levelTextView.setText("专家");
				holder3.levelTextView.setBackgroundResource(R.drawable.back_zj);
				break;
			case 5:
				holder3.levelTextView.setText("官方");
				holder3.levelTextView.setBackgroundResource(R.drawable.back_gf);
				break;
			default:
				break;
			}
			break;
		case 3:
				holderPlant1 = (HolderPlant1) convertView.getTag();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
								holderPlant1.headIv, ImageOptions.options);
				holderPlant1.nameTv.setText(question.getWriter().getAlias());
				holderPlant1.timeTv.setText(question.getTime());
				holderPlant1.contentTv.setText(content);
				
				if(question.getCate()!=null){
					holderPlant1.cropTv.setVisibility(View.VISIBLE);
					holderPlant1.cropTv.setText(question.getCate().getName());
				}
				else{
					holderPlant1.cropTv.setVisibility(View.GONE);
				}
				if(!TextUtils.isEmpty(question.getDeal())){
					holderPlant1.cateTv.setVisibility(View.VISIBLE);
					holderPlant1.cateTv.setText(question.getDeal());
				}
				else{
					holderPlant1.cateTv.setVisibility(View.GONE);
				}
				holderPlant1.headIv.setTag(question.getWriter());
				holderPlant1.headIv.setOnClickListener(mClickListener);
				holderPlant1.countTv.setText("评论（"+question.getResponsecount()+"）");
				holderPlant1.agreeTextView.setText("赞同（"+question.getAgree()+"）");
				holderPlant1.agreeLayout.setOnClickListener(mClickListener);
				holderPlant1.forwardTextView.setText("转发");
				holderPlant1.forwardLayout.setTag(question);
				holderPlant1.forwardLayout.setOnClickListener(mClickListener);
				if(question.isFine()){
					holderPlant1.jpLayout.setVisibility(View.VISIBLE);
				}
				else{
					holderPlant1.jpLayout.setVisibility(View.GONE);
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holderPlant1.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holderPlant1.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holderPlant1.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holderPlant1.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holderPlant1.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holderPlant1.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}				
					holderPlant1.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holderPlant1.levelTextView.setText("专家");
					holderPlant1.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holderPlant1.levelTextView.setText("官方");
					holderPlant1.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				break;
		case 4:
				holderPlant2 = (HolderPlant2) convertView.getTag();
				holderPlant2.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
								holderPlant2.headIv, ImageOptions.options);
				holderPlant2.nameTv.setText(question.getWriter().getAlias());
				holderPlant2.contentTv.setText(content);
				holderPlant2.timeTv.setText(question.getTime());
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(0).getUrl(),
								holderPlant2.imageView1, ImageOptions.options);
				holderPlant2.imageView1.setVisibility(View.VISIBLE);
				holderPlant2.imageView1.setIndex(0);
				holderPlant2.imageView1.setImages(question.getAttachments(),
						"plant");
				if (question.getAttachments().size() > 1) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"plant/small/"
											+ question.getAttachments().get(1)
													.getUrl(),
													holderPlant2.imageView2, ImageOptions.options);
					holderPlant2.imageView2.setVisibility(View.VISIBLE);
					holderPlant2.imageView2.setIndex(1);
					holderPlant2.imageView2.setImages(question.getAttachments(),
							"plant");
				}
				if (question.getAttachments().size() > 2) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"plant/small/"
											+ question.getAttachments().get(2)
													.getUrl(),
													holderPlant2.imageView3, ImageOptions.options);
					holderPlant2.imageView3.setVisibility(View.VISIBLE);
					holderPlant2.imageView3.setIndex(2);
					holderPlant2.imageView3.setImages(question.getAttachments(),
							"plant");
				}
				
				if(question.getCate()!=null){
					holderPlant2.cropTv.setVisibility(View.VISIBLE);
					holderPlant2.cropTv.setText(question.getCate().getName());
				}
				else{
					holderPlant2.cropTv.setVisibility(View.GONE);
				}
				if(!TextUtils.isEmpty(question.getDeal())){
					holderPlant2.cateTv.setVisibility(View.VISIBLE);
					holderPlant2.cateTv.setText(question.getDeal());
				}
				else{
					holderPlant2.cateTv.setVisibility(View.GONE);
				}
				holderPlant2.headIv.setTag(question.getWriter());
				holderPlant2.headIv.setOnClickListener(mClickListener);
				holderPlant2.countTv.setText("评论（"+question.getResponsecount()+"）");
				holderPlant2.agreeTextView.setText("赞同（"+question.getAgree()+"）");
				holderPlant2.agreeLayout.setOnClickListener(mClickListener);
				holderPlant2.forwardTextView.setText("转发");
				holderPlant2.forwardLayout.setTag(question);
				holderPlant2.forwardLayout.setOnClickListener(mClickListener);
				if(question.isFine()){
					holderPlant2.jpLayout.setVisibility(View.VISIBLE);
				}
				else{
					holderPlant2.jpLayout.setVisibility(View.GONE);
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holderPlant2.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holderPlant2.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holderPlant2.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holderPlant2.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holderPlant2.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holderPlant2.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}	
					holderPlant2.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holderPlant2.levelTextView.setText("专家");
					holderPlant2.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holderPlant2.levelTextView.setText("官方");
					holderPlant2.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				break;
		case 5:
				holderPlant3 = (HolderPlant3) convertView.getTag();
				holderPlant3.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
								holderPlant3.headIv, ImageOptions.options);
				holderPlant3.nameTv.setText(question.getWriter().getAlias());
				holderPlant3.timeTv.setText(question.getTime());
				holderPlant3.contentTv.setText(content);
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(0).getUrl(),
								holderPlant3.imageView1, ImageOptions.options);
				holderPlant3.imageView1.setVisibility(View.VISIBLE);
				holderPlant3.imageView1.setIndex(0);
				holderPlant3.imageView1.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(1).getUrl(),
								holderPlant3.imageView2, ImageOptions.options);
				holderPlant3.imageView2.setVisibility(View.VISIBLE);
				holderPlant3.imageView2.setIndex(1);
				holderPlant3.imageView2.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(2).getUrl(),
								holderPlant3.imageView3, ImageOptions.options);
				holderPlant3.imageView3.setVisibility(View.VISIBLE);
				holderPlant3.imageView3.setIndex(2);
				holderPlant3.imageView3.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(3).getUrl(),
								holderPlant3.imageView4, ImageOptions.options);
				holderPlant3.imageView4.setVisibility(View.VISIBLE);
				holderPlant3.imageView4.setIndex(3);
				holderPlant3.imageView4.setImages(question.getAttachments(),
						"plant");
				if (question.getAttachments().size() > 4) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"plant/small/"
											+ question.getAttachments().get(4)
													.getUrl(),
													holderPlant3.imageView5, ImageOptions.options);
					holderPlant3.imageView5.setVisibility(View.VISIBLE);
					holderPlant3.imageView5.setIndex(4);
					holderPlant3.imageView5.setImages(question.getAttachments(),
							"plant");
				}
				if (question.getAttachments().size() > 5) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"plant/small/"
											+ question.getAttachments().get(5)
													.getUrl(),
													holderPlant3.imageView6, ImageOptions.options);
					holderPlant3.imageView6.setVisibility(View.VISIBLE);
					holderPlant3.imageView6.setIndex(5);
					holderPlant3.imageView6.setImages(question.getAttachments(),
							"plant");
				}
				if(question.getCate()!=null){
					holderPlant3.cropTv.setVisibility(View.VISIBLE);
					holderPlant3.cropTv.setText(question.getCate().getName());
				}
				else{
					holderPlant3.cropTv.setVisibility(View.GONE);
				}
				if(!TextUtils.isEmpty(question.getDeal())){
					holderPlant3.cateTv.setVisibility(View.VISIBLE);
					holderPlant3.cateTv.setText(question.getDeal());
				}
				else{
					holderPlant3.cateTv.setVisibility(View.GONE);
				}
				holderPlant3.headIv.setTag(question.getWriter());
				holderPlant3.headIv.setOnClickListener(mClickListener);
				holderPlant3.countTv.setText("评论（"+question.getResponsecount()+"）");
				holderPlant3.agreeTextView.setText("赞同（"+question.getAgree()+"）");
				holderPlant3.agreeLayout.setOnClickListener(mClickListener);
				holderPlant3.forwardTextView.setText("转发");
				holderPlant3.forwardLayout.setTag(question);
				holderPlant3.forwardLayout.setOnClickListener(mClickListener);
				if(question.isFine()){
					holderPlant3.jpLayout.setVisibility(View.VISIBLE);
				}
				else{
					holderPlant3.jpLayout.setVisibility(View.GONE);
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holderPlant3.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holderPlant3.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holderPlant3.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holderPlant3.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holderPlant3.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holderPlant3.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}	
					holderPlant3.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holderPlant3.levelTextView.setText("专家");
					holderPlant3.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holderPlant3.levelTextView.setText("官方");
					holderPlant3.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				break;
		case 6:
				holderPlant4 = (HolderPlant4) convertView.getTag();
				holderPlant4.reSetImageViews();
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"users/small/"
								+ question.getWriter().getThumbnail(),
								holderPlant4.headIv, ImageOptions.options);
				holderPlant4.nameTv.setText(question.getWriter().getAlias());
				holderPlant4.timeTv.setText(question.getTime());
				holderPlant4.contentTv.setText(content);
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(0).getUrl(),
								holderPlant4.imageView1, ImageOptions.options);
				holderPlant4.imageView1.setVisibility(View.VISIBLE);
				holderPlant4.imageView1.setIndex(0);
				holderPlant4.imageView1.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(1).getUrl(),
								holderPlant4.imageView2, ImageOptions.options);
				holderPlant4.imageView2.setVisibility(View.VISIBLE);
				holderPlant4.imageView2.setIndex(1);
				holderPlant4.imageView2.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(2).getUrl(),
								holderPlant4.imageView3, ImageOptions.options);
				holderPlant4.imageView3.setVisibility(View.VISIBLE);
				holderPlant4.imageView3.setIndex(2);
				holderPlant4.imageView3.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(3).getUrl(),
								holderPlant4.imageView4, ImageOptions.options);
				holderPlant4.imageView4.setVisibility(View.VISIBLE);
				holderPlant4.imageView4.setIndex(3);
				holderPlant4.imageView4.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(4).getUrl(),
								holderPlant4.imageView5, ImageOptions.options);
				holderPlant4.imageView5.setVisibility(View.VISIBLE);
				holderPlant4.imageView5.setIndex(4);
				holderPlant4.imageView5.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(5).getUrl(),
								holderPlant4.imageView6, ImageOptions.options);
				holderPlant4.imageView6.setVisibility(View.VISIBLE);
				holderPlant4.imageView6.setIndex(5);
				holderPlant4.imageView6.setImages(question.getAttachments(),
						"plant");
				ImageLoader.getInstance().displayImage(
						HttpUtil.ImageUrl+"plant/small/"
								+ question.getAttachments().get(6).getUrl(),
								holderPlant4.imageView7, ImageOptions.options);
				holderPlant4.imageView7.setVisibility(View.VISIBLE);
				holderPlant4.imageView7.setIndex(6);
				holderPlant4.imageView7.setImages(question.getAttachments(),
						"plant");
				if (question.getAttachments().size() > 7) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"plant/small/"
											+ question.getAttachments().get(7)
													.getUrl(),
													holderPlant4.imageView8, ImageOptions.options);
					holderPlant4.imageView8.setVisibility(View.VISIBLE);
					holderPlant4.imageView8.setIndex(7);
					holderPlant4.imageView8.setImages(question.getAttachments(),
							"plant");
				}
				if (question.getAttachments().size() > 8) {
					ImageLoader.getInstance()
							.displayImage(
									HttpUtil.ImageUrl+"plant/small/"
											+ question.getAttachments().get(8)
													.getUrl(),
													holderPlant4.imageView9, ImageOptions.options);
					holderPlant4.imageView9.setVisibility(View.VISIBLE);
					holderPlant4.imageView9.setIndex(8);
					holderPlant4.imageView9.setImages(question.getAttachments(),
							"plant");
				}
				if(question.getCate()!=null){
					holderPlant4.cropTv.setVisibility(View.VISIBLE);
					holderPlant4.cropTv.setText(question.getCate().getName());
				}
				else{
					holderPlant4.cropTv.setVisibility(View.GONE);
				}
				if(!TextUtils.isEmpty(question.getDeal())){
					holderPlant4.cateTv.setVisibility(View.VISIBLE);
					holderPlant4.cateTv.setText(question.getDeal());
				}
				else{
					holderPlant4.cateTv.setVisibility(View.GONE);
				}
				holderPlant4.headIv.setTag(question.getWriter());
				holderPlant4.headIv.setOnClickListener(mClickListener);
				holderPlant4.countTv.setText("评论（"+question.getResponsecount()+"）");
				holderPlant4.agreeTextView.setText("赞同（"+question.getAgree()+"）");
				holderPlant4.agreeLayout.setOnClickListener(mClickListener);
				holderPlant4.forwardTextView.setText("转发");
				holderPlant4.forwardLayout.setTag(question);
				holderPlant4.forwardLayout.setOnClickListener(mClickListener);
				if(question.isFine()){
					holderPlant4.jpLayout.setVisibility(View.VISIBLE);
				}
				else{
					holderPlant4.jpLayout.setVisibility(View.GONE);
				}
				switch (question.getWriter().getLevelID()) {
				case 1:
					holderPlant4.levelTextView.setText(question.getWriter().getIdentifier()+"农友");
					holderPlant4.levelTextView.setBackgroundResource(R.drawable.back_ny);
					break;
				case 2:
					holderPlant4.levelTextView.setText(question.getWriter().getIdentifier()+"农技达人");
					holderPlant4.levelTextView.setBackgroundResource(R.drawable.back_dr);
					break;
				case 3:
					if(question.getWriter().isTeamwork()){
						holderPlant4.levelTextView.setText(question.getWriter().getIdentifier()+"农资店-合作店");
					}
					else{
						holderPlant4.levelTextView.setText(question.getWriter().getIdentifier()+"农资店");
					}	
					holderPlant4.levelTextView.setBackgroundResource(R.drawable.back_dp);
					break;
				case 4:
					holderPlant4.levelTextView.setText("专家");
					holderPlant4.levelTextView.setBackgroundResource(R.drawable.back_zj);
					break;
				case 5:
					holderPlant4.levelTextView.setText("官方");
					holderPlant4.levelTextView.setBackgroundResource(R.drawable.back_gf);
					break;
				default:
					break;
				}
				break;
		default:
			break;
		}

		return convertView;
	}
}

package com.zhonghaodi.goodfarming;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhonghaodi.model.Question;
import com.zhonghaodi.networking.HttpUtil;
import com.zhonghaodi.networking.ImageOptions;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Question> allQuestions;
	private QuestionAdpter adapter;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.obj != null) {
				Gson gson = new Gson();
				List<Question> questions = gson.fromJson(msg.obj.toString(),
						new TypeToken<List<Question>>() {
						}.getType());
				if (msg.what == 0) {
					allQuestions.clear();
				}
				for (Question question : questions) {
					allQuestions.add(question);
				}
				adapter.notifyDataSetChanged();
				pullToRefreshListView.onRefreshComplete();
			} else {
				Toast.makeText(HomeFragment.this.getActivity(),
						"连接服务器失败,请稍候再试!", Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		Button questionButton=(Button)view.findViewById(R.id.question_button);
		questionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it=new Intent();
				it.setClass(getActivity(), CreateQuestionActivity.class);
				getActivity().startActivity(it);
			}
		});
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						loadNewDate();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (allQuestions.size() == 0) {
							return;
						}
						Question question = allQuestions.get(allQuestions
								.size() - 1);
						loadMoreData(question.getId());
					}

				});
		allQuestions = new ArrayList<Question>();
		adapter = new HomeFragment.QuestionAdpter();
		HomeFragment.this.pullToRefreshListView.getRefreshableView()
				.setAdapter(adapter);
		loadNewDate();
		return view;
	}

	private void loadNewDate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuestionsString();
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private void loadMoreData(final int qid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonString = HttpUtil.getQuestionsString(qid);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = jsonString;
				msg.sendToTarget();
			}
		}).start();
	}

	private class QuestionAdpter extends BaseAdapter {

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
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			Question question = allQuestions.get(position);
			if (question.getAttachments().size() == 0) {
				return 0;
			}
			if (question.getAttachments().size() > 0
					&& question.getAttachments().size() < 4) {
				return 1;
			}
			if (question.getAttachments().size() > 3) {
				return 2;
			}
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Question question = allQuestions.get(position);
			// if (convertView == null) {
			// if (question.getAttachments().size() == 0) {
			// convertView = LayoutInflater.from(
			// HomeFragment.this.getActivity()).inflate(
			// R.layout.cell_question, null);
			//
			// }
			// if (question.getAttachments().size() > 0
			// && question.getAttachments().size() < 4) {
			// convertView = LayoutInflater.from(
			// HomeFragment.this.getActivity()).inflate(
			// R.layout.cell_question_3_image, null);
			// }
			// if (question.getAttachments().size() > 3) {
			// convertView = LayoutInflater.from(
			// HomeFragment.this.getActivity()).inflate(
			// R.layout.cell_question_6_image, null);
			// }
			// }
			// ImageView headIv = (ImageView) convertView
			// .findViewById(R.id.head_image);
			// ImageLoader.getInstance().displayImage(
			// "http://121.40.62.120/appimage/users/small/"
			// + question.getWriter().getThumbnail(), headIv,
			// ImageOptions.options);
			// setAttachmentImage(convertView, question);
			//
			// TextView nameTv = (TextView) convertView
			// .findViewById(R.id.name_text);
			// nameTv.setText(question.getWriter().getAlias());
			// TextView timeTv = (TextView) convertView
			// .findViewById(R.id.time_text);
			// timeTv.setText(question.getTime());
			// TextView contentTv = (TextView) convertView
			// .findViewById(R.id.content_text);
			// contentTv.setText(question.getContent());
			// TextView countTv = (TextView) convertView
			// .findViewById(R.id.count_text);
			// countTv.setText("已有" +
			// String.valueOf(question.getResponsecount())
			// + "个答案");

			int type = getItemViewType(position);
			Holder1 holder1 = null;
			Holder2 holder2 = null;
			Holder3 holder3 = null;
			if (convertView == null) {
				switch (type) {
				case 0:
					convertView = LayoutInflater.from(
							HomeFragment.this.getActivity()).inflate(
							R.layout.cell_question, parent,false);
					holder1 = new Holder1(convertView);
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder1.headIv, ImageOptions.options);
					holder1.nameTv.setText(question.getWriter().getAlias());
					holder1.timeTv.setText(question.getTime());
					holder1.contentTv.setText(question.getContent());
					holder1.countTv.setText("已有" + question.getResponsecount()
							+ "个答案");
					convertView.setTag(holder1);
					break;
				case 1:
					convertView = LayoutInflater.from(
							HomeFragment.this.getActivity()).inflate(
							R.layout.cell_question_3_image, parent,false);
					holder2 = new Holder2(convertView);
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder2.headIv, ImageOptions.options);
					holder2.nameTv.setText(question.getWriter().getAlias());
					holder2.contentTv.setText(question.getContent());
					holder2.timeTv.setText(question.getTime());
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(0)
													.getUrl(),
									holder2.imageView1, ImageOptions.options);
					holder2.imageView1.setVisibility(View.VISIBLE);
					if (question.getAttachments().size() > 1) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(1)
												.getUrl(), holder2.imageView2,
								ImageOptions.options);
						holder2.imageView2.setVisibility(View.VISIBLE);
					}
					if (question.getAttachments().size() > 2) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(2)
												.getUrl(), holder2.imageView3,
								ImageOptions.options);
						holder2.imageView3.setVisibility(View.VISIBLE);
					}
					holder2.countTv.setText("已有" + question.getResponsecount()
							+ "个答案");
					convertView.setTag(holder2);
					break;
				case 2:
					convertView = LayoutInflater.from(
							HomeFragment.this.getActivity()).inflate(
							R.layout.cell_question_6_image, parent,false);
					holder3 = new Holder3(convertView);
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder3.headIv, ImageOptions.options);
					holder3.nameTv.setText(question.getWriter().getAlias());
					holder3.timeTv.setText(question.getTime());
					holder3.contentTv.setText(question.getContent());
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(0)
													.getUrl(),
									holder3.imageView1, ImageOptions.options);
					holder3.imageView1.setVisibility(View.VISIBLE);
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(1)
													.getUrl(),
									holder3.imageView2, ImageOptions.options);
					holder3.imageView2.setVisibility(View.VISIBLE);
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(2)
													.getUrl(),
									holder3.imageView3, ImageOptions.options);
					holder3.imageView3.setVisibility(View.VISIBLE);
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(3)
													.getUrl(),
									holder3.imageView4, ImageOptions.options);
					holder3.imageView4.setVisibility(View.VISIBLE);
					if (question.getAttachments().size() > 4) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(4)
												.getUrl(), holder3.imageView5,
								ImageOptions.options);
						holder3.imageView5.setVisibility(View.VISIBLE);
					}
					if (question.getAttachments().size() > 5) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(5)
												.getUrl(), holder3.imageView6,
								ImageOptions.options);
						holder3.imageView6.setVisibility(View.VISIBLE);
					}
					holder3.countTv.setText("已有" + question.getResponsecount()
							+ "个答案");
					convertView.setTag(holder3);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case 0:
					holder1 = (Holder1) convertView.getTag();
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder1.headIv, ImageOptions.options);
					holder1.nameTv.setText(question.getWriter().getAlias());
					holder1.timeTv.setText(question.getTime());
					holder1.contentTv.setText(question.getContent());
					holder1.countTv.setText("已有" + question.getResponsecount()
							+ "个答案");
					break;
				case 1:
					holder2 = (Holder2) convertView.getTag();
					holder2.reSetImageViews();
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder2.headIv, ImageOptions.options);
					holder2.nameTv.setText(question.getWriter().getAlias());
					holder2.contentTv.setText(question.getContent());
					holder2.timeTv.setText(question.getTime());
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(0)
													.getUrl(),
									holder2.imageView1, ImageOptions.options);
					holder2.imageView1.setVisibility(View.VISIBLE);
					if (question.getAttachments().size() > 1) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(1)
												.getUrl(), holder2.imageView2,
								ImageOptions.options);
						holder2.imageView2.setVisibility(View.VISIBLE);
					}
					if (question.getAttachments().size() > 2) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(2)
												.getUrl(), holder2.imageView3,
								ImageOptions.options);
						holder2.imageView3.setVisibility(View.VISIBLE);
					}
					holder2.countTv.setText("已有" + question.getResponsecount()
							+ "个答案");
					break;
				case 2:
					holder3 = (Holder3) convertView.getTag();
					holder3.reSetImageViews();
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder3.headIv, ImageOptions.options);
					holder3.nameTv.setText(question.getWriter().getAlias());
					holder3.timeTv.setText(question.getTime());
					holder3.contentTv.setText(question.getContent());
					ImageLoader.getInstance().displayImage(
							"http://121.40.62.120/appimage/users/small/"
									+ question.getWriter().getThumbnail(),
							holder3.headIv, ImageOptions.options);
					holder3.nameTv.setText(question.getWriter().getAlias());
					holder3.timeTv.setText(question.getTime());
					holder3.contentTv.setText(question.getContent());
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(0)
													.getUrl(),
									holder3.imageView1, ImageOptions.options);
					holder3.imageView1.setVisibility(View.VISIBLE);
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(1)
													.getUrl(),
									holder3.imageView2, ImageOptions.options);
					holder3.imageView2.setVisibility(View.VISIBLE);
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(2)
													.getUrl(),
									holder3.imageView3, ImageOptions.options);
					holder3.imageView3.setVisibility(View.VISIBLE);
					ImageLoader.getInstance()
							.displayImage(
									"http://121.40.62.120/appimage/questions/small/"
											+ question.getAttachments().get(3)
													.getUrl(),
									holder3.imageView4, ImageOptions.options);
					holder3.imageView4.setVisibility(View.VISIBLE);
					if (question.getAttachments().size() > 4) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(4)
												.getUrl(), holder3.imageView5,
								ImageOptions.options);
						holder3.imageView5.setVisibility(View.VISIBLE);
					}
					if (question.getAttachments().size() > 5) {
						ImageLoader.getInstance().displayImage(
								"http://121.40.62.120/appimage/questions/small/"
										+ question.getAttachments().get(5)
												.getUrl(), holder3.imageView6,
								ImageOptions.options);
						holder3.imageView6.setVisibility(View.VISIBLE);
					}
					holder3.countTv.setText("已有" + question.getResponsecount()
							+ "个答案");
					break;
				default:
					break;
				}
			}

			return convertView;
		}

		private void setAttachmentImage(View convertView, Question question) {
			if (question.getAttachments() == null
					|| question.getAttachments().size() == 0) {
				return;
			}
			// try {
			switch (question.getAttachments().size()) {
			case 6:
				ImageView imageView6 = (ImageView) convertView
						.findViewById(R.id.image6);
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/questions/small/"
								+ question.getAttachments().get(5).getUrl(),
						imageView6, ImageOptions.options);
			case 5:
				ImageView imageView5 = (ImageView) convertView
						.findViewById(R.id.image5);
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/questions/small/"
								+ question.getAttachments().get(4).getUrl(),
						imageView5, ImageOptions.options);
			case 4:
				ImageView imageView4 = (ImageView) convertView
						.findViewById(R.id.image4);
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/questions/small/"
								+ question.getAttachments().get(3).getUrl(),
						imageView4, ImageOptions.options);
			case 3:
				ImageView imageView3 = (ImageView) convertView
						.findViewById(R.id.image3);
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/questions/small/"
								+ question.getAttachments().get(2).getUrl(),
						imageView3, ImageOptions.options);
			case 2:
				ImageView imageView2 = (ImageView) convertView
						.findViewById(R.id.image2);
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/questions/small/"
								+ question.getAttachments().get(1).getUrl(),
						imageView2, ImageOptions.options);
			case 1:
				ImageView imageView1 = (ImageView) convertView
						.findViewById(R.id.image1);
				ImageLoader.getInstance().displayImage(
						"http://121.40.62.120/appimage/questions/small/"
								+ question.getAttachments().get(0).getUrl(),
						imageView1, ImageOptions.options);
			default:
				break;
			}
			// } catch (Exception e) {
			//
			// Log.e("eeeeee", "----------------------"+e.getMessage());
			// // TODO: handle exception
			// }

		}

		class Holder1 {
			TextView nameTv;
			TextView timeTv;
			TextView contentTv;
			TextView countTv;
			ImageView headIv;

			Holder1(View view) {
				nameTv = (TextView) view.findViewById(R.id.name_text);
				timeTv = (TextView) view.findViewById(R.id.time_text);
				contentTv = (TextView) view.findViewById(R.id.content_text);
				countTv = (TextView) view.findViewById(R.id.count_text);
				headIv = (ImageView) view.findViewById(R.id.head_image);
			}

		}

		class Holder2 {
			TextView nameTv;
			TextView timeTv;
			TextView contentTv;
			TextView countTv;
			ImageView headIv;
			ImageView imageView1;
			ImageView imageView2;
			ImageView imageView3;

			Holder2(View view) {
				nameTv = (TextView) view.findViewById(R.id.name_text);
				timeTv = (TextView) view.findViewById(R.id.time_text);
				contentTv = (TextView) view.findViewById(R.id.content_text);
				countTv = (TextView) view.findViewById(R.id.count_text);
				headIv = (ImageView) view.findViewById(R.id.head_image);
				imageView1 = (ImageView) view.findViewById(R.id.image1);
				imageView2 = (ImageView) view.findViewById(R.id.image2);
				imageView3 = (ImageView) view.findViewById(R.id.image3);
				reSetImageViews();
			}

			public void reSetImageViews() {
				imageView1.setVisibility(View.INVISIBLE);
				imageView2.setVisibility(View.INVISIBLE);
				imageView3.setVisibility(View.INVISIBLE);
			}
		}

		class Holder3 {
			TextView nameTv;
			TextView timeTv;
			TextView contentTv;
			TextView countTv;
			ImageView headIv;
			ImageView imageView1;
			ImageView imageView2;
			ImageView imageView3;
			ImageView imageView4;
			ImageView imageView5;
			ImageView imageView6;

			Holder3(View view) {
				nameTv = (TextView) view.findViewById(R.id.name_text);
				timeTv = (TextView) view.findViewById(R.id.time_text);
				contentTv = (TextView) view.findViewById(R.id.content_text);
				countTv = (TextView) view.findViewById(R.id.count_text);
				headIv = (ImageView) view.findViewById(R.id.head_image);
				imageView1 = (ImageView) view.findViewById(R.id.image1);
				imageView2 = (ImageView) view.findViewById(R.id.image2);
				imageView3 = (ImageView) view.findViewById(R.id.image3);
				imageView4 = (ImageView) view.findViewById(R.id.image4);
				imageView5 = (ImageView) view.findViewById(R.id.image5);
				imageView6 = (ImageView) view.findViewById(R.id.image6);
				reSetImageViews();
			}

			public void reSetImageViews() {
				imageView1.setVisibility(View.INVISIBLE);
				imageView2.setVisibility(View.INVISIBLE);
				imageView3.setVisibility(View.INVISIBLE);
				imageView4.setVisibility(View.INVISIBLE);
				imageView5.setVisibility(View.INVISIBLE);
				imageView6.setVisibility(View.INVISIBLE);
			}
		}
	}
}

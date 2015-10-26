package com.app.khclub.personal.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.activity.NewsDetailActivity;
import com.app.khclub.news.ui.model.NewsConstants;
import com.app.khclub.news.ui.model.NewsModel;
import com.app.khclub.news.ui.utils.NewsOperate;
import com.app.khclub.news.ui.utils.NewsOperate.LikeCallBack;
import com.app.khclub.news.ui.view.MultiImageView;
import com.app.khclub.news.ui.view.MultiImageView.JumpCallBack;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;

public class PersonalNewsActivity extends BaseActivityWithTopBar {

	// 其他Activity传递进入的被查看的用户id
	public final static String INTNET_KEY_UID = "user_id";
	// 动态listview
	@ViewInject(R.id.personal_news_listView)
	private PullToRefreshListView newsListView;
	// 动态listview
	@ViewInject(R.id.tv_personal_news_prompt)
	private TextView prompTextView;
	// 原始数据源
	private List<NewsModel> newsList = new ArrayList<NewsModel>();
	// 动态列表适配器
	private HelloHaAdapter<NewsModel> newsAdapter = null;
	// 当前的数据页
	private int currentPage = 1;
	// 是否是最后一页数据
	private boolean islastPage = false;
	// 是否下拉
	private boolean isPullDowm = true;
	// 是否正在请求数据
	private boolean isRequestData = false;
	// 对动态的操作
	private NewsOperate newsOPerate;
	// 被查看者的用户ID
	private String currentUid = "";

	@Override
	public int setLayoutId() {
		return R.layout.activity_personal_news_list;
	}

	@Override
	protected void setUpView() {
		init();
		initBoradcastReceiver();
		newsListViewSet();

		// ////////// 首次获取数据 ////////////////
		showLoading(getResources().getString(R.string.loading), true);
		getMyNewsData(currentUid, String.valueOf(currentPage));
	}

	/**
	 * listView 的设置
	 * */
	private void newsListViewSet() {
		// 设置刷新模式
		newsListView.setMode(Mode.BOTH);
		/**
		 * 刷新监听
		 * */
		newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isRequestData) {
					isRequestData = true;
					currentPage = 1;
					isPullDowm = true;
					getMyNewsData(currentUid, String.valueOf(currentPage));
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!islastPage && !isRequestData) {
					isRequestData = true;
					isPullDowm = false;
					getMyNewsData(currentUid, String.valueOf(currentPage));
				}
			}
		});

		/**
		 * 设置底部自动刷新
		 * */
		newsListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!islastPage) {
							newsListView.setMode(Mode.PULL_FROM_END);
							newsListView.setRefreshing(true);
						}
					}
				});

		/**
		 * adapter的设置
		 * */
		newsAdapter = new HelloHaAdapter<NewsModel>(PersonalNewsActivity.this,
				R.layout.personal_news_content_layout, newsList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					NewsModel newsData) {
				helper.setText(R.id.txt_personal_news_publish_time, TimeHandle
						.getShowTimeFormat(newsData.getSendTime(),
								getApplicationContext()));

				// 绑定内容
				if (newsData.getNewsContent().equals("")) {
					helper.setVisible(R.id.txt_personal_news_content, false);
				} else {
					helper.setVisible(R.id.txt_personal_news_content, true);
					helper.setText(R.id.txt_personal_news_content,
							newsData.getNewsContent());
				}
				// 九宫格图片内容
				MultiImageView newsPictures = (MultiImageView) helper
						.getView(R.id.miv_personal_news_images);
				newsPictures.imageDataSet(newsData.getImageNewsList());
				// 快速滑动时不加载图片
				newsPictures.loadImageOnFastSlide(newsListView, true);
				newsPictures.setJumpListener(new JumpCallBack() {

					@Override
					public void onImageClick(Intent intentToimageoBig) {
						startActivity(intentToimageoBig);
					}
				});
				// 设置地理位置
				if (newsData.getLocation().equals("")) {
					helper.setVisible(R.id.txt_personal_news_location, false);
				} else {
					helper.setVisible(R.id.txt_personal_news_location, true);
					helper.setText(R.id.txt_personal_news_location,
							newsData.getLocation());
				}

				// ////////////// 点赞按钮状态设置////////////////////////////////
				TextView likeBtn = helper.getView(R.id.btn_personal_news_like);
				Drawable drawable = null;
				if (newsData.getIsLike()) {
					drawable = getResources().getDrawable(
							R.drawable.like_btn_press);
				} else {
					drawable = getResources().getDrawable(
							R.drawable.like_btn_normal);
				}
				likeBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
						null, null);

				helper.setText(R.id.btn_personal_news_like,
						String.valueOf(newsData.getLikeQuantity()));
				/*
				 * if (newsData.getLikeQuantity() > 0) {
				 * helper.setText(R.id.btn_personal_news_like,
				 * String.valueOf(newsData.getLikeQuantity())); } else {
				 * helper.setText(R.id.btn_personal_news_like, getResources()
				 * .getString(R.string.news_like)); }
				 */

				// ///////////////// 评论///////////////////////////////////
				helper.setText(R.id.btn_personal_news_comment,
						String.valueOf(newsData.getCommentQuantity()));
				/*
				 * if (newsData.getCommentQuantity() > 0) {
				 * helper.setText(R.id.btn_personal_news_comment,
				 * String.valueOf(newsData.getCommentQuantity())); } else {
				 * helper.setText(R.id.btn_personal_news_comment,
				 * getResources().getString(R.string.news_comment)); }
				 */

				// 设置item点击事件
				final int postion = helper.getPosition();
				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View view) {
						onControlsClick(view, postion, view.getId());
					}
				};
				helper.setOnClickListener(R.id.personal_news_root_view,
						listener);
				helper.setOnClickListener(R.id.txt_personal_news_content,
						listener);
				helper.setOnClickListener(R.id.btn_personal_news_share,
						listener);
				helper.setOnClickListener(R.id.btn_personal_news_comment,
						listener);
				helper.setOnClickListener(R.id.btn_personal_news_like, listener);
			}
		};

		// 设置不可点击
		newsAdapter.setItemsClickEnable(false);
		newsListView.setAdapter(newsAdapter);
	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		setBarText(getResources().getString(R.string.personal_news));
		newsOPerate = new NewsOperate(PersonalNewsActivity.this);

		Intent intent = this.getIntent();
		if (null != intent && intent.hasExtra(INTNET_KEY_UID)) {
			currentUid = String.valueOf(intent.getIntExtra(INTNET_KEY_UID, -1));
		} else {
			LogUtils.e("用户id传输错误，用户id为：" + currentUid);
		}
	}

	/**
	 * 初始化广播信息
	 * */
	private void initBoradcastReceiver() {
		LocalBroadcastManager mLocalBroadcastManager;
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(PersonalNewsActivity.this);
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(KHConst.BROADCAST_NEWS_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				myIntentFilter);
	}

	/**
	 * 获取动态数据
	 * */
	private void getMyNewsData(String userID, String page) {
		String path = KHConst.USER_NEWS_LIST + "?" + "user_id=" + userID
				+ "&page=" + page + "&size=" + "";
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							hideLoading();
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							// 获取动态列表
							List<JSONObject> JSONList = (List<JSONObject>) jResult
									.get("list");
							JsonToNewsModel(JSONList);
							newsListView.onRefreshComplete();
							if (jResult.getString("is_last").equals("0")) {
								islastPage = false;
								currentPage++;
								newsListView.setMode(Mode.BOTH);
							} else {
								islastPage = true;
								newsListView.setMode(Mode.PULL_FROM_START);
							}
							isRequestData = false;
						}

						if (status == KHConst.STATUS_FAIL) {
							hideLoading();
							ToastUtil.show(PersonalNewsActivity.this,
									jsonResponse
											.getString(KHConst.HTTP_MESSAGE));
							newsListView.onRefreshComplete();
							if (!islastPage) {
								newsListView.setMode(Mode.BOTH);
							}
							isRequestData = false;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(PersonalNewsActivity.this,
								getResources()
										.getString(R.string.Network_error));
						newsListView.onRefreshComplete();
						if (!islastPage) {
							newsListView.setMode(Mode.BOTH);
						}
						isRequestData = false;
					}

				}, null));
	}

	/**
	 * 点赞操作
	 * */
	private void likeOperate(int postion, View view, final NewsModel newsData) {

		final TextView oprtView = (TextView) view;
		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onLikeStart(boolean isLike) {
				Drawable drawable = null;
				if (isLike) {
					// 点赞操作
					drawable = getResources().getDrawable(
							R.drawable.like_btn_press);
					newsData.setLikeQuantity(String.valueOf(newsData
							.getLikeQuantity() + 1));
					newsData.setIsLike("1");
				} else {
					// 取消点赞
					drawable = getResources().getDrawable(
							R.drawable.like_btn_normal);
					newsData.setLikeQuantity(String.valueOf(newsData
							.getLikeQuantity() - 1));
					newsData.setIsLike("0");
				}
				oprtView.setCompoundDrawablesWithIntrinsicBounds(drawable,
						null, null, null);

				if (newsData.getLikeQuantity() > 0) {
					oprtView.setText(String.valueOf(newsData.getLikeQuantity()));
				} else {
					oprtView.setText(getResources().getString(
							R.string.news_like));
				}
			}

			@Override
			public void onLikeFail(boolean isLike) {
				// 撤销上次
				Drawable drawable = null;
				if (isLike) {
					drawable = getResources().getDrawable(
							R.drawable.like_btn_normal);
					newsData.setLikeQuantity(String.valueOf(newsData
							.getLikeQuantity() - 1));
					newsData.setIsLike("0");
				} else {
					drawable = getResources().getDrawable(
							R.drawable.like_btn_press);
					newsData.setLikeQuantity(String.valueOf(newsData
							.getLikeQuantity() + 1));
					newsData.setIsLike("1");
				}
				oprtView.setCompoundDrawablesWithIntrinsicBounds(drawable,
						null, null, null);

				if (newsData.getLikeQuantity() > 0) {
					oprtView.setText(String.valueOf(newsData.getLikeQuantity()));
				} else {
					oprtView.setText(getResources().getString(
							R.string.news_like));
				}

			}
		});
		if (newsData.getIsLike()) {
			newsOPerate.uploadLikeOperate(newsData.getNewsID(), false);
		} else {
			newsOPerate.uploadLikeOperate(newsData.getNewsID(), true);
		}
	}

	/**
	 * 数据处理
	 */
	private void JsonToNewsModel(List<JSONObject> dataList) {
		List<NewsModel> newDatas = new ArrayList<NewsModel>();
		for (JSONObject newsObj : dataList) {
			NewsModel tempNews = new NewsModel();
			tempNews.setContentWithJson(newsObj);
			newDatas.add(tempNews);
		}
		if (isPullDowm) {
			newsList.clear();
			newsList.addAll(newDatas);
			newsAdapter.replaceAll(newsList);
		} else {
			newsList.addAll(newDatas);
			newsAdapter.addAll(newDatas);
		}
		// 提示状态
		if (newsAdapter.getCount() <= 0) {
			prompTextView.setVisibility(View.VISIBLE);
		} else {
			prompTextView.setVisibility(View.GONE);
		}

	}

	/**
	 * listview上面的点击事件
	 * */
	public void onControlsClick(View view, int postion, int viewID) {
		switch (viewID) {
		case R.id.personal_news_root_view:
		case R.id.txt_personal_news_content:
		case R.id.btn_personal_news_comment:
			// 跳转至动态详情
			jumpToNewsDetail(postion);
			break;

		case R.id.btn_personal_news_share:
			// 分享操作
			break;

		case R.id.btn_personal_news_like:
			// 点赞操作
			likeOperate(postion, view, newsList.get(postion));
			break;

		default:
			break;
		}
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(int index) {
		if (index < newsList.size()) {
			// 跳转到动态详情
			Intent intentToNewsDetail = new Intent(PersonalNewsActivity.this,
					NewsDetailActivity.class);
			// 当前操作的动态id
			intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_ID,
					newsList.get(index).getNewsID());
			intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_OBJ,
					newsList.get(index));
			// 带有返回参数的跳转至动态详情
			startActivityForResult(intentToNewsDetail, 1);
			PersonalNewsActivity.this.overridePendingTransition(
					R.anim.push_right_in, R.anim.push_right_out);
		} else {
			LogUtils.e("数据错误");
		}
	}

	/**
	 * 广播接收处理
	 * */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent resultIntent) {
			String action = resultIntent.getAction();
			if (action.equals(KHConst.BROADCAST_NEWS_LIST_REFRESH)) {
				if (resultIntent.hasExtra(NewsConstants.OPERATE_UPDATE)) {
					// 更新动态列表
					NewsModel resultNews = (NewsModel) resultIntent
							.getSerializableExtra(NewsConstants.OPERATE_UPDATE);
					for (int index = 0; index < newsList.size(); index++) {
						if (resultNews.getNewsID().equals(
								newsList.get(index).getNewsID())) {
							newsList.set(index, resultNews);
							newsAdapter.replaceAll(newsList);
							break;
						}
					}
				} else if (resultIntent.hasExtra(NewsConstants.OPERATE_DELETET)) {
					String resultID = resultIntent
							.getStringExtra(NewsConstants.OPERATE_DELETET);
					// 删除该动态
					for (int index = 0; index < newsList.size(); index++) {
						if (resultID.equals(newsList.get(index).getNewsID())) {
							newsList.remove(index);
							newsAdapter.replaceAll(newsList);
							break;
						}
					}
				} else if (resultIntent
						.hasExtra(NewsConstants.OPERATE_NO_ACTION)) {
					// 无改变
				} else if (resultIntent.hasExtra(NewsConstants.PUBLISH_FINISH)) {
					if (!isRequestData) {
						// 发布了动态
						isRequestData = true;
						currentPage = 1;
						isPullDowm = true;
						getMyNewsData(currentUid, String.valueOf(currentPage));
					}
				}
			}
		}
	};
}

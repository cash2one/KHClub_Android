package com.app.khclub.news.ui.activity;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.easeim.widget.MainPopupMenu.ClickListener;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.model.BetterMembersModel;
import com.app.khclub.news.ui.model.CirclePageModel;
import com.app.khclub.news.ui.model.LikeModel;
import com.app.khclub.news.ui.model.NewsConstants;
import com.app.khclub.news.ui.model.NewsModel;
import com.app.khclub.news.ui.model.NoticeDetailsModel;
import com.app.khclub.news.ui.model.NoticeModel;
import com.app.khclub.news.ui.model.NewsItemModel.OperateItem;
import com.app.khclub.news.ui.utils.NewsOperate;
import com.app.khclub.news.ui.utils.NewsToItemData;
import com.app.khclub.news.ui.utils.NoticeOperate;
import com.app.khclub.news.ui.utils.NoticeOperate.LikeCallBack;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.easemob.chat.InitSmackStaticCode;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AnnouncementActivity extends BaseActivityWithTopBar {
	public static final String NOTICEID = "noticeid";
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDown = false;
	// 当前的数据页
	private int currentPage = 1;
	// 是否是最后一页数据
	private String lastPage = "0";
	// 是否正在请求数据
	private boolean isRequestingData = false;
	// 是否是最后一页
	private CirclePageModel circleData;
	@ViewInject(R.id.no_notice)
	TextView txt_no_notice;
	@ViewInject(R.id.announcement_listView)
	// 公告listview
	private PullToRefreshListView noticeListView;
	private ImageView rightBtn;
	// 公告列表适配器
	private HelloHaAdapter noticeModelAdapter;
	private List<NoticeModel> datalist;
	private NoticeOperate noticeOperate;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_announcement;
	}

	@Override
	protected void setUpView() {
		setBarText("公告");
		rightBtn = addRightImgBtn(R.layout.right_image_button, R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		rightBtn.setImageResource(R.drawable.revise);
		rightBtn.setVisibility(View.GONE);
		circleData = (CirclePageModel) getIntent().getSerializableExtra("data");
		sendNotice();
		initBoradcastReceiver();
		getData(currentPage);
		InitListView();
	}

	private void sendNotice() {
		// TODO Auto-generated method stub
		if ((UserManager.getInstance().getUser().getUid() + "").equals(circleData.getUserID())) {
			rightBtn.setVisibility(View.VISIBLE);
			rightBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 跳转发布页面
					Intent intent = new Intent(AnnouncementActivity.this, PublisAnnouncementActivity.class);
					intent.putExtra("data", circleData);
					// startActivityForResult(intent, 0);
					startActivityWithRight(intent);
				}
			});
		}
	}

	protected void likeOperate(View v, final HelloHaBaseAdapterHelper helper, final NoticeModel item) {
		// TODO Auto-generated method stub
		noticeOperate.setLikeListener(new LikeCallBack() {

			@Override
			public void onLikeStart(boolean isLike) {
				// Log.i("wx", item.getIs_like());
				if (isLike) {
					helper.setImageResource(R.id.like_image, R.drawable.like_btn_press);
					item.setLike_quantity((String.valueOf(Integer.parseInt(item.getLike_quantity()) + 1)));
					item.setIs_like(1 + "");
				} else {
					helper.setImageResource(R.id.like_image, R.drawable.like_btn_normal);
					item.setLike_quantity((String.valueOf(Integer.parseInt(item.getLike_quantity()) - 1)));
					item.setIs_like(0 + "");
				}
				helper.setText(R.id.btn_notice_like, item.getLike_quantity());
			}

			@Override
			public void onLikeFail(boolean isLike) {
				// 撤销上次
				if (isLike) {
					helper.setImageResource(R.id.like_image, R.drawable.like_btn_normal);
					item.setLike_quantity((String.valueOf(Integer.parseInt(item.getLike_quantity()) - 1)));
					item.setIs_like(0 + "");
				} else {
					helper.setImageResource(R.id.like_image, R.drawable.like_btn_press);
					item.setLike_quantity((String.valueOf(Integer.parseInt(item.getLike_quantity()) + 1)));
					item.setIs_like(1 + "");
				}
				helper.setText(R.id.btn_notice_like, item.getLike_quantity());
			}
		});

		if ("1".equals(item.getIs_like())) {
			noticeOperate.uploadLikeOperate(item.getId(), false);
		} else {
			// Log.i("wx", 1111+"");
			noticeOperate.uploadLikeOperate(item.getId(), true);
		}

	}

	protected void handerClick(View v, HelloHaBaseAdapterHelper helper, NoticeModel item) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.xinxi_layout:
		case R.id.notice_content_root:
			noticeDetail(item.getId());
			break;
		case R.id.like_layout:
			likeOperate(v, helper, item);
			break;

		default:
			break;
		}

	}

	private void InitListView() {
		noticeOperate = new NoticeOperate(AnnouncementActivity.this);
		noticeModelAdapter = new HelloHaAdapter<NoticeModel>(AnnouncementActivity.this,
				R.layout.activity_announcement_item) {

			@Override
			protected void convert(final HelloHaBaseAdapterHelper helper, final NoticeModel item) {
				final int position = helper.getPosition();
				// Log.i("wwww", item.getContent_text());
				helper.setText(R.id.notice_content, item.getContent_text());
				helper.setText(R.id.notice_publish_date,
						TimeHandle.getShowTimeFormat(item.getAdd_date(), getApplicationContext()));
				helper.setText(R.id.btn_mian_reply, item.getComment_quantity());
				helper.setText(R.id.btn_notice_like, item.getLike_quantity());
				if ("1".equals(item.getIs_like())) {
					helper.setImageResource(R.id.like_image, R.drawable.like_btn_press);
				} else {
					helper.setImageResource(R.id.like_image, R.drawable.like_btn_normal);
				}
				// 点赞监听

				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						handerClick(v, helper, item);
					}

				};
				// 点赞
				helper.setOnClickListener(R.id.like_layout, listener);

				// 评论监听
				helper.setOnClickListener(R.id.xinxi_layout, listener);
				helper.setOnClickListener(R.id.notice_content_root, listener);

			}
		};

		// 适配器绑定
		noticeListView.setAdapter(noticeModelAdapter);
		noticeListView.setMode(Mode.BOTH);
		noticeListView.setPullToRefreshOverScrollEnabled(false);
		/**
		 * 刷新监听
		 */
		noticeListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!isRequestingData) {
					isRequestingData = true;
					currentPage = 1;
					isPullDown = true;
					getData(currentPage);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!lastPage.equals("1") && !isRequestingData) {
					isRequestingData = true;
					isPullDown = false;
					getData(currentPage);
				}
			}
		});
		/**
		 * 设置底部自动刷新
		 */
		noticeListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (!lastPage.equals("1")) {
					noticeListView.setMode(Mode.PULL_FROM_END);
					noticeListView.setRefreshing(true);
				}
			}
		});

		// 快宿滑动时不加载图片
		// noticeModelAdapter.setOnScrollListener(new
		// PauseOnScrollListener(ImageLoader.getInstance(), false, true));

		// noticeModelAdapter.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// Intent intentToUsrMain = new Intent(AnnouncementActivity.this,
		// OtherPersonalActivity.class);
		// BetterMembersModel membersModel = noticeModelAdapter.getItem(position
		// - 1);
		// intentToUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY,
		// KHUtils.stringToInt(membersModel.getUser_id()));
		// startActivityWithRight(intentToUsrMain);
	}
	// });

	// }

	private void getData(int page) {

		// String circleid = getIntent().getStringExtra("circle_id");
		String path = KHConst.GET_NOTICE_LIST + "?circle_id=" + circleData.getCircleId() + "&user_id="
				+ circleData.getUserID() + "&page=" + page;
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					// 获取动态列表
					String followJsonArray = jResult.getString("list");
					datalist = JSON.parseArray(followJsonArray, NoticeModel.class);
					if (datalist.size() == 0) {
						txt_no_notice.setVisibility(View.VISIBLE);
					}
					// 如果是下拉刷新
					if (isPullDown) {
						noticeModelAdapter.replaceAll(datalist);
					} else {
						noticeModelAdapter.addAll(datalist);
					}

					noticeListView.onRefreshComplete();
					// 是否是最后页
					lastPage = jResult.getString("is_last");
					if (lastPage.equals("0")) {
						currentPage++;
						noticeListView.setMode(Mode.BOTH);
					} else {
						noticeListView.setMode(Mode.PULL_FROM_START);
					}
					isRequestingData = false;

				}

				if (status == KHConst.STATUS_FAIL) {
					noticeListView.onRefreshComplete();
					if (lastPage.equals("0")) {
						noticeListView.setMode(Mode.BOTH);
					}
					ToastUtil.show(AnnouncementActivity.this, jsonResponse.getString(KHConst.HTTP_MESSAGE));
				}
				isRequestingData = false;
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				noticeListView.onRefreshComplete();
				if (lastPage.equals("0")) {
					noticeListView.setMode(Mode.BOTH);
				}
				ToastUtil.show(AnnouncementActivity.this, getString(R.string.net_error));
				isRequestingData = false;
			}

		}, null));
	}

	private LocalBroadcastManager mLocalBroadcastManager;

	/**
	 * 初始化广播信息
	 */
	private void initBoradcastReceiver() {
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(KHConst.BROADCAST_NOTICE_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * 广播接收处理
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent resultIntent) {
			String action = resultIntent.getAction();
			// Log.i("wx", action);
			if (action.equals(KHConst.BROADCAST_NOTICE_LIST_REFRESH)) {
				if (resultIntent.hasExtra(NewsConstants.OPERATE_UPDATE)) {
				// 更新动态列表
					if (!isRequestingData) {
						isRequestingData = true;
						currentPage = 1;
						isPullDown = true;
						getData(currentPage);
					}
//					NoticeDetailsModel resultNews = (NoticeDetailsModel) resultIntent
//							.getSerializableExtra(NewsConstants.OPERATE_UPDATE);
//					for (int index = 0; index < datalist.size(); index++) {
//						if (resultNews.getId().equals(datalist.get(index).getId())) {
//							datalist.get(index).setComment_quantity(resultNews.getCommentQuantity());
//							datalist.get(index).setLike_quantity(resultNews.getLikeQuantity());
//							datalist.get(index).setIs_like(resultNews.getIsLike());
//							noticeModelAdapter.replaceAll(datalist);
//							break;
//						}
//					}
				} else if (resultIntent.hasExtra(NewsConstants.OPERATE_DELETET)) {
					String resultID = resultIntent.getStringExtra(NewsConstants.OPERATE_DELETET);
					// 删除该动态
					for (int index = 0; index < datalist.size(); index++) {
						if (resultID.equals(datalist.get(index).getId())) {
							datalist.remove(index);
							noticeModelAdapter.replaceAll(datalist);
							break;
						}
					}
				} else if (resultIntent.hasExtra(NewsConstants.OPERATE_NO_ACTION)) {
					// 无改变
				} else if (resultIntent.hasExtra(NewsConstants.PUBLISH_FINISH)) {
					// 发布了公告,进行刷新
					if (!isRequestingData) {
						isRequestingData = true;
						currentPage = 1;
						isPullDown = true;
						getData(currentPage);
					}
				} else if (resultIntent.hasExtra(NewsConstants.NEWS_LISTVIEW_REFRESH)) {
					// 点击table栏进行刷新
					// smoothToTop();
				}
			}
		}
	};

	private void noticeDetail(String noticeid) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(AnnouncementActivity.this, NoticeDetailActivity.class);
		intent.putExtra(NOTICEID, noticeid);
		startActivityWithRight(intent);
	}

}

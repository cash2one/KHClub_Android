package com.app.khclub.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.adapter.MultiItemTypeSupport;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.ImageModel;
import com.app.khclub.base.model.NewsPushModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.HttpCacheUtils;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.activity.NewsDetailActivity;
import com.app.khclub.news.ui.activity.NoticeActivity;
import com.app.khclub.news.ui.activity.PublishNewsActivity;
import com.app.khclub.news.ui.model.NewsConstants;
import com.app.khclub.news.ui.model.NewsItemModel;
import com.app.khclub.news.ui.model.NewsItemModel.BodyItem;
import com.app.khclub.news.ui.model.NewsItemModel.OperateItem;
import com.app.khclub.news.ui.model.NewsItemModel.TitleItem;
import com.app.khclub.news.ui.model.NewsModel;
import com.app.khclub.news.ui.utils.NewsOperate;
import com.app.khclub.news.ui.utils.NewsOperate.LikeCallBack;
import com.app.khclub.news.ui.utils.NewsToItemData;
import com.app.khclub.news.ui.utils.TextViewHandel;
import com.app.khclub.news.ui.view.MultiImageView;
import com.app.khclub.news.ui.view.MultiImageView.JumpCallBack;
import com.app.khclub.news.ui.view.NewsBottomPopupMenu;
import com.app.khclub.news.ui.view.NewsBottomPopupMenu.NewsBottomClickListener;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsListFragment extends BaseFragment {

	// 动态listview
	@ViewInject(R.id.news_listview)
	private PullToRefreshListView newsListView;
	// 发布按钮
	@ViewInject(R.id.img_news_publish_btn)
	private ImageView publishBtn;
	// 通知按钮
	@ViewInject(R.id.img_notice_btn)
	private ImageView noticeBtn;
	// 未读提示（小红点）
	@ViewInject(R.id.news_unread_image_view)
	private ImageView unreadImageView;
	// 原始数据源
	private List<NewsModel> newsList = new ArrayList<NewsModel>();
	// item数据源
	private List<NewsItemModel> itemDataList = null;
	// 动态列表适配器
	private HelloHaAdapter<NewsItemModel> newsAdapter = null;
	// 使支持多种item
	private MultiItemTypeSupport<NewsItemModel> multiItemTypeSupport = null;
	// 上下文信息
	private Context mContext;
	// 当前数据的页
	private int pageIndex = 1;
	// 是否是最后一页数据
	private boolean lastPage = false;
	// 时间戳
	private String latestTimesTamp = "";
	// 是否下拉
	private boolean isPullDowm = true;
	// 是否正在请求数据
	private boolean isRequestingData = false;
	// 点击view监听对象
	private ItemViewClick itemViewClickListener;
	// 对动态的操作
	private NewsOperate newsOPerate;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 是否为文字长按事件
	private boolean isLongClick = false;
	// 底部操作弹出菜单
	private NewsBottomPopupMenu shareMenu;

	@Override
	public int setLayoutId() {
		return R.layout.fragment_main_news;
	}

	@Override
	public void loadLayout(View rootView) {

	}

	@Override
	public void setUpViews(View rootView) {
		init();
		initBoradcastReceiver();
		multiItemTypeSet();
		newsListViewSet();
		// 获取上次缓存的数据
		// setLastData(UserManager.getInstance().getUser().getUid());
		// 从服务器加载数据
		getNewsData(UserManager.getInstance().getUser().getUid(), pageIndex, "");
		// 点击发布按钮
		publishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intentUsrMain = new Intent(mContext,
						PublishNewsActivity.class);
				startActivityWithRight(intentUsrMain);
			}
		});
		// 点击通知按钮
		noticeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转至通知页面
				Intent intentCampusInfo = new Intent(mContext,
						NoticeActivity.class);
				startActivityWithRight(intentCampusInfo);
			}
		});

		// 设置点击事件回调
		shareMenu.setListener(new NewsBottomClickListener() {

			@Override
			public void shareToWeiboClick(NewsModel news) {
				// 分享到微博
				LogUtils.i("-----"+news.getUserName());
			}

			@Override
			public void shareToWeChatClick(NewsModel news) {
				// 分享到微信

			}

			@Override
			public void shareToQzoneClick(NewsModel news) {
				// 分享到qq空间

			}

			@Override
			public void shareToCircleofFriendsClick(NewsModel news) {
				// 分享到朋友圈

			}

			@Override
			public void cancelClick() {
				// 取消操作

			}
		});
		registerNotify();
		refreshPush();
	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		mContext = this.getActivity();
		shareMenu = new NewsBottomPopupMenu(getActivity());
		itemViewClickListener = new ItemViewClick();
		newsOPerate = new NewsOperate(mContext);
		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.icon).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private LocalBroadcastManager mLocalBroadcastManager;

	/**
	 * 初始化广播信息
	 * */
	private void initBoradcastReceiver() {
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(getActivity());
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(KHConst.BROADCAST_NEWS_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				myIntentFilter);
	}

	/**
	 * listView 支持多种item的设置
	 * */
	private void multiItemTypeSet() {
		multiItemTypeSupport = new MultiItemTypeSupport<NewsItemModel>() {

			@Override
			public int getLayoutId(int position, NewsItemModel itemData) {
				int layoutId = 0;
				switch (itemData.getItemType()) {
				case NewsItemModel.NEWS_TITLE:
					layoutId = R.layout.mian_news_item_title_layout;
					break;
				case NewsItemModel.NEWS_BODY:
					layoutId = R.layout.main_news_item_body_layout;
					break;
				case NewsItemModel.NEWS_OPERATE:
					layoutId = R.layout.mian_news_item_operate_layout;
					break;
				default:
					break;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return NewsItemModel.NEWS_ITEM_TYPE_COUNT;
			}

			@Override
			public int getItemViewType(int postion, NewsItemModel itemData) {
				int itemtype = 0;
				switch (itemData.getItemType()) {
				case NewsItemModel.NEWS_TITLE:
					itemtype = NewsItemModel.NEWS_TITLE;
					break;
				case NewsItemModel.NEWS_BODY:
					itemtype = NewsItemModel.NEWS_BODY;
					break;
				case NewsItemModel.NEWS_OPERATE:
					itemtype = NewsItemModel.NEWS_OPERATE;
					break;
				default:
					break;
				}
				return itemtype;
			}
		};
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
				if (!isRequestingData) {
					isRequestingData = true;
					pageIndex = 1;
					isPullDowm = true;
					getNewsData(UserManager.getInstance().getUser().getUid(),
							pageIndex, "");
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!lastPage && !isRequestingData) {
					isRequestingData = true;
					isPullDowm = false;
					getNewsData(UserManager.getInstance().getUser().getUid(),
							pageIndex, latestTimesTamp);
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
						if (!lastPage) {
							newsListView.setMode(Mode.PULL_FROM_END);
							newsListView.setRefreshing(true);
						}
					}
				});

		/**
		 * adapter的设置
		 * */
		newsAdapter = new HelloHaAdapter<NewsItemModel>(mContext, itemDataList,
				multiItemTypeSupport) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					NewsItemModel item) {

				switch (helper.layoutId) {
				case R.layout.mian_news_item_title_layout:
					setTitleItemView(helper, item);
					break;
				case R.layout.main_news_item_body_layout:
					setBodyItemView(helper, item);
					break;
				case R.layout.mian_news_item_operate_layout:
					setOperateItemView(helper, item);
					break;

				default:
					break;
				}
			}
		};

		// 设置不可点击
		newsAdapter.setItemsClickEnable(false);
		newsListView.setAdapter(newsAdapter);
	}

	/***
	 * 上次缓存的数据
	 * */
	@SuppressWarnings("unchecked")
	private void setLastData(int userID) {
		String path = KHConst.NEWS_LIST + "?" + "user_id=" + userID + "&page="
				+ 1 + "&frist_time=";
		try {
			JSONObject JObject = HttpCacheUtils.getHttpCache(path);
			if (null != JObject) {
				JSONObject jResult = JObject.getJSONObject(KHConst.HTTP_RESULT);
				if (null != jResult) {
					List<JSONObject> JSONList = (List<JSONObject>) jResult
							.get(KHConst.HTTP_LIST);
					if (null != JSONList) {
						JsonToNewsModel(JSONList);
					}
				}
			}
		} catch (Exception e) {
			LogUtils.e("解析本地缓存错误.");
		}

	}

	/**
	 * titleItem的数据绑定与设置
	 * */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper,
			NewsItemModel item) {
		TitleItem titleData = (TitleItem) item;
		// 绑定头像
		if (null != titleData.getHeadSubImage()
				&& titleData.getHeadSubImage().length() > 0) {
			imgLoader.displayImage(titleData.getHeadSubImage(),
					(ImageView) helper.getView(R.id.img_mian_news_user_head),
					options);
		} else {
			((ImageView) helper.getView(R.id.img_mian_news_user_head))
					.setImageResource(R.drawable.icon);
		}

		// 设置用户名，职位，公司
		helper.setText(R.id.txt_main_news_user_name, titleData.getUserName());
		// 绑定职位
		if (titleData.getUserJob().equals("")) {
			helper.setVisible(R.id.txt_main_news_user_job, false);
		} else {
			helper.setVisible(R.id.txt_main_news_user_job, true);
			helper.setText(R.id.txt_main_news_user_job, titleData.getUserJob()
					+ " | ");
		}
		// 绑定公司名
		if (titleData.getUserJob().equals("")) {
			helper.setVisible(R.id.txt_main_news_user_company, true);
		} else {
			helper.setVisible(R.id.txt_main_news_user_company, true);
			helper.setText(R.id.txt_main_news_user_company,
					titleData.getUserCompany());
		}

		// 设置事件监听
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};
		helper.setOnClickListener(R.id.img_mian_news_user_head, listener);
		helper.setOnClickListener(R.id.txt_main_news_user_name, listener);
		helper.setOnClickListener(R.id.layout_news_title_rootview, listener);
	}

	/**
	 * 设置新闻主体item
	 * */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper,
			NewsItemModel item) {
		final BodyItem bodyData = (BodyItem) item;
		List<ImageModel> pictureList = bodyData.getNewsImageListList();
		MultiImageView bodyImages = helper.getView(R.id.miv_main_news_images);
		bodyImages.imageDataSet(pictureList);
		// 快速滑动时不加载图片
		bodyImages.loadImageOnFastSlide(newsListView, true);

		bodyImages.setJumpListener(new JumpCallBack() {

			@Override
			public void onImageClick(Intent intentToimageoBig) {
				startActivity(intentToimageoBig);
			}
		});

		// 点击监听对象
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!isLongClick) {
					itemViewClickListener.onClick(view, postion, view.getId());
				}
			}
		};

		// 设置 文字内容
		if (bodyData.getNewsContent().equals("")) {
			helper.setVisible(R.id.txt_main_news_content, false);
		} else {
			helper.setVisible(R.id.txt_main_news_content, true);
			TextView contentView = helper.getView(R.id.txt_main_news_content);
			contentView.setText(bodyData.getNewsContent());
			// customTvHandel.setTextContent(contentView);
			// 长按复制
			contentView.setOnLongClickListener(TextViewHandel
					.getLongClickListener(getActivity(),
							bodyData.getNewsContent()));
			// 点击
			helper.setOnClickListener(R.id.txt_main_news_content, listener);
		}
		// 设置地理位置
		if (bodyData.getLocation().equals("")) {
			helper.setVisible(R.id.txt_main_news_location, false);
		} else {
			helper.setVisible(R.id.txt_main_news_location, true);
			helper.setText(R.id.txt_main_news_location, bodyData.getLocation());
		}
		// 父布局监听
		helper.setOnClickListener(R.id.miv_main_news_images, listener);
		helper.setOnClickListener(R.id.layout_news_body_rootview, listener);
	}

	/**
	 * 设置操作部分item
	 * */
	private void setOperateItemView(HelloHaBaseAdapterHelper helper,
			NewsItemModel item) {
		OperateItem opData = (OperateItem) item;
		// ///////////////// 绑定时间///////////////////////////////////////
		helper.setText(R.id.txt_main_news_publish_time,
				TimeHandle.getShowTimeFormat(opData.getSendTime()));

		// /////////////////// 绑定评论/////////////////////////////////////////
		if (opData.getCommentCount() > 0) {
			helper.setText(R.id.btn_mian_reply,
					String.valueOf(opData.getCommentCount()));
		} else {
			helper.setText(R.id.btn_mian_reply, "评论");
		}

		// /////////////////////点赞按钮///////////////////////////////////
		TextView likeBtn = helper.getView(R.id.btn_news_like);
		Drawable drawable = null;
		if (opData.getIsLike()) {
			drawable = getResources().getDrawable(R.drawable.like_btn_press);
		} else {
			drawable = getResources().getDrawable(R.drawable.like_btn_normal);
		}
		likeBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
				null);
		// 点赞数大于0才显示点赞数量
		if (opData.getLikeCount() > 0) {
			likeBtn.setText(String.valueOf(opData.getLikeCount()));
		} else {
			likeBtn.setText("赞");
		}

		// /////////////////////////设置事件监听///////////////////////////////
		final int postion = helper.getPosition();
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				itemViewClickListener.onClick(view, postion, view.getId());
			}
		};

		// 事件监听绑定
		helper.setOnClickListener(R.id.btn_mian_reply, listener);
		helper.setOnClickListener(R.id.btn_mian_share, listener);
		helper.setOnClickListener(R.id.btn_news_like, listener);
		helper.setOnClickListener(R.id.layout_news_operate_rootview, listener);
	}

	/**
	 * 获取动态数据
	 * */
	private void getNewsData(int userID, int desPage, String lastTime) {
		String path = KHConst.NEWS_LIST + "?" + "user_id=" + userID + "&page="
				+ desPage + "&frist_time=" + lastTime;
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							// 获取动态列表
							List<JSONObject> JSONList = (List<JSONObject>) jResult
									.get("list");
							JsonToNewsModel(JSONList);
							newsListView.onRefreshComplete();
							if (jResult.getString("is_last").equals("0")) {
								lastPage = false;
								pageIndex++;
								newsListView.setMode(Mode.BOTH);
							} else {
								lastPage = true;
								newsListView.setMode(Mode.PULL_FROM_START);
							}
							isRequestingData = false;
						}

						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(mContext, jsonResponse
									.getString(KHConst.HTTP_MESSAGE));
							newsListView.onRefreshComplete();
							newsListView.setMode(Mode.BOTH);
							isRequestingData = false;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(mContext, "网络故障，请检查");
						newsListView.onRefreshComplete();
						newsListView.setMode(Mode.BOTH);
						isRequestingData = false;
					}

				}, null));
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
			// 更新时间戳
			latestTimesTamp = newDatas.get(0).getTimesTamp();
			newsList.clear();
			newsList.addAll(newDatas);
			newsAdapter.replaceAll(NewsToItemData.newsDataToItems(newDatas));
		} else {
			newsList.addAll(newDatas);
			newsAdapter.addAll(NewsToItemData.newsDataToItems(newDatas));
		}
		dataList.clear();
	}

	/**
	 * item上的view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			switch (viewID) {
			case R.id.layout_news_title_rootview:
			case R.id.img_mian_news_user_head:
			case R.id.txt_main_news_user_name:
				TitleItem titleData = (TitleItem) newsAdapter.getItem(postion);
				if (R.id.layout_news_title_rootview == viewID) {
					// 跳转到动态详情
					jumpToNewsDetail(titleData, NewsConstants.KEY_BOARD_CLOSE,
							null);
				} else {
					// 跳转至用户主页
					jumpToHomepage(KHUtils.stringToInt(titleData.getUserID()));
				}
				break;

			case R.id.layout_news_body_rootview:
			case R.id.txt_main_news_content:
			case R.id.miv_main_news_images:
				BodyItem bodyData = (BodyItem) newsAdapter.getItem(postion);
				// 跳转到动态详情
				jumpToNewsDetail(bodyData, NewsConstants.KEY_BOARD_CLOSE, null);
				break;

			case R.id.btn_mian_reply:
			case R.id.btn_news_like:
			case R.id.btn_mian_share:
			case R.id.layout_news_operate_rootview:

				final OperateItem operateData = (OperateItem) newsAdapter
						.getItem(postion);
				if (R.id.btn_news_like == viewID) {
					// 点赞操作
					likeOperate(postion, view, operateData);
				} else if (R.id.btn_mian_share == viewID) {
					// 分享操作
					for (int index = 0; index < newsList.size(); index++) {
						if (operateData.getNewsID().equals(
								newsList.get(index).getNewsID())) {
							shareMenu.showPopupWindow(publishBtn,
									newsList.get(index));
							break;
						}
					}

				} else {
					// 跳转到动态详情
					jumpToNewsDetail(operateData,
							NewsConstants.KEY_BOARD_CLOSE, null);
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * listview点击事件接口,用于区分不同view的点击事件
	 * 
	 * @author Alan
	 */
	private interface ListItemClickHelp {
		void onClick(View view, int postion, int viewID);
	}

	/**
	 * 点赞操作
	 * */
	private void likeOperate(int postion, View view,
			final OperateItem operateData) {

		final TextView oprtView = (TextView) view;
		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onLikeStart(boolean isLike) {
				Drawable drawable = null;
				if (isLike) {
					// 点赞操作
					operateData.setLikeCount(operateData.getLikeCount() + 1);
					drawable = getResources().getDrawable(
							R.drawable.like_btn_press);
					operateData.setIsLike(true);
				} else {
					// 取消点赞
					drawable = getResources().getDrawable(
							R.drawable.like_btn_normal);
					operateData.setLikeCount(operateData.getLikeCount() - 1);
					operateData.setIsLike(false);
				}

				// 点赞数大于0才显示点赞数量
				if (operateData.getLikeCount() > 0) {
					oprtView.setText(String.valueOf(operateData.getLikeCount()));
				} else {
					oprtView.setText("赞");
				}
				oprtView.setCompoundDrawablesWithIntrinsicBounds(drawable,
						null, null, null);
			}

			@Override
			public void onLikeFail(boolean isLike) {
				// 撤销上次
				Drawable drawable = null;
				if (isLike) {
					// 取消点赞
					drawable = getResources().getDrawable(
							R.drawable.like_btn_normal);
					operateData.setLikeCount(operateData.getLikeCount() - 1);
					operateData.setIsLike(false);
				} else {
					// 点赞操作
					operateData.setLikeCount(operateData.getLikeCount() + 1);
					drawable = getResources().getDrawable(
							R.drawable.like_btn_press);
					operateData.setIsLike(true);
				}
				// 点赞数大于0才显示点赞数量
				if (operateData.getLikeCount() > 0) {
					oprtView.setText(String.valueOf(operateData.getLikeCount()));
				} else {
					oprtView.setText("赞");
				}
				oprtView.setCompoundDrawablesWithIntrinsicBounds(drawable,
						null, null, null);
			}
		});
		if (operateData.getIsLike()) {
			newsOPerate.uploadLikeOperate(operateData.getNewsID(), false);
		} else {
			newsOPerate.uploadLikeOperate(operateData.getNewsID(), true);
		}
	}

	/**
	 * 跳转至用户的主页
	 */
	private void jumpToHomepage(int userID) {
		Intent intentToUsrMain = new Intent(mContext,
				OtherPersonalActivity.class);
		intentToUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentToUsrMain);
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(NewsItemModel itemModel, int keyBoardMode,
			String commentId) {
		// 跳转到动态详情
		Intent intentToNewsDetail = new Intent(mContext,
				NewsDetailActivity.class);
		// 当前操作的动态id
		intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_ID,
				itemModel.getNewsID());

		// 找到当前的动态对象
		for (int index = 0; index < newsList.size(); ++index) {
			if (newsList.get(index).getNewsID().equals(itemModel.getNewsID())) {
				intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_OBJ,
						newsList.get(index));
				break;
			}
		}

		// 带有返回参数的跳转至动态详情
		startActivityForResult(intentToNewsDetail, 1);
		getActivity().overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBroadcastReceiver != null && mLocalBroadcastManager != null) {
			mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		}
		if (newMessageReceiver != null) {
			getActivity().unregisterReceiver(newMessageReceiver);
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
							newsAdapter.replaceAll(NewsToItemData
									.newsDataToItems(newsList));
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
							newsAdapter.replaceAll(NewsToItemData
									.newsDataToItems(newsList));
							break;
						}
					}
				} else if (resultIntent
						.hasExtra(NewsConstants.OPERATE_NO_ACTION)) {
					// 无改变
				} else if (resultIntent.hasExtra(NewsConstants.PUBLISH_FINISH)) {
					// 发布了动态,进行刷新
					if (!isRequestingData) {
						isRequestingData = true;
						pageIndex = 1;
						isPullDowm = true;
						getNewsData(UserManager.getInstance().getUser()
								.getUid(), pageIndex, "");
					}
				} else if (resultIntent
						.hasExtra(NewsConstants.NEWS_LISTVIEW_REFRESH)) {
					// 点击table栏进行刷新
					smoothToTop();
				}
			}
		}
	};

	// 平滑滚动到顶
	private void smoothToTop() {
		int firstVisiblePosition = newsListView.getRefreshableView()
				.getFirstVisiblePosition();
		if (0 == firstVisiblePosition) {
			// 已经在顶部
			newsListView.setMode(Mode.PULL_FROM_START);
			newsListView.setRefreshing();
		} else {
			if (firstVisiblePosition < 20) {
				newsListView.getRefreshableView().smoothScrollToPosition(0);
			} else {
				newsListView.getRefreshableView().setSelection(20);
				newsListView.getRefreshableView().smoothScrollToPosition(0);
			}
			newsListView.getRefreshableView().clearFocus();
		}
	}

	private BroadcastReceiver newMessageReceiver;

	// 注册通知
	private void registerNotify() {
		// 刷新push
		newMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 刷新push
				refreshPush();
			}
		};
		IntentFilter intentFilter = new IntentFilter(
				KHConst.BROADCAST_NEW_MESSAGE_PUSH);
		getActivity().registerReceiver(newMessageReceiver, intentFilter);
	}

	// 刷新tab 未读标志
	private void refreshPush() {
		int newsUnreadCount = NewsPushModel.findUnreadCount().size();
		if (newsUnreadCount < 1) {
			unreadImageView.setVisibility(View.GONE);
		} else {
			unreadImageView.setVisibility(View.VISIBLE);
		}

	}
}

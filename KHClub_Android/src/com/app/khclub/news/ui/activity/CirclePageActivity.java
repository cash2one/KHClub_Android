package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

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
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.contact.ui.activity.ShareContactsActivity;
import com.app.khclub.news.ui.model.CircleMembersModel;
import com.app.khclub.news.ui.model.CirclePageModel;
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
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu.BottomClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("ResourceAsColor")
public class CirclePageActivity extends BaseActivityWithTopBar {
	private static final String CIRCLEDETAIL = "circledetail";
	// private Fresh fresh;
	private int userID;
	protected static final String CIRCLE_ID = "circle_id";
	public static String INTENT_CIRCLE_KEY = "circleModel";
	//private View headerView;
	// 圈子信息
	private CirclePageModel circleModel2;

	// 标题
	@ViewInject(R.id.title_text_view)
	private TextView titleTextView;
	// 介绍
	@ViewInject(R.id.intro_text_view)
	private TextView introTextView;
	// 地址
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;
	// 名字
	@ViewInject(R.id.manager_name_text_view)
	private TextView managerNameTextView;
	// 电话
	@ViewInject(R.id.phone_text_view)
	private TextView phoneTextView;
	// 介绍
	@ViewInject(R.id.wx_text_view)
	private TextView wxTextView;
	// 网页
	@ViewInject(R.id.web_text_view)
	private TextView webTextView;
	// 模型
//	private CircleModel circleModel;
	// 新图片缓存工具 头像
	DisplayImageOptions options;
	// pageViews
	private List<View> pageViews = new ArrayList<View>();
	// 需要显示的图片
	private String[] images;

	@Override
	public int setLayoutId() {
		return R.layout.activity_circle_page;

	}

	// private void initUI() {
	// options = new
	// DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
	// .showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
	// .bitmapConfig(Bitmap.Config.RGB_565).build();
	// // 标题
	// titleTextView.setText(circleModel.getTitle());
	// // 介绍
	// introTextView.setText(" " + circleModel.getIntro());
	// // 地址
	// addressTextView.setText(circleModel.getAddress());
	// // 名
	// managerNameTextView.setText(circleModel.getManager_name());
	// // 电话
	// phoneTextView.setText(circleModel.getPhone_num());
	// // 微信
	// wxTextView.setText(circleModel.getWx_num());
	// // 网页
	// webTextView.setText(circleModel.getWeb());
	// // 初始化pageView
	// images = circleModel.getImage().split(",");
	// if (images.length > 0) {
	// int size = images.length;
	// if (size > 3) {
	// size = 3;
	// }
	// switch (size) {
	// case 1:
	// // pageView2.setVisibility(View.GONE);
	// // pageView3.setVisibility(View.GONE);
	// break;
	// case 2:
	// // pageView3.setVisibility(View.GONE);
	// break;
	// default:
	// break;
	// }
	// }
	// }

	// 动态listview
	@ViewInject(R.id.news_circle_listView)
	private PullToRefreshListView newsListView;
	@ViewInject(R.id.send_news)
    private Button sendnews;
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
	// 是否为文字长按事件
	private boolean isLongClick = false;
	// 底部操作弹出菜单
	private PersonalBottomPopupMenu shareMenu;
	// 圈子ID
	private String circle_id;

	@Override
	protected void setUpView() {
		circle_id = getIntent().getStringExtra("circle_id");
		// TODO Auto-generated method stub
		setBarText(getString(R.string.club_main_page));
		// addRightImgBtn(R.id.layout_base_title,R.id.base_ll_right_btns,
		// R.drawable.share_btn_normal);
		//右上角分享按钮
		final ImageView rightBtn = addRightImgBtn(R.layout.right_image_button, R.id.layout_top_btn_root_view,
				R.id.img_btn_right_top);
		rightBtn.setImageResource(R.drawable.personal_more);
		rightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 分享点击
				shareMenu.showPopupWindow(rightBtn);				
			}
		});
		
		
		init();
		initBoradcastReceiver();
		multiItemTypeSet();
		newsListViewSet();
		sendnews.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendnews();
			}
		});
		// 获取上次缓存的数据
		// setLastData(UserManager.getInstance().getUser().getUid());
		// 从服务器加载数据
		getNewsData(UserManager.getInstance().getUser().getUid(), pageIndex, "");
		headerClickListener = new ListItemClickHelp() {

			@Override
			public void onClick(View view, int postion, int viewID) {
				// TODO Auto-generated method stub
				switch (view.getId()) {
				case R.id.circle_unattention_btn:
					attention();
					break;
				case R.id.circle_page_head_layout:
					toCircleDetail();
					break;
				case R.id.better_members_layout:
					toBetterMembers();
					break;

				default:
					break;
				}

			}

			private void toBetterMembers() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CirclePageActivity.this, BetterMemberActivity.class);
				intent.putExtra(CIRCLE_ID, circleModel2.getCircleId());
				startActivity(intent);
			}

			private void toCircleDetail() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CirclePageActivity.this, CircleDetailActivity.class);
				intent.putExtra(CIRCLEDETAIL, circleModel2);
				startActivity(intent);
			}

			private void attention() {
				// TODO Auto-generated method stub
				RequestParams params = new RequestParams();
				final UserModel userModel = UserManager.getInstance().getUser();
				params.addBodyParameter("user_id", userModel.getUid() + "");
				params.addBodyParameter("circle_id", circle_id);
				params.addBodyParameter("isFollow", "0");
				// 关注
				HttpManager.post(KHConst.FOLLOW_OR_UNFOLLOW_CIRCLE, params,
						new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse.getIntValue("status");
						switch (status) {
						case KHConst.STATUS_SUCCESS:
							ToastUtil.show(CirclePageActivity.this, "取消成功");
							break;
						case KHConst.STATUS_FAIL:
							Toast.makeText(CirclePageActivity.this, "取消关注失败", Toast.LENGTH_SHORT).show();
							break;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1, String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						Toast.makeText(CirclePageActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
					}
				}, null));
				finish();
			}
		};
		// 设置点击事件回调
		shareMenu.setListener(new BottomClickListener() {

			@Override
			public void shareToWeiboClick() {
			}

			@Override
			public void shareToWeChatClick() {
				if (circleModel2 == null) {
					return;
				}
				// 分享到微信
				ShareParams sp = new ShareParams();
				sp.setTitle(circleModel2.getCircleName());
				sp.setUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
				sp.setTitleUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setText(circleModel2.getUserName());
				if (null != circleModel2.getCircleCoverSubImage() && circleModel2.getCircleCoverSubImage().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+circleModel2.getCircleCoverSubImage());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform wexin = ShareSDK.getPlatform(Wechat.NAME);
				wexin.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				wexin.share(sp);
			}

			@Override
			public void shareToQzoneClick() {
				// 分享到朋友圈
				// 分享到微信
				ShareParams sp = new ShareParams();
				sp.setTitle(circleModel2.getCircleName());
				sp.setUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
				sp.setTitleUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setText(circleModel2.getUserName());
				if (null != circleModel2.getCircleCoverSubImage() && circleModel2.getCircleCoverSubImage().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+circleModel2.getCircleCoverSubImage());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform qq = ShareSDK.getPlatform(QZone.NAME);
				qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				qq.share(sp);
			}

			@Override
			public void shareToQQFriendsClick() {
				// 分享给qq好友
				ShareParams sp = new ShareParams();
				sp.setTitle(circleModel2.getCircleName());
				sp.setUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
				sp.setTitleUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setText(circleModel2.getUserName());
				if (null != circleModel2.getCircleCoverSubImage() && circleModel2.getCircleCoverSubImage().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+circleModel2.getCircleCoverSubImage());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				qq.share(sp);
			}

			@Override
			public void shareToFriendClick() {
				if (circleModel2 == null) {
					return;
				}
				// 分享给好友
				JSONObject object = new JSONObject();
				//单聊
				object.put("type", "100");
				object.put("id", circle_id);
				object.put("title",circleModel2.getCircleName());
				object.put("avatar", circleModel2.getCircleCoverSubImage());
				
				Intent intent = new Intent(CirclePageActivity.this, ShareContactsActivity.class);
				intent.putExtra(ShareContactsActivity.INTENT_CARD_KEY, object.toJSONString());
				startActivityWithRight(intent);
			}

			@Override
			public void shareToCircleofFriendsClick() {
				// 分享到朋友圈
				ShareParams sp = new ShareParams();
				sp.setTitle(circleModel2.getCircleName());
				sp.setUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
				sp.setTitleUrl(KHConst.SHARE_CIRCLE_WEB+"?circle_id="+circle_id); // 标题的超链接
				sp.setText(circleModel2.getUserName());
				if (null != circleModel2.getCircleCoverSubImage() && circleModel2.getCircleCoverSubImage().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+circleModel2.getCircleCoverSubImage());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform wexin = ShareSDK.getPlatform(WechatMoments.NAME);
				wexin.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				wexin.share(sp);
			}

			@Override
			public void editRemarkClick() {
				// 设置备注

			}

			@Override
			public void deleteFriendClick() {
				// 删除好友

			}

			@Override
			public void cancelClick() {
				// 取消操作
			}			
			
		});
		// registerNotify();
		// refreshPush();
	}

	private void sendnews() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(CirclePageActivity.this,PublishNewsActivity.class);
		startActivity(intent);
	}

	/**
	 * 数据的初始化
	 */
	private void init() {
		//headerView = getLayoutInflater().inflate(R.layout.activity_circle_page_header, null);
		mContext = CirclePageActivity.this;
		shareMenu = new PersonalBottomPopupMenu(this, false);
		itemViewClickListener = new ItemViewClick();
		newsOPerate = new NewsOperate(mContext);
		// 获取显示图片的实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private LocalBroadcastManager mLocalBroadcastManager;

	/**
	 * 初始化广播信息
	 */
	private void initBoradcastReceiver() {

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(CirclePageActivity.this);
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(KHConst.BROADCAST_NEWS_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * listView 支持多种item的设置
	 */
	private void multiItemTypeSet() {
		multiItemTypeSupport = new MultiItemTypeSupport<NewsItemModel>() {
			@Override
			public int getLayoutId(int position, NewsItemModel itemData) {
				int layoutId = 0;
				// if (!isNullList) {
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
				// }
				if (position == 0) {
					layoutId = R.layout.activity_circle_page_header;
				}
				return layoutId;
			}

			@Override
			public int getViewTypeCount() {
				return NewsItemModel.NEWS_ITEM_TYPE_COUNT + 1;
			}

			@Override
			public int getItemViewType(int postion, NewsItemModel itemData) {
				int itemtype = 0;
				if (postion == 0) {
					return 3;
				}
				//if (!isNullList) {
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
				//}
				return itemtype;
			}
		};
	}

	/**
	 * listView 的设置
	 */
	private void newsListViewSet() {
		// 设置刷新模式
		newsListView.setMode(Mode.BOTH);

		/**
		 * 刷新监听
		 */
		newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!isRequestingData) {
					isRequestingData = true;
					pageIndex = 1;
					isPullDowm = true;
					getNewsData(UserManager.getInstance().getUser().getUid(), pageIndex, "");
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!lastPage && !isRequestingData) {
					isRequestingData = true;
					isPullDowm = false;
					getNewsData(UserManager.getInstance().getUser().getUid(), pageIndex, latestTimesTamp);
				}
			}
		});

		/**
		 * 设置底部自动刷新
		 */
		newsListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
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
		 */
		newsAdapter = new HelloHaAdapter<NewsItemModel>(mContext, itemDataList, multiItemTypeSupport) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, NewsItemModel item) {
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
				case R.layout.activity_circle_page_header:
					setHeaderItemView(helper, item);
					break;
				default:
					break;
				}
			}

			private void setHeaderItemView(HelloHaBaseAdapterHelper helper, NewsItemModel item) {
				// TODO Auto-generated method stub
				if ("0".equals(circleModel2.getIsFollow())) {
					helper.setText(R.id.circle_unattention_btn, "关注");
				} else {
					userID = UserManager.getInstance().getUser().getUid();
					String userid = String.valueOf(userID);
					// Log.i("wwww", userid);
					// Log.i("wwww", circleModel2.getUserID());
					if (userid.equals(circleModel2.getUserID())) {
						helper.setVisible(R.id.circle_unattention_btn, false);
					} else {
						helper.setText(R.id.circle_unattention_btn, "取消关注");
					}
				}
				helper.setText(R.id.news_circle_name, circleModel2.getCircleName());
				helper.setText(R.id.news_master_name, circleModel2.getUserName());
				helper.setText(R.id.news_circle_friend, " " + circleModel2.getFollowQuantity() + "人");
				imgLoader.displayImage(circleModel2.getCircleCoverSubImage(),
						(ImageView) helper.getView(R.id.circle_cover_image), options);
				lodermembersImage(helper);

				final int postion = helper.getPosition();
				OnClickListener headListener = new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						headerClickListener.onClick(v, postion, v.getId());
					}
				};
				helper.setOnClickListener(R.id.better_members_layout, headListener);
				helper.setOnClickListener(R.id.circle_unattention_btn, headListener);
				helper.setOnClickListener(R.id.circle_page_head_layout, headListener);

			}

			private void lodermembersImage(HelloHaBaseAdapterHelper helper) {
				// TODO Auto-generated method stub
				// Log.i("wwww", membersDatas.toString());
				for (int i = 0; i <= (membersDatas.size() - 1); i++) {
					switch (i) {
					case 0:
						helper.setVisible(R.id.members_image0, true);
						imgLoader.displayImage(membersDatas.get(i).getMembersimage(),
								(ImageView) helper.getView(R.id.members_image0), options);
						break;
					case 1:
						helper.setVisible(R.id.members_image1, true);
						imgLoader.displayImage(membersDatas.get(i).getMembersimage(),
								(ImageView) helper.getView(R.id.members_image1), options);
						break;
					case 2:
						helper.setVisible(R.id.members_image2, true);
						imgLoader.displayImage(membersDatas.get(i).getMembersimage(),
								(ImageView) helper.getView(R.id.members_image1), options);
						break;
					case 3:
						helper.setVisible(R.id.members_image3, true);
						imgLoader.displayImage(membersDatas.get(i).getMembersimage(),
								(ImageView) helper.getView(R.id.members_image1), options);
						break;
					case 4:
						helper.setVisible(R.id.members_image4, true);
						imgLoader.displayImage(membersDatas.get(i).getMembersimage(),
								(ImageView) helper.getView(R.id.members_image1), options);
						break;

					default:
						break;
					}

				}

			}
		};

		// 设置不可点击
		newsAdapter.setItemsClickEnable(false);
		// 给listview加上头部
		// newsListView.add
		newsListView.setAdapter(newsAdapter);
	}

	/***
	 * 上次缓存的数据
	 */
	// private void setLastData(int userID) {
	// String path = KHConst.GET_CIRCLE_HOME_LIST + "?" + "user_id=" + userID +
	// "&circle_id" + circle_id;
	// try {
	// JSONObject JObject = HttpCacheUtils.getHttpCache(path);
	// if (null != JObject) {
	// JSONObject jResult = JObject.getJSONObject(KHConst.HTTP_RESULT);
	// if (null != jResult) {
	// List<JSONObject> JSONList = (List<JSONObject>)
	// jResult.get(KHConst.HTTP_LIST);
	// if (null != JSONList) {
	// JsonToNewsModel(JSONList);
	// }
	// }
	// }
	// } catch (Exception e) {
	// LogUtils.e("解析本地缓存错误.");
	// }
	//
	// }

	/**
	 * titleItem的数据绑定与设置
	 */
	private void setTitleItemView(HelloHaBaseAdapterHelper helper, NewsItemModel item) {
		TitleItem titleData = (TitleItem) item;

		// 绑定头像
		if (null != titleData.getHeadSubImage() && titleData.getHeadSubImage().length() > 0) {
			imgLoader.displayImage(titleData.getHeadSubImage(),
					(ImageView) helper.getView(R.id.img_mian_news_user_head), options);
		} else {
			((ImageView) helper.getView(R.id.img_mian_news_user_head)).setImageResource(R.drawable.default_avatar);
		}

		if (("").equals(titleData.getUserName())) {
			// 设置用户名，职位，公司
			helper.setText(R.id.txt_main_news_user_name, getString(R.string.personal_none));
		} else {
			helper.setText(R.id.txt_main_news_user_name, titleData.getUserName());
		}

		// 绑定职位
		if (("").equals(titleData.getUserJob())) {
			helper.setText(R.id.txt_main_news_user_job, getString(R.string.personal_none));
		} else {
			helper.setText(R.id.txt_main_news_user_job, titleData.getUserJob() + " | ");
		}
		// 绑定公司名
		if (("").equals(titleData.getUserJob())) {
			helper.setVisible(R.id.txt_main_news_user_company, false);
		} else {
			helper.setVisible(R.id.txt_main_news_user_company, true);
			helper.setText(R.id.txt_main_news_user_company, titleData.getUserCompany());
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
	 */
	private void setBodyItemView(HelloHaBaseAdapterHelper helper, NewsItemModel item) {
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
		if (("").equals(bodyData.getNewsContent())) {
			helper.setVisible(R.id.txt_main_news_content, false);
		} else {
			helper.setVisible(R.id.txt_main_news_content, true);
			TextView contentView = helper.getView(R.id.txt_main_news_content);
			contentView.setText(bodyData.getNewsContent());
			// customTvHandel.setTextContent(contentView);
			// 长按复制
			contentView.setOnLongClickListener(TextViewHandel.getLongClickListener(this, bodyData.getNewsContent()));
			// 点击
			helper.setOnClickListener(R.id.txt_main_news_content, listener);
		}
		// 设置地理位置
		if (("").equals(bodyData.getLocation())) {
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
	 */
	private void setOperateItemView(HelloHaBaseAdapterHelper helper, NewsItemModel item) {
		OperateItem opData = (OperateItem) item;
		// ///////////////// 绑定时间///////////////////////////////////////
		helper.setText(R.id.txt_main_news_publish_time, TimeHandle.getShowTimeFormat(opData.getSendTime(), mContext));

		// /////////////////// 绑定评论/////////////////////////////////////////
		helper.setText(R.id.btn_mian_reply, String.valueOf(opData.getCommentCount()));
		// if (opData.getCommentCount() > 0) {
		// helper.setText(R.id.btn_mian_reply,
		// String.valueOf(opData.getCommentCount()));
		// } else {
		// helper.setText(R.id.btn_mian_reply,
		// getString(R.string.news_comment));
		// }

		// /////////////////////点赞按钮///////////////////////////////////
		TextView likeBtn = helper.getView(R.id.btn_news_like);
		// TextView unattention = helper.getView(R.id.circle_unattention_btn);
		// Log.i("wwww", unattention.toString());
		Drawable drawable = null;
		if (opData.getIsLike()) {
			drawable = getResources().getDrawable(R.drawable.like_btn_press);
		} else {
			drawable = getResources().getDrawable(R.drawable.like_btn_normal);
		}
		likeBtn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		likeBtn.setText(String.valueOf(opData.getLikeCount()));
		// 点赞数大于0才显示点赞数量
		// if (opData.getLikeCount() > 0) {
		// likeBtn.setText(String.valueOf(opData.getLikeCount()));
		// } else {
		// likeBtn.setText(getString(R.string.news_like));
		// }

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
	 */
	private void getNewsData(int userID, int desPage, String lastTime) {
		String path = KHConst.GET_CIRCLE_HOME_LIST + "?" + "circle_id=" + circle_id + "&" + "user_id=" + userID;
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {

					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					// 圈子信息
					JSONObject circle = jResult.getJSONObject("circle");
					// Log.i("wwww", circle.toString());
					circleModel2 = new CirclePageModel();
					circleModel2.setContentWithJson(circle);
					// 圈子帖子
					List<JSONObject> JSONList = (List<JSONObject>) jResult.get("list");
					List<JSONObject> JSONMemberslist = (List<JSONObject>) jResult.get("circleMembers");

					jsonJsonToCircleMembersModel(JSONMemberslist);
					JsonToNewsModel(JSONList);
					newsListView.onRefreshComplete();
					// if (jResult.getString("is_last").equals("0")) {
					// lastPage = false;
					// pageIndex++;
					// newsListView.setMode(Mode.BOTH);
					// } else {
					// lastPage = true;
					// newsListView.setMode(Mode.PULL_FROM_START);
					// }
					// isRequestingData = false;
				}
				if (status == KHConst.STATUS_FAIL) {
					ToastUtil.show(mContext, jsonResponse.getString(KHConst.HTTP_MESSAGE));
					newsListView.onRefreshComplete();
					newsListView.setMode(Mode.BOTH);
					isRequestingData = false;
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				ToastUtil.show(mContext, getString(R.string.net_error));
				newsListView.onRefreshComplete();
				newsListView.setMode(Mode.BOTH);
				isRequestingData = false;
			}

		}, null));
	}

	/**
	 * 数据处理
	 */
	private void jsonJsonToCircleMembersModel(List<JSONObject> jSONMemberslist) {
		membersDatas = new ArrayList<CircleMembersModel>();
		for (JSONObject newsObj : jSONMemberslist) {
			CircleMembersModel MembersNews = new CircleMembersModel();
			MembersNews.setContentWithJson(newsObj);
			membersDatas.add(MembersNews);
		}
	}

	private void JsonToNewsModel(List<JSONObject> dataList) {
		List<NewsModel> newDatas = new ArrayList<NewsModel>();
	
		if (null != dataList && !dataList.isEmpty()) {
			for (JSONObject newsObj : dataList) {
				NewsModel tempNews = new NewsModel();
				tempNews.setContentWithJson(newsObj);
				newDatas.add(tempNews);
			}	
		}
		if (isPullDowm) {
			if (newDatas.isEmpty() == false) {
				// 更新时间戳
				latestTimesTamp = newDatas.get(0).getTimesTamp();
				newsList.clear();
				newsList.addAll(newDatas);
			}

			List<NewsItemModel> newsItemList = NewsToItemData.newsDataToItems(newDatas);
			newsItemList.add(0, new NewsItemModel());

			newsAdapter.replaceAll(newsItemList);
		} else {
			newsList.addAll(newDatas);
			newsAdapter.addAll(NewsToItemData.newsDataToItems(newDatas));
		}
		dataList.clear();
	}

	/**
	 * item上的view点击事件
	 */
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
					jumpToNewsDetail(titleData, NewsConstants.KEY_BOARD_CLOSE, null);
				} else {
					// 跳转至用户主页
					jumpToHomepage(KHUtils.stringToInt(titleData.getUserID()));
				}
				break;
			// case R.id.circle_attention_btn:
			// Log.i("wwww", "取消关注");
			// unrecommend();
			// break;
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

				final OperateItem operateData = (OperateItem) newsAdapter.getItem(postion);
				if (R.id.btn_news_like == viewID) {
					// 点赞操作
					likeOperate(postion, view, operateData);
				} else if (R.id.btn_mian_share == viewID) {
					// 分享操作
					// for (int index = 0; index < newsList.size(); index++) {
					// if (operateData.getNewsID().equals(
					// newsList.get(index).getNewsID())) {
					// shareMenu.showPopupWindow(publishBtn,
					// newsList.get(index));
					// break;
					// }
					// }

				} else {
					// 跳转到动态详情
					jumpToNewsDetail(operateData, NewsConstants.KEY_BOARD_CLOSE, null);
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
	 */
	private void likeOperate(int postion, View view, final OperateItem operateData) {

		final TextView oprtView = (TextView) view;
		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onLikeStart(boolean isLike) {
				Drawable drawable = null;
				if (isLike) {
					// 点赞操作
					operateData.setLikeCount(operateData.getLikeCount() + 1);
					drawable = getResources().getDrawable(R.drawable.like_btn_press);
					operateData.setIsLike(true);
				} else {
					// 取消点赞
					drawable = getResources().getDrawable(R.drawable.like_btn_normal);
					operateData.setLikeCount(operateData.getLikeCount() - 1);
					operateData.setIsLike(false);
				}

				oprtView.setText(String.valueOf(operateData.getLikeCount()));
				// // 点赞数大于0才显示点赞数量
				// if (operateData.getLikeCount() > 0) {
				// oprtView.setText(String.valueOf(operateData.getLikeCount()));
				// } else {
				// oprtView.setText(getString(R.string.news_like));
				// }
				oprtView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
			}

			@Override
			public void onLikeFail(boolean isLike) {
				// 撤销上次
				Drawable drawable = null;
				if (isLike) {
					// 取消点赞
					drawable = getResources().getDrawable(R.drawable.like_btn_normal);
					operateData.setLikeCount(operateData.getLikeCount() - 1);
					operateData.setIsLike(false);
				} else {
					// 点赞操作
					operateData.setLikeCount(operateData.getLikeCount() + 1);
					drawable = getResources().getDrawable(R.drawable.like_btn_press);
					operateData.setIsLike(true);
				}
				oprtView.setText(String.valueOf(operateData.getLikeCount()));
				// // 点赞数大于0才显示点赞数量
				// if (operateData.getLikeCount() > 0) {
				// oprtView.setText(String.valueOf(operateData.getLikeCount()));
				// } else {
				// oprtView.setText(getString(R.string.news_like));
				// }
				oprtView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
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
		Intent intentToUsrMain = new Intent(mContext, OtherPersonalActivity.class);
		intentToUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentToUsrMain);
	}

	/***
	 * 跳转至动态相详情
	 */
	private void jumpToNewsDetail(NewsItemModel itemModel, int keyBoardMode, String commentId) {
		// 跳转到动态详情
		Intent intentToNewsDetail = new Intent(mContext, NewsDetailActivity.class);
		// 当前操作的动态id
		intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_ID, itemModel.getNewsID());

		// 找到当前的动态对象
		for (int index = 0; index < newsList.size(); ++index) {
			if (newsList.get(index).getNewsID().equals(itemModel.getNewsID())) {
				intentToNewsDetail.putExtra(NewsConstants.INTENT_KEY_NEWS_OBJ, newsList.get(index));
				break;
			}
		}

		// 带有返回参数的跳转至动态详情
		startActivityForResult(intentToNewsDetail, 1);
		CirclePageActivity.this.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBroadcastReceiver != null && mLocalBroadcastManager != null) {
			mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		}
		// if (newMessageReceiver != null) {
		// CirclePageActivity.this.unregisterReceiver(newMessageReceiver);
		// }
	}

	/**
	 * 广播接收处理
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent resultIntent) {
			String action = resultIntent.getAction();
			if (action.equals(KHConst.BROADCAST_NEWS_LIST_REFRESH)) {
				if (resultIntent.hasExtra(NewsConstants.OPERATE_UPDATE)) {
					// 更新动态列表
					NewsModel resultNews = (NewsModel) resultIntent.getSerializableExtra(NewsConstants.OPERATE_UPDATE);
					for (int index = 0; index < newsList.size(); index++) {
						if (resultNews.getNewsID().equals(newsList.get(index).getNewsID())) {
							newsList.set(index, resultNews);
							newsAdapter.replaceAll(NewsToItemData.newsDataToItems(newsList));
							break;
						}
					}
				} else if (resultIntent.hasExtra(NewsConstants.OPERATE_DELETET)) {
					String resultID = resultIntent.getStringExtra(NewsConstants.OPERATE_DELETET);
					// 删除该动态
					for (int index = 0; index < newsList.size(); index++) {
						if (resultID.equals(newsList.get(index).getNewsID())) {
							newsList.remove(index);
							newsAdapter.replaceAll(NewsToItemData.newsDataToItems(newsList));
							break;
						}
					}
				} else if (resultIntent.hasExtra(NewsConstants.OPERATE_NO_ACTION)) {
					// 无改变
				} else if (resultIntent.hasExtra(NewsConstants.PUBLISH_FINISH)) {
					// 发布了动态,进行刷新
					if (!isRequestingData) {
						isRequestingData = true;
						pageIndex = 1;
						isPullDowm = true;

						getNewsData(userID, pageIndex, "");
					}
				} else if (resultIntent.hasExtra(NewsConstants.NEWS_LISTVIEW_REFRESH)) {
					// 点击table栏进行刷新
					smoothToTop();
				}
			}
		}
	};

	// 平滑滚动到顶
	private void smoothToTop() {
		int firstVisiblePosition = newsListView.getRefreshableView().getFirstVisiblePosition();
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

	// private BroadcastReceiver newMessageReceiver;
	//
	// // 注册通知
	// private void registerNotify() {
	// // 刷新push
	// newMessageReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // 刷新push
	// refreshPush();
	// }
	// };
	// IntentFilter intentFilter = new IntentFilter(
	// KHConst.BROADCAST_NEW_MESSAGE_PUSH);
	// CirclePageActivity.this.registerReceiver(newMessageReceiver,
	// intentFilter);
	// }

	// 刷新tab 未读标志
	// private void refreshPush() {
	// int newsUnreadCount = NewsPushModel.findUnreadCount().size();
	// if (newsUnreadCount < 1) {
	// unreadImageView.setVisibility(View.GONE);
	// } else {
	// unreadImageView.setVisibility(View.VISIBLE);
	// }
	//
	// }

	// 分享监听
	PlatformActionListener platformActionListener = new PlatformActionListener() {
		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			ToastUtil.show(CirclePageActivity.this, R.string.personal_share_fail);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			// ToastUtil.show(CirclePageActivity.this,
			// R.string.personal_share_ok);
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
		}
	};
	private ListItemClickHelp headerClickListener;
	private List<CircleMembersModel> membersDatas;
	private boolean isNullList = false;

}

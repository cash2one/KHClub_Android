package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.view.CustomAlertDialog;
import com.app.khclub.base.ui.view.CustomListViewDialog;
import com.app.khclub.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.app.khclub.base.ui.view.KeyboardLayout;
import com.app.khclub.base.ui.view.KeyboardLayout.onKeyboardsChangeListener;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.model.CommentModel;
import com.app.khclub.news.ui.model.LikeModel;
import com.app.khclub.news.ui.model.NewsConstants;
import com.app.khclub.news.ui.model.NewsModel;
import com.app.khclub.news.ui.utils.NewsOperate;
import com.app.khclub.news.ui.utils.NewsOperate.LikeCallBack;
import com.app.khclub.news.ui.utils.NewsOperate.OperateCallBack;
import com.app.khclub.news.ui.view.MultiImageView;
import com.app.khclub.news.ui.view.MultiImageView.JumpCallBack;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsDetailActivity extends BaseActivityWithTopBar {

	// 与评论或点赞数据相关的key
	private final static String USER_HEAD = "user_head";
	private final static String USER_NAME = "user_name";
	private final static String USER_OFFICE = "user_office";
	private final static String PUBLISH_TIME = "publish_time";
	private final static String COMMENT_CONTENT = "comment_content";
	// 当前显示的是否为评论数据(可以显示点赞数据)
	private boolean isCurrentShowComment = true;
	// 记录对动态的操作
	private String actionType = NewsConstants.OPERATE_NO_ACTION;
	// 主listview
	@ViewInject(R.id.news_detail_listView)
	private PullToRefreshListView newsDetailListView;
	// 评论输入框
	@ViewInject(R.id.edt_comment_input)
	private EditText commentEditText;
	// 评论发送按钮
	@ViewInject(R.id.btn_comment_send)
	private Button btnSendComment;
	// 动态内容head
	private View contenteader;
	// 与动态发相关的一些信息
	private ImageView newsUserHeadImgView;
	private TextView newsUserName;
	private TextView newsUserOffice;
	private TextView newsUserCompany;
	private TextView newsContent;
	private MultiImageView newsPictures;
	private TextView newsLocation;
	private TextView newsPublishTime;
	private TextView newsShareBtn;
	private TextView newsLikeBtn;
	private TextView newsCommentCount;
	private TextView newsLikeCount;
	private List<ImageView> likePersonList;

	// 当前的动态对象
	private NewsModel currentNews;
	// 评论源数据
	private List<CommentModel> commentDataList;
	// 点赞源数据
	private List<LikeModel> likeDataList;
	// 符合显示格式的数据
	private List<Map<String, String>> dataList;
	// 主适配器
	private HelloHaAdapter<Map<String, String>> detailAdapter;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 点击item监听对象
	private ItemViewClick itemViewClickListener;
	// 对动态的操作
	private NewsOperate newsOPerate;
	// 评论的内容
	private String commentContent = "";
	// 当前操作的位置
	private int currentOperateIndex = 0;

	/**
	 * 事件监听函数
	 * */
	@OnClick(value = { R.id.base_ll_right_btns, R.id.btn_comment_send })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 删除动态
		case R.id.base_ll_right_btns:
			deleteCurrentNews();
			break;

		// 发布评论
		case R.id.btn_comment_send:
			publishComment();
			break;
		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_news_detail;
	}

	@Override
	protected void setUpView() {
		init();
		listViewSet();
		newsOperateSet();

		// 监听输入框文本的变化
		commentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int start, int before,
					int count) {
				commentContent = str.toString().trim();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// 监听软键盘是否打开
		((KeyboardLayout) findViewById(R.id.news_detail_root_view))
				.setOnkeyboarddStateListener(new onKeyboardsChangeListener() {

					@Override
					public void onKeyBoardStateChange(int state) {
						if (KeyboardLayout.KEYBOARD_STATE_HIDE == state) {
							// 评论框内容为空并且，软键盘隐藏时
							if (commentContent.length() <= 0) {
								commentEditText.setHint("请输入评论内容...");
							}
						}
					}
				});

		// 处理从上一页面传递过来的动态数据
		Intent intent = this.getIntent();
		if (null != intent) {
			if (intent.hasExtra(NewsConstants.INTENT_KEY_NEWS_OBJ)) {
				// 如果传递过来的是动态的对象
				currentNews = (NewsModel) intent
						.getSerializableExtra(NewsConstants.INTENT_KEY_NEWS_OBJ);
				newsContentDataSet(currentNews);
			} else if (intent.hasExtra(NewsConstants.INTENT_KEY_NEWS_ID)) {
				// 如果传递过来的是动态的id
				currentNews = new NewsModel();
				currentNews.setNewsID(intent
						.getStringExtra(NewsConstants.INTENT_KEY_NEWS_ID));
			} else {
				LogUtils.e("未传递任何动态信息到详情页面.");
			}
		} else {
			LogUtils.e("跳转到详情页面时，意图为null.");
		}

		// 从网络上更新动态数据
		// getNewsDetailData(
		// String.valueOf(UserManager.getInstance().getUser().getUid()),
		// currentNews.getNewsID());

		getNewsDetailData("121", currentNews.getNewsID());
	}

	/**
	 * 数据的初始化
	 * */
	private void init() {
		setBarText("详情");
		commentDataList = new ArrayList<CommentModel>();
		likeDataList = new ArrayList<LikeModel>();
		itemViewClickListener = new ItemViewClick();
		likePersonList = new ArrayList<ImageView>();
		dataList = new ArrayList<Map<String, String>>();
		// 图片加载初始化
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 设置键盘状态打开或收起
	 * */
	private void setKeyboardStatu(boolean state) {
		if (state) {
			InputMethodManager imm = (InputMethodManager) commentEditText
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		} else {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(NewsDetailActivity.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 动态操作设置
	 * */
	private void newsOperateSet() {
		newsOPerate = new NewsOperate(NewsDetailActivity.this);
		newsOPerate.setOperateListener(new OperateCallBack() {
			@Override
			public void onStart(int operateType) {
				switch (operateType) {
				case NewsOperate.OP_Type_Delete_News:
					// 删除动态
					break;

				case NewsOperate.OP_Type_Add_Comment:
					showLoading("发布中...", true);

				case NewsOperate.OP_Type_Delete_Comment:
					break;

				default:
					break;
				}
			}

			@Override
			public void onFinish(int operateType, boolean isSucceed,
					Object resultValue) {
				actionType = NewsConstants.OPERATE_UPDATE;
				switch (operateType) {
				case NewsOperate.OP_Type_Delete_News:
					if (isSucceed) {
						ToastUtil.show(NewsDetailActivity.this, "删除成功");
						// 返回上一页
						actionType = NewsConstants.OPERATE_DELETET;
						finishWithRight();
					}
					break;

				case NewsOperate.OP_Type_Add_Comment:
					if (isSucceed) {
						// 发布成功则更新评论
						CommentModel resultmModel = (CommentModel) resultValue;
						Map<String, String> tempMap = new HashMap<String, String>();
						tempMap.put(USER_HEAD, resultmModel.getHeadSubImage());
						tempMap.put(USER_NAME, resultmModel.getPublishName());
						tempMap.put(PUBLISH_TIME, resultmModel.getAddDate());
						tempMap.put(USER_OFFICE, resultmModel.getUserOffice());
						tempMap.put(COMMENT_CONTENT,
								resultmModel.getCommentContent());
						detailAdapter.add(tempMap);
						// 滚动到底部
						newsDetailListView.getRefreshableView().setSelection(
								detailAdapter.getCount() - 1);
						hideLoading();
					} else {
						hideLoading();
					}
					break;

				case NewsOperate.OP_Type_Delete_Comment:
					if (isSucceed) {
						// 删除评论
						detailAdapter.remove(currentOperateIndex);
						ToastUtil.show(NewsDetailActivity.this, "删除成功");
					} else {
						ToastUtil.show(NewsDetailActivity.this, "删除失败，检查网络");
					}
					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * listview的head的初始化
	 * */
	private void listViewHeadInit() {
		// 添加顶部布局与初始化事件
		contenteader = View.inflate(NewsDetailActivity.this,
				R.layout.news_detail_content_layout, null);
		newsDetailListView.getRefreshableView().addHeaderView(contenteader);
		// 获取控件
		newsUserHeadImgView = (ImageView) contenteader
				.findViewById(R.id.img_news_detail_user_head);
		newsUserName = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_user_name);
		newsUserOffice = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_user_office);
		newsUserCompany = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_user_company);
		newsContent = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_content);
		newsPictures = (MultiImageView) contenteader
				.findViewById(R.id.miv_news_detail_images);
		newsLocation = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_location);
		newsPublishTime = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_publish_time);
		newsShareBtn = (TextView) contenteader
				.findViewById(R.id.btn_news_detail_share);
		newsLikeBtn = (TextView) contenteader
				.findViewById(R.id.btn_news_detail_like);
		newsCommentCount = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_comment_count);
		newsLikeCount = (TextView) contenteader
				.findViewById(R.id.txt_news_detail_like_count);
		likePersonList.add((ImageView) contenteader
				.findViewById(R.id.img_news_detail_like_user_C));
		likePersonList.add((ImageView) contenteader
				.findViewById(R.id.img_news_detail_like_user_B));
		likePersonList.add((ImageView) contenteader
				.findViewById(R.id.img_news_detail_like_user_A));
		// 点击头像
		newsUserHeadImgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转至用户主页
			}
		});
		// 点击名字
		newsUserName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		// 点击分享按钮
		newsShareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		// 点击点赞按钮
		newsLikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				actionType = NewsConstants.OPERATE_UPDATE;
				likeOperate();
			}
		});
		// 点击查看所有评论
		newsCommentCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		// 点击查看所有点赞的人
		newsLikeCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		// 点击点赞者的头像
		for (int index = 0; index < likePersonList.size(); index++) {
			likePersonList.get(index).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

				}
			});
		}
	}

	/**
	 * 绑定动态内容数据
	 * */
	private void newsContentDataSet(NewsModel data) {
		imgLoader.displayImage(data.getUserHeadSubImage(), newsUserHeadImgView,
				options);
		newsUserName.setText(data.getUserName());
		newsUserOffice.setText("首席执行官"/* data.getUserOffice() */);
		newsUserCompany.setText("腾讯科技"/* data.getUserCompany() */);
		if (data.getNewsContent().equals("")) {
			newsContent.setVisibility(View.GONE);
		} else {
			newsContent.setVisibility(View.VISIBLE);
			newsContent.setText(data.getNewsContent());
		}
		// 九宫格图片内容
		newsPictures.imageDataSet(data.getImageNewsList());
		// 快速滑动时不加载图片
		newsPictures.loadImageOnFastSlide(newsDetailListView, true);
		newsPictures.setJumpListener(new JumpCallBack() {

			@Override
			public void onImageClick(Intent intentToimageoBig) {
				startActivity(intentToimageoBig);
			}
		});

		if (data.getLocation().equals("")) {
			newsLocation.setVisibility(View.GONE);
		} else {
			newsLocation.setVisibility(View.VISIBLE);
			newsLocation.setText(data.getLocation());
		}

		newsPublishTime
				.setText(TimeHandle.getShowTimeFormat(data.getSendTime()));
		newsCommentCount.setText("评论 " + data.getCommentQuantity());
		newsLikeCount.setText("点赞" + data.getLikeQuantity());
	}

	/**
	 * listview设置
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void listViewSet() {
		// 设置刷新模式
		newsDetailListView.setMode(Mode.PULL_FROM_START);
		// 初始化动态内容控件
		listViewHeadInit();
		/**
		 * 刷新监听
		 * */
		newsDetailListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				getNewsDetailData(
						String.valueOf(UserManager.getInstance().getUser()
								.getUid()), currentNews.getNewsID());
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
			}
		});

		detailAdapter = new HelloHaAdapter<Map<String, String>>(
				NewsDetailActivity.this,
				R.layout.news_detail_like_and_comment_item, dataList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					Map<String, String> item) {
				// 设置评论与点赞的数据
				imgLoader.displayImage(item.get(USER_HEAD), (ImageView) helper
						.getView(R.id.img_news_reply_user_head), options);
				helper.setText(R.id.txt_news_reply_user_name,
						item.get(USER_NAME));
				helper.setText(R.id.txt_news_reply_user_office, "首席执行官"/*
																		 * item.get
																		 * (
																		 * USER_OFFICE
																		 * )
																		 */);
				if (isCurrentShowComment) {
					// 评论的内容
					helper.setVisible(R.id.txt_news_reply_time, true);
					helper.setVisible(R.id.txt_news_reply_content, true);
					//
					helper.setText(R.id.txt_news_reply_time, TimeHandle
							.getShowTimeFormat(item.get(PUBLISH_TIME)));
					helper.setText(R.id.txt_news_reply_content,
							item.get(COMMENT_CONTENT));
				} else {
					// 隐藏控件
					helper.setVisible(R.id.txt_news_reply_time, false);
					helper.setVisible(R.id.txt_news_reply_content, false);
				}

				// 设置评论item的点击事件
				final int postion = helper.getPosition();
				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View view) {
						itemViewClickListener.onClick(view, postion,
								view.getId());
					}
				};
				//
				helper.setOnClickListener(R.id.layout_news_reply_rootview,
						listener);
				helper.setOnClickListener(R.id.txt_news_reply_user_name,
						listener);
				helper.setOnClickListener(R.id.img_news_reply_user_head,
						listener);
			}
		};

		// 设置不可点击
		detailAdapter.setItemsClickEnable(false);
		newsDetailListView.setAdapter(detailAdapter);
	}

	/**
	 * 显示评论数据
	 * */
	private void showCommentData() {
		dataList.clear();
		for (int index = 0; index < commentDataList.size(); index++) {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put(USER_HEAD, commentDataList.get(index).getHeadSubImage());
			tempMap.put(USER_NAME, commentDataList.get(index).getPublishName());
			tempMap.put(PUBLISH_TIME, commentDataList.get(index).getAddDate());
			tempMap.put(USER_OFFICE, commentDataList.get(index).getUserOffice());
			tempMap.put(COMMENT_CONTENT, commentDataList.get(index)
					.getCommentContent());
			dataList.add(tempMap);
		}
		isCurrentShowComment = true;
		detailAdapter.replaceAll(dataList);
	}

	/**
	 * 显示点赞数据
	 * */
	private void showLikeData() {
		dataList.clear();
		for (int index = 0; index < likeDataList.size(); index++) {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put(USER_HEAD, likeDataList.get(index).getHeadSubImage());
			tempMap.put(USER_NAME, likeDataList.get(index).getName());
			tempMap.put(USER_OFFICE, likeDataList.get(index).getUserOffice());
			dataList.add(tempMap);
		}
		isCurrentShowComment = false;
		detailAdapter.replaceAll(dataList);
	}

	/**
	 * 数据处理
	 */
	@SuppressWarnings("unchecked")
	private void jsonToNewsModel(JSONObject data) {
		currentNews.setContentWithJson(data);
		commentDataList.clear();
		// 评论数据的转换
		if (data.containsKey("comments")) {
			List<JSONObject> JCommentObj = (List<JSONObject>) data
					.get("comments");
			for (JSONObject cmtObject : JCommentObj) {
				CommentModel cmtTemp = new CommentModel();
				cmtTemp.setContentWithJson(cmtObject);
				commentDataList.add(cmtTemp);
			}
		}
		// 点赞数据的转换
		likeDataList.clear();
		if (data.containsKey("likes")) {
			List<JSONObject> JLikeObj = (List<JSONObject>) data.get("likes");
			for (JSONObject lkObject : JLikeObj) {
				LikeModel lkTemp = new LikeModel();
				lkTemp.setContentWithJson(lkObject);
				likeDataList.add(lkTemp);
			}
		}
		if (currentNews.getUid().equals(
				String.valueOf(UserManager.getInstance().getUser().getUid()))) {
			// 如果是自己发布的动态则添加删除按钮
			addRightImgBtn(R.layout.right_image_button,
					R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		}
		// 显示评论数据
		showCommentData();
	}

	/**
	 * 获取动态详情数据
	 * */
	private void getNewsDetailData(String uid, String newsId) {
		// String path = KHConst.NEWS_DETAIL + "?" + "news_id=" + newsId
		// + "&user_id=" + uid;
		String path = "http://www.90newtec.com/jlxc_php/index.php/Home/MobileApi/newsDetail?news_id="
				+ newsId + "+&user_id=121";
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							jsonToNewsModel(jResult);
							newsDetailListView.onRefreshComplete();
						}

						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(NewsDetailActivity.this,
									jsonResponse
											.getString(KHConst.HTTP_MESSAGE));
							newsDetailListView.onRefreshComplete();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						newsDetailListView.onRefreshComplete();
						ToastUtil.show(NewsDetailActivity.this, "网络故障，请检查");
					}

				}, null));
	}

	/**
	 * 删除动态
	 * */
	private void deleteCurrentNews() {
		final CustomAlertDialog confirmDialog = new CustomAlertDialog(
				NewsDetailActivity.this, "确定删除该动态？", "确认", "取消");
		confirmDialog.show();
		confirmDialog
				.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						newsOPerate.deleteNews(currentNews.getNewsID());
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});
	}

	// 点击回复发送按钮
	private void publishComment() {
		if (commentContent.length() > 0) {
			String tempContent = commentContent;
			// 清空输入内容
			commentEditText.setText("");
			commentEditText.setHint("请输入评论内容...");
			// 隐藏输入键盘
			setKeyboardStatu(false);

			// 发布评论
			CommentModel temMode = new CommentModel();
			temMode.setCommentContent(tempContent);
			temMode.setAddDate(TimeHandle.getCurrentDataStr());
			newsOPerate.publishComment(UserManager.getInstance().getUser(),
					currentNews.getNewsID(), tempContent);
		}
	}

	/**
	 * view点击事件
	 * */
	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			currentOperateIndex = postion;
			switch (viewID) {

			case R.id.layout_news_reply_rootview:
			case R.id.img_news_reply_user_head:
			case R.id.txt_news_reply_user_name:
				// 在显示评论的情况下点击的是根布局
				if (isCurrentShowComment
						&& viewID == R.id.layout_news_reply_rootview) {
					if (commentDataList.get(currentOperateIndex).getUserId()
							.equals("121"/*
										 * String.valueOf( UserManager
										 * .getInstance() .getUser().getUid())
										 */)) {
						// 如果是自己发布的评论，则删除评论
						List<String> menuList = new ArrayList<String>();
						menuList.add("删除评论");
						final CustomListViewDialog downDialog = new CustomListViewDialog(
								NewsDetailActivity.this, menuList);
						downDialog.setClickCallBack(new ClickCallBack() {

							@Override
							public void Onclick(View view, int which) {
								newsOPerate.deleteComment(
										commentDataList
												.get(currentOperateIndex)
												.getCommentID(), currentNews
												.getNewsID());
								downDialog.cancel();
							}
						});
						downDialog.show();
					} else {
						// 发布回复别人的评论
						commentEditText.requestFocus();
						commentEditText.setHint("回复："
								+ commentDataList.get(currentOperateIndex)
										.getPublishName());
						// 显示键盘
						setKeyboardStatu(true);
					}
				} else {
					// 跳转至别人主页
					JumpToHomepage(KHUtils.stringToInt(commentDataList.get(
							currentOperateIndex).getUserId()));
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 点赞操作
	 * */
	private void likeOperate() {
		newsOPerate.setLikeListener(new LikeCallBack() {

			@Override
			public void onOperateStart(boolean isLike) {
				if (isLike) {
					// 点赞操作
					currentNews.setLikeQuantity(String.valueOf(currentNews
							.getLikeQuantity() + 1));
					currentNews.setIsLike("1");
				} else {
					// 取消点赞
					currentNews.setLikeQuantity(String.valueOf(currentNews
							.getLikeQuantity() - 1));
					currentNews.setIsLike("0");
				}
			}

			@Override
			public void onOperateFail(boolean isLike) {
				// 撤销上次
				newsOPerate.operateRevoked();
				if (isLike) {
					currentNews.setLikeQuantity(String.valueOf(currentNews
							.getLikeQuantity() - 1));
					currentNews.setIsLike("0");
				} else {
					currentNews.setLikeQuantity(String.valueOf(currentNews
							.getLikeQuantity() + 1));
					currentNews.setIsLike("1");
				}
			}
		});
		if (currentNews.getIsLike().endsWith("0")) {
			newsOPerate.uploadLikeOperate(currentNews.getNewsID(), false);
		} else {
			newsOPerate.uploadLikeOperate(currentNews.getNewsID(), true);
		}
	}

	/**
	 * listview点击事件接口,用于区分不同view的点击事件
	 * 
	 * @author Alan
	 * 
	 */
	private interface ListItemClickHelp {
		void onClick(View view, int postion, int viewID);
	}

	/**
	 * 跳转至用户的主页
	 */
	private void JumpToHomepage(int userID) {
		// Intent intentUsrMain = new Intent(NewsDetailActivity.this,
		// OtherPersonalActivity.class);
		// intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		// startActivityWithRight(intentUsrMain);
	}

	// 重写
	@Override
	public void finishWithRight() {
		updateResultData();
		super.finishWithRight();
	}

	// 监听返回事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finishWithRight();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 保存对动态的数据，并广播给上一个activity
	 * */
	private void updateResultData() {
		Intent mIntent = new Intent("BROADCAST_NEWS_LIST_REFRESH"/*
																 * KHConst.
																 * BROADCAST_NEWS_LIST_REFRESH
																 */);
		if (actionType.equals(NewsConstants.OPERATE_UPDATE)) {
			mIntent.putExtra(NewsConstants.OPERATE_UPDATE, currentNews);
		} else if (actionType.equals(NewsConstants.OPERATE_DELETET)) {
			// 删除操作
			mIntent.putExtra(NewsConstants.OPERATE_DELETET,
					currentNews.getNewsID());
		} else if (actionType.equals(NewsConstants.OPERATE_NO_ACTION)) {
			// 没有操作
			mIntent.putExtra(NewsConstants.OPERATE_NO_ACTION, "");
		}
		// 发送广播
		LocalBroadcastManager.getInstance(NewsDetailActivity.this)
				.sendBroadcast(mIntent);
	}
}

package com.app.khclub.message.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.message.model.IMModel;
import com.app.khclub.message.ui.model.FindResultModel;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//同校的人
public class SearchActivity extends BaseActivityWithTopBar {

	// 下拉列表
	@ViewInject(R.id.search_user_refresh_list)
	private PullToRefreshListView searchListView;
	// 搜索et
	@ViewInject(R.id.search_edit_text)
	private EditText searchEditText;
	// 显示helloHa号tv
	@ViewInject(R.id.search_top_text_view)
	private TextView searchTopTextView;
	// adapter
	HelloHaAdapter<FindResultModel> searchAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = true;
	// 是否是最后一页
	private boolean isLast = false;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 当前的页数
	private int currentPage = 1;
	// 自己
	private UserModel userModel;
	// source list
	private List<FindResultModel> findUserModels;
	// 当前的helloHaID
	private String currentHelloHaID;

	@OnClick({ R.id.search_top_layout })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.search_top_layout:
			// 查找该helloHaId
			searchHaHaId();
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_search_user;
	}

	@Override
	protected void setUpView() {
		setBarText("查找");
		// 显示头像的配置
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		findUserModels = new ArrayList<FindResultModel>();
		userModel = UserManager.getInstance().getUser();
		initListViewSet();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* 隐藏软键盘 */
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive()) {
				inputMethodManager.hideSoftInputFromWindow(SearchActivity.this
						.getCurrentFocus().getWindowToken(), 0);
			}
			showLoading("查找中...", false);
			// 下拉刷新
			isPullDowm = true;
			currentPage = 1;
			// 点击查询
			getSearchData();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	// //////////////////////////////////private
	// method//////////////////////////////////////
	/***
	 * listview的设置
	 */
	private void initListViewSet() {

		searchEditText.setFocusable(true);
		searchEditText.setFocusableInTouchMode(true);
		searchEditText.requestFocus();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) searchEditText
						.getContext().getSystemService(INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(searchEditText, 0);
			}
		}, 500);

		// 设置内容
		searchAdapter = new HelloHaAdapter<FindResultModel>(
				SearchActivity.this, R.layout.search_user_adapter) {
			@SuppressLint("ResourceAsColor")
			@Override
			protected void convert(final HelloHaBaseAdapterHelper helper,
					final FindResultModel item) {

				if (helper.getPosition() == 0) {
					helper.setVisible(R.id.top_text_view, true);
				} else {
					helper.setVisible(R.id.top_text_view, false);
				}

				helper.setText(R.id.name_text_view, item.getName());
				ImageView headImageView = helper.getView(R.id.head_image_view);
				if (null != item.getHead_sub_image()
						&& item.getHead_sub_image().length() > 0) {
					imgLoader.displayImage(
							KHConst.ATTACHMENT_ADDR + item.getHead_sub_image(),
							headImageView, options);
				} else {
					headImageView.setImageResource(R.drawable.ic_launcher);
				}
				// 添加好友tv
				Button addImageBtn = helper.getView(R.id.add_image_button);
				// 点击添加
				addImageBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// IMModel imModel = new IMModel();
						// imModel.setTargetId(KHConst.JLXC + item.getUid());
						// imModel.setAvatarPath(item.getHead_image());
						// imModel.setTitle(item.getName());
						// addFriend(imModel, helper.getPosition());
					}
				});

				// 是否是好友
				if (item.getIs_friend() == 1) {
					addImageBtn.setEnabled(false);
					addImageBtn.setBackgroundResource(R.color.main_gary);
					addImageBtn.setTextColor(getResources().getColorStateList(
							R.color.main_white));
					addImageBtn.setText("已关注");
				} else {
					addImageBtn.setEnabled(true);
					addImageBtn.setBackgroundResource(R.color.main_yellow);
					addImageBtn.setTextColor(getResources().getColorStateList(
							R.color.main_brown));
					addImageBtn.setText("关注");
				}

				LinearLayout linearLayout = (LinearLayout) helper.getView();
				// 点击事件
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到其他人页面
						Intent intent = new Intent(SearchActivity.this,
								OtherPersonalActivity.class);
						intent.putExtra(OtherPersonalActivity.INTENT_KEY,
								item.getUid());
						startActivityWithRight(intent);
					}
				});

			}
		};

		// 适配器绑定
		searchListView.setAdapter(searchAdapter);
		searchListView.setMode(Mode.PULL_FROM_START);
		searchListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		searchListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				currentPage = 1;
				getSearchData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// if (isLast) {
				// CountDownTimer countdownTimer = new CountDownTimer(500, 1000)
				// {
				// @Override
				// public void onTick(long millisUntilFinished) {
				// }
				// @Override
				// public void onFinish() {
				// searchListView.onRefreshComplete();
				// }
				// };
				// // 开始倒计时
				// countdownTimer.start();
				// return;
				// }
				// currentPage++;
				// // 上拉刷新
				// isPullDowm = false;
				// getSearchData();
			}

		});

		// 设置底部自动刷新
		searchListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (isLast) {
							searchListView.onRefreshComplete();
							return;
						}
						currentPage++;
						// 底部自动加载
						searchListView.setMode(Mode.PULL_FROM_END);
						searchListView.setRefreshing(true);
						isPullDowm = false;
						getSearchData();
					}
				});
	}

	/**
	 * 获取动态数据
	 * */
	private void getSearchData() {

		String path = KHConst.FIND_USER_LIST + "?" + "user_id="
				+ userModel.getUid() + "&content="
				+ searchEditText.getText().toString().trim() + "&page="
				+ currentPage;
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							// 最后一页
							if (0 < jResult.getIntValue("is_last")) {
								isLast = true;
							} else {
								isLast = false;
							}

							// 获取动态列表
							String jsonArrayStr = jResult
									.getString(KHConst.HTTP_LIST);
							List<FindResultModel> list = JSON.parseArray(
									jsonArrayStr, FindResultModel.class);
							if (list.size() < 1) {
								ToastUtil.show(SearchActivity.this, "没这人");
							}

							// 如果是下拉刷新
							if (isPullDowm) {
								findUserModels.clear();
								findUserModels.addAll(list);
								searchAdapter.replaceAll(findUserModels);
							} else {
								findUserModels.addAll(list);
								searchAdapter.addAll(findUserModels);
							}
							searchListView.onRefreshComplete();
							// 是否是最后一页
							if (isLast) {
								searchListView.setMode(Mode.PULL_FROM_START);
							} else {
								searchListView.setMode(Mode.BOTH);
							}

							// 如果是helloHa号格式
							// if
							// (searchEditText.getText().toString().trim().matches(KHConst.USER_ACCOUNT_PATTERN))
							// {
							// searchTopTextView.setVisibility(View.VISIBLE);
							// searchTopTextView.setText("查找HelloHa号："+searchEditText.getText().toString().trim());
							// currentHelloHaID =
							// searchEditText.getText().toString().trim();
							// }else {
							// searchTopTextView.setVisibility(View.GONE);
							// }
						}

						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(SearchActivity.this, jsonResponse
									.getString(KHConst.HTTP_MESSAGE));
							searchListView.onRefreshComplete();
							// 是否是最后一页
							if (isLast) {
								searchListView.setMode(Mode.PULL_FROM_START);
							} else {
								searchListView.setMode(Mode.BOTH);
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(SearchActivity.this, "网络有毒=_=");
						searchListView.onRefreshComplete();
						// 是否是最后一页
						if (isLast) {
							searchListView.setMode(Mode.PULL_FROM_START);
						} else {
							searchListView.setMode(Mode.BOTH);
						}
					}

				}, null));
	}

	// 添加好友
	private void addFriend(final IMModel imModel, final int index) {

		// // 参数设置
		// RequestParams params = new RequestParams();
		// params.addBodyParameter("user_id",
		// UserManager.getInstance().getUser()
		// .getUid()
		// + "");
		// params.addBodyParameter("friend_id",
		// imModel.getTargetId().replace(KHConst.JLXC, "") + "");
		//
		// showLoading("添加中^_^", false);
		// HttpManager.post(KHConst.Add_FRIEND, params,
		// new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
		//
		// @Override
		// public void onSuccess(JSONObject jsonResponse, String flag) {
		// super.onSuccess(jsonResponse, flag);
		//
		// hideLoading();
		// int status = jsonResponse
		// .getInteger(KHConst.HTTP_STATUS);
		// ToastUtil.show(SearchActivity.this,
		// jsonResponse.getString(KHConst.HTTP_MESSAGE));
		//
		// if (status == KHConst.STATUS_SUCCESS) {
		// // 添加好友
		// MessageAddFriendHelper.addFriend(imModel);
		// // 更新
		// FindResultModel FindUserModel = findUserModels
		// .get(index);
		// FindUserModel.setIs_friend(1);
		// searchAdapter.replaceAll(findUserModels);
		// }
		// }
		//
		// @Override
		// public void onFailure(HttpException arg0, String arg1,
		// String flag) {
		// super.onFailure(arg0, arg1, flag);
		// hideLoading();
		// ToastUtil.show(SearchActivity.this, "网络异常");
		// }
		// }, null));
	}

	// 查找HelloHa号
	private void searchHaHaId() {
		String path = KHConst.HELLOHA_ID_EXISTS + "?" + "helloha_id="
				+ currentHelloHaID;
		LogUtils.i(path, 1);
		showLoading("", false);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();

						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);

						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							int uid = jResult.getIntValue("uid");
							// 跳转到其他人页面
							Intent intent = new Intent(SearchActivity.this,
									OtherPersonalActivity.class);
							intent.putExtra(OtherPersonalActivity.INTENT_KEY,
									uid);
							startActivityWithRight(intent);
						} else {
							ToastUtil.show(SearchActivity.this, jsonResponse
									.getString(KHConst.HTTP_MESSAGE));
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(SearchActivity.this, "网络有毒=_=");
					}

				}, null));

	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getCurrentHelloHaID() {
		return currentHelloHaID;
	}

	public void setCurrentHelloHaID(String currentHelloHaID) {
		this.currentHelloHaID = currentHelloHaID;
	}
}

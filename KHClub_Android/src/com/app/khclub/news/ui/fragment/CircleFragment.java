package com.app.khclub.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.sharesdk.framework.authorize.g;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.activity.CirclePageActivity;
import com.app.khclub.news.ui.model.CircleItemModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class CircleFragment extends BaseFragment {
	public static final String IS_FOLLOWOPERATOR = "isFollowoperator";
	private static final String FOLLOW_LIST = "followList";
	public static final String CIRCLE_ID = "circle_id";
	public static final String CATEGORYNAME = "categoryname";
	private static final String CIRCLE_ISFOLLOW = "isFollow";
	private static final String UNFOLLOW_LIST = "unfollowList";
	private List<CircleItemModel> followList, unfollowList, dataList;
	// 临时数组
	private List<CircleItemModel> list;
	// private String[] data={"1}
	// private List<CircleItemModel> dataList=new ArrayList<CircleItemModel>();
	// private boolean ISNOTATTENTION=true;
	// 下拉列表
	@ViewInject(R.id.circle_refresh_list)
	private PullToRefreshListView circleListView;
	// adapter
	HelloHaAdapter<CircleItemModel> circleAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = false;
	// 是否是最后一页
	private boolean isattention = true;
	// private BitmapUtils bitmapUtils;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	// item位置
	private int position;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_circle_list;
	}

	@Override
	public void loadLayout(View rootView) {
	}

	@Override
	public void setUpViews(View rootView) {
		followList = new ArrayList<CircleItemModel>();
		headImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(7)).bitmapConfig(Bitmap.Config.RGB_565).build();
		initListViewSet();
		getData();
		initBoradcastReceiver();
	}

	/***
	 * 
	 * listview的设置
	 */
	private void initListViewSet() {
		// 设置内容
		circleAdapter = new HelloHaAdapter<CircleItemModel>(getActivity(), R.layout.fragment_circle_attention) {
			private int datacount;

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, final CircleItemModel item) {
				// item 位置
				position = helper.getPosition();
				datacount = dataList.size();
				// helper.setOnClickListener(viewId, listener)
				// 圈子标题
				helper.setText(R.id.circle_name, item.getCircle_name());
				helper.setText(R.id.category_name, item.getCategory_name());
				helper.setVisible(R.id.circle_attention_type, false);
				helper.setVisible(R.id.circle_attention_layout, false);
				helper.setVisible(R.id.circle_recommend_layout, false);
				helper.setVisible(R.id.circle_recommend_type, false);
				// 圈子类型（是否关注）
				if (followList.size() > 0) {
					if (followList.get(0).getId().equals(item.getId())) {
						helper.setVisible(R.id.circle_attention_layout, true);
						helper.setVisible(R.id.circle_attention_type, true);
						helper.setText(R.id.circle_attention_type,
								getResources().getString(R.string.circle_attention_type_name));
					}
				}
				if (unfollowList.size() > 0) {
					if (unfollowList.get(0).getId().equals(item.getId())) {
						// if(！dataList.size()==1){
						helper.setVisible(R.id.circle_recommend_layout, true);
						helper.setVisible(R.id.circle_recommend_type, true);
						helper.setText(R.id.circle_recommend_type,
								getResources().getString(R.string.circle_recommend_type_name));
					}
				}
				if (position >= followList.size()) {
					helper.setVisible(R.id.recommend_btn, true);
					helper.setVisible(R.id.unread_news_num, false);
				} else {
					helper.setVisible(R.id.unread_news_num, true);
					// Log.i("wwww", item.getNews_newsnum());
					if ("0".equals(item.getNews_newsnum())) {
						helper.setVisible(R.id.unread_news_num, false);
					} else {
						helper.setVisible(R.id.unread_news_num, true);
						// 取消显示未读数量了
						 helper.setText(R.id.unread_news_num, "+" +
						 item.getNews_newsnum());
					}
					helper.setVisible(R.id.recommend_btn, false);
				}
				helper.setText(R.id.circle_people_count, item.getFollow_quantity());
				ImageView headImageView = helper.getView(R.id.circle_image);
				String image = item.getCircle_cover_sub_image();
				LogUtils.i(KHConst.ATTACHMENT_ADDR + image + " " + image, 1);
				// 加入图片
				if (image.length() > 0) {
					ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR + image, headImageView,
							headImageOptions);
				} else {
					headImageView.setImageResource(R.drawable.loading_default);
				}
				helper.setOnClickListener(R.id.recommend_btn, new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						recommend(item);
						// Log.i("wwww", dataList.get(position).getId());
						// Log.i("wwww", item.getId());
						// Log.i("wwww", position+"");
					}
				});
				LinearLayout linearLayout = (LinearLayout) helper.getView(R.id.circle_list_root);
				// 点击事件
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到圈子首页
						Intent intent = new Intent(getActivity(), CirclePageActivity.class);
						intent.putExtra(CIRCLE_ID, item.getId());
						startActivityWithRight(intent);
					}
				});
				RelativeLayout moreClublayout = (RelativeLayout) helper.getView(R.id.butter_club_root);
				moreClublayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						jumpCategory();
					}

				});
			}
		};

		// 适配器绑定
		circleListView.setAdapter(circleAdapter);
		// circleListView.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// 跳转到其他人页面
		// Intent intent = new Intent(getActivity(),
		// CircleDetailActivity.class);
		// intent.putExtra(CircleDetailActivity.INTENT_CIRCLE_KEY,
		// circleAdapter.getItem(position - 1));
		// startActivityWithRight(intent);
		// }
		// });
		circleListView.setMode(Mode.PULL_FROM_START);
		circleListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		circleListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

		});

		// 快宿滑动时不加载图片
		circleListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
	}

	// 关注请求
	private void recommend(final CircleItemModel circleItemModel) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		final UserModel userModel = UserManager.getInstance().getUser();
		params.addBodyParameter("user_id", userModel.getUid() + "");
		params.addBodyParameter("circle_id", circleItemModel.getId());
		// if (position >= (followList.size() - unfollowList.size())) {
		// 设置为关注
		params.addBodyParameter("isFollow", "1");
		refreshCategory(circleItemModel.getId().trim(), circleItemModel.getCategory_name().trim());
		// isattention = true;
		// } else {
		// params.addBodyParameter("isFollow", "0");
		// isattention = false;
		// }
		// Log.i("wwww", "关注成功");
		showLoading(getActivity(), getString(R.string.uploading));
		// 关注
		HttpManager.post(KHConst.FOLLOW_OR_UNFOLLOW_CIRCLE, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse.getIntValue("status");
						hideLoading();

						switch (status) {
						case KHConst.STATUS_SUCCESS:
							ToastUtil.show(getActivity(), R.string.attention_success);
							circleItemModel.setNews_newsnum("0");
							followList.add(circleItemModel);
							unfollowList.remove(circleItemModel);
							LogUtils.i(followList + "----" + unfollowList, 1);
							// 临时用
							List<CircleItemModel> tmpList = new ArrayList<CircleItemModel>();
							tmpList.addAll(followList);
							tmpList.addAll(unfollowList);
							dataList = tmpList;
							circleAdapter.replaceAll(dataList);
							break;
						case KHConst.STATUS_FAIL:
							Toast.makeText(getActivity(), R.string.circle_attention_fail, Toast.LENGTH_SHORT).show();
							break;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1, String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
					}
				}, null));

	}

	// private void freshAttentionData() {
	// // TODO Auto-generated method stub
	// if (isattention) {
	// CircleItemModel attention = dataList.get(position);
	// followList.add(attention);
	// unfollowList.remove(attention);
	// circleAdapter.notifyDataSetChanged();
	// } else {
	// CircleItemModel attention = dataList.get(position);
	// unfollowList.add(attention);
	// followList.remove(attention);
	// circleAdapter.notifyDataSetChanged();
	// }
	// }

	/**
	 * 获取动态数据
	 */
	private void getData() {
		final UserModel userModel = UserManager.getInstance().getUser();
		String path = KHConst.GET_PERSONAL_CIRCLE_LIST + "?user_id=" + userModel.getUid();
		Log.i("wwww", path);
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					// 获取动态列表
					String unfollowJsonArray = jResult.getString(UNFOLLOW_LIST);
					// String followJsonArray = jResult.getString(FOLLOW_LIST);
					// followList = JSON.parseArray(followJsonArray,
					// CircleItemModel.class);
					followList.clear();
					JSONArray followJsonArray = jResult.getJSONArray(FOLLOW_LIST);
					for (Object object : followJsonArray) {
						CircleItemModel itemModel = new CircleItemModel();
						itemModel.setContentWithJson((JSONObject) object);
						followList.add(itemModel);
					}
					unfollowList = JSON.parseArray(unfollowJsonArray, CircleItemModel.class);
					if (dataList == null) {
						dataList = new ArrayList<CircleItemModel>();
					}
					// 显示的数组
					dataList.clear();
					dataList.addAll(followList);
					dataList.addAll(unfollowList);

					// 如果是下拉刷新
					if (isPullDowm) {
						circleAdapter.replaceAll(dataList);
					} else {
						circleAdapter.addAll(dataList);
					}
					circleListView.onRefreshComplete();
					circleListView.setMode(Mode.PULL_FROM_START);

				}
				if (status == KHConst.STATUS_FAIL) {
					circleListView.onRefreshComplete();
					circleListView.setMode(Mode.PULL_FROM_START);
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				circleListView.onRefreshComplete();
				// 是否是最后一页
				circleListView.setMode(Mode.PULL_FROM_START);

			}

		}, null));
	}

	/**
	 * 刷新分类列表
	 * @param 圈子ID
	 * @param 分类名称
	 */
	private void refreshCategory(String circleid, String categoryname) {
         Intent intent=new Intent(KHConst.BROADCAST_CIRCLE_LIST_REFRESH);
         intent.putExtra(CIRCLE_ID, circleid);
         intent.putExtra(CATEGORYNAME, categoryname);
         intent.putExtra(IS_FOLLOWOPERATOR, true);
         mLocalBroadcastManager.sendBroadcast(intent);
	}

	private LocalBroadcastManager mLocalBroadcastManager;

	/**
	 * 初始化广播信息
	 */
	private void initBoradcastReceiver() {

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(KHConst.BROADCAST_CIRCLE_LIST_REFRESH);
		// 注册广播
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	/**
	 * 跳转至分类
	 */
	private void jumpCategory() {
		// TODO Auto-generated method stub;
		// Log.i("wwww", "调整");
		Intent intent = new Intent(KHConst.BROADCAST_NEW_MESSAGE_PUSH);
		// 通知跳转至分类页面
		intent.putExtra("jumpcategory", "jumpcategory");
		getActivity().sendBroadcast(intent);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// Log.i("wwww","刷新");
			if (intent.hasExtra(CirclePageActivity.CIRCLEFRESH)) {
				//Log.i("wwww", "刷新");
				getData();
				circleAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBroadcastReceiver != null && mLocalBroadcastManager != null) {
			mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		}
	};
}

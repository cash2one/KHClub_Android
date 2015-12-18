package com.app.khclub.news.ui.activity;

import java.util.List;

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
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.message.ui.activity.CollectCardActivity;
import com.app.khclub.news.ui.model.BetterMembersModel;
import com.app.khclub.news.ui.model.CircleItemModel;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
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

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class BetterMemberActivity extends BaseActivityWithTopBar {

	private static final String CIRCLE_ID = "circle_id";
	private List<BetterMembersModel> dataList;
	// 下拉列表
	@ViewInject(R.id.better_members_listView)
	private PullToRefreshListView membersListView;
	// adapter
	HelloHaAdapter<BetterMembersModel> MembersModelAdapter;
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
	private boolean isattention = true;
	// private BitmapUtils bitmapUtils;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	// item位置
	private int position;

	/***
	 * 
	 * listview的设置
	 */
	private void initListViewSet() {
		// 设置内容
		MembersModelAdapter = new HelloHaAdapter<BetterMembersModel>(BetterMemberActivity.this,
				R.layout.activity_better_expert_item) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, final BetterMembersModel item) {
				// item 位置
				position = helper.getPosition();
				if (position == 0) {
					helper.setVisible(R.id.all_club_members_layout, true);
				} else {
					helper.setVisible(R.id.all_club_members_layout, false);
				}
				helper.setText(R.id.member_user_name, item.getName());
				if ("".equals(item.getJob())) {
					helper.setText(R.id.member_job, "暂无信息");
				} else {
					helper.setText(R.id.member_job, item.getJob());
				}
				ImageView userImageView = helper.getView(R.id.member_user_image);
				ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR + item.getHead_sub_image(),
						userImageView, headImageOptions);
			}
		};

		// 适配器绑定
		membersListView.setAdapter(MembersModelAdapter);
		membersListView.setMode(Mode.BOTH);
		membersListView.setPullToRefreshOverScrollEnabled(false);
		/**
		 * 刷新监听
		 * */
		membersListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isRequestingData) {
					isRequestingData = true;
					currentPage = 1;
					isPullDown = true;
					getData(currentPage);
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!lastPage.equals("1") && !isRequestingData) {
					isRequestingData = true;
					isPullDown = false;
					getData(currentPage);
				}
			}
		});
		/**
		 * 设置底部自动刷新
		 * */
		membersListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!lastPage.equals("1")) {
							membersListView.setMode(Mode.PULL_FROM_END);
							membersListView.setRefreshing(true);
						}
					}
				});

		// 快宿滑动时不加载图片
		membersListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

		membersListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intentToUsrMain = new Intent(BetterMemberActivity.this, OtherPersonalActivity.class);
				BetterMembersModel membersModel = MembersModelAdapter.getItem(position - 1);
				intentToUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY,
						KHUtils.stringToInt(membersModel.getUser_id()));
				startActivityWithRight(intentToUsrMain);
			}
		});
	}

	// private void freshAttentionData() {
	// // TODO Auto-generated method stub
	// if (isattention) {
	// CircleItemModel attention = dataList.get(position);
	// followList.add(attention);
	// unfollowList.remove(position);
	// circleAdapter.notifyDataSetChanged();
	// } else {
	// CircleItemModel attention = dataList.get(position);
	// unfollowList.add(attention);
	// followList.remove(position);
	// circleAdapter.notifyDataSetChanged();
	// }
	// }

	/**
	 * 获取动态数据
	 */
	private void getData(int page) {
		String circleid = getIntent().getStringExtra("circle_id");
		String path = KHConst.GET_CIRCLE_MEMBERS + "?circle_id=" + circleid + "&page="
				+ page;
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					// 获取动态列表
					String followJsonArray = jResult.getString("list");
					dataList = JSON.parseArray(followJsonArray, BetterMembersModel.class);
					// 如果是下拉刷新
					if (isPullDown) {
						MembersModelAdapter.replaceAll(dataList);
					} else {
						MembersModelAdapter.addAll(dataList);
					}
					
					membersListView.onRefreshComplete();
					// 是否是最后页
					lastPage = jResult.getString("is_last");
					if (lastPage.equals("0")) {
						currentPage++;
						membersListView.setMode(Mode.BOTH);
					} else {
						membersListView.setMode(Mode.PULL_FROM_START);
					}
					isRequestingData = false;					

				}
				
				if (status == KHConst.STATUS_FAIL) {
					membersListView.onRefreshComplete();
					if (lastPage.equals("0")) {
						membersListView.setMode(Mode.BOTH);
					}
					ToastUtil.show(BetterMemberActivity.this,jsonResponse.getString(KHConst.HTTP_MESSAGE));
				}
				isRequestingData = false;
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				membersListView.onRefreshComplete();
				if (lastPage.equals("0")) {
					membersListView.setMode(Mode.BOTH);
				}
				ToastUtil.show(BetterMemberActivity.this,getString(R.string.net_error));
				isRequestingData = false;
			}

		}, null));
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		setBarText(getString(R.string.news_club_expert));
		headImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.displayer(new RoundedBitmapDisplayer(7)).showImageOnFail(R.drawable.loading_default)
				.cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		getData(currentPage);
		initListViewSet();
	}

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_better_expert;
	}

}

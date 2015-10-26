package com.app.khclub.message.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.activity.NewsDetailActivity;
import com.app.khclub.news.ui.activity.NewsDetailActivity.ItemViewClick;
import com.app.khclub.news.ui.model.CommentModel;
import com.app.khclub.news.ui.model.LikeModel;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectCardActivity extends BaseActivityWithTopBar {

	// 数据key
	private final static String USER_ID = "user_id";
	private final static String USER_HEAD = "user_head";
	private final static String USER_NAME = "user_name";
	private final static String USER_JOB = "user_job";
	private final static String USER_COMPANY = "user_company";

	// 名片listview
	@ViewInject(R.id.card_collect_listView)
	private PullToRefreshListView cardListView;
	// 名片的数据
	private List<Map<String, String>> dataList;
	// 主适配器
	private HelloHaAdapter<Map<String, String>> dataAdapter;
	// 当前的刷新模式
	private boolean isPullDown = false;
	// 当前的数据页
	private int currentPage = 1;
	// 是否是最后一页数据
	private String lastPage = "0";
	// 是否正在请求数据
	private boolean isRequestingData = false;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	@Override
	public int setLayoutId() {
		return R.layout.activity_collect_card;
	}

	@Override
	protected void setUpView() {
		setBarText(getString(R.string.my_collect_code));
		init();
		listviewSet();

		// 首次进入获取数据
		isPullDown = true;
		getCollectCardData(String.valueOf(currentPage));
	}

	/**
	 * 初始化
	 * */
	private void init() {
		// 获取实例
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.icon).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 数据绑定初始化
	 * */
	private void listviewSet() {
		// 设置刷新模式
		cardListView.setMode(Mode.BOTH);

		dataAdapter = new HelloHaAdapter<Map<String, String>>(
				CollectCardActivity.this,
				R.layout.listitem_collect_card_layout, dataList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					Map<String, String> item) {
				// 绑定头像图片
				if (null != item.get(USER_HEAD)
						&& item.get(USER_HEAD).length() > 0) {
					imgLoader.displayImage(item.get(USER_HEAD),
							(ImageView) helper
									.getView(R.id.img_collect_card_user_head),
							options);
				} else {
					((ImageView) helper
							.getView(R.id.img_collect_card_user_head))
							.setImageResource(R.drawable.icon);
				}
				// 绑定昵称
				helper.setText(R.id.txt_collect_card_user_name,
						item.get(USER_NAME));
				// 绑定公司
				helper.setText(R.id.txt_collect_card_user_company,
						item.get(USER_COMPANY));
				// 绑定职位
				helper.setText(R.id.txt_collect_card_user_job,
						item.get(USER_JOB));

				// 设置点击事件监听
				final int postion = helper.getPosition();
				helper.setOnClickListener(R.id.layout_collect_card_rootview,
						new OnClickListener() {

							@Override
							public void onClick(View view) {
								Intent intentUsrMain = new Intent(
										CollectCardActivity.this,
										OtherPersonalActivity.class);
								intentUsrMain
										.putExtra(
												OtherPersonalActivity.INTENT_KEY,
												KHUtils.stringToInt(dataAdapter
														.getItem(postion).get(
																USER_ID)));
								startActivityWithRight(intentUsrMain);
							}
						});
			}
		};

		/**
		 * 刷新监听
		 * */
		cardListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isRequestingData) {
					isRequestingData = true;
					currentPage = 1;
					isPullDown = true;
					getCollectCardData(String.valueOf(currentPage));
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!lastPage.equals("1") && !isRequestingData) {
					isRequestingData = true;
					isPullDown = false;
					getCollectCardData(String.valueOf(currentPage));
				}
			}
		});
		/**
		 * 设置底部自动刷新
		 * */
		cardListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (!lastPage.equals("1")) {
							cardListView.setMode(Mode.PULL_FROM_END);
							cardListView.setRefreshing(true);
						}
					}
				});

		cardListView.setAdapter(dataAdapter);
	}

	private void JsonToPersonData(List<JSONObject> jPersonList) {
		List<Map<String, String>> List = new ArrayList<Map<String, String>>();
		// 解析数据
		for (JSONObject cardObj : jPersonList) {
			Map<String, String> tempCard = new HashMap<String, String>();
			tempCard.put(USER_ID, cardObj.getString("user_id"));
			tempCard.put(
					USER_HEAD,
					KHConst.ATTACHMENT_ADDR
							+ cardObj.getString("head_sub_image"));
			tempCard.put(USER_NAME, cardObj.getString("name"));
			tempCard.put(USER_JOB, cardObj.getString("job"));
			tempCard.put(USER_COMPANY, cardObj.getString("company_name"));

			// 如果是好友则不显示
			if (!cardObj.getString("is_friend").equals("1")) {
				List.add(tempCard);
			}
		}
		if (isPullDown) {
			dataAdapter.replaceAll(List);
		} else {
			dataAdapter.addAll(List);
		}
		if (null != jPersonList) {
			jPersonList.clear();
		}
	}

	/**
	 * 获所有收藏的名片信息
	 * */
	private void getCollectCardData(String page) {
		String path = KHConst.GET_COLLECT_CARD_LIST + "?" + "&user_id="
				+ UserManager.getInstance().getUser().getUid() + "&page="
				+ page;

		LogUtils.i("path=" + path);
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
							// 获取数据列表
							List<JSONObject> JCardList = (List<JSONObject>) jResult
									.get("list");
							JsonToPersonData(JCardList);
							cardListView.onRefreshComplete();

							// 是否是最后页
							lastPage = jResult.getString("is_last");
							if (lastPage.equals("0")) {
								currentPage++;
								cardListView.setMode(Mode.BOTH);
							} else {
								cardListView.setMode(Mode.PULL_FROM_START);
							}
							isRequestingData = false;
						}

						if (status == KHConst.STATUS_FAIL) {
							cardListView.onRefreshComplete();
							if (lastPage.equals("0")) {
								cardListView.setMode(Mode.BOTH);
							}
							ToastUtil.show(CollectCardActivity.this,
									jsonResponse
											.getString(KHConst.HTTP_MESSAGE));
						}
						isRequestingData = false;
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						cardListView.onRefreshComplete();
						if (lastPage.equals("0")) {
							cardListView.setMode(Mode.BOTH);
						}
						ToastUtil.show(CollectCardActivity.this,
								getString(R.string.net_error));
						isRequestingData = false;
					}
				}, null));
	}
}

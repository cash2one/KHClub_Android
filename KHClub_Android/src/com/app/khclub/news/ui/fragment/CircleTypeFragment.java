package com.app.khclub.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.r;
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
import com.app.khclub.news.ui.utils.CircleTypeConstant;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CircleTypeFragment extends BaseFragment {
	public static final String FRESHCATEGORYLIST = "freshcategorylist";
	public static final String CIRCLEFRESH = "Circlefresh";
	// 图片配置
	private DisplayImageOptions headImageOptions;
	// 是否是下拉刷新
	private boolean isPullDowm = false;
	// 是否是点击选择分类
	private boolean isChangeCategory = false;
	private static final String CIRCLE_ID = "circle_id";
	// 选中的Position
	private int index = 0;
	@ViewInject(R.id.circle_type_list)
	private ListView typeListView;
	@ViewInject(R.id.category_circle_list)
	private PullToRefreshListView categoryListView;
	// 是否是最后一页数据
	private boolean lastPage = false;
	// 当前数据的页
	private int pageIndex = 1;
	// 是否正在请求数据
	private boolean isRequestingData = false;
	// 进入分类时默认是选中第一个分类
	private int categoryID = CircleTypeConstant.INVESTMENT;
	// 圈子分类列表数据源
	protected List<CircleItemModel> dataList;
	// 圈子分类列表适配器
	private HelloHaAdapter<CircleItemModel> categoryAdapter;
	//记录点击的Item 所对应的关注按钮
	private TextView impTextView;
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragemt_classify;
	}

	@Override
	public void loadLayout(View rootView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpViews(View rootView) {
		// TODO Auto-generated method stub
		headImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(7)).bitmapConfig(Bitmap.Config.RGB_565).build();
		dataList = new ArrayList<CircleItemModel>();
		initTypeList();
		initCategoryListData();
		initCategoryLisView();
	}

	/**
	 * 初始化圈子分类列表
	 */
	private void initCategoryLisView() {
		categoryAdapter = new HelloHaAdapter<CircleItemModel>(getActivity(), R.layout.fragment_categorylist_item) {
			@Override
			protected void convert(final HelloHaBaseAdapterHelper helper, final CircleItemModel item) {
				// TODO Auto-generated method stub
				helper.setText(R.id.category_circle_name, item.getCircle_name());
				ImageView headImageView = helper.getView(R.id.category_head_image);
				String image = item.getCircle_cover_sub_image();
				// 加入图片
				if (image.length() > 0) {
					ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR + image, headImageView,
							headImageOptions);
				} else {
					headImageView.setImageResource(R.drawable.loading_default);
				}

				if ("0".equals(item.getIs_follow())) {
					helper.setBackgroundRes(R.id.category_circle_isfollow, R.drawable.category_unfollow);
					helper.setText(R.id.category_circle_isfollow, getString(R.string.attention));
					helper.setTextColor(R.id.category_circle_isfollow, getResources().getColor(R.color.main_gold));
					helper.setOnClickListener(R.id.category_circle_isfollow, new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// Log.i("wx", item.getIs_follow());
							follow(item, helper);
							// categoryAdapter.remove(item);
						}

					});
				} else {
					helper.setBackgroundRes(R.id.category_circle_isfollow, R.drawable.category_follow);
					helper.setText(R.id.category_circle_isfollow, getString(R.string.already_follow));
					helper.setTextColor(R.id.category_circle_isfollow,
							getResources().getColor(R.color.follow_char_gary));
				}
				helper.getView().setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						impTextView=(TextView) v.findViewById(R.id.category_circle_isfollow);
						// 跳转到圈子首页
						Intent intent = new Intent(getActivity(), CirclePageActivity.class);
						intent.putExtra(CIRCLE_ID, item.getId());
						startActivityWithRight(intent);
					}
				});

			}
		};
		categoryListView.setAdapter(categoryAdapter);
		categoryListView.setMode(Mode.PULL_FROM_START);
		categoryListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		categoryListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				if (!isRequestingData) {
					isPullDowm = true;
					isRequestingData = true;
					pageIndex = 1;
					isChangeCategory = false;
					initCategoryListData();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!lastPage && !isRequestingData) {
					isRequestingData = true;
					isPullDowm = false;
					isChangeCategory = false;
					initCategoryListData();
				}
			}

		});
		/**
		 * 设置底部自动刷新
		 */
		categoryListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (!lastPage) {
					categoryListView.setMode(Mode.PULL_FROM_END);
					categoryListView.setRefreshing(true);
				}
			}
		});
		// 快宿滑动时不加载图片
		categoryListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
	}

	private void follow(final CircleItemModel circleItemModel, final HelloHaBaseAdapterHelper helper) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		final UserModel userModel = UserManager.getInstance().getUser();
		params.addBodyParameter("user_id", userModel.getUid() + "");
		params.addBodyParameter("circle_id", circleItemModel.getId());
		// 设置为关注
		params.addBodyParameter("isFollow", "1");
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
							circleItemModel.setIs_follow("1");
							TextView textView = helper.getView(R.id.category_circle_isfollow);
							setting(textView);
							freshCircle();
							// isPullDowm=true;
							// initCategoryListData();
							// categoryAdapter.add(circleItemModel);
							// categoryAdapter.notifyDataSetChanged();
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

	protected void setting(TextView textView) {
		// TODO Auto-generated method stub
		textView.setBackgroundResource(R.drawable.category_follow);
		textView.setText(getString(R.string.already_follow));
		textView.setTextColor(getResources().getColor(R.color.follow_char_gary));
		textView.setClickable(false);
	}

	/**
	 * 获取圈子分类列表数据
	 */
	private void initCategoryListData() {
		// TODO Auto-generated method stub
		dataList.clear();
		final UserModel userModel = UserManager.getInstance().getUser();
		String path = KHConst.GET_CIRCLE_CATEGORY_LIST + "?user_id=" + userModel.getUid() + "&category_id=" + categoryID
				+ "&page=" + pageIndex;
		// Log.i("wwww", path);
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					// 获取动态列表
					JSONArray jsonArray = jResult.getJSONArray(KHConst.HTTP_LIST);
					// jsonArray.toString();
					// 显示的数组
					for (Object object : jsonArray) {
						CircleItemModel itemModel = new CircleItemModel();
						itemModel.setContentWithJson((JSONObject) object);
						dataList.add(itemModel);
					}

					// 如果是下拉刷新或者是切换分类
					if (isPullDowm || isChangeCategory) {
						// categoryAdapter.clear();
						categoryAdapter.replaceAll(dataList);
						categoryAdapter.notifyDataSetChanged();
					} else {
						categoryAdapter.addAll(dataList);
					}
					categoryListView.onRefreshComplete();
					// 是否是最后页
					// Log.i("wwww", jResult.getString("is_last"));
					if (jResult.getString("is_last").equals("0")) {
						lastPage = false;
						pageIndex++;
						categoryListView.setMode(Mode.BOTH);
					} else {
						lastPage = true;
						categoryListView.setMode(Mode.PULL_FROM_START);
					}
					isRequestingData = false;

				}
				if (status == KHConst.STATUS_FAIL) {
					categoryListView.onRefreshComplete();
					categoryListView.setMode(Mode.BOTH);
					isRequestingData = false;
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				categoryListView.onRefreshComplete();
				categoryListView.setMode(Mode.PULL_FROM_START);
				isRequestingData = false;
			}

		}, null));

	}

	/**
	 * 初始化类型列表
	 */
	private void initTypeList() {
		// TODO Auto-generated method stub
		int type[] = { R.string.investment, R.string.business_trade, R.string.tea, R.string.liquor_cigar,
				R.string.money, R.string.artwork, R.string.travel, R.string.golf, R.string.college, R.string.luxury,
				R.string.car_yacht, R.string.other };
		List<String> data = toStringArray(type);
		final HelloHaAdapter<String> adapter = new HelloHaAdapter<String>(getActivity(),
				R.layout.fragment_circle_list_type_item, data) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, String item) {
				// TODO Auto-generated method stub
				helper.setText(R.id.typetext, item);
				if (index == helper.getPosition()) {
					helper.setBackgroundRes(R.id.category_list_root, R.color.main_deep_white);
					helper.setVisible(R.id.gold_triangle, true);
					helper.setTextColorRes(R.id.typetext, R.color.main_gold);
				} else {
					helper.setBackgroundColor(R.id.category_list_root, Color.parseColor("#f7f9f8"));
					helper.setVisible(R.id.gold_triangle, false);
					helper.setTextColorRes(R.id.typetext, R.color.black_deep);
				}
			}
		};
		typeListView.setAdapter(adapter);
		// typeListView.setSelector(sel);
		typeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				categoryID = getCategoryID(position);
				isChangeCategory = true;
				pageIndex = 1;
				initCategoryListData();
				index = position;
				LinearLayout root = (LinearLayout) view.findViewById(R.id.category_list_root);
				root.setBackgroundColor(getResources().getColor(R.color.main_deep_white));
				ImageView triangle = (ImageView) view.findViewById(R.id.gold_triangle);
				triangle.setVisibility(View.VISIBLE);
				TextView categoryText = (TextView) view.findViewById(R.id.typetext);
				categoryText.setTextColor(getResources().getColor(R.color.main_gold));
				adapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Log.i("wwww", "1111");
	}

	/**
	 * 刷新圈子页面
	 */
	protected void freshCircle() {
		// TODO Auto-generated method stub
		Intent freshIntent = new Intent(KHConst.BROADCAST_CIRCLE_LIST_REFRESH);
		Log.i("wwww", "发广播");
		freshIntent.putExtra(CIRCLEFRESH, "fresh");
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(freshIntent);
	}

	protected int getCategoryID(int position) {
		// TODO Auto-generated method stub
		int categoryid = -1;
		switch (position + 1) {
		case 1:
			categoryid = CircleTypeConstant.INVESTMENT;
			break;
		case 2:
			categoryid = CircleTypeConstant.BUSINESS_TRADE;
			break;
		case 3:
			categoryid = CircleTypeConstant.TEA;
			break;
		case 4:
			categoryid = CircleTypeConstant.LIQUOR_CIGAR;
			break;
		case 5:
			categoryid = CircleTypeConstant.MONEY;
			break;
		case 6:
			categoryid = CircleTypeConstant.ARTWORK;
			break;

		case 7:
			categoryid = CircleTypeConstant.TRAVEL;
			break;
		case 8:
			categoryid = CircleTypeConstant.GOLF;
			break;
		case 9:
			categoryid = CircleTypeConstant.COLLEGE;
			break;
		case 10:
			categoryid = CircleTypeConstant.LUXURY;
			break;
		case 11:
			categoryid = CircleTypeConstant.CAR_YACHT;
			break;
		case 12:
			categoryid = CircleTypeConstant.OTHER;
			break;
		default:
			break;
		}
		return categoryid;
	}

	private List<String> toStringArray(int array[]) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(getString(array[i]));
		}
		return list;
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.hasExtra(FRESHCATEGORYLIST)) {
                    setting(impTextView);
			}
		}
	};
}

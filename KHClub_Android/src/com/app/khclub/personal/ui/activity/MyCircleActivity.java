package com.app.khclub.personal.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
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
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.news.ui.activity.CirclePageActivity;
import com.app.khclub.news.ui.model.CircleItemModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class MyCircleActivity extends BaseActivityWithTopBar{
	private List<CircleItemModel> dataList;
	//圈子ID
	private String CIRCLE_ID="circle_id";
	// 下拉列表
	@ViewInject(R.id.mycircle_listView)
	private PullToRefreshListView circleListView;
	// 我的圈子适配器
	HelloHaAdapter<CircleItemModel> circleAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
//	// 是否下拉刷新
	private boolean isPullDowm = false;
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
		circleAdapter = new HelloHaAdapter<CircleItemModel>(MyCircleActivity.this, R.layout.activity_mycircle_item) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, final CircleItemModel item) {
				// helper.setOnClickListener(viewId, listener)
				// 圈子标题
				helper.setText(R.id.circle_name, item.getCircle_name());
				// 圈子类型（是否关注）
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
				
				LinearLayout linearLayout = (LinearLayout) helper.getView();
				// 点击事件
				linearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转到其他人页面
						Intent intent = new Intent(MyCircleActivity.this, CirclePageActivity.class);
						intent.putExtra(CIRCLE_ID, item.getId());
						startActivityWithRight(intent);
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
		// Intent intent = new Intent(MyCircleActivity.this,
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
	/**
	 * 获取动态数据
	 */
	private void getData() {
		dataList=new ArrayList<CircleItemModel>();
		String path = KHConst.GET_MY_CIRCLE_LIST + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
		Log.i("wwww", path);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					// 数据处理
					JSONArray array = jsonResponse.getJSONArray(KHConst.HTTP_RESULT);
					for (int i = 0; i < array.size(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						CircleItemModel myCircle=new CircleItemModel();
						myCircle.setContentWithJson(object);
						dataList.add(myCircle);
					}
					if (isPullDowm) {
						circleAdapter.replaceAll(dataList);
					} else {
						circleAdapter.addAll(dataList);
					}
					circleListView.onRefreshComplete();
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				hideLoading();
				Toast.makeText(MyCircleActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
			}

		}, null));
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		setBarText(getString(R.string.circle_attention_type_name));
		headImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(7))
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		getData();
		initListViewSet();
	}

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_mycircle;
	}
}

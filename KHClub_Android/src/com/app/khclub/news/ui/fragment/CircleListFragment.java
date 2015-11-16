package com.app.khclub.news.ui.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.news.ui.activity.CircleDetailActivity;
import com.app.khclub.news.ui.model.CircleModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class CircleListFragment extends BaseFragment{

	//下拉列表
	@ViewInject(R.id.circle_refresh_list)
	private PullToRefreshListView circleListView;
	//adapter
	HelloHaAdapter<CircleModel> circleAdapter;
	// 下拉模式
	public static final int PULL_DOWM_MODE = 0;
	// 上拉模式
	public static final int PULL_UP_MODE = 1;
	// 是否下拉刷新
	private boolean isPullDowm = false;
	//是否是最后一页
	private boolean isLast = false; 
//	private BitmapUtils bitmapUtils;
	//新图片缓存工具 头像
	DisplayImageOptions headImageOptions;		
	//当前的页数
	private int currentPage = 1;	
	
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

		headImageOptions = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.loading_default)  
        .showImageOnFail(R.drawable.loading_default)  
        .cacheInMemory(true)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		initListViewSet();
		getData();		
	}
	
	////////////////////////////////////private method //////////////////////////////////////
	/***
	 * 
	 * listview的设置
	 */
	private void initListViewSet() {
		
		//设置内容
		circleAdapter = new HelloHaAdapter<CircleModel>(
				getActivity(), R.layout.adapter_circle_info) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					final CircleModel item) {
				//圈子标题
				helper.setText(R.id.circle_name_text_view, item.getTitle());
				//圈子详情
				helper.setText(R.id.circle_desc_text_view, item.getIntro());
				ImageView headImageView = helper.getView(R.id.circle_image_view);
				
				String[] images = item.getImage().split(",");
				LogUtils.i(KHConst.ATTACHMENT_ADDR + images[0]+" "+images, 1);
				//取第一张就行
				if (null != images && images.length > 0) {
					ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR + images[0], headImageView, headImageOptions);					
				}else {
					headImageView.setImageResource(R.drawable.loading_default);
				}
//				LinearLayout linearLayout = (LinearLayout) helper.getView();
//				//点击事件
//				linearLayout.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						//跳转到其他人页面
//						Intent intent = new Intent(getActivity(), CircleDetailActivity.class);
//						intent.putExtra(CircleDetailActivity.INTENT_CIRCLE_KEY, item);
//						startActivityWithRight(intent);
//					}
//				});
				
			}
		};

		// 适配器绑定
		circleListView.setAdapter(circleAdapter);
		circleListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//跳转到其他人页面
				Intent intent = new Intent(getActivity(), CircleDetailActivity.class);
				intent.putExtra(CircleDetailActivity.INTENT_CIRCLE_KEY, circleAdapter.getItem(position-1));
				startActivityWithRight(intent);
			}
		});
		circleListView.setMode(Mode.PULL_FROM_START);
		circleListView.setPullToRefreshOverScrollEnabled(false);
		// 设置刷新事件监听
		circleListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				isPullDowm = true;
				currentPage = 1;
				getData();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}

		});

		// 设置底部自动刷新
		circleListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						if (isLast) {
							circleListView.onRefreshComplete();
							return;
						}
						currentPage++;
						// 底部自动加载
						circleListView.setMode(Mode.PULL_FROM_END);
						circleListView.setRefreshing(true);
						isPullDowm = false;
						getData();
					}
				});
		
		// 快宿滑动时不加载图片
		circleListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),
				false, true));
	}
	
	
	/**
	 * 获取动态数据
	 * */
	private void getData() {

		String path = KHConst.GET_CIRCLE_LIST + "?" + "&page="+currentPage;
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
							//最后一页
							if (0 < jResult.getIntValue("is_last")) {
								isLast = true;								
							}else {
								isLast = false;
							}
							// 获取动态列表							
							String jsonArrayStr = jResult.getString(KHConst.HTTP_LIST);
							List<CircleModel> list = JSON.parseArray(jsonArrayStr, CircleModel.class);
							//如果是下拉刷新
							if (isPullDowm) {
								circleAdapter.replaceAll(list);
							}else {
								circleAdapter.addAll(list);
							}
							circleListView.onRefreshComplete();
							//是否是最后一页
							if (isLast) {
								circleListView.setMode(Mode.PULL_FROM_START);
							}else {
								circleListView.setMode(Mode.BOTH);
							}
						}

						if (status == KHConst.STATUS_FAIL) {
							circleListView.onRefreshComplete();
							//是否是最后一页
							if (isLast) {
								circleListView.setMode(Mode.PULL_FROM_START);
							}else {
								circleListView.setMode(Mode.BOTH);
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						circleListView.onRefreshComplete();
						//是否是最后一页
						if (isLast) {
							circleListView.setMode(Mode.PULL_FROM_START);
						}else {
							circleListView.setMode(Mode.BOTH);
						}
					}

				}, null));
	}	

}

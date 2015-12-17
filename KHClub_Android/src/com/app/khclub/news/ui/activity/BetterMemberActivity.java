package com.app.khclub.news.ui.activity;

import java.io.Serializable;
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
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.model.CircleItemModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class BetterMemberActivity extends  BaseActivityWithTopBar{

	private static final String FOLLOW_LIST = "followList";
	private static final String CIRCLE_ID = "circle_id";
	private static final String CIRCLE_ISFOLLOW= "isFollow";
	private static final String UNFOLLOW_LIST = "unfollowList";
	private List<CircleItemModel> followList, unfollowList, dataList;
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
	public void loadLayout(View rootView) {
		
	}


	/***
	 * 
	 * listview的设置
	 */
	private void initListViewSet() {
		// 设置内容
		circleAdapter = new HelloHaAdapter<CircleItemModel>(BetterMemberActivity.this, R.layout.activity_better_expert_item) {
			private int datacount;
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, final CircleItemModel item) {
				// item 位置
				position = helper.getPosition();
				datacount = dataList.size();
				// helper.setOnClickListener(viewId, listener)
				// 圈子标题
				helper.setText(R.id.circle_name, item.getCircle_name());
				// 圈子类型（是否关注）
				if (followList.size() > 0) {
					if (followList.get(0).getId().equals(item.getId())) {
						helper.setVisible(R.id.circle_attention_type, true);
						helper.setText(R.id.circle_attention_type,
								getResources().getString(R.string.circle_attention_type_name));
					} else {
						helper.setVisible(R.id.circle_attention_type, false);
					}
				}
				if (unfollowList.size() > 0) {
					if (unfollowList.get(0).getId().equals(item.getId())) {
						helper.setVisible(R.id.circle_recommend_type, true);
						helper.setText(R.id.circle_recommend_type,
								getResources().getString(R.string.circle_recommend_type_name));
					} else {
						helper.setVisible(R.id.circle_recommend_type, false);
					}
				}
				if (position >= (followList.size() - unfollowList.size())) {
					helper.setVisible(R.id.recommend_btn, true);
				} else {
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



	private void freshAttentionData() {
		// TODO Auto-generated method stub
		Log.i("wwww", position+"");
		if (isattention) {
			CircleItemModel attention = dataList.get(position);
			followList.add(attention);
			unfollowList.remove(position);
			circleAdapter.notifyDataSetChanged();
		} else {
			CircleItemModel attention = dataList.get(position);
			unfollowList.add(attention);
			followList.remove(position);
			circleAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 获取动态数据
	 */
	private void getData() {
		String circleid="";
		String path = KHConst.GET_PERSONAL_CIRCLE_LIST + "?circle_id=" +circleid;
		// Log.i("wwww", path);
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					// 获取动态列表
					String unfollowJsonArray = jResult.getString(UNFOLLOW_LIST);
					String followJsonArray = jResult.getString(FOLLOW_LIST);
					followList = JSON.parseArray(followJsonArray, CircleItemModel.class);
					unfollowList = JSON.parseArray(unfollowJsonArray, CircleItemModel.class);
					followList.addAll(unfollowList);
					dataList = followList;
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
 /*   public  interface freshCircleData{
    	void fresh();
    }*/
	
	 Fresh fresh =new Fresh();
     class Fresh implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void fresh() {
			// TODO Auto-generated method stub
			getData();
			circleAdapter.replaceAll(dataList);
		}}
	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_better_expert;
	}
   

}

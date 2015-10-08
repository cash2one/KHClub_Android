package com.app.khclub.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.model.NewsPushModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.ui.view.CustomListViewDialog;
import com.app.khclub.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.news.ui.activity.NewsDetailActivity;
import com.app.khclub.news.ui.model.NewsConstants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NotifyNewsFragment extends BaseFragment {

	// 列表
	@ViewInject(R.id.notify_list_view)
	private ListView notifyListView;
	// adapter
	private HelloHaAdapter<NewsPushModel> newsAdapter;
	// BitmapUtils bitmapUtils;
	// 新图片缓存工具 头像
	private DisplayImageOptions headImageOptions;
	private int page = 1;
	private int size = 30;

	@Override
	public int setLayoutId() {
		return R.layout.fragment_new_notify;
	}
	@Override
	public void loadLayout(View rootView) {
	}

	@Override
	public void setUpViews(View rootView) {
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		// 注册通知
		registerNotify();
		setListView();
		// 刷新列表
		refreshList();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (messageReceiver != null) {
			getActivity().unregisterReceiver(messageReceiver);
			messageReceiver = null;
		}
	}

	//////////////////////////private method/////////////////////////////
	// 设置listView
	private void setListView() {

		newsAdapter = new HelloHaAdapter<NewsPushModel>(getActivity(),
				R.layout.new_notify_adapter) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					NewsPushModel item) {
				// TODO Auto-generated method stub
				// 姓名
				helper.setText(R.id.name_text_view, item.getName());
				// 时间
				helper.setText(R.id.time_text_view,
						TimeHandle.getShowTimeFormat(item.getPush_time()));
				// 内容
				if (item.getType() == NewsPushModel.PushLikeNews) {
					helper.setText(R.id.content_text_view, "为你点了赞");
				} else {
					helper.setText(R.id.content_text_view,
							item.getComment_content());
				}

				String headString = item.getHead_image();
				if (item.getHead_image().equals("null")) {
					headString = "";
				}
				// 头像
				ImageView headImageView = helper.getView(R.id.head_image_view);
				if (null != headString && headString.length() > 0) {
					ImageLoader.getInstance().displayImage(
							KHConst.ATTACHMENT_ADDR + headString,
							headImageView, headImageOptions);
				} else {
					headImageView.setImageResource(R.drawable.loading_default);
				}

				// 有图片显示图片 没图片显示内容
				ImageView newsImageView = helper.getView(R.id.news_image_view);
				TextView newsTextView = helper.getView(R.id.news_text_view);
				if (null != item.getNews_image()
						&& !"".equals(item.getNews_image())) {
					newsImageView.setVisibility(View.VISIBLE);
					newsTextView.setVisibility(View.GONE);
					if (null != item.getNews_image()
							&& item.getNews_image().length() > 0) {
						ImageLoader.getInstance().displayImage(
								KHConst.ATTACHMENT_ADDR
										+ item.getNews_image(), newsImageView,
								headImageOptions);
					} else {
						newsImageView
								.setImageResource(R.drawable.loading_default);
					}
				} else {
					newsImageView.setVisibility(View.GONE);
					newsTextView.setVisibility(View.VISIBLE);
					newsTextView.setText(item.getNews_content());
				}
			}
		};

		notifyListView.setAdapter(newsAdapter);
		// 点击进入详情
		notifyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NewsPushModel newsPushModel = newsAdapter.getItem(position - 1);
				Intent detailIntent = new Intent(getActivity(),
						NewsDetailActivity.class);
				detailIntent.putExtra(NewsConstants.INTENT_KEY_NEWS_ID, ""
						+ newsPushModel.getNews_id());
				startActivityWithRight(detailIntent);
				//发送通知刷新界面
				NewsPushModel.setIsRead();
				sendNotify();
			}
		});
		// 底部检测
		notifyListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 滚到底部了
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
					page++;
					List<NewsPushModel> pushModels = NewsPushModel
							.findWithPage(page, size);
					newsAdapter.addAll(pushModels);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		// 长按删除
		notifyListView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, final int position, long id) {

						List<String> menuList = new ArrayList<String>();
						menuList.add("删除内容");
						final CustomListViewDialog confirmDialog = new CustomListViewDialog(
								getActivity(), menuList);
						confirmDialog.setClickCallBack(new ClickCallBack() {

							@Override
							public void Onclick(View view, int which) {
								//
								NewsPushModel newsPushModel = newsAdapter
										.getItem(position - 1);
								newsPushModel.remove();
								refreshList();
								confirmDialog.dismiss();
							}
						});
						if (null != confirmDialog && !confirmDialog.isShowing()) {
							confirmDialog.show();
						}

						return true;
					}
				});

	}

	// 刷新页面
	private void refreshList() {
		page = 1;
		List<NewsPushModel> pushModels = NewsPushModel.findWithPage(page, size);
		newsAdapter.replaceAll(pushModels);
	}
	
	private BroadcastReceiver messageReceiver;
	// 注册通知
	private void registerNotify() {
		messageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				refreshList();
			}
		};
		// 回复或者点赞或新朋友到达
		IntentFilter newsfilter = new IntentFilter(
				KHConst.BROADCAST_NEW_MESSAGE_PUSH);
		getActivity().registerReceiver(messageReceiver, newsfilter);
	}
	
	//发送通知
	private void sendNotify() {
		//通知刷新
		Intent tabIntent = new Intent(KHConst.BROADCAST_TAB_BADGE);
		getActivity().sendBroadcast(tabIntent);
		//通知页面刷新
		Intent messageIntent = new Intent(KHConst.BROADCAST_NEW_MESSAGE_PUSH);
		getActivity().sendBroadcast(messageIntent);
	}

}

package com.app.khclub.news.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.app.khclub.R;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.view.RoundImageView;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.news.ui.model.LikeModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LikeImageListView extends LinearLayout {

	// 超小分辨率手机
	private final static int TINY_PIX = 320;
	// 小分辨率手机
	private final static int SMALL_PIX = 480;
	// 大
	private final static int LAGER_PIX = 1280;
	// 上行文信息
	private Context mContext;
	// 屏幕的尺寸
	private static int screenWidth = 0;
	// 根布局
	private LinearLayout rootView;
	// 赞的图片
	private List<RoundImageView> likeImageViews = new ArrayList<RoundImageView>();
	// 最多点赞数默认为8
	private int maxLikeCount = 3;
	// 点赞数据集
	private List<LikeModel> likeList;
	// 点击事件回调接口
	private EventCallBack callInterface;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;

	public LikeImageListView(Context context) {
		super(context);
	}

	public LikeImageListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
		getWidget();
		controlSizeInit();
	}

	// 初始化
	private void init() {
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.icon).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 获取控件
	 * */
	private void getWidget() {
		View view = View.inflate(mContext, R.layout.custom_like_list_layout,
				this);
		rootView = (LinearLayout) view
				.findViewById(R.id.layout_like_list_root_view);
		likeImageViews.add((RoundImageView) view
				.findViewById(R.id.iv_like_head_img_A));
		likeImageViews.add((RoundImageView) view
				.findViewById(R.id.iv_like_head_img_B));
		likeImageViews.add((RoundImageView) view
				.findViewById(R.id.iv_like_head_img_C));

		setWidgetListener();
	}

	/**
	 * 事件监听
	 * */
	private void setWidgetListener() {
		for (int index = 0; index < likeImageViews.size(); index++) {
			likeImageViews.get(index).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					for (int index = 0; index < likeImageViews.size(); index++) {
						if (view.getId() == likeImageViews.get(index).getId()) {
							callInterface.onItemClick(KHUtils
									.stringToInt(likeList.get(index)
											.getUserID()));
							break;
						}
					}
				}
			});
		}
	}

	/**
	 * 设置操作回调
	 * 
	 * @param callInterface
	 */
	public void setEventListener(EventCallBack callInterface) {
		this.callInterface = callInterface;
	}

	/**
	 * 计算尺寸
	 * */
	private void controlSizeInit() {
		// 获取屏幕尺寸
		DisplayMetrics displayMet = getResources().getDisplayMetrics();
		screenWidth = displayMet.widthPixels;
		// 计算显示的头像个数
		// if (screenWidth <= TINY_PIX) {
		// maxLikeCount = 6;
		// } else if (screenWidth > TINY_PIX && screenWidth <= SMALL_PIX) {
		// maxLikeCount = 7;
		// } else if (screenWidth > SMALL_PIX && screenWidth <= LAGER_PIX) {
		// maxLikeCount = 8;
		// } else {
		// maxLikeCount = 9;
		// }
	}

	/**
	 * 数据绑定
	 * */
	public void listDataBindSet(List<LikeModel> imageList) {
		likeList = imageList;
		listRefresh();
	}

	/**
	 * 数据绑定
	 * */
	private void listRefresh() {
		if (likeList.size() == 0) {
			rootView.setVisibility(View.GONE);
		} else {
			rootView.setVisibility(View.VISIBLE);
		}

		for (int index = 0; index < likeImageViews.size(); index++) {
			RoundImageView tpImageView = likeImageViews.get(index);
			if (index < likeList.size() && index < maxLikeCount) {
				if (tpImageView.getVisibility() == View.GONE) {
					tpImageView.setVisibility(View.VISIBLE);
				}

				// 显示头像
				if (null != likeList.get(index).getHeadSubImage()
						&& likeList.get(index).getHeadSubImage().length() > 0) {
					imgLoader.displayImage(likeList.get(index)
							.getHeadSubImage(), likeImageViews.get(index),
							options);
				} else {
					likeImageViews.get(index).setImageResource(
							R.drawable.icon);
				}
			} else {
				tpImageView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 插入到首部
	 * */
	public void insertToFirst(LikeModel newData) {
		if (!likeList.contains(newData)) {
			likeList.add(0, newData);
			listRefresh();
		} else {
			LogUtils.e("我的点赞数据已经存在");
		}
	}

	/**
	 * 移除头像
	 * */
	public void removeHeadImg() {
		for (int index = 0; index < likeList.size(); index++) {
			if (KHUtils.stringToInt(likeList.get(index).getUserID()) == UserManager
					.getInstance().getUser().getUid()) {
				likeList.remove(index);
				break;
			} else if (index >= likeList.size()) {
				LogUtils.e("点赞发生了错误，未找到数据.");
			}
		}
		listRefresh();
	}

	/**
	 * 点击事件回调接口
	 * */
	public interface EventCallBack {
		public void onItemClick(int userId);
	}
}
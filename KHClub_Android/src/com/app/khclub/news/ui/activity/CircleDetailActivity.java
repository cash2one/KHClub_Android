package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.news.ui.model.CircleModel;
import com.app.khclub.news.ui.model.CirclePageModel;
import com.app.khclub.news.ui.view.LoopViewPager;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("ResourceAsColor")
public class CircleDetailActivity extends BaseActivityWithTopBar {

	private static final String CIRCLEDETAIL = "circledetail";

	public static String INTENT_CIRCLE_KEY = "circleModel";

	// 可循环滑动的viewpage
	// @ViewInject(R.id.loop_view_page_group)
	// private LoopViewPager groupViewPage;
	// 循环滚动page提示
	// @ViewInject(R.id.page_view_1)
	// private View pageView1;
	// @ViewInject(R.id.page_view_2)
	// private View pageView2;
	// @ViewInject(R.id.page_view_3)
	// private View pageView3;
	// 圈子关注人数
	@ViewInject(R.id.circle_like_tv)
	private TextView circleLikeCount;
	// 圈层介绍
	@ViewInject(R.id.circle_introduce_tv)
	private TextView introTextView;
	// 圈子名称
	@ViewInject(R.id.circle_name_tv)
	private TextView nameTextView;
	// 圈子地址
	@ViewInject(R.id.circle_address_tv)
	private TextView addressTextView;
	// 圈主电话
	@ViewInject(R.id.card_phone_tv)
	private TextView phoneTextView;
	// 微信公众号
	@ViewInject(R.id.circle_wx_num)
	private TextView wxNumTextView;
	// 网页
	@ViewInject(R.id.circle_web_tv)
	private TextView webTextView;
	// 模型
	private CirclePageModel circleModel;
	// 新图片缓存工具 头像
	DisplayImageOptions options;
	// pageViews
	private List<View> pageViews = new ArrayList<View>();
	// 需要显示的图片
	private String[] images;

	@Override
	public int setLayoutId() {
		return R.layout.circle_details;
	}

	@Override
	protected void setUpView() {
//
//		setBarText(getString(R.string.news_circle_detail_title));
//
//		if (getIntent().hasExtra(CIRCLEDETAIL)) {
//			circleModel = (CirclePageModel) getIntent().getSerializableExtra(CIRCLEDETAIL);
//			if (circleModel == null) {
//				return;
//			}
//			// 初始化
//			initUI();
//		} else {
//			// 没有数据 说明数据异常 暂不处理
//		}
	}

	private void initUI() {

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// 圈子关注人数
		circleLikeCount.setText(circleModel.getFollowQuantity());
		// 介绍
		introTextView.setText("    " + circleModel.getCircleDetail());
		// 地址
		addressTextView.setText(circleModel.getAdress());
		// 圈子名称
		nameTextView.setText(circleModel.getCircleName());
		// 电话
		phoneTextView.setText(circleModel.getPhoneNum());
		// 微信
		wxNumTextView.setText(circleModel.getWxNum());
		// 网页
		webTextView.setText(circleModel.getCircleUrl());
		// 初始化pageView

	}
}

package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.manager.UserManager;
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

	private static final int succeed = 1;

	private static final String CIRCLEDETAIL = "circledetail";

	public static String INTENT_CIRCLE_KEY = "circleModel";
	// 圈子封面
	@ViewInject(R.id.circle_cover)
	private ImageView coverImage;
	// 二维码
	@ViewInject(R.id.qrcode_image)
	private ImageView qrcodeImage;
	// 修改按钮
	@ViewInject(R.id.modify_circle_detail)
	private Button modifyCirlce;
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

	private ImageLoader imgLoader;

	@Override
	public int setLayoutId() {
		return R.layout.circle_details;
	}

	@Override
	protected void setUpView() {
		setBarText(getString(R.string.news_circle_detail_title));
		ImageView rightBtn = addRightImgBtn(R.layout.right_image_button, R.id.layout_top_btn_root_view,
				R.id.img_btn_right_top);
		rightBtn.setImageResource(R.drawable.personal_more);
		if (getIntent().hasExtra(CIRCLEDETAIL)) {
			circleModel = (CirclePageModel) getIntent().getSerializableExtra(CIRCLEDETAIL);
			if (circleModel == null) {
				return;
			}
			// 初始化
			initUI();
		} else {
			// 没有数据 说明数据异常 暂不处理
		}
	}

	private void initUI() {
		imgLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		String userID = String.valueOf(UserManager.getInstance().getUser().getUid());
		if (userID.equals(circleModel.getUserID())) {
			modifyCirlce.setVisibility(View.VISIBLE);
			modifyCirlce.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(CircleDetailActivity.this, ModifyCircleActivity.class);
					intent.putExtra(CIRCLEDETAIL, circleModel);
					startActivityForResult(intent, 1);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}
			});
		}
		// 二维码
		if (KHConst.ATTACHMENT_ADDR.equals(circleModel.getWxqrCode())) {
			qrcodeImage.setVisibility(View.GONE);
		}else {
			qrcodeImage.setVisibility(View.VISIBLE);
			imgLoader.displayImage(circleModel.getWxqrCode(), qrcodeImage, options);
		}

		// 圈子封面
		imgLoader.displayImage(circleModel.getCircleCoverImage(), coverImage, options);
		// 圈子关注人数
		circleLikeCount.setText(circleModel.getFollowQuantity());
		// 介绍
		if ("".equals(circleModel.getCircleDetail())) {
			introTextView.setText("    " + getString(R.string.personal_none));
		} else {
			introTextView.setText("    " + circleModel.getCircleDetail());
		}

		// 地址
		if ("".equals(circleModel.getAdress())) {
			addressTextView.setText("    " + getString(R.string.personal_none));
		} else {
			addressTextView.setText("    " + circleModel.getAdress());
		}
		// 圈子名称
		if ("".equals(circleModel.getCircleName())) {
			nameTextView.setText("    " + getString(R.string.personal_none));
		} else {
			nameTextView.setText("    " + circleModel.getCircleName());
		}
		// 电话
		if ("".equals(circleModel.getPhoneNum())) {
			phoneTextView.setText("    " + getString(R.string.personal_none));
		} else {
			phoneTextView.setText("    " + circleModel.getPhoneNum());
		}
		// 微信
		if ("".equals(circleModel.getWxNum())) {
			wxNumTextView.setText("    " + getString(R.string.personal_none));
		} else {
			wxNumTextView.setText("    " + circleModel.getWxNum());
		}
		// 网页
		if ("".equals(circleModel.getCircleUrl())) {
			webTextView.setText("    " + getString(R.string.personal_none));
		} else {
			webTextView.setText("    " + circleModel.getCircleUrl());
		}
		// 初始化pageView

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			circleModel = (CirclePageModel) data.getSerializableExtra(CIRCLEDETAIL);
			initUI();
		}
	}
}

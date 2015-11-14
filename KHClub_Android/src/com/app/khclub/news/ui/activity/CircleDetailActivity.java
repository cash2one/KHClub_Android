package com.app.khclub.news.ui.activity;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.news.ui.model.CircleModel;
import com.app.khclub.news.ui.view.LoopViewPager;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CircleDetailActivity extends BaseActivityWithTopBar {

	public static String INTENT_CIRCLE_KEY = "circleModel";
	
	// 可循环滑动的viewpage
	@ViewInject(R.id.loop_view_page_group)
	private LoopViewPager groupViewPage;	
	//标题
	@ViewInject(R.id.title_text_view)
	private TextView titleTextView;
	//介绍
	@ViewInject(R.id.intro_text_view)
	private TextView introTextView;
	//地址
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;
	//名字
	@ViewInject(R.id.manager_name_text_view)
	private TextView managerNameTextView;
	//电话
	@ViewInject(R.id.phone_text_view)
	private TextView phoneTextView;	
	//介绍
	@ViewInject(R.id.wx_text_view)
	private TextView wxTextView;
	//模型
	private CircleModel circleModel;
	//新图片缓存工具 头像
	DisplayImageOptions options;
	
	@Override
	public int setLayoutId() {
		return R.layout.activity_circle_detail;
	}

	@Override
	protected void setUpView() {
		if (getIntent().hasExtra(INTENT_CIRCLE_KEY)) {
			circleModel = (CircleModel) getIntent().getSerializableExtra(INTENT_CIRCLE_KEY);
		}else {
			//没有数据 说明数据异常 暂不处理
		}
		//初始化
		initUI();
	}
	
	private void initUI(){
		
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.loading_default)  
        .showImageOnFail(R.drawable.loading_default)  
        .cacheInMemory(true)  
        .cacheOnDisk(true)  
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .build();
		
		//标题
		titleTextView.setText(circleModel.getTitle());
		//介绍
		introTextView.setText(circleModel.getIntro());
		//地址
		addressTextView.setText(circleModel.getAddress());
		//名
		managerNameTextView.setText(circleModel.getManager_name());
		//电话
		phoneTextView.setText(circleModel.getPhone_num());
		//介绍
		wxTextView.setText(circleModel.getWx_num());
		groupViewPage.setAdapter(new MyPagerAdapter());
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View groupPageView = View.inflate(CircleDetailActivity.this,
					R.layout.circle_page_layout, null);
			// 数据模型
			String[] images = circleModel.getImage().split(",");
			// 背景图
			ImageView topicImageView = (ImageView) groupPageView.findViewById(R.id.imageView);
			if (images.length > 0) {
				ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR+ images[position],topicImageView, options);	
			}else {
				//没有
			}
			return groupPageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			// 返回page的数量
			String[] images = circleModel.getImage().split(",");
			if (images.length > 0) {
				return images.length;
			}else {
				return 1;
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}

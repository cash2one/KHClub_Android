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
import com.app.khclub.news.ui.view.LoopViewPager;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("ResourceAsColor") public class CircleDetailActivity extends BaseActivityWithTopBar {

	public static String INTENT_CIRCLE_KEY = "circleModel";
	
	// 可循环滑动的viewpage
	@ViewInject(R.id.loop_view_page_group)
	private LoopViewPager groupViewPage;	
	//循环滚动page提示
	@ViewInject(R.id.page_view_1)
	private View pageView1;
	@ViewInject(R.id.page_view_2)
	private View pageView2;
	@ViewInject(R.id.page_view_3)
	private View pageView3;	
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
	//网页
	@ViewInject(R.id.web_text_view)
	private TextView webTextView;	
	//模型
	private CircleModel circleModel;
	//新图片缓存工具 头像
	DisplayImageOptions options;
	//pageViews
	private List<View> pageViews = new ArrayList<View>();
	//需要显示的图片
	private String[] images;
	
	@Override
	public int setLayoutId() {
		return R.layout.activity_circle_detail;
	}

	@Override
	protected void setUpView() {
		
		setBarText(getString(R.string.news_circle_detail_title));
		
		pageViews.add(pageView1);
		pageViews.add(pageView2);
		pageViews.add(pageView3);
		
		if (getIntent().hasExtra(INTENT_CIRCLE_KEY)) {
			circleModel = (CircleModel) getIntent().getSerializableExtra(INTENT_CIRCLE_KEY);
			if (circleModel == null) {
				return;
			}
			//初始化
			initUI();			
		}else {
			//没有数据 说明数据异常 暂不处理
		}
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
		introTextView.setText("    "+circleModel.getIntro());
		//地址
		addressTextView.setText(circleModel.getAddress());
		//名
		managerNameTextView.setText(circleModel.getManager_name());
		//电话
		phoneTextView.setText(circleModel.getPhone_num());
		//微信
		wxTextView.setText(circleModel.getWx_num());
		//网页
		webTextView.setText(circleModel.getWeb());
		//初始化pageView
		images = circleModel.getImage().split(",");
		if (images.length > 0) {
			int size = images.length;
			if (size > 3) {
				size = 3;
			}
			switch (size) {
			case 1:
				pageView2.setVisibility(View.GONE);
				pageView3.setVisibility(View.GONE);
				break;
			case 2:
				pageView3.setVisibility(View.GONE);
				break;				
			default:
				break;
			}
		}
		groupViewPage.setAdapter(new MyPagerAdapter());
		groupViewPage.setOnPageChangeListener(opcl);
		
		final CountDownTimer verifyCountdownTimer = new CountDownTimer(6000000, 2000) {
			@Override
			public void onTick(long millisUntilFinished) {
				int position = groupViewPage.getCurrentItem()+1;
				if (position > 3) {
					position = 0;
				}
				groupViewPage.setCurrentItem(position, true);
				
				//page提示部分变化
				for (int i = 0; i < images.length; i++) {
					if (i <= 3) {
						for(View view : pageViews) {
							view.setBackgroundResource(R.color.main_clear_white);
						}
					}
				}
				if (position == 3) {
					position = 0;
				}
				pageViews.get(position).setBackgroundResource(R.color.main_white);						
			}
			@Override
			public void onFinish() {
			}
		};			
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// 开始倒计时
				verifyCountdownTimer.start();				
			}
		}, 2000);
		
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View groupPageView = View.inflate(CircleDetailActivity.this,
					R.layout.circle_page_layout, null);
			// 背景图
			ImageView topicImageView = (ImageView) groupPageView.findViewById(R.id.imageView);
			if (images.length > 0) {
				ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR+ images[position],topicImageView, options);	
			}else {
				//没有
			}
			container.addView(groupPageView);
			
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
				if (images.length > 3) {
					return 3;
				}
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
	
	//page修改监听器
	OnPageChangeListener opcl = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			//page提示部分变化
			for (int i = 0; i < images.length; i++) {
				if (i <= 3) {
					for(View view : pageViews) {
						view.setBackgroundResource(R.color.main_clear_white);
					}
				}
			}
			if (position < 3) {
				pageViews.get(position).setBackgroundResource(R.color.main_white);						
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
}

package com.app.khclub.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.model.NewsPushModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.news.ui.activity.CreateCircleActivity;
import com.app.khclub.news.ui.activity.NoticeActivity;
import com.app.khclub.news.ui.activity.PublishNewsActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainPageFragment extends BaseFragment {

	// 上下文信息
	private Context mContext;
	// 主页viewpager
	@ViewInject(R.id.viewpager_main)
	private ViewPager mainPager;
	// title的项目
	// 商务圈bar
	@ViewInject(R.id.layout_title_content)
	private LinearLayout titleContent;
	// 发布按钮
	@ViewInject(R.id.img_news_publish_btn)
	private ImageView publishBtn;
	// 通知按钮
	@ViewInject(R.id.img_notice_btn)
	private ImageView noticeBtn;
	// 未读提示（小红点）
	@ViewInject(R.id.news_unread_image_view)
	private ImageView unreadImageView;
	// 主页viewpager
	@ViewInject(R.id.tv_news_guid)
	private TextView newsTitleTextView;
	// 校园
	@ViewInject(R.id.tv_campus_guid)
	private TextView campusTitleTextView;
	@ViewInject(R.id.tv_circle_type)
	private TextView typeTitleTextView;
	@ViewInject(R.id.tab1)
	private View tabView1;
	@ViewInject(R.id.tab2)
	private View tabView2;
	@ViewInject(R.id.tab3)
	private View tabView3;
	// 所有的
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	// 偏移图片
	@ViewInject(R.id.img_cursor)
	private ImageView imageCursor;
	// 当前页卡编号
	private int currIndex;
	//当前页卡
	private int currpage;
	// 横线图片宽度
	private int cursorWidth;
	// 图片移动的偏移量
	private int offset;

	@Override
	public int setLayoutId() {
		return R.layout.fragment_main_page;
	}

	@Override
	public void loadLayout(View rootView) {
		
	}

	@Override
	public void setUpViews(View rootView) {
		mContext = this.getActivity().getApplicationContext();
		InitImage();
		InitViewPager();
		// 点击发布按钮
		publishBtn.setOnClickListener(new OnClickListener() {
         
			@Override
			public void onClick(View arg0) {
				if (currpage == 0||currpage == 1) {
					Intent intentUsrMain = new Intent(mContext, CreateCircleActivity.class);
					startActivityWithRight(intentUsrMain);
				}
				if (currpage == 2) {
					Intent intentUsrMain = new Intent(mContext, PublishNewsActivity.class);
					startActivityWithRight(intentUsrMain);
				}
			}
		});
		// 点击通知按钮
		noticeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转至通知页面
				Intent intentCampusInfo = new Intent(mContext, NoticeActivity.class);
				startActivityWithRight(intentCampusInfo);
			}
		});
		registerNotify();
		refreshPush();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (newMessageReceiver != null) {
			getActivity().unregisterReceiver(newMessageReceiver);
		}
	}

	/*
	 * 初始化图片的位移像素
	 */
	public void InitImage() {
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int contentLeftMargin = ((FrameLayout.LayoutParams) titleContent.getLayoutParams()).leftMargin;
		int contentWidth = screenWidth - (2 * contentLeftMargin);
		// 设置游标的尺寸与位置
		LayoutParams cursorParams = (LayoutParams) imageCursor.getLayoutParams();
		cursorWidth = cursorParams.width;
		offset = contentWidth / 2;
		cursorParams.leftMargin = (contentWidth / 2 - cursorWidth) / 2 + contentLeftMargin;
		imageCursor.setLayoutParams(cursorParams);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {
		newsTitleTextView.setOnClickListener(new ViewClickListener(0));
		typeTitleTextView.setOnClickListener(new ViewClickListener(1));
		campusTitleTextView.setOnClickListener(new ViewClickListener(2));
		mFragmentList.add(new CircleFragment());
		mFragmentList.add(new CircleTypeFragment());
		mFragmentList.add(new NewsListFragment());
		mainPager.setAdapter(new MainFragmentPagerAdapter(getChildFragmentManager(), mFragmentList));
		mainPager.setCurrentItem(0);
		mainPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 头标点击监听
	 */
	private class ViewClickListener implements OnClickListener {
		private int index = 0;

		public ViewClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mainPager.setCurrentItem(index);
		}
	}

	/**
	 * ViewPager的适配器
	 */
	class MainFragmentPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragmentList;

		public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			fragmentList = list;
		}

		// 得到每个item
		@Override
		public Fragment getItem(int index) {
			return fragmentList.get(index);
			//return fragmentList.get(index%3);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
			//return 10000;
		}

		@Override
		public int getItemPosition(Object object) {
			//
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int position) {
			// 初始化每个页卡选项
			return super.instantiateItem(arg0, position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//
			super.destroyItem(container, position, object);
		}
	}

	/**
	 * 监听选项卡改变事件
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		float lastpostion = 0;

		public void onPageScrollStateChanged(int index) {
			
			currIndex = index;
			
			
		}
		
		// CurrentTab:当前页面序号
		// OffsetPercent:当前页面偏移的百分比
		// offsetPixel:当前页面偏移的像素位置
		public void onPageScrolled(int CurrentTab, float OffsetPercent, int offsetPixel) {
			// 下标的移动动画
			Animation animation = new TranslateAnimation(offset * lastpostion, offset * (CurrentTab + OffsetPercent), 0,
					0);
		
			lastpostion = OffsetPercent + CurrentTab;
			// True:图片停在动画结束位置
			animation.setFillAfter(true);
			animation.setDuration(300);
			imageCursor.startAnimation(animation);
		}

		/**
		 * 状态改变后
		 */
		public void onPageSelected(int index) {
			currpage=index;
			if (0 == currpage) {
				publishBtn.setImageResource(R.drawable.create_cirlce_bnt);
				typeTitleTextView.setTextColor(getResources().getColor(R.color.main_deep_black));
				newsTitleTextView.setTextColor(getResources().getColor(R.color.main_gold));
				campusTitleTextView.setTextColor(getResources().getColor(R.color.main_deep_black));
				tabView1.setBackgroundResource(R.color.main_gold);
				tabView2.setBackgroundResource(R.color.main_light_gary);
				tabView3.setBackgroundResource(R.color.main_light_gary);
				//newsTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
				//campusTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			} else if(2 == currpage){
				publishBtn.setImageResource(R.drawable.news_publish_btn_selector);
				typeTitleTextView.setTextColor(getResources().getColor(R.color.main_deep_black));
				campusTitleTextView.setTextColor(getResources().getColor(R.color.main_gold));
				newsTitleTextView.setTextColor(getResources().getColor(R.color.main_deep_black));
				tabView1.setBackgroundResource(R.color.main_light_gary);
				tabView2.setBackgroundResource(R.color.main_light_gary);
				tabView3.setBackgroundResource(R.color.main_gold);
//				newsTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//				campusTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			}else {
				publishBtn.setImageResource(R.drawable.create_cirlce_bnt);
				typeTitleTextView.setTextColor(getResources().getColor(R.color.main_gold));
				newsTitleTextView.setTextColor(getResources().getColor(R.color.main_deep_black));
				campusTitleTextView.setTextColor(getResources().getColor(R.color.main_deep_black));
				tabView1.setBackgroundResource(R.color.main_light_gary);
				tabView2.setBackgroundResource(R.color.main_gold);
				tabView3.setBackgroundResource(R.color.main_light_gary);
			}
		}
	}

	private BroadcastReceiver newMessageReceiver;

	// 注册通知
	private void registerNotify() {
		// 刷新push
		newMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra("jumpcategory")){
					//从圈子类表跳转，直接写死
					mainPager.setCurrentItem(1);
				}else {
					// 刷新push
					refreshPush();
				}
				
			}
		};
		IntentFilter intentFilter = new IntentFilter(KHConst.BROADCAST_NEW_MESSAGE_PUSH);
		getActivity().registerReceiver(newMessageReceiver, intentFilter);
	}

	private void refreshPush() {
		int newsUnreadCount = NewsPushModel.findUnreadCount().size();
		if (newsUnreadCount < 1) {
			unreadImageView.setVisibility(View.GONE);
		} else {
			unreadImageView.setVisibility(View.VISIBLE);
		}
	}

}

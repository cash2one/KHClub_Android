package com.app.khclub.login.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.app.khclub.R;
import com.app.khclub.base.manager.ActivityManager;
import com.app.khclub.base.ui.activity.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LaunchActivity extends BaseActivity {

	@ViewInject(R.id.vPager)
	private ViewPager mPager;//页卡内容
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_launch;
	}

	@Override
	protected void setUpView() {

		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		//这个名字起的有问题 因为产品已经上线了 等下一次更换启动图的时候修改
//		boolean launchConfig = ConfigUtils.getBooleanConfig("launchTest");
//		
//		if (!launchConfig) {
//			
//			ConfigUtils.saveConfig("launchTest", true);
//			//初始化
//			initViewPager();			
//			mPager.setVisibility(View.VISIBLE);
//			
//		}else {
			//如果后台有程序
			if (ActivityManager.getActivityStack().size()>1) {

				finish();
				return;
			}
			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
//					UserModel userModel = UserManager.getInstance().getUser();
//					if (null != userModel.getUsername() && null != userModel.getLogin_token()) {
//						startActivity(new Intent(LaunchActivity.this, MainTabActivity.class));
//					} else {
						startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
//					}
					finish();
				}
			}, 2000);
//		}
		
	}

	@Override
	protected void loadLayout(View v) {
		// TODO Auto-generated method stub
		
	}
	
	
//	/**
//     * 初始化ViewPager
//     */
//    @SuppressLint("InflateParams") 
//    private void initViewPager() {
//    	
//        mPager.setAdapter(new MessageFragmentPagerAdapter(getSupportFragmentManager()));
//        mPager.setCurrentItem(0);
//    }
//    
//    private class MessageFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
//
//        public MessageFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            Fragment fragment = null;
//            switch (i) {
//                case 0:
//                	fragment = new LaunchCircleFragment1();
//                    break;
//                case 1:
//                	fragment = new LaunchCircleFragment2();
//                    break;
//                case 2:
//                	fragment = new LaunchCircleFragment3();
//                    break;                    
//
//            }
//            return fragment;
//        }
//
//        @Override
//        public int getCount() {
//            return 3;
//        }
//    }

}

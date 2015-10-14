package com.app.khclub.login.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.khclub.R;
import com.app.khclub.base.easeim.Constant;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.db.UserDao;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.manager.ActivityManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivity;
import com.app.khclub.base.ui.activity.MainTabActivity;
import com.app.khclub.base.utils.KHConst;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LaunchActivity extends BaseActivity {

	@ViewInject(R.id.vPager)
	private ViewPager mPager;//页卡内容
	private static final int sleepTime = 2000;
	
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
			//如果本地有数据 并且环信登录存在 自动登录
			new Thread(new Runnable() {
				public void run() {
					if (KHHXSDKHelper.getInstance().isLogined()) {
						// ** 免登陆情况 加载所有本地群和会话
						//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
						//加上的话保证进了主页面会话和群组都已经load完毕
						long start = System.currentTimeMillis();
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						long costTime = System.currentTimeMillis() - start;
						//等待sleeptime时长
						if (sleepTime - costTime > 0) {
							try {
								Thread.sleep(sleepTime - costTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						//自动登录
						UserModel userModel = UserManager.getInstance().getUser();
						if (null != userModel.getUsername() && null != userModel.getLogin_token()) {
							startActivity(new Intent(LaunchActivity.this, MainTabActivity.class));
						} else {
							startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
						}
						finish();
					}else {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
						startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
						finish();
					}
				}
			}).start();
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

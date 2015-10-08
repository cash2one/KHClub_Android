package com.app.khclub.base.ui.activity;

import io.yunba.android.manager.YunBaManager;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.NewsPushModel;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.contact.ui.fragment.ContactFragment;
import com.app.khclub.message.ui.fragment.MessageFragment;
import com.app.khclub.news.ui.fragment.NewsListFragment;
import com.app.khclub.personal.ui.fragment.PersonalFragment;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainTabActivity extends BaseActivity {

	private final static int SCANNIN_GREQUEST_CODE = 1;

	// FragmentTabHost对象
	@ViewInject(android.R.id.tabhost)
	public FragmentTabHost mTabHost;

	private LayoutInflater layoutInflater;

	private Class<?> fragmentArray[] = { NewsListFragment.class,
			MessageFragment.class, ContactFragment.class,
			PersonalFragment.class };

	private int mImageViewArray[] = { R.drawable.tab_home_btn,R.drawable.tab_message_btn,R.drawable.tab_contact_btn,
			R.drawable.tab_me_btn };

	private String mTextviewArray[] = { "主页", "消息", "通讯录", "我" };

	// //已经连接
	// private boolean isConnect = false;

	// im未读数量
	public void initTab() {

		layoutInflater = LayoutInflater.from(this);

		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.getTabWidget().setDividerDrawable(null);
		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// mTabHost.getTabWidget().getChildAt(i)
			// .setBackgroundResource(R.drawable.selector_tab_background);
//			final int index = i;
//			if (index == 0) {
//				// 选择首页刷新和其他的不太一样
//				mTabHost.getTabWidget().getChildAt(i)
//						.setOnClickListener(new OnClickListener() {
//							@Override
//							public void onClick(View arg0) {
//
//								if (mTabHost.getCurrentTab() == 0) {
//									
//								}
//								mTabHost.setCurrentTab(index);
//							}
//						});
//			} 

		}

		// 注册通知
		registerNotify();
	}

	// 初始化云巴
	private void initYunBa() {
		// registerMessageRecevier();
		final UserModel userModel = UserManager.getInstance().getUser();
		if (userModel.getUid() != 0) {
			YunBaManager.subscribe(this, new String[] { KHConst.KH
					+ userModel.getUid() }, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken arg0) {
					LogUtils.i("yunba init ok", 1);
					// LogUtils.i("yunba success"+JLXCConst.JLXC+userModel.getUid(),
					// 1);
					// Looper.prepare();
					// Toast.makeText(MainTabActivity.this, "yunba success",
					// Toast.LENGTH_SHORT).show();
					// Looper.loop();
				}

				@Override
				public void onFailure(IMqttToken arg0, Throwable arg1) {
					Looper.prepare();
					// Toast.makeText(MainTabActivity.this, "yunba fail",
					// Toast.LENGTH_SHORT).show();
					Looper.loop();
				}
			});
		}
	}

	// 获取最新版本号
//	private void getLastVersion() {
//		new NewVersionCheckManager(this, this).checkNewVersion(false, null);
//	}

	@SuppressLint("InflateParams")
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void loadLayout(View v) {

	}

	@Override
	protected void setUpView() {

		// 初始化tab
		initTab();
		// 初始化云巴
		initYunBa();
		// 获取最新版本
//		getLastVersion();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * 重写返回操作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			moveTaskToBack(true);

			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {

		if (null != newMessageReceiver) {
			unregisterReceiver(newMessageReceiver);
			newMessageReceiver = null;
		}

		super.onDestroy();
	}

	// 友盟集成
	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			
			break;
		}
	}

	// //////////////////////////////private
	// method////////////////////////////////
	private BroadcastReceiver newMessageReceiver;

	// 注册通知
	private void registerNotify() {

		// 刷新tab
		newMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 刷新tab
				refreshTab();
			}
		};
		IntentFilter intentFilter = new IntentFilter(
				KHConst.BROADCAST_TAB_BADGE);
		registerReceiver(newMessageReceiver, intentFilter);
	}

	// 刷新tab 未读标志
	private void refreshTab() {
		View newsView = mTabHost.getTabWidget().getChildAt(0);
		TextView newsUnreadTextView = (TextView) newsView
				.findViewById(R.id.unread_text_view);
		// 聊天页面
		// 新好友请求未读
//		int newFriendsCount = 0;
		// 徽标 最多显示99
		// 未读推送
		int newsUnreadCount = 0;
		try {
//			newFriendsCount = IMModel.unReadNewFriendsCount();
			newsUnreadCount = NewsPushModel.findUnreadCount().size();
		} catch (Exception e) {
		}
		//首页未读
		if (newsUnreadCount == 0) {
			newsUnreadTextView.setVisibility(View.GONE);
		} else {
			newsUnreadTextView.setVisibility(View.VISIBLE);
		}
	}

	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// // TODO Auto-generated method stub
	// outState.putBoolean("isConnect", isConnect);
	// LogUtils.i("on save" + " "+ isConnect, 1);
	//
	// super.onSaveInstanceState(outState);
	// }

	// @Override
	// public void onConfigurationChanged(android.content.res.Configuration
	// newConfig) {
	// super.onConfigurationChanged(newConfig);
	//
	// };
	//
	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	//
	// isConnect = savedInstanceState.getBoolean("isConnect");
	// LogUtils.i("on restroe" + " "+ isConnect, 1);
	// isConnect = true;
	//
	// super.onRestoreInstanceState(savedInstanceState);
	// }

	// 杀死进程
	public static void killThisPackageIfRunning(final Context context,
			String packageName) {
		android.app.ActivityManager activityManager = (android.app.ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(packageName);
	}

}

package com.app.khclub.personal.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.app.khclub.R;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.KHHXSDKModel;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.manager.NewVersionCheckManager;
import com.app.khclub.base.manager.NewVersionCheckManager.VersionCallBack;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.view.CustomAlertDialog;
import com.app.khclub.base.utils.DataCleanManager;
import com.app.khclub.base.utils.HttpCacheUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.login.ui.activity.LoginActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AppSettingActivity extends BaseActivityWithTopBar {

	/**
	 * 打开声音提示imageview
	 */
	@ViewInject(R.id.iv_switch_open_sound)
	private ImageView iv_switch_open_sound;
	/**
	 * 关闭声音提示imageview
	 */
	@ViewInject(R.id.iv_switch_close_sound)
	private ImageView iv_switch_close_sound;
	/**
	 * 打开消息震动提示
	 */
	@ViewInject(R.id.iv_switch_open_vibrate)
	private ImageView iv_switch_open_vibrate;
	/**
	 * 关闭消息震动提示
	 */
	@ViewInject(R.id.iv_switch_close_vibrate)
	private ImageView iv_switch_close_vibrate;
	
	private EMChatOptions chatOptions;
	private KHHXSDKModel model;
	
	@OnClick(value = {R.id.version_text_view,R.id.logout_button,R.id.rl_switch_sound,R.id.rl_switch_vibrate,
			 R.id.clear_cache_text_view})
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.clear_cache_text_view:
			//清除缓存
			clearCache();
			break;
		case R.id.logout_button:
			//退出
			logout();
			break;
		case R.id.version_text_view:
			//版本检测
			checkVersion();
			break;
		case R.id.rl_switch_sound:
			//声音
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			//震动
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
			}
			break;			
		default:
			break;
		}
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_app_setting;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		setBarText(getString(R.string.personal_setting));
		chatOptions = EMChatManager.getInstance().getChatOptions();
		model = (KHHXSDKModel) HXSDKHelper.getInstance().getModel();
		// 是否打开声音
		// sound notification is switched on or not?
		if (model.getSettingMsgSound()) {
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		
		// 是否打开震动
		// vibrate notification is switched on or not?
		if (model.getSettingMsgVibrate()) {
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}
	}

	
	//清除缓存
	private void clearCache(){
		try {
			DataCleanManager.clearAllCache(this);
			//清除缓存
			HttpCacheUtils.clearHttpCache();
			ToastUtil.show(this, R.string.personal_clear_ok);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//退出
	private void logout(){
		final CustomAlertDialog confirmDialog = new CustomAlertDialog(
				this, getString(R.string.personal_confirm_logout), getString(R.string.alert_confirm), getString(R.string.alert_cancel));
		confirmDialog.show();
		confirmDialog.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						
						final ProgressDialog pd = new ProgressDialog(AppSettingActivity.this);
						String st = getResources().getString(R.string.Are_logged_out);
						pd.setMessage(st);
						pd.setCanceledOnTouchOutside(false);
						pd.show();
						KHHXSDKHelper.getInstance().logout(true,new EMCallBack() {
							
							@Override
							public void onSuccess() {
								runOnUiThread(new Runnable() {
									public void run() {
										pd.dismiss();
										// 重新显示登陆页面
						                //清空数据
						                UserManager.getInstance().clear();
						                UserManager.getInstance().setUser(new UserModel());
						                Intent exit = new Intent(AppSettingActivity.this, LoginActivity.class);
						                exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
										startActivity(exit);
//											ActivityManager.getInstence().exitApplication();
										
									}
								});
							}
							
							@Override
							public void onProgress(int progress, String status) {
								
							}
							
							@Override
							public void onError(int code, String message) {
								runOnUiThread(new Runnable() {
									
									public void run() {
										// TODO Auto-generated method stub
										pd.dismiss();
									}
								});
							}
						});
						
						
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});	  
	}
	
	//检测版本
	private void checkVersion() {
		
		showLoading(getString(R.string.downloading), true);
		new NewVersionCheckManager(this, this).checkNewVersion(true, new VersionCallBack() {
			@Override
			public void finish() {
				hideLoading();
			}
		});
	} 
}

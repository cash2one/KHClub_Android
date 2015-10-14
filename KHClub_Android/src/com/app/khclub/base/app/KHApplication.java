package com.app.khclub.base.app;

import io.yunba.android.manager.YunBaManager;
import android.app.Application;

import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.manager.DBManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.utils.FileUtil;
import com.easemob.EMCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 *Application
 * 
 * @author Direct.Hao
 * 
 */
public class KHApplication extends Application {
	// application
	public static KHApplication application;
	public static boolean isDebug;
	
	public static KHHXSDKHelper hxSDKHelper = new KHHXSDKHelper();

	public static KHApplication getInstance() {
        return application;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = (KHApplication) getApplicationContext();
		//数据库初始化
		DBManager.getInstance(); 
		//FileUtils初始化
		FileUtil.makeDirs();
		//云巴初始化
		YunBaManager.start(getApplicationContext());
        //初始化用户模型
        UserManager.getInstance().getUser();
//        //友盟测试模式
//        MobclickAgent.setDebugMode(true);
        //初始化图片加载对象
        ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(getApplicationContext()));
        hxSDKHelper.onInit(application);
	}  

	
	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM,final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(isGCM,emCallBack);
	}
}

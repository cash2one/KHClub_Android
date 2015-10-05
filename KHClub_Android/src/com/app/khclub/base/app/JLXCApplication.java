package com.app.khclub.base.app;

import io.yunba.android.manager.YunBaManager;
import android.app.Application;

import com.app.khclub.base.manager.DBManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.utils.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;

/**
 *Application
 * 
 * @author Direct.Hao
 * 
 */
public class JLXCApplication extends Application {
	// application
	public static JLXCApplication application;
	public static boolean isDebug;

	public static JLXCApplication getInstance() {
        return application;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = (JLXCApplication) getApplicationContext();
//		//数据库初始化
//		DBManager.getInstance(); 
//		//FileUtils初始化
//		FileUtil.makeDirs();
//		//云巴初始化
//		YunBaManager.start(getApplicationContext());
//        //初始化用户模型
//        UserManager.getInstance().getUser();
//        //友盟测试模式
//        MobclickAgent.setDebugMode(true);
        //初始化图片加载对象
        ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(getApplicationContext()));
	}  

}

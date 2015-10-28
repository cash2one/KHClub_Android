package com.app.khclub.base.utils;

import com.app.khclub.base.app.KHApplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//配置存储 同时用于存储IM非好友的姓名和头像
public class ConfigUtils {
		
	//名片配置key
	public static String CARD_CONFIG = "cardConfig";
	public static int CARD_ONE = 1;
	public static int CARD_TWO = 2;
	
	
	//存储配置
	public static void saveConfig(String key,String value){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			Editor editor = httpPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}
	//存储配置
	public static void saveConfig(String key,int value){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			Editor editor = httpPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}
	
	//存储配置
	public static void saveConfig(String key,boolean value){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			Editor editor = httpPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}	
	
	//获取配置
	public static String getStringConfig(String key){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			return httpPreferences.getString(key, "");
		} catch (Exception e) {
			return null;
		}
	}
	//获取配置
	public static int getIntConfig(String key){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			return httpPreferences.getInt(key, 0);
		} catch (Exception e) {
			return 0;
		}
	}
	//获取配置
	public static boolean getBooleanConfig(String key){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			return httpPreferences.getBoolean(key, false);
		} catch (Exception e) {
			return false;
		}
	}	
	
	
	//清空缓存
	public static void clearConfig(){
		try {
			SharedPreferences httpPreferences = KHApplication.getInstance().getSharedPreferences("config", Activity.MODE_PRIVATE);
			httpPreferences.edit().clear();
		} catch (Exception e) {
		}
	}
	
}

package com.shs.contact.helper;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;

/**
 * 
 * @author Michael.liu
 * 
 */
public class LoadDataHandler<T> {

	public void onStart(T flag){
		
	}

	public void onFailure(HttpException e, String arg1,T flag){
		//Log.i("wx", e.toString() + arg1 + ":" + flag);
	}

	public void onSuccess(JSONObject jsonResponse,T flag){
		
	}

	public void onCancelled(T flag) {
	}

	public void onLoading(long total, long current, boolean isUploading,T flag) {
	}
}

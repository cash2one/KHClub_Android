package com.app.khclub.base.easeim;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper.HXSyncListener;
import com.app.khclub.base.easeim.applib.utils.HXPreferenceUtils;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.message.model.IMModel;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.util.HanziToPinyin;
import com.lidroid.xutils.exception.HttpException;

public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */
	private List<HXSyncListener> syncContactInfosListeners;

	private boolean isSyncingContactInfosWithServer = false;

	private User currentUser;

	public UserProfileManager() {
	}

	public synchronized boolean onInit(Context context) {
		if (sdkInited) {
			return true;
		}
		syncContactInfosListeners = new ArrayList<HXSyncListener>();
		sdkInited = true;
		return true;
	}

	public void addSyncContactInfoListener(HXSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.add(listener);
		}
	}

	public void removeSyncContactInfoListener(HXSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.remove(listener);
		}
	}

	public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<User>> callback) {
		if (isSyncingContactInfosWithServer) {
			return;
		}
		isSyncingContactInfosWithServer = true;
		// 同步
		String path = KHConst.GET_ALL_FRIENDS_LIST + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
							JSONArray jsonArray = jResult.getJSONArray(KHConst.HTTP_LIST);
							List<User> users = new ArrayList<User>();
							// 建立模型数组
							for (int i = 0; i < jsonArray.size(); i++) {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);
								User user = new User();
								user.setUsername(KHConst.KH + jsonObject.getIntValue("user_id"));
								user.setAvatar(KHConst.ATTACHMENT_ADDR + jsonObject.getString("head_sub_image"));
								user.setNick(jsonObject.getString("name"));
								if (jsonObject.getString("friend_remark").length() > 0) {
									user.setNick(jsonObject.getString("friend_remark"));
								}
								
								if (user.getNick().length() < 1) {
									user.setHeader("K");
								}else if (Character.isDigit(user.getNick().charAt(0))) {
						            user.setHeader("#");
						        } else {
						            user.setHeader(HanziToPinyin.getInstance().get(user.getNick().substring(0, 1)).get(0).target.substring(0, 1)
						                    .toUpperCase());
						            char header = user.getHeader().toLowerCase().charAt(0);
						            if (header < 'a' || header > 'z') {
						                user.setHeader("#");
						            }
						        }
								
								users.add(user);
							}
							
							isSyncingContactInfosWithServer = false;
							// in case that logout already before server returns,we should
							// return immediately
							if (!EMChat.getInstance().isLoggedIn()) {
								return;
							}
							if (callback != null) {
								callback.onSuccess(users);
							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						isSyncingContactInfosWithServer = false;
						if (callback != null) {
							callback.onError(0, "");
						}
					}

				}, null));		
		
//		ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<User>>() {
//
//			@Override
//			public void onSuccess(List<User> value) {
//				isSyncingContactInfosWithServer = false;
//				// in case that logout already before server returns,we should
//				// return immediately
//				if (!EMChat.getInstance().isLoggedIn()) {
//					return;
//				}
//				if (callback != null) {
//					callback.onSuccess(value);
//				}
//			}
//
//			@Override
//			public void onError(int error, String errorMsg) {
//				isSyncingContactInfosWithServer = false;
//				if (callback != null) {
//					callback.onError(error, errorMsg);
//				}
//			}
//
//		});

	}

	public void notifyContactInfosSyncListener(boolean success) {
		for (HXSyncListener listener : syncContactInfosListeners) {
			listener.onSyncSucess(success);
		}
	}

	public boolean isSyncingContactInfoWithServer() {
		return isSyncingContactInfosWithServer;
	}

	synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentUser = null;
		HXPreferenceUtils.getInstance().removeCurrentUserInfo();
	}

	public synchronized User getCurrentUserInfo() {
		if (currentUser == null) {
			String username = EMChatManager.getInstance().getCurrentUser();
			currentUser = new User(username);
			String nick = getCurrentUserNick();
			currentUser.setNick((nick != null) ? nick : username);
			currentUser.setAvatar(getCurrentUserAvatar());
		}
		return currentUser;
	}

	public boolean updateParseNickName(final String nickname) {
//		boolean isSuccess = ParseManager.getInstance().updateParseNickName(nickname);
//		if (isSuccess) {
		setCurrentUserNick(UserManager.getInstance().getUser().getName());
//		}
//		return isSuccess;
		return true;
	}

	public String uploadUserAvatar(byte[] data) {
//		String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
//		if (avatarUrl != null) {
		setCurrentUserAvatar(KHConst.ATTACHMENT_ADDR + UserManager.getInstance().getUser().getHead_sub_image());
//		}
//		return avatarUrl;
		return "";
	}

	public void asyncGetCurrentUserInfo() {
		
		setCurrentUserNick(UserManager.getInstance().getUser().getName());
		setCurrentUserAvatar(KHConst.ATTACHMENT_ADDR + UserManager.getInstance().getUser().getHead_sub_image());
	}
	public void asyncGetUserInfo(final String username,final EMValueCallBack<User> callback){
//		ParseManager.getInstance().asyncGetUserInfo(username, callback);
	}
	private void setCurrentUserNick(String nickname) {
		getCurrentUserInfo().setNick(nickname);
		HXPreferenceUtils.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentUserAvatar(String avatar) {
		getCurrentUserInfo().setAvatar(avatar);
		HXPreferenceUtils.getInstance().setCurrentUserAvatar(avatar);
	}

	private String getCurrentUserNick() {
		return HXPreferenceUtils.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return HXPreferenceUtils.getInstance().getCurrentUserAvatar();
	}

}

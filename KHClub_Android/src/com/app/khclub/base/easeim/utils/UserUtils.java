package com.app.khclub.base.easeim.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.app.KHApplication;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.db.UserDao;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.utils.ConfigUtils;
import com.app.khclub.base.utils.KHConst;
import com.easemob.chat.EMGroup;
import com.lidroid.xutils.exception.HttpException;
import com.squareup.picasso.Picasso;

public class UserUtils {
	
	private static String NAMEKEY = "name";
	private static String AVATARKEY = "avatar";
	public static String GROUP_AVATARKEY = "groupAvatar";
	public static String GROUP_QRCODEKEY = "groupQrcode";
	
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
    	if (null == username) {
			return null;
		}
    	
        User user = ((KHHXSDKHelper)KHHXSDKHelper.getInstance()).getContactList().get(username);
        boolean isFriend = true;
        if(user == null){
        	isFriend = false;
            user = new User(username);
            
            String name = ConfigUtils.getStringConfig(username+NAMEKEY);
            String avatar = ConfigUtils.getStringConfig(username+AVATARKEY);
            //获取缓存
            if (null != name && name.length() > 0) {
            	user.setNick(name);	
			}else {
				user.setNick(KHApplication.getInstance().getResources().getString(R.string.personal_none));
			}
            if (null != avatar && avatar.length() > 0) {
            	user.setAvatar(avatar);	
			}
        }
            
        //每次获取都更新一次
        final User tmpUser = user;
        //是否是好友
        final boolean tmpIsFriend = isFriend;
		String path = KHConst.GET_IMAGE_AND_NAME+"?user_id="+username.replace(KHConst.KH, "")+"&self_id="+UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				// TODO Auto-generated method stub
				super.onSuccess(jsonResponse, flag);
				if (jsonResponse.getInteger(KHConst.HTTP_STATUS) == KHConst.STATUS_SUCCESS) {
					JSONObject resultObject = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					//获取信息
					String name = resultObject.getString("name");
					String headImage = resultObject.getString("head_sub_image");
					//是好友更新不是缓存
					if (tmpIsFriend) {
						tmpUser.setNick(name);
						tmpUser.setAvatar(KHConst.ATTACHMENT_ADDR+headImage);
						UserDao userDao = new UserDao(KHApplication.getInstance());
						userDao.saveContact(tmpUser);						
					}else {
						//缓存非好友
						ConfigUtils.saveConfig(tmpUser.getUsername()+NAMEKEY, name);
						ConfigUtils.saveConfig(tmpUser.getUsername()+AVATARKEY, KHConst.ATTACHMENT_ADDR+headImage);
					}
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
			}
		}, null));
        
        return user;
    }
    
    //获取名字
    public static String getUserName(String username) {
    	User user = ((KHHXSDKHelper)KHHXSDKHelper.getInstance()).getContactList().get(username);
          if (user != null) {
  			return user.getNick();
  		}else {
  			return ConfigUtils.getStringConfig(username+NAMEKEY);
  		}
	}
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	if (username.equals(KHConst.KH_ROBOT)) {
			Picasso.with(context).load(R.drawable.icon).into(imageView);
    		return;
		}
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((KHHXSDKHelper)KHHXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	if (username.equals(KHConst.KH_ROBOT)) {
    		textView.setText(R.string.personal_official_business_manager);
    		return;
		}
    	User user = getUserInfo(username);
    	if(user != null){
    		if (user.getNick().equals(user.getUsername())) {
    			textView.setText(R.string.personal_none);
    			return;
			}
    		if (user.getNick() == null || user.getNick().length() < 1) {
    			textView.setText(R.string.personal_none);
    			return;
			}
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(R.string.personal_none);
    	}
    }
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	User user = ((KHHXSDKHelper)KHHXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }
    
    /**
     * 保存或更新某个用户
     * @param user
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((KHHXSDKHelper) KHHXSDKHelper.getInstance()).saveContact(newUser);
	}
	
    /**
     * 设置群聊名 
     */
    public static void setGroupNick(final EMGroup group,final TextView textView){

    	//先用群组然后获取
		textView.setText(group.getGroupName());
		String path = KHConst.GET_GROUP_IMAGE_AND_NAME_AND_QRCODE+"?group_id="+group.getGroupId();
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				if (jsonResponse.getInteger(KHConst.HTTP_STATUS) == KHConst.STATUS_SUCCESS) {
					JSONObject resultObject = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					//获取信息
					String name = resultObject.getString("group_name");
					String avatar = resultObject.getString("group_cover");
					String qrcode = resultObject.getString("group_qr_code");
					//缓存二维码和图片
					ConfigUtils.saveConfig(group.getGroupId()+GROUP_AVATARKEY, KHConst.ATTACHMENT_ADDR+avatar);
					ConfigUtils.saveConfig(group.getGroupId()+GROUP_QRCODEKEY, KHConst.ROOT_PATH+qrcode);
					//名字不相等 用数据库里的
					if (!name.equals(group.getGroupName())) {
						textView.setText(name);
					}
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
			}
		}, null));
    }
    
    /**
     * 设置群组头像
     * @param username
     */
    public static void setGroupAvatar(final Context context, final EMGroup group,final ImageView imageView){
    	//本地缓存
    	String avatarPath = ConfigUtils.getStringConfig(group.getGroupId()+GROUP_AVATARKEY);
        if(avatarPath.length() > 0){
            Picasso.with(context).load(avatarPath).placeholder(R.drawable.groups_icon).into(imageView);
        }
    	
    	String path = KHConst.GET_GROUP_IMAGE_AND_NAME_AND_QRCODE+"?group_id="+group.getGroupId();
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				if (jsonResponse.getInteger(KHConst.HTTP_STATUS) == KHConst.STATUS_SUCCESS) {
					JSONObject resultObject = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					//获取信息
					String avatar = resultObject.getString("group_cover");
					String qrcode = resultObject.getString("group_qr_code");
					//缓存二维码和图片
					ConfigUtils.saveConfig(group.getGroupId()+GROUP_AVATARKEY, KHConst.ATTACHMENT_ADDR+avatar);
					ConfigUtils.saveConfig(group.getGroupId()+GROUP_QRCODEKEY, KHConst.ROOT_PATH+qrcode);
					if(avatar.length() > 0){
			            Picasso.with(context).load(KHConst.ATTACHMENT_ADDR+avatar).placeholder(R.drawable.groups_icon).into(imageView);
			        }
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
			}
		}, null));

    }
    
    /**
     * 设置群组二维码
     * @param username
     */
    public static void setGroupQRCode(final Context context, final String groupid,final ImageView imageView){
    	//本地缓存
    	String avatarPath = ConfigUtils.getStringConfig(groupid+GROUP_QRCODEKEY);
        if(avatarPath.length() > 0){
            Picasso.with(context).load(avatarPath).placeholder(R.drawable.groups_icon).into(imageView);
        }
    	String path = KHConst.GET_GROUP_IMAGE_AND_NAME_AND_QRCODE+"?group_id="+groupid;
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				if (jsonResponse.getInteger(KHConst.HTTP_STATUS) == KHConst.STATUS_SUCCESS) {
					JSONObject resultObject = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					//获取信息
					String avatar = resultObject.getString("group_cover");
					String qrcode = resultObject.getString("group_qr_code");
					//缓存二维码和图片
					ConfigUtils.saveConfig(groupid+GROUP_AVATARKEY, KHConst.ATTACHMENT_ADDR+avatar);
					ConfigUtils.saveConfig(groupid+GROUP_QRCODEKEY, KHConst.ROOT_PATH+qrcode);
					if(qrcode.length() > 0){
			            Picasso.with(context).load(KHConst.ROOT_PATH+qrcode).placeholder(R.drawable.groups_icon).into(imageView);
			        }
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
			}
		}, null));

    }
    
}

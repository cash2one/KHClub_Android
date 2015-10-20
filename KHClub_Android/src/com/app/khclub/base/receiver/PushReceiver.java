package com.app.khclub.base.receiver;

import io.yunba.android.manager.YunBaManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.Constant;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.domain.InviteMessage;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.easeim.domain.InviteMessage.InviteMesageStatus;
import com.app.khclub.base.easeim.utils.UserUtils;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.NewsPushModel;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.login.ui.activity.LaunchActivity;
import com.app.khclub.message.model.IMModel;

public class PushReceiver extends BroadcastReceiver {

	private NotificationManager mNotificationManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
			
			try {
				String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
			    //如果不是自己订阅的则不接收
				if (!topic.equals(KHConst.KH+UserManager.getInstance().getUser().getUid())) {
			        return;
				}
				String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append("[Message] ").append(YunBaManager.MQTT_TOPIC)
						.append(" = ").append(topic).append(" ,")
							.append(YunBaManager.MQTT_MSG).append(" = ").append(msg);	
				LogUtils.i(showMsg.toString(),1);
				
				//json解析
				JSONObject obj = JSON.parseObject(msg);
				if (obj == null) {
					return;
				}
				int type = obj.getIntValue("type");
//				LogUtils.i(type+"",1);
				
				switch (type) {
				case NewsPushModel.PushAddFriend:
					//添加好友信息
					handleNewFriend(obj, context);
					break;
				case NewsPushModel.PushNewsAnwser:
		            //如果是状态回复消息
					handleNewsPush(obj, context);
					break;
				case NewsPushModel.PushSecondComment:
		            //如果是二级回复消息
					handleNewsPush(obj, context);
					break;
				case NewsPushModel.PushLikeNews:
		            //如果是点赞			
					handleNewsPush(obj, context);
					break;
				case NewsPushModel.PushGroupInvite:
					handleGroupPush(obj, context);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		} else if(YunBaManager.MESSAGE_CONNECTED_ACTION.equals(intent.getAction())) {
			
		} else if(YunBaManager.MESSAGE_DISCONNECTED_ACTION.equals(intent.getAction())) {
			
		} else if (YunBaManager.PRESENCE_RECEIVED_ACTION.equals(intent.getAction())) {
			
		}
	}
	
	//处理新好友通知
	private void handleNewFriend(JSONObject jsonObject, Context context) {
		JSONObject pushObject = jsonObject.getJSONObject("content");
		IMModel imModel = IMModel.findByGroupId(pushObject.getString("uid"));
		String title = "有人添加了你";
		String content = pushObject.getString("name")+"添加了你";
		if (null != imModel) {
			//存在 加好友 但是有新朋友
			if (imModel.getIsNew() != 1) {
				imModel.setTitle(pushObject.getString("name"));
				imModel.setAvatarPath(pushObject.getString("avatar"));
				imModel.setAddDate(pushObject.getString("time"));
				imModel.setIsNew(1);
				imModel.setIsRead(0);
				imModel.update();
				showNotification(context, title, title, content);
				
				//发送通知
				Intent newsGroupIntent = new Intent(KHConst.BROADCAST_NEW_MESSAGE_PUSH);
				context.sendBroadcast(newsGroupIntent);
			}
		}else {
			
			showNotification(context, title, title, content);
			
			imModel = new IMModel();
			//保存群组信息
			imModel.setType(pushObject.getIntValue("type"));
			imModel.setTargetId(pushObject.getString("uid"));
			imModel.setTitle(pushObject.getString("name"));
			imModel.setAvatarPath(pushObject.getString("avatar"));
			imModel.setAddDate(pushObject.getString("time"));
			imModel.setIsNew(1);
			imModel.setCurrentState(IMModel.GroupNotAdd);
			imModel.setIsRead(0);
			imModel.setOwner(UserManager.getInstance().getUser().getUid());
			imModel.save();
			
			//发送通知
			Intent newsGroupIntent = new Intent(KHConst.BROADCAST_NEW_MESSAGE_PUSH);
			context.sendBroadcast(newsGroupIntent);
		}
		
		//徽标更新
		Intent tabIntent = new Intent(KHConst.BROADCAST_TAB_BADGE);
		context.sendBroadcast(tabIntent);
	}
	
	//新闻回复点赞到来 处理消息
	private void handleNewsPush(JSONObject jsonObject, Context context) {
		
		JSONObject pushObject = jsonObject.getJSONObject("content");
		
		int type = jsonObject.getIntValue("type");
		String title = "";
		String content = "";
		switch (type) {
		case NewsPushModel.PushNewsAnwser:
			title = "有人为你评论";
			content = pushObject.getString("name")+":"+pushObject.getString("comment_content");
			break;
		case NewsPushModel.PushSecondComment:
			title = "有人为你评论";
			content = pushObject.getString("name")+":"+pushObject.getString("comment_content");
			break;
		case NewsPushModel.PushLikeNews:
			title = "有人为你点赞";
			content = pushObject.getString("name")+"赞了你";
			break;
		default:
			break;
		}
		
		showNotification(context, title, title, content);
		
		NewsPushModel pushModel = new NewsPushModel();
		//存储
		pushModel.setUid(pushObject.getIntValue("uid"));
		pushModel.setName(pushObject.getString("name"));
		pushModel.setHead_image(pushObject.getString("head_image"));
		pushModel.setComment_content(pushObject.getString("comment_content"));
		pushModel.setType(jsonObject.getIntValue("type"));
		pushModel.setNews_id(pushObject.getIntValue("news_id"));
		pushModel.setNews_content(pushObject.getString("news_content"));
		pushModel.setNews_image(pushObject.getString("news_image"));
		pushModel.setNews_user_name(pushObject.getString("news_user_name"));
		pushModel.setIs_read(0);
		pushModel.setPush_time(pushObject.getString("push_time"));
		pushModel.setOwner(UserManager.getInstance().getUser().getUid());
		
		if (!pushModel.isExist()) {
			pushModel.save();
			//发送通知
			Intent newsPushIntent = new Intent(KHConst.BROADCAST_NEW_MESSAGE_PUSH);
			context.sendBroadcast(newsPushIntent);
			//徽标更新
			Intent tabIntent = new Intent(KHConst.BROADCAST_TAB_BADGE);
			context.sendBroadcast(tabIntent);
		}
		
	}
	
	//群组推送 处理消息
	private void handleGroupPush(JSONObject jsonObject, Context context) {
		
		JSONObject pushObject = jsonObject.getJSONObject("content");
		
		// 用户申请加入群聊
		InviteMessage msg = new InviteMessage();
		msg.setFrom(pushObject.getString("target_id"));
		msg.setTime(System.currentTimeMillis());
		msg.setGroupId(pushObject.getString("groupid"));
		msg.setGroupName(pushObject.getString("groupname"));
		msg.setReason(pushObject.getString("name")+"邀请加入");
		msg.setStatus(InviteMesageStatus.BEAPPLYED);
		
		InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(context);
		inviteMessgeDao.saveMessage(msg);
		//获取缓存信息
		UserUtils.getUserInfo(msg.getFrom());
		// 未读数加1
		User user = ((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
		//发通知
		Intent newsPushIntent = new Intent(KHConst.BROADCAST_GROUP_INVITE);
		newsPushIntent.putExtra("type", NewsPushModel.PushGroupInvite);
		context.sendBroadcast(newsPushIntent);
	}
	
	/**
	 * 显示通知
	 * 
	 * @param context
	 * @param ewMessage
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(Context context, CharSequence tickerText, CharSequence contentTitle, CharSequence contentText) {

		long newWhen = System.currentTimeMillis();
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int iconId = R.drawable.ic_launcher;
		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setClass(context.getApplicationContext(), LaunchActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new Notification();
		notification.when = newWhen;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.defaults = Notification.DEFAULT_SOUND;
		notification.icon = iconId;
		notification.tickerText = tickerText;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		// notification的id使用发送者的id
		mNotificationManager.notify(0, notification);

	}
	
	//send msg to MainActivity
//	private void processCustomMessage(Context context, Intent intent) {
//	
////			intent.setAction(MainActivity.MESSAGE_RECEIVED_ACTION);
////			intent.addCategory(context.getPackageName());
////			context.sendBroadcast(intent);
//		
//	}
	
	
}

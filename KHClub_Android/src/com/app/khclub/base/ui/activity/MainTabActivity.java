package com.app.khclub.base.ui.activity;

import io.yunba.android.manager.YunBaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.Constant;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.UserProfileManager;
import com.app.khclub.base.easeim.activity.ChatActivity;
import com.app.khclub.base.easeim.activity.ChatAllHistoryFragment;
import com.app.khclub.base.easeim.activity.ContactlistFragment;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.db.UserDao;
import com.app.khclub.base.easeim.domain.InviteMessage;
import com.app.khclub.base.easeim.domain.InviteMessage.InviteMesageStatus;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.easeim.utils.UserUtils;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.NewVersionCheckManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.NewsPushModel;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.login.ui.activity.LoginActivity;
import com.app.khclub.news.ui.fragment.NewsListFragment;
import com.app.khclub.personal.ui.fragment.PersonalFragment;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainTabActivity extends BaseActivity implements EMEventListener{

	// FragmentTabHost对象
	@ViewInject(android.R.id.tabhost)
	public FragmentTabHost mTabHost;
	
	private LayoutInflater layoutInflater;	
	
	private Class<?> fragmentArray[] = { NewsListFragment.class,
			ChatAllHistoryFragment.class, ContactlistFragment.class,
			PersonalFragment.class };

	private int mImageViewArray[] = { R.drawable.tab_home_btn,R.drawable.tab_message_btn,R.drawable.tab_contact_btn,
			R.drawable.tab_me_btn };

	private String mTextviewArray[] = { "主页", "消息", "通讯录", "我" };
	private MyConnectionListener connectionListener = null;
	private MyGroupChangeListener groupChangeListener = null;
	
	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;
	private ContactlistFragment contactListFragment;
	private ChatAllHistoryFragment chatHistoryFragment;
	
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
	private void getLastVersion() {
		new NewVersionCheckManager(this, this).checkNewVersion(false, null);
	}

	@SuppressLint("InflateParams")
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		String title = "";
		switch (index) {
		case 0:
			title = getString(R.string.main_home_title);
			break;
		case 1:
			title = getString(R.string.main_msg_title);
			break;
		case 2:
			title = getString(R.string.main_contact_title);
			break;
		case 3:
			title = getString(R.string.main_me_title);
			break;			

		default:
			break;
		}
		textView.setText(title);

		return view;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
		    KHHXSDKHelper.getInstance().logout(true,null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		
		
//		10-22 18:05:46.784: A/MobUncaughtExceptionHandler(22675): java.lang.NullPointerException
//		10-22 18:05:46.784: A/MobUncaughtExceptionHandler(22675): 	at com.app.khclub.base.easeim.activity.ContactlistFragment$HXBlackListSyncListener.onSyncSucess(ContactlistFragment.java:151)
//		10-22 18:05:46.784: A/MobUncaughtExceptionHandler(22675): 	at com.app.khclub.base.easeim.applib.controller.HXSDKHelper.notifyBlackListSyncListener(HXSDKHelper.java:631)
//		10-22 18:05:46.784: A/MobUncaughtExceptionHandler(22675): 	at com.app.khclub.base.ui.activity.MainTabActivity$4.onSuccess(MainTabActivity.java:469)
//		10-22 18:05:46.784: A/MobUncaughtExceptionHandler(22675): 	at com.app.khclub.base.ui.activity.MainTabActivity$4.onSuccess(MainTabActivity.java:1)
//		10-22 18:05:46.784: A/MobUncaughtExceptionHandler(22675): 	at com.app.khclub.base.easeim.applib.controller.HXSDKHelper$5.run(HXSDKHelper.java:611)
//		10-22 18:05:46.784: E/Environment(22675): tzyl---update PrimaryVolume everytime
//		10-22 18:05:46.784: E/Environment(22675): tzyl---getPrimaryVolume StorageVolume [mStorageId=65537 mPath=/storage/sdcard0 mDescriptionId=17040671 mPrimary=true mRemovable=false mEmulated=true mMtpReserveSpace=100 mAllowMassStorage=false mMaxFileSize=0 mOwner=UserHandle{0}]
//		10-22 18:05:46.784: W/Environment(22675): tzyl---getExternalStorageStatemounted
//		10-22 18:05:46.784: E/AndroidRuntime(22675): FATAL EXCEPTION: Thread-24729
//		10-22 18:05:46.784: E/AndroidRuntime(22675): java.lang.NullPointerException
//		10-22 18:05:46.784: E/AndroidRuntime(22675): 	at com.app.khclub.base.easeim.activity.ContactlistFragment$HXContactSyncListener.onSyncSucess(ContactlistFragment.java:121)
//		10-22 18:05:46.784: E/AndroidRuntime(22675): 	at com.app.khclub.base.easeim.applib.controller.HXSDKHelper.notifyContactsSyncListener(HXSDKHelper.java:582)
//		10-22 18:05:46.784: E/AndroidRuntime(22675): 	at com.app.khclub.base.ui.activity.MainTabActivity$3.onSuccess(MainTabActivity.java:435)
//		10-22 18:05:46.784: E/AndroidRuntime(22675): 	at com.app.khclub.base.ui.activity.MainTabActivity$3.onSuccess(MainTabActivity.java:1)
//		10-22 18:05:46.784: E/AndroidRuntime(22675): 	at com.app.khclub.base.easeim.applib.controller.HXSDKHelper$4.run(HXSDKHelper.java:564)

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

		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		
		// 初始化tab
		initTab();
		// 初始化云巴
		initYunBa();
		// 获取最新版本
		getLastVersion();
		init();
		//更新iOS push
		EMChatManager.getInstance().updateCurrentUserNick(UserManager.getInstance().getUser().getName());
		
		asycContact();
	}
	
	private void init() {     
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		
		groupChangeListener = new MyGroupChangeListener();
		// 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);
		
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
		//每次进入 都更新自己的信息到IM 这里不用单例 先用原版
		UserProfileManager manager = new UserProfileManager();
		manager.asyncGetCurrentUserInfo();
		
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}

		// unregister this event listener when this activity enters the
		KHHXSDKHelper sdkHelper = (KHHXSDKHelper) KHHXSDKHelper.getInstance();
		sdkHelper.pushActivity(this);
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage ,EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
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
				if (intent.hasExtra("type")) {
					if (intent.getIntExtra("type", 0) == NewsPushModel.PushGroupInvite) {
						//如果是邀请
						notifyNewIviteMessage(null);
						return;
					}
				}
				// 刷新tab
				refreshTab();
			}
		};
		
		IntentFilter intentFilter = new IntentFilter(KHConst.BROADCAST_TAB_BADGE);
		IntentFilter intentFilter2 = new IntentFilter(KHConst.BROADCAST_GROUP_INVITE);
		registerReceiver(newMessageReceiver, intentFilter);
		registerReceiver(newMessageReceiver, intentFilter2);
	}

	// 刷新tab 未读标志
	private void refreshTab() {
		View newsView = mTabHost.getTabWidget().getChildAt(0);
		TextView newsUnreadTextView = (TextView) newsView
				.findViewById(R.id.unread_text_view);
		// 未读推送
		int newsUnreadCount = 0;
		try {
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

	// 杀死进程
	public static void killThisPackageIfRunning(final Context context,
			String packageName) {
		android.app.ActivityManager activityManager = (android.app.ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.killBackgroundProcesses(packageName);
	}

	static void asyncFetchContactsFromServer(){
	    HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>(){

            @Override
            public void onSuccess(List<String> usernames) {
                Context context = HXSDKHelper.getInstance().getAppContext();
                 
                System.out.println("----------------"+usernames.toString());
                EMLog.d("roster", "contacts size: " + usernames.size());
                Map<String, User> userlist = new HashMap<String, User>();
                for (String username : usernames) {
                    User user = new User();
                    user.setUsername(username);
                    setUserHearder(username, user);
                    userlist.put(username, user);
                }
                // 添加user"申请与通知"
                User newFriends = new User();
                newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                String strChat = context.getString(R.string.Application_and_notify);
                newFriends.setNick(strChat);
        
                userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                // 添加"群聊"
                User groupUser = new User();
                String strGroup = context.getString(R.string.group_chat);
                groupUser.setUsername(Constant.GROUP_USERNAME);
                groupUser.setNick(strGroup);
                groupUser.setHeader("");
                userlist.put(Constant.GROUP_USERNAME, groupUser);
                
                // 添加"Robot" 更改为名片列表
        		User robotUser = new User();
//        		String strRobot = context.getString(R.string.robot_chat);
        		String strRobot = context.getString(R.string.personal_collect_card);
        		robotUser.setUsername(Constant.CHAT_ROBOT);
        		robotUser.setNick(strRobot);
        		robotUser.setHeader("");
        		userlist.put(Constant.CHAT_ROBOT, robotUser);
        		
                 // 存入内存
                ((KHHXSDKHelper)HXSDKHelper.getInstance()).setContactList(userlist);
                 // 存入db
                UserDao dao = new UserDao(context);
                List<User> users = new ArrayList<User>(userlist.values());
                dao.saveContactList(users);

                HXSDKHelper.getInstance().notifyContactsSyncListener(true);
                
                if(HXSDKHelper.getInstance().isGroupsSyncedWithServer()){
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
                
                ((KHHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().asyncFetchContactInfosFromServer(usernames,new EMValueCallBack<List<User>>() {

					@Override
					public void onSuccess(List<User> uList) {
						((KHHXSDKHelper)HXSDKHelper.getInstance()).updateContactList(uList);
						((KHHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().notifyContactInfosSyncListener(true);
					}

					@Override
					public void onError(int error, String errorMsg) {
					}
				});
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyContactsSyncListener(false);
            }
	        
	    });
	}
	
	static void asyncFetchBlackListFromServer(){
	    HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>(){

            @Override
            public void onSuccess(List<String> value) {
                EMContactManager.getInstance().saveBlackList(value);
                HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
            }
	        
	    });
	}
	
	/**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     * 
     * @param username
     * @param user
     */
    private static void setUserHearder(String username, User user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }	
	
	
	@Override
	public void onEvent(EMNotifierEvent event) {
		// TODO Auto-generated method stub
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			refreshUI();
			break;
		}
		case EventOfflineMessage: {
			refreshUI();
			break;
		}
		case EventConversationListChanged: {
		    refreshUI();
		    break;
		}
		
		default:
			break;
		}
	}
	
	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (mTabHost.getCurrentTab() == 1) {
					// 当前页面如果为聊天历史页面，刷新此页面
					chatHistoryFragmentRefresh();
				}
			}
		});
	}
	
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		View messageView = mTabHost.getTabWidget().getChildAt(1);
		TextView unreadTextView = (TextView) messageView.findViewById(R.id.unread_text_view);
		if (count > 0) {
			unreadTextView.setVisibility(View.VISIBLE);
		} else {
			unreadTextView.setVisibility(View.GONE);
		}
	}
	

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				View messageView = mTabHost.getTabWidget().getChildAt(2);
				TextView unreadTextView = (TextView) messageView.findViewById(R.id.unread_text_view);
				if (count > 0) {
					unreadTextView.setVisibility(View.VISIBLE);
				} else {
					unreadTextView.setVisibility(View.GONE);
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = ((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
			chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}	
	
	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}
	
	public static void asyncFetchGroupsFromServer(){
	    HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack(){

            @Override
            public void onSuccess() {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);
                
                if(HXSDKHelper.getInstance().isContactsSyncedWithServer()){
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
            }

            @Override
            public void onError(int code, String message) {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);                
            }

            @Override
            public void onProgress(int progress, String status) {
                
            }
            
        });
	}
	
	/***
	 * 好友变化listener
	 * 
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {		
			// 保存增加的联系人
			final Map<String, User> localUsers = ((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList();
			
			for (final String username : usernameList) {
				final User user = setUserHead(username);
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					//获取头像和nickName
					String path = KHConst.GET_IMAGE_AND_NAME+"?user_id="+username.replace(KHConst.KH, "");
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
								user.setNick(name);
								user.setAvatar(KHConst.ATTACHMENT_ADDR+headImage);
								//失败了也存上
								userDao.saveContact(user);								
							}else {
								//失败了也存上
								userDao.saveContact(user);								
							}
							localUsers.put(username, user);
						}
						@Override
						public void onFailure(HttpException arg0, String arg1, String flag) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1, flag);
							//失败了也存上
							userDao.saveContact(user);
							localUsers.put(username, user);
						}
					}, null));
				}
			}
			// 刷新ui
			contactListFragmentRefresh();
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = ((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					// 如果正在与此用户的聊天页面
					String st10 = getResources().getString(R.string.have_you_removed);
					if (ChatActivity.activityInstance != null
							&& usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
						Toast.makeText(MainTabActivity.this, st10, 1)
								.show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
					// 刷新ui
					contactListFragmentRefresh();
					chatHistoryFragmentRefresh();
				}
			});

		}

		@Override
		public void onContactInvited(String username, String reason) {
			
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
//					//保存一次拒绝
//					try {
//						EMChatManager.getInstance().refuseInvitation(username);
//					} catch (EaseMobException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d("--", username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

			//保存一次拒绝
//			try {
//				EMChatManager.getInstance().refuseInvitation(username);
//			} catch (EaseMobException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d("--", username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
			
			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}

	}

	/**
	 * 连接监听listener
	 * 
	 */
	public class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
            boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();
            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if(groupSynced && contactSynced){
                new Thread(){
                    @Override
                    public void run(){
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            }else{
                if(!groupSynced){
                    asyncFetchGroupsFromServer();
                }
                
                if(!contactSynced){
                    asyncFetchContactsFromServer();
                }
                
                if(!HXSDKHelper.getInstance().isBlackListSyncedWithServer()){
                    asyncFetchBlackListFromServer();
                }
            }
            
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					chatHistoryFragmentRefresh();
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
			final String st2 = getResources().getString(R.string.the_current_network);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
//						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {
						if (chatHistoryFragment != null) {
							chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
							if (NetUtils.hasNetwork(MainTabActivity.this))
								chatHistoryFragment.errorText.setText(st1);
							else
								chatHistoryFragment.errorText.setText(st2);	
						}
					}
				}

			});
		}
	}
	
	
	/**
	 * MyGroupChangeListener
	 */
	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
			
			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;

			// 被邀请
			String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
//			msg.addBody(new TextMessageBody(inviter + " " +st3));
			msg.addBody(new TextMessageBody(st3));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					
					// 刷新ui
					chatHistoryFragmentRefresh();
//					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//						GroupsActivity.instance.onResume();
//					}
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {
			
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
						
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						chatHistoryFragmentRefresh();
//						if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//							GroupsActivity.instance.onResume();
//						}
					} catch (Exception e) {
//						EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					chatHistoryFragmentRefresh();
					
//					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//						GroupsActivity.instance.onResume();
//					}
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
//			//申请过了 不添加
//			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
//			for (InviteMessage inviteMessage : msgs) {
//				if (inviteMessage.getGroupId() != null && inviteMessage.getFrom().equals(applyer)) {
//					return;
//				}
//			}
			
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d("--", applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {

			String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
//			msg.addBody(new TextMessageBody(accepter + " " +st4));
			msg.addBody(new TextMessageBody(st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					chatHistoryFragmentRefresh();
					
//					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//						GroupsActivity.instance.onResume();
//					}
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	}
	
	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		contactListFragmentRefresh();
		
	}

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		if (null == msg) {
			return;
		}
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		//获取缓存信息
		UserUtils.getUserInfo(msg.getFrom());
		// 未读数加1
		User user = ((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
		LogUtils.i(user.getUnreadMsgCount()+"", 1);
	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}
	
	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;
	
	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		KHHXSDKHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainTabActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainTabActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						startActivity(new Intent(MainTabActivity.this, LoginActivity.class));
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e("--", "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
//	private void showAccountRemovedDialog() {
//		isAccountRemovedDialogShow = true;
//		DemoHXSDKHelper.getInstance().logout(true,null);
//		String st5 = getResources().getString(R.string.Remove_the_notification);
//		if (!MainActivity.this.isFinishing()) {
//			// clear up global variables
//			try {
//				if (accountRemovedBuilder == null)
//					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
//				accountRemovedBuilder.setTitle(st5);
//				accountRemovedBuilder.setMessage(R.string.em_user_remove);
//				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						accountRemovedBuilder = null;
//						finish();
//						startActivity(new Intent(MainActivity.this, LoginActivity.class));
//					}
//				});
//				accountRemovedBuilder.setCancelable(false);
//				accountRemovedBuilder.create().show();
//				isCurrentAccountRemoved = true;
//			} catch (Exception e) {
//				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
//			}
//
//		}
//
//	}

	private void contactListFragmentRefresh(){
		if (contactListFragment != null) {
			contactListFragment.refresh();
		}else {
			contactListFragment = (ContactlistFragment) getSupportFragmentManager().findFragmentByTag(mTextviewArray[2]);
			if (contactListFragment != null) {
				contactListFragment.refresh();
			}	
		}
	}
	
	private void chatHistoryFragmentRefresh(){
		if (chatHistoryFragment != null) {
			chatHistoryFragment.refresh();
		}else {
			chatHistoryFragment = (ChatAllHistoryFragment) getSupportFragmentManager().findFragmentByTag(mTextviewArray[1]);
			if (chatHistoryFragment != null) {
				chatHistoryFragment.refresh();
			}	
		}
	}

	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		// TODO Auto-generated method stub
		if (data == null) {
			return;
		}
		super.onActivityResult(arg0, arg1, data);
		
	}

	private void asycContact() {
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
							((KHHXSDKHelper)HXSDKHelper.getInstance()).updateContactList(users);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}

				}, null));	
		
	}

}

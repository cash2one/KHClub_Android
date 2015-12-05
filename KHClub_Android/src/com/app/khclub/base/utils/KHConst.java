package com.app.khclub.base.utils;

public interface KHConst {

	// 正式环境 120.25.213.171
	// 测试环境 112.74.199.145
	public static final String DOMIN = "http://120.25.213.171/khclub_php/index.php/Home/MobileApi";
	public static final String ATTACHMENT_ADDR = "http://120.25.213.171/khclub_php/Uploads/";
	public static final String ROOT_PATH = "http://120.25.213.171/khclub_php/";

	public static final int STATUS_SUCCESS = 1;// 接口返回成功
	public static final int STATUS_FAIL = 0;// 接口返回失败
	public static final String HTTP_MESSAGE = "message"; // 返回值信息
	public static final String HTTP_RESULT = "result";// 返回值结果
	public static final String HTTP_STATUS = "status";// 返回值状态
	public static final String HTTP_LIST = "list";// 返回值列表

	public static final int PAGE_SIZE = 10;

	//客服账号
	public static final String KH_ROBOT = "kh100";
	//分享的网址
	public static final String SHARE_WEB = "http://www.khclub.sg/";
	//名片分享网址
//	public static final String SHARE_CARD_WEB = "http://120.25.121.179/api/index.html";
	public static final String SHARE_CARD_WEB = ROOT_PATH + "index.php/Home/WX/cardDetail";
	//本地持久的额外信息key
	public static final String TITLE_1_KEY = "title_1";
	public static final String TITLE_2_KEY = "title_2";
	public static final String TITLE_3_KEY = "title_3";
	public static final String TITLE_4_KEY = "title_4";
	
	// IM和推送 公用前缀
	public static final String KH = "kh";
	public static final String KH_GROUP = "khGroup";
	// IM通用密码
	public static final String IM_PWD = "123456";

	//服务器默认图片
	public static final String ROOT_IMG = ROOT_PATH + "/icon.png";
	
	// broadCast
	// 状态回复消息或点赞或者新好友
	public static final String BROADCAST_NEW_MESSAGE_PUSH = "com.khclub.broadcastreceiver.newsPush";
	public static final String BROADCAST_NEWS_LIST_REFRESH = "com.khclub.broadcastreceiver.newsPush";
	public static final String BROADCAST_TAB_BADGE = "com.khclub.broadcastreceiver.tabBadge";
	//群组邀请
	public static final String BROADCAST_GROUP_INVITE = "com.khclub.broadcastreceiver.groupInvite";

	// 匹配网页
	public static final String URL_PATTERN = "[http|https]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*";
	// 匹配手机号
	public static final String PHONENUMBER_PATTERN = "1[3|4|5|7|8|][0-9]{9}";
	// 用户名匹配
	public static final String USER_ACCOUNT_PATTERN = "^[a-zA-Z0-9]{6,20}+$";
	// 匹配身份证号:15位 18位
	public static final String ID_CARD = "^\\d{15}|^\\d{17}([0-9]|X|x)$";
	// 姓名正则
	public static final String NAME_PATTERN = "([\u4e00-\u9fa5]{2,5})(&middot;[\u4e00-\u9fa5]{2,5})*";

	// //////////////////////////////////////////////登录注册部分////////////////////////////////////////////////
	// 是否有该用户
	public static final String IS_USER = DOMIN + "/isUser";
	// 用户注册
	public static final String REGISTER_USER = DOMIN + "/registerUser";
	// 找回密码
	public static final String FIND_PWD = DOMIN + "/findPwd";
	// 用户登录
	public static final String LOGIN_USER = DOMIN + "/loginUser";
	// //////////////////////////////////////////////首页'状态'部分////////////////////////////////////////////////
	// 发布状态
	public static final String PUBLISH_NEWS = DOMIN + "/publishNews";
	// 状态新闻列表
	public static final String NEWS_LIST = DOMIN + "/newsList";
	// 发送评论
	public static final String SEND_COMMENT = DOMIN + "/sendComment";
	// 删除评论
	public static final String DELETE_COMMENT = DOMIN + "/deleteComment";
	// 点赞或者取消赞
	public static final String LIKE_OR_CANCEL = DOMIN + "/likeOrCancel";
	// 新闻详情
	public static final String NEWS_DETAIL = DOMIN + "/newsDetail";
	// 圈子列表
	public static final String GET_CIRCLE_LIST = DOMIN + "/getCircleList";	
	// //////////////////////////////////////////////个人信息////////////////////////////////////////////////
	// 修改个人信息
	public static final String CHANGE_PERSONAL_INFORMATION = DOMIN
			+ "/changePersonalInformation";
	// 修改个人额外信息
	public static final String CHANGE_PERSONAL_EXTRA_INFORMATION = DOMIN
			+ "/changePersonalExtraInformation";	
	// 获取个人额外信息
	public static final String GET_PERSONAL_EXTRA_INFORMATION = DOMIN
			+ "/getPersonalExtraInformation";		
	// 修改有可见状态的个人信息
	public static final String CHANGE_PERSONAL_INFORMATION_STATE = DOMIN
			+ "/changePersonalInformationState";
	// 获取用户二维码
	public static final String GET_USER_QRCODE = DOMIN + "/getUserQRCode";
	// 修改个人信息中的图片 如背景图 头像
	public static final String CHANGE_INFORMATION_IMAGE = DOMIN
			+ "/changeInformationImage";
	// 个人信息中 获取最新动态的十张图片
	public static final String GET_NEWS_COVER_LIST = DOMIN
			+ "/getNewsCoverList";
	// 个人信息中 用户发布过的状态列表
	public static final String USER_NEWS_LIST = DOMIN + "/userNewsList";
	// 个人信息 删除状态
	public static final String DELETE_NEWS = DOMIN + "/deleteNews";
	// 个人信息 查看别人的信息
	public static final String PERSONAL_INFO = DOMIN + "/personalInfo";
	// 举报用户
	public static final String REPORT_OFFENCE = DOMIN + "/reportOffence";
	// 版本更新
	public static final String GET_LASTEST_VERSION = DOMIN
			+ "/getLastestVersion";
	// ////////////////////////////////////////IM模块//////////////////////////////////////////
	// 添加好友
	public static final String Add_FRIEND = DOMIN + "/addFriend";
	// 删除好友
	public static final String DELETE_FRIEND = DOMIN + "/deleteFriend";
	// 添加好友备注
	public static final String ADD_REMARK = DOMIN + "/addRemark";
	// 获取图片和名字
	public static final String GET_IMAGE_AND_NAME = DOMIN + "/getImageAndName";
	// 创建圈子
	public static final String CREATE_GROUP = DOMIN + "/createGroup";
	// 获取群组图片和名字
	public static final String GET_GROUP_IMAGE_AND_NAME_AND_QRCODE = DOMIN
			+ "/getGroupImageAndNameAndQrcode";
	// 更新群组名字
	public static final String UPDATE_GROUP_NAME = DOMIN + "/updateGroupName";
	// 更新群组封面
	public static final String UPDATE_GROUP_COVER = DOMIN + "/updateGroupCover";
	//同步全部好友
	public static final String GET_ALL_FRIENDS_LIST = DOMIN + "/getAllFriendsList";

	// //////////////////////////////////////发现模块//////////////////////////////////////////
	// 获取联系人用户
	public static final String GET_CONTACT_USER = DOMIN + "/getContactUser";
	// 搜索用户列表
	public static final String FIND_USER_LIST = DOMIN + "/findUserList";;
	// //////////////////////////////////////通讯录部分//////////////////////////////////////////
	// 收藏名片
	public static final String COLLECT_CARD = DOMIN + "/collectCard";
	// 取消收藏名片
	public static final String COLLECT_CARD_DELETE = DOMIN + "/deleteCard";
	// 获取所收藏的名片列表
	public static final String GET_COLLECT_CARD_LIST = DOMIN + "/getCardList";
	// ////////////////////////////主菜，搜索，二维码，创建群///////////////////////////////////
	// 搜索
	public static final String SEARCH_USER_OR_GROUP = DOMIN
			+ "/findUserOrGroup";
}

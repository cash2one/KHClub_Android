package com.app.khclub.personal.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.utils.LogUtils;

/**
 * 个人中心底部弹出菜单
 * */
public class PersonalBottomPopupMenu extends PopupWindow {

	// 布局
	private View conentView;
	// onclick
	private BottomClickListener listener;

	@SuppressLint("NewApi")
	public PersonalBottomPopupMenu(final Context context, boolean isFriend) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater.inflate(
				R.layout.popup_window_personal_bottom_menu, null);

		//点击根布局
		((LinearLayout) conentView.findViewById(R.id.layout_popup_root))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						PersonalBottomPopupMenu.this.dismiss();
					}
				});
		// 分享给好友
		((ImageView) conentView.findViewById(R.id.img_share_to_friend))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToFriendClick();
						PersonalBottomPopupMenu.this.dismiss();
					}
				});

		// 分享给微信好友
		((ImageView) conentView.findViewById(R.id.img_share_to_wechat))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToWeChatClick();
						PersonalBottomPopupMenu.this.dismiss();
					}
				});

		// 分享到朋友圈
		((ImageView) conentView
				.findViewById(R.id.img_share_to_circel_of_friends))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToCircleofFriendsClick();
						PersonalBottomPopupMenu.this.dismiss();
					}
				});
		// 分享给微博
		((ImageView) conentView.findViewById(R.id.img_share_to_weibo))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToWeiboClick();
						PersonalBottomPopupMenu.this.dismiss();
					}
				});

		// 分享给qq空间
		((ImageView) conentView.findViewById(R.id.img_share_to_qzone))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToQzoneClick();
						PersonalBottomPopupMenu.this.dismiss();
					}
				});
		// 分享给qq好友
		((ImageView) conentView.findViewById(R.id.img_share_to_qq_friends))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToQQFriendsClick();
						PersonalBottomPopupMenu.this.dismiss();
					}
				});

		// 设置PopupWindow的View
		this.setContentView(conentView);
		// 设置窗体的尺寸
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		//this.setInputMethodMode(Popupwindows.iNPUT_METHOD_NEEDED);
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// 刷新状态
		this.update();
		// 背景变暗
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.anim_bottom_popup);
	}

	/**
	 * 显示窗体
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}

	// 监听器
	public interface BottomClickListener {
		// 分享给好友
		public void shareToFriendClick();

		// 分享给微信
		public void shareToWeChatClick();

		// 分享到朋友圈
		public void shareToCircleofFriendsClick();

		// 分享到微博
		public void shareToWeiboClick();

		// 分享到qq空间
		public void shareToQzoneClick();

		// 分享到qq空间
		public void shareToQQFriendsClick();

		// 删除好友
		public void deleteFriendClick();

		// 修改备注
		public void editRemarkClick();

		// 取消
		public void cancelClick();
	}

	// 回调接口
	public BottomClickListener getListener() {
		return listener;
	}

	// 设置回调接口
	public void setListener(BottomClickListener listener) {
		this.listener = listener;
	}
}

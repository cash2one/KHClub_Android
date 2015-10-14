package com.app.khclub.base.easeim.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.khclub.R;

/**
 * 个人中心下拉操作菜单
 * */
public class MainPopupMenu extends PopupWindow {

	// 布局
	private View conentView;
	// onclick
	private ClickListener listener;

	@SuppressLint("NewApi")
	public MainPopupMenu(final Context context) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater
				.inflate(R.layout.popup_window_main_menu, null);
		// 扫描二维码
		((LinearLayout) conentView.findViewById(R.id.layout_scanQR))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.scanQRcodeClick();
						MainPopupMenu.this.dismiss();
					}
				});

		// 加好友加群
		((LinearLayout) conentView.findViewById(R.id.layout_search))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.searchClick();
						MainPopupMenu.this.dismiss();
					}
				});

		// 创建群
		((LinearLayout) conentView.findViewById(R.id.layout_create_group))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.createGroupClick();
						MainPopupMenu.this.dismiss();
					}
				});

		// 用户二维码
		((LinearLayout) conentView.findViewById(R.id.layout_user_QRcard))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.userQRShowClick();
						MainPopupMenu.this.dismiss();
					}
				});

		// 设置PopupWindow的View
		this.setContentView(conentView);
		// 设置窗体的尺寸
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 背景变暗
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.anim_main_menu);
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
	public interface ClickListener {
		// 扫描二维码
		public void scanQRcodeClick();

		// 搜索
		public void searchClick();

		// 创建群聊
		public void createGroupClick();

		// 用户的二维码
		public void userQRShowClick();
	}

	// 回调接口
	public ClickListener getListener() {
		return listener;
	}

	public void setListener(ClickListener listener) {
		this.listener = listener;
	}
}

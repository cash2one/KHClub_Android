package com.app.khclub.news.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.news.ui.model.NewsModel;

/**
 * 动态分享底部弹出菜单
 * */
public class NewsBottomPopupMenu extends PopupWindow {

	// 布局
	private View conentView;
	// 当前分享的动态
	private NewsModel currentNews;
	// onclick
	private NewsBottomClickListener listener;

	@SuppressLint("NewApi")
	public NewsBottomPopupMenu(final Context context) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater.inflate(R.layout.popup_window_news_bottom_menu,
				null);

		// 点击根布局
		((LinearLayout) conentView.findViewById(R.id.layout_news_pop_win_root))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						NewsBottomPopupMenu.this.dismiss();
					}
				});

		// 分享给微信好友
		((ImageView) conentView
				.findViewById(R.id.img_news_pop_win_share_to_wechat))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToWeChatClick(currentNews);
						NewsBottomPopupMenu.this.dismiss();
					}
				});

		// 分享到朋友圈
		((ImageView) conentView
				.findViewById(R.id.img_news_pop_win_share_to_circel_of_friends))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToCircleofFriendsClick(currentNews);
						NewsBottomPopupMenu.this.dismiss();
					}
				});
		// 分享给微博
		((ImageView) conentView
				.findViewById(R.id.img_news_pop_win_share_to_weibo))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToWeiboClick(currentNews);
						NewsBottomPopupMenu.this.dismiss();
					}
				});

		// 分享给qq空间
		((ImageView) conentView
				.findViewById(R.id.img_news_pop_win_share_to_qzone))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						listener.shareToQzoneClick(currentNews);
						NewsBottomPopupMenu.this.dismiss();
					}
				});

		// 取操作
		((TextView) conentView.findViewById(R.id.txt_news_pop_win_cancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						NewsBottomPopupMenu.this.dismiss();
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
	public void showPopupWindow(View parent, NewsModel crentNews) {
		currentNews = crentNews;
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}

	// 监听器
	public interface NewsBottomClickListener {

		// 分享给微信
		public void shareToWeChatClick(NewsModel news);

		// 分享到朋友圈
		public void shareToCircleofFriendsClick(NewsModel news);

		// 分享到微博
		public void shareToWeiboClick(NewsModel news);

		// 分享到qq空间
		public void shareToQzoneClick(NewsModel news);

		// 取消
		public void cancelClick();
	}

	// 回调接口
	public NewsBottomClickListener getListener() {
		return listener;
	}

	// 设置回调接口
	public void setListener(NewsBottomClickListener listener) {
		this.listener = listener;
	}
}

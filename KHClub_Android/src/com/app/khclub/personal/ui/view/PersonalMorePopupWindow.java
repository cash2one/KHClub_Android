package com.app.khclub.personal.ui.view;

import com.app.khclub.R;
import com.app.khclub.base.easeim.activity.AlertDialog;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu.BottomClickListener;
import com.easemob.chat.EMChatManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PersonalMorePopupWindow extends PopupWindow {

	// 布局
	private View conentView;
	// onclick
	private BottomClickListener listener;
	private Context context;
	private String username;

	@SuppressLint("NewApi")
	public PersonalMorePopupWindow(final Context context, boolean isFriend, final String username) {
		this.context = context;
		this.username = username;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater.inflate(R.layout.popupwindow_other_resume_more, null);

		// 点击根布局
		((LinearLayout) conentView.findViewById(R.id.layout_popup_root)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PersonalMorePopupWindow.this.dismiss();
			}
		});
		// 修改备注
		((LinearLayout) conentView.findViewById(R.id.modify_remark)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.editRemarkClick();
				PersonalMorePopupWindow.this.dismiss();
			}
		});
		// 删除好友
		LinearLayout deletelyout = ((LinearLayout) conentView.findViewById(R.id.delete_friends));
		deletelyout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.deleteFriendClick();
				PersonalMorePopupWindow.this.dismiss();
			}
		});
		// 清除记录
		((LinearLayout) conentView.findViewById(R.id.crear_record)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String st5 = context.getResources().getString(R.string.Whether_to_empty_all_chats);
				context.startActivity((new Intent(context, AlertDialog.class).putExtra("titleIsCancel", true)
						.putExtra("msg", st5).putExtra("cancel", true)));
				EMChatManager.getInstance().clearConversation(username);
				PersonalMorePopupWindow.this.dismiss();
				// adapter.refresh();
			}
		});
		// // 分享给好友
		// ((ImageView) conentView.findViewById(R.id.img_share_to_friend))
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.shareToFriendClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		//
		// // 分享给微信好友
		// ((ImageView) conentView.findViewById(R.id.img_share_to_wechat))
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.shareToWeChatClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		//
		// // 分享到朋友圈
		// ((ImageView) conentView
		// .findViewById(R.id.img_share_to_circel_of_friends))
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.shareToCircleofFriendsClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		// // 分享给微博
		// ((ImageView) conentView.findViewById(R.id.img_share_to_weibo))
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.shareToWeiboClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		//
		// // 分享给qq空间
		// ((ImageView) conentView.findViewById(R.id.img_share_to_qzone))
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.shareToQzoneClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		// // 分享给qq好友
		// ((ImageView) conentView.findViewById(R.id.img_share_to_qq_friends))
		// .setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.shareToQQFriendsClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		//
		// 删除好友
		// TextView deleteFriend = (TextView)
		// conentView.findViewById(R.id.txt_delete_friend);
		// deleteFriend.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// listener.deleteFriendClick();
		// PersonalMorePopupWindow.this.dismiss();
		// }
		// });
		// // 编辑备注
//		TextView editRemark = (TextView) conentView.findViewById(R.id.txt_edit_remark);
//		editRemark.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				listener.editRemarkClick();
//				PersonalMorePopupWindow.this.dismiss();
//			}
//		});

		// 取操作
//		((TextView) conentView.findViewById(R.id.txt_cancel)).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				PersonalMorePopupWindow.this.dismiss();
//			}
//		});

		if (!isFriend) {
			// 非好友隐藏相关控件
			// editRemark.setVisibility(View.GONE);
			deletelyout.setVisibility(View.GONE);
		}
		// 设置PopupWindow的View
		this.setContentView(conentView);
		// 设置窗体的尺寸
		this.setWidth(200);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 背景变暗
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.AnimTop);
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


	// 回调接口
	public BottomClickListener getListener() {
		return listener;
	}

	// 设置回调接口
	public void setListener(BottomClickListener listener) {
		this.listener = listener;
	}
}
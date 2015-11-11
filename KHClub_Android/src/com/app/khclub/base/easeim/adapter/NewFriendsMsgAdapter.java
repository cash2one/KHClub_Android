/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.khclub.base.easeim.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.domain.InviteMessage;
import com.app.khclub.base.easeim.domain.InviteMessage.InviteMesageStatus;
import com.app.khclub.base.easeim.utils.UserUtils;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
	private InviteMessgeDao messgeDao;

	public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.row_invite_msg, null);
			holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(R.id.message);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (Button) convertView.findViewById(R.id.user_state);
			holder.groupContainer = (LinearLayout) convertView.findViewById(R.id.ll_group);
			holder.groupname = (TextView) convertView.findViewById(R.id.tv_groupName);
			// holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String str1 = context.getResources().getString(R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(R.string.agree);
		
		String str3 = context.getResources().getString(R.string.Request_to_add_you_as_a_friend);
		String str4 = context.getResources().getString(R.string.Apply_to_the_group_of);
		String str5 = context.getResources().getString(R.string.Has_agreed_to);
		String str6 = context.getResources().getString(R.string.Has_refused_to);
		final InviteMessage msg = getItem(position);
		if (msg != null) {
			if(msg.getGroupId() != null){ // 显示群聊提示
				holder.groupContainer.setVisibility(View.VISIBLE);
				holder.groupname.setText(msg.getGroupName());
			} else{
				holder.groupContainer.setVisibility(View.GONE);
			}
			
			holder.reason.setText(msg.getReason());
//			holder.name.setText(msg.getFrom());
			
			//名字和头像
			UserUtils.setUserNick(msg.getFrom(), holder.name);
			UserUtils.setUserAvatar(context, msg.getFrom(), holder.avator);
			
			// holder.time.setText(DateUtils.getTimestampString(new
			// Date(msg.getTime())));
			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
				holder.status.setVisibility(View.INVISIBLE);
				holder.reason.setText(str1);
			} else if (msg.getStatus() == InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMesageStatus.BEAPPLYED) {
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setEnabled(true);
				holder.status.setBackgroundResource(R.color.main_gold);
				holder.status.setText(str2);
				if(msg.getStatus() == InviteMesageStatus.BEINVITEED){
					if (msg.getReason() == null) {
						// 如果没写理由
						holder.reason.setText(str3);
					}
				}else{ //入群申请
					if (TextUtils.isEmpty(msg.getReason())) {
						holder.reason.setText(str4 + msg.getGroupName());
					}
				}
				// 设置点击事件
				holder.status.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						acceptInvitation(holder.status, msg);
					}
				});
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				holder.status.setText(str5);
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			} else if(msg.getStatus() == InviteMesageStatus.REFUSED){
				holder.status.setText(str6);
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			}

			// 设置用户头像
		}

		return convertView;
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 * @param username
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg) {
		
		final ProgressDialog pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(R.string.Are_agree_with);
		final String str2 = context.getResources().getString(R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(R.string.Agree_with_failure);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		//不是分组
		if(msg.getGroupId() == null) {
			// 参数设置
			RequestParams params = new RequestParams();
			params.addBodyParameter("user_id", UserManager.getInstance().getUser().getUid()+"");
			params.addBodyParameter("target_id", msg.getFrom().replace(KHConst.KH, ""));
			//先处理自己数据库
			HttpManager.post(KHConst.Add_FRIEND, params,
					new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

						@Override
						public void onSuccess(JSONObject jsonResponse, String flag) {
							super.onSuccess(jsonResponse, flag);
							int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
							if (status == KHConst.STATUS_SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										// 调用sdk的同意方法
										try {
											//同意好友请求
											EMChatManager.getInstance().acceptInvitation(msg.getFrom());
											((Activity) context).runOnUiThread(new Runnable() {

												@Override
												public void run() {
													pd.dismiss();
													button.setText(str2);
													msg.setStatus(InviteMesageStatus.AGREED);
													// 更新db
													ContentValues values = new ContentValues();
													values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
													messgeDao.updateMessage(msg.getId(), values);
													button.setBackgroundDrawable(null);
													button.setEnabled(false);
													//进入个人详情
													Intent intent = new Intent(context,OtherPersonalActivity.class);
													intent.putExtra(
															OtherPersonalActivity.INTENT_KEY,
															KHUtils.stringToInt(msg.getFrom().replace(KHConst.KH, "")));
													intent.putExtra(OtherPersonalActivity.INTENT_FRIEND_KEY, true);
													context.startActivity(intent);
													((Activity)context).overridePendingTransition(
															R.anim.push_right_in, R.anim.push_right_out);
												}
											});

										} catch (final Exception e) {
											((Activity) context).runOnUiThread(new Runnable() {
												@Override
												public void run() {
													pd.dismiss();
//													Toast.makeText(context, str3 + e.getMessage(), 1).show();
													Toast.makeText(context, str3, 1).show();												
												}
											});

										}
									}
								}).start();
							}else {
								ToastUtil.show(context, R.string.net_error);
								pd.dismiss();
							}
						}
						@Override
						public void onFailure(HttpException arg0, String arg1,
								String flag) {
							super.onFailure(arg0, arg1, flag);
							ToastUtil.show(context, R.string.net_error);
							pd.dismiss();
						}
					}, null));	
		}else {
			new Thread(new Runnable() {
				public void run() {
					// 调用sdk的同意方法
					try {
						//同意加群申请
						EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								pd.dismiss();
								button.setText(str2);
								msg.setStatus(InviteMesageStatus.AGREED);
								// 更新db
								ContentValues values = new ContentValues();
								values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
								messgeDao.updateMessage(msg.getId(), values);
								button.setBackgroundDrawable(null);
								button.setEnabled(false);
	
							}
						});
					} catch (final Exception e) {
						((Activity) context).runOnUiThread(new Runnable() {
	
							@Override
							public void run() {
								pd.dismiss();
								Toast.makeText(context, str3 + e.getMessage(), 1).show();
							}
						});
	
					}
				}
			}).start();
		}

	}

	private static class ViewHolder {
		ImageView avator;
		TextView name;
		TextView reason;
		Button status;
		LinearLayout groupContainer;
		TextView groupname;
		// TextView time;
	}

}

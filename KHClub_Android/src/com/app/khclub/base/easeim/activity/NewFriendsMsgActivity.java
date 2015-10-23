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
package com.app.khclub.base.easeim.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.app.khclub.R;
import com.app.khclub.base.easeim.Constant;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.adapter.NewFriendsMsgAdapter;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.domain.InviteMessage;
import com.app.khclub.base.ui.view.CustomListViewDialog;
import com.app.khclub.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
	private ListView listView;
	private List<InviteMessage> msgs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friends_msg);

		listView = (ListView) findViewById(R.id.list);
		final InviteMessgeDao dao = new InviteMessgeDao(this);
		msgs = dao.getMessagesList();
		//设置adapter
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs); 
		listView.setAdapter(adapter);
		((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
		//点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InviteMessage msg = msgs.get(position);
				Intent intent = new Intent(NewFriendsMsgActivity.this,OtherPersonalActivity.class);
				intent.putExtra(
						OtherPersonalActivity.INTENT_KEY,
						KHUtils.stringToInt(msg.getFrom().replace(KHConst.KH, "")));
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				//长按删除
				List<String> menuList = new ArrayList<String>();
				menuList.add(getString(R.string.news_push_delete));
				final CustomListViewDialog downDialog = new CustomListViewDialog(NewFriendsMsgActivity.this,menuList);
				downDialog.setClickCallBack(new ClickCallBack() {
					@Override
					public void Onclick(View view, int which) {
						
						InviteMessage msg = msgs.get(position);
						if (msg.getGroupId() != null) {
							dao.deleteMessage(msg.getFrom());							
						}else {
							//先拒绝
							try {
								EMChatManager.getInstance().refuseInvitation(msg.getFrom());
								dao.deleteMessage(msg.getFrom());
							} catch (EaseMobException e) {
								e.printStackTrace();
							}
						}
						
						msgs = dao.getMessagesList();
						NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(NewFriendsMsgActivity.this, 1, msgs); 
						listView.setAdapter(adapter);
						downDialog.cancel();
					}
				});
				downDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
					}
				});
				downDialog.show();
				
				return false;
			}
		});
		
	}

	public void back(View view) {
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	
}

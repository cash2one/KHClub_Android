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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.Constant;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.adapter.ContactAdapter;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper.HXSyncListener;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.db.UserDao;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.easeim.widget.MainPopupMenu;
import com.app.khclub.base.easeim.widget.QRCodePopupMenu;
import com.app.khclub.base.easeim.widget.MainPopupMenu.ClickListener;
import com.app.khclub.base.easeim.widget.Sidebar;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.activity.MainTabActivity;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.message.ui.activity.MipcaCaptureActivity;
import com.app.khclub.message.ui.activity.SearchActivity;
import com.app.khclub.personal.ui.activity.CollectCardActivity;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupInfo;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

/**
 * 联系人列表页
 * 
 */
public class ContactlistFragment extends Fragment {

	private final static int SCANNIN_GREQUEST_CODE = 1;
	public static final String TAG = "ContactlistFragment";
	private ContactAdapter adapter;
	private List<User> contactList;
	private ListView listView;
	private boolean hidden;
	private Sidebar sidebar;
	private InputMethodManager inputMethodManager;
	private List<String> blackList;
	ImageButton clearSearch;
	EditText query;
	HXContactSyncListener contactSyncListener;
	HXBlackListSyncListener blackListSyncListener;
	HXContactInfoSyncListener contactInfoSyncListener;
	View progressBar;
	Handler handler = new Handler();
	private User toBeProcessUser;
	private String toBeProcessUsername;
	// 右上角弹出菜单
	private MainPopupMenu mainMenu;
	// 二维码显示控件
	private QRCodePopupMenu qrImageView;

	class HXContactSyncListener implements HXSDKHelper.HXSyncListener {
		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "on contact list sync success:" + success);
			ContactlistFragment.this.getActivity().runOnUiThread(
					new Runnable() {
						public void run() {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (success) {
										progressBar.setVisibility(View.GONE);
										refresh();
									} else {
										String s1 = getResources()
												.getString(
														R.string.get_failed_please_check);
										Toast.makeText(getActivity(), s1, 1)
												.show();
										progressBar.setVisibility(View.GONE);
									}
								}

							});
						}
					});
		}
	}

	class HXBlackListSyncListener implements HXSyncListener {

		@Override
		public void onSyncSucess(boolean success) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					blackList = EMContactManager.getInstance()
							.getBlackListUsernames();
					refresh();
				}

			});
		}

	};

	class HXContactInfoSyncListener implements HXSDKHelper.HXSyncListener {

		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "on contactinfo list sync success:" + success);
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
					if (success) {
						refresh();
					}
				}
			});
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false))
			return;
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		listView = (ListView) getView().findViewById(R.id.list);
		sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
		sidebar.setListView(listView);

		// 黑名单列表
		blackList = EMContactManager.getInstance().getBlackListUsernames();
		contactList = new ArrayList<User>();
		// 获取设置contactlist
		getContactList();

		// 搜索框
		query = (EditText) getView().findViewById(R.id.query);
		query.setHint(R.string.search);
		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);

				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				query.getText().clear();
				hideSoftKeyboard();
			}
		});

		// 设置adapter
		adapter = new ContactAdapter(getActivity(), R.layout.row_contact,
				contactList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String username = adapter.getItem(position).getUsername();
				if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
					// 进入申请与通知页面
					User user = ((KHHXSDKHelper) HXSDKHelper.getInstance())
							.getContactList()
							.get(Constant.NEW_FRIENDS_USERNAME);
					user.setUnreadMsgCount(0);
					startActivity(new Intent(getActivity(),
							NewFriendsMsgActivity.class));
				} else if (Constant.GROUP_USERNAME.equals(username)) {
					// 进入群聊列表页面
					startActivity(new Intent(getActivity(),
							GroupsActivity.class));
				} else if (Constant.CHAT_ROOM.equals(username)) {
					// 进入聊天室列表页面
					startActivity(new Intent(getActivity(),
							PublicChatRoomsActivity.class));
				} else if (Constant.CHAT_ROBOT.equals(username)) {
					// // 进入Robot列表页面
					// startActivity(new Intent(getActivity(),
					// RobotsActivity.class));
					// 收藏的名片
					Intent intentToCardList = new Intent(getActivity(),
							CollectCardActivity.class);
					startActivity(intentToCardList);
					// 右侧进而动画
					getActivity().overridePendingTransition(
							R.anim.push_right_in, R.anim.push_right_out);
				} else {
					// demo中直接进入聊天页面，实际一般是进入用户详情页
					// startActivity(new Intent(getActivity(),
					// ChatActivity.class)
					// .putExtra("userId", adapter.getItem(position)
					// .getUsername()));
					// 跳转到其他人页面
					Intent intent = new Intent(getActivity(),
							OtherPersonalActivity.class);
					intent.putExtra(
							OtherPersonalActivity.INTENT_KEY,
							KHUtils.stringToInt(adapter.getItem(position)
									.getUsername().replace(KHConst.KH, "")));
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.push_right_in, R.anim.push_right_out);
				}
			}
		});
		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(
								getActivity().getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});

		// 二维码显示控件
		qrImageView = new QRCodePopupMenu(getActivity());
		final ImageView operateMenuView = (ImageView) getView().findViewById(
				R.id.iv_new_contact);
		// 右上角菜单
		operateMenuView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null == mainMenu) {
					mainMenu = new MainPopupMenu(getActivity());
					mainMenu.setListener(new ClickListener() {

						@Override
						public void scanQRcodeClick() {
							// 扫描二维码
							Intent intent = new Intent();
							intent.setClass(getActivity(),MipcaCaptureActivity.class);
							startActivityForResult(intent,SCANNIN_GREQUEST_CODE);
						}

						@Override
						public void searchClick() {
							// 跳转至搜索页面
							Intent intentToSearch = new Intent(getActivity(),
									SearchActivity.class);
							startActivity(intentToSearch);
							// 右侧进而动画
							getActivity()
									.overridePendingTransition(
											R.anim.push_right_in,
											R.anim.push_right_out);
						}

						@Override
						public void createGroupClick() {
							// 创建群聊
							startActivity(new Intent(getActivity(),
									NewGroupActivity.class));
							getActivity()
									.overridePendingTransition(
											R.anim.push_right_in,
											R.anim.push_right_out);
						}

						@Override
						public void userQRShowClick() {
							// TODO 我的二维码
							qrImageView.setQRcode(false);
							qrImageView
									.showPopupWindow((RelativeLayout) getView()
											.findViewById(R.id.title_bar));
						}
					});
				}
				mainMenu.showPopupWindow(operateMenuView);
				// startActivity(new Intent(getActivity(),
				// AddContactActivity.class));
			}
		});
		registerForContextMenu(listView);

		progressBar = (View) getView().findViewById(R.id.progress_bar);

		contactSyncListener = new HXContactSyncListener();
		HXSDKHelper.getInstance().addSyncContactListener(contactSyncListener);

		blackListSyncListener = new HXBlackListSyncListener();
		HXSDKHelper.getInstance().addSyncBlackListListener(
				blackListSyncListener);

		contactInfoSyncListener = new HXContactInfoSyncListener();
		((KHHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager()
				.addSyncContactInfoListener(contactInfoSyncListener);

		if (!HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (((AdapterContextMenuInfo) menuInfo).position > 2) {
			toBeProcessUser = adapter
					.getItem(((AdapterContextMenuInfo) menuInfo).position);
			toBeProcessUsername = toBeProcessUser.getUsername();
			getActivity().getMenuInflater().inflate(
					R.menu.context_contact_list, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			try {
				// 删除此联系人
				deleteContact(toBeProcessUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.add_to_blacklist) {
			moveToBlacklist(toBeProcessUsername);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	/**
	 * 删除联系人
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("target_id", tobeDeleteUser.getUsername()
				.replace(KHConst.KH, ""));

		HttpManager.post(KHConst.DELETE_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							new Thread(new Runnable() {
								public void run() {
									try {
										EMContactManager.getInstance()
												.deleteContact(
														tobeDeleteUser
																.getUsername());
										// 删除db和内存中此用户的数据
										UserDao dao = new UserDao(getActivity());
										dao.deleteContact(tobeDeleteUser
												.getUsername());
										((KHHXSDKHelper) HXSDKHelper
												.getInstance())
												.getContactList().remove(
														tobeDeleteUser
																.getUsername());
										getActivity().runOnUiThread(
												new Runnable() {
													public void run() {
														pd.dismiss();
														adapter.remove(tobeDeleteUser);
														adapter.notifyDataSetChanged();
													}
												});
										// 删除相关的邀请消息
										InviteMessgeDao msgDao = new InviteMessgeDao(
												getActivity());
										msgDao.deleteMessage(toBeProcessUser
												.getUsername());
									} catch (final Exception e) {
										getActivity().runOnUiThread(
												new Runnable() {
													public void run() {
														pd.dismiss();
														Toast.makeText(
																getActivity(),
																st2
																		+ e.getMessage(),
																1).show();
													}
												});
									}
								}
							}).start();
						} else {
							ToastUtil.show(getActivity(), R.string.net_error);
							pd.dismiss();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(getActivity(), R.string.net_error);
						pd.dismiss();
					}
				}, null));

		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
		// // 删除db和内存中此用户的数据
		// UserDao dao = new UserDao(getActivity());
		// dao.deleteContact(tobeDeleteUser.getUsername());
		// ((KHHXSDKHelper)HXSDKHelper.getInstance()).getContactList().remove(tobeDeleteUser.getUsername());
		// getActivity().runOnUiThread(new Runnable() {
		// public void run() {
		// pd.dismiss();
		// adapter.remove(tobeDeleteUser);
		// adapter.notifyDataSetChanged();
		//
		// }
		// });
		// // 删除相关的邀请消息
		// InviteMessgeDao msgDao = new InviteMessgeDao(getActivity());
		// msgDao.deleteMessage(toBeProcessUser.getUsername());
		// } catch (final Exception e) {
		// getActivity().runOnUiThread(new Runnable() {
		// public void run() {
		// pd.dismiss();
		// Toast.makeText(getActivity(), st2 + e.getMessage(), 1).show();
		// }
		// });
		//
		// }
		//
		// }
		// }).start();
	}

	/**
	 * 把user移入到黑名单
	 */
	private void moveToBlacklist(final String username) {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
		final String st2 = getResources().getString(
				R.string.Move_into_blacklist_success);
		final String st3 = getResources().getString(
				R.string.Move_into_blacklist_failure);
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					// 加入到黑名单
					EMContactManager.getInstance().addUserToBlackList(username,
							false);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2, 0).show();
							refresh();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st3, 0).show();
						}
					});
				}
			}
		}).start();

	}

	// 刷新ui
	public void refresh() {
		try {
			// 可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					getContactList();
					adapter.notifyDataSetChanged();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		if (contactSyncListener != null) {
			HXSDKHelper.getInstance().removeSyncContactListener(
					contactSyncListener);
			contactSyncListener = null;
		}

		if (blackListSyncListener != null) {
			HXSDKHelper.getInstance().removeSyncBlackListListener(
					blackListSyncListener);
		}

		if (contactInfoSyncListener != null) {
			((KHHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager()
					.removeSyncContactInfoListener(contactInfoSyncListener);
		}
		super.onDestroy();
	}

	public void showProgressBar(boolean show) {
		if (progressBar != null) {
			if (show) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				progressBar.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 获取联系人列表，并过滤掉黑名单和排序
	 */
	private void getContactList() {
		contactList.clear();
		// 获取本地好友列表
		Map<String, User> users = ((KHHXSDKHelper) HXSDKHelper.getInstance())
				.getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
			if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME)
					&& !entry.getKey().equals(Constant.GROUP_USERNAME)
					&& !entry.getKey().equals(Constant.CHAT_ROOM)
					&& !entry.getKey().equals(Constant.CHAT_ROBOT)
					&& !blackList.contains(entry.getKey()))
				contactList.add(entry.getValue());
		}
		// 排序
		Collections.sort(contactList, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {
				return lhs.getUsername().compareTo(rhs.getUsername());
			}
		});

		if (users.get(Constant.CHAT_ROBOT) != null) {
			contactList.add(0, users.get(Constant.CHAT_ROBOT));
		}
		// 加入"群聊"和"聊天室"
		if (users.get(Constant.CHAT_ROOM) != null)
			contactList.add(0, users.get(Constant.CHAT_ROOM));
		if (users.get(Constant.GROUP_USERNAME) != null)
			contactList.add(0, users.get(Constant.GROUP_USERNAME));

		// 把"申请与通知"添加到首位
		if (users.get(Constant.NEW_FRIENDS_USERNAME) != null)
			contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));

	}

	void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainTabActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainTabActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				Bundle bundle = data.getExtras();
				String resultString = bundle.getString("result");
				
				// 如果是可以用的
				if (resultString.contains(KHConst.KH_GROUP)) {
					String baseUid = resultString.replace(KHConst.KH_GROUP, "");
					// 跳转到群结果页面
					EMGroupInfo	group = new EMGroupInfo(baseUid, "");
					Intent intent = new Intent(getActivity(), GroupSimpleDetailActivity.class).
	                putExtra("groupinfo", group);
					startActivity(intent);
					return;
				}
				
				// 如果是可以用的
				if (resultString.contains(KHConst.KH)) {
					String baseUid = resultString.substring(2);
					int uid = KHUtils.stringToInt(new String(Base64.decode(
							baseUid, Base64.DEFAULT)));
					Intent intent = new Intent(getActivity(), OtherPersonalActivity.class);
					intent.putExtra(OtherPersonalActivity.INTENT_KEY, uid);
					startActivity(intent);
					return;
				}
				
				ToastUtil.show(getActivity(), "'" + resultString + "'");
				break;
		}
	}
}

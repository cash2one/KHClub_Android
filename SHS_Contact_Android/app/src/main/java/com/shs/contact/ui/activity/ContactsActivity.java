package com.shs.contact.ui.activity;

import com.shs.contact.R;

/**
 * Created by wenhai on 2016/2/19.
 */
public class ContactsActivity extends BaseActivityWithTopBar{
    @Override
    public int setLayoutId() {
        return R.layout.activity_contacts;
    }

    @Override
    protected void setUpView() {
        setBarText("联系人");
    }
}

package com.shs.contact.ui.activity;

import android.util.Log;
import android.widget.ListView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.shs.contact.R;
import com.shs.contact.helper.SHSContactAdapter;
import com.shs.contact.helper.SHSContactBaseAdapterHelper;
import com.shs.contact.model.ContactsModel;
import com.shs.contact.utils.ReadContactsUtils;

import java.util.List;

/**
 * Created by wenhai on 2016/2/19.
 */
public class ContactsActivity extends BaseActivityWithTopBar{
   @ViewInject(R.id.contact_list)
    private ListView contactListView;

    @Override
    public int setLayoutId() {
        return R.layout.activity_contacts;
    }

    @Override
    protected void setUpView() {
        setBarText("联系人");
      initListView();
    }

    private void initListView() {
        ReadContactsUtils contactsUtils=ReadContactsUtils.getInstance(this);
        List  data=contactsUtils.getContacts();
        Log.i("wx",data.size()+"");
        SHSContactAdapter contactAdapter=new SHSContactAdapter<ContactsModel>(this,R.layout.activity_contacts_list_item) {
            @Override
            protected void convert(SHSContactBaseAdapterHelper helper, ContactsModel item) {
                   helper.setText(R.id.contact_name,item.getName());
                   helper.setText(R.id.contact_phone, item.getPhoneNum());
            }
        };

        contactAdapter.replaceAll(data);
        contactListView.setAdapter(contactAdapter);

    }

}

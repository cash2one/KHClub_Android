package com.shs.contact.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by wenhai on 2016/2/18.
 */
public class ReadContactsUtils {
    private static final String TAG = "ContactsTest";
    private static Context contexts;
   private static ReadContactsUtils contacts=new ReadContactsUtils();


    public static ReadContactsUtils getInstance(Context context) {
       contexts=context;
        return contacts;
    }

    /**
     * 获取联系人
     */
    public void getContacts() {
        Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 访问所有联系人
        ContentResolver resolver = contexts.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        while (cursor.moveToNext()) {
            int contactsId = cursor.getInt(0);
            StringBuilder sb = new StringBuilder("contactsId=");
            sb.append(contactsId);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data"); //某个联系人下面的所有数据
            Cursor dataCursor = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            while (dataCursor.moveToNext()) {
                String data = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(type)) {    // 如果他的mimetype类型是name
                    sb.append(", name=" + data);
                } else if ("vnd.android.cursor.item/email_v2".equals(type)) { // 如果他的mimetype类型是email
                    sb.append(", email=" + data);
                } else if ("vnd.android.cursor.item/phone_v2".equals(type)) { // 如果他的mimetype类型是phone
                    sb.append(", phone=" + data);
                }
            }
            Log.i(TAG, sb.toString());
        }
    }

    /**
     * 根据来电号码获取联系人名字
     */
    public String getContactsByNumber(String phonenum) {
        String name=null;
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phonenum);
        ContentResolver resolver = contexts.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        if (cursor.moveToFirst()) {
             name = cursor.getString(0);
            Log.i(TAG, name);
        }
        return name;
    }

    /**
     * 添加联系人
     * 数据一个表一个表的添加，每次都调用insert方法
     */
    public void addContacts(String name, String phone) {
        /* 往 raw_contacts 中添加数据，并获取添加的id号*/
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = contexts.getContentResolver();
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(resolver.insert(uri, values));
        Log.i(TAG, contactId + "");
        /* 往 data 中添加数据（要根据前面获取的id号） */
        // 添加姓名
        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", name);
        resolver.insert(uri, values);

        // 添加电话
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");
        values.put("data1", phone);
        resolver.insert(uri, values);

        // 添加Email
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/email_v2");
        values.put("data2", "2");
        values.put("data1", "zhouguoping@qq.com");
        resolver.insert(uri, values);
        Toast.makeText(contexts, "加入成功", Toast.LENGTH_SHORT);
    }

    /**
     * 添加联系人
     * 在同一个事务中完成联系人各项数据的添加
     * 使用ArrayList<ContentProviderOperation>，把每步操作放在它的对象中执行
     */
    public void addContacts2(String name, String phone) {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = contexts.getContentResolver();
        // 第一个参数：内容提供者的主机名
        // 第二个参数：要执行的操作
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();



        // 操作1.添加data表中name字段
        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation operation1 = ContentProviderOperation.newInsert(uri)
                // 第二个参数int previousResult:表示上一个操作的位于operations的第0个索引，
                // 所以能够将上一个操作返回的raw_contact_id作为该方法的参数
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/name")
                .withValue("data2", name)
                .build();

        // 操作2.添加data表中phone字段
        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation operation2 = ContentProviderOperation.newInsert(uri)
                .withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                .withValue("data2", "2")
                .withValue("data1", phone)
                .build();

        // 操作3.添加data表中的Email字段
        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation operation3 = ContentProviderOperation
                .newInsert(uri).withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/email_v2")
                .withValue("data2", "2")
                .withValue("data1", "zhouguoping@qq.com").build();
        operations.add(operation1);
        operations.add(operation2);
        operations.add(operation3);
        try {
            resolver.applyBatch("com.android.contacts", operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.shs.contact.model;

import java.util.List;

/**
 * Created by wenhai on 2016/2/22.
 */

public class ContactsModel {
    private String name;
    private List<String> phoneNums;
    private String phoneNum;
//    //判断是否当前联系人是否只有一个号码
//    private boolean isSimpleNum;

//    public boolean isSimpleNum() {
//        return isSimpleNum;
//    }
//
//    public void setIsSimpleNum(boolean isSimpleNum) {
//        this.isSimpleNum = isSimpleNum;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhoneNums() {
        return phoneNums;
    }

    public void setPhoneNums(List<String> phoneNums) {
        this.phoneNums = phoneNums;
    }

  public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}

package com.app.khclub.base.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class UserModel implements Serializable{

	public static final int SexBoy = 0;
	public static final int SexGirl = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//用户id
	private int uid;
	//用户名
	private String username;
	//用户密码
	private String password;
	//kh_id
	private String kh_id;
	//姓名
	private String name;
	//电话号
	private String phone_num;
	//姓别 0男 1女 2不知道
	private int sex;	
	//学校
	private String company_name;	
	//学校编码
	private String address;	
	//头像地址
	private String head_image;
	//头像缩略图地址
	private String head_sub_image;	
	//职位
	private String job;		
	//生日
	private String birthday;
	//城市
	private String e_mail;
	//签名
	private String signature;
	//背景图片
	private String qr_code;
	//登录token
	private String login_token;
	//融云im_token
	private String im_token;
	
	//Congestion
	//内容注入
	public void setContentWithJson(JSONObject object) {
		
		setUid(object.getIntValue("id"));
		setKh_id(object.getString("kh_id"));
		setHead_image(object.getString("head_image"));
		setHead_sub_image(object.getString("head_sub_image"));
		setName(object.getString("name"));
		setPassword(object.getString("password"));
		setUsername(object.getString("username"));
		setPhone_num(object.getString("phone_num"));
		setCompany_name(object.getString("company_name"));
		setE_mail(object.getString("e_mail"));
		setQr_code(object.getString("qr_code"));
		setSex(object.getIntValue("sex"));
		setBirthday(object.getString("birthday"));
		setAddress(object.getString("address"));
		setSignature(object.getString("signature"));
		setJob(object.getString("job"));
		setLogin_token(object.getString("login_token"));
		setIm_token(object.getString("im_token"));
	}
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getHead_image() {
		return head_image;
	}
	public void setHead_image(String head_image) {
		this.head_image = head_image;
	}
	public String getHead_sub_image() {
		return head_sub_image;
	}
	public void setHead_sub_image(String head_sub_image) {
		this.head_sub_image = head_sub_image;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getLogin_token() {
		return login_token;
	}
	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}
	public String getIm_token() {
		return im_token;
	}
	public void setIm_token(String im_token) {
		this.im_token = im_token;
	}

	public String getKh_id() {
		return kh_id;
	}

	public void setKh_id(String kh_id) {
		this.kh_id = kh_id;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getE_mail() {
		return e_mail;
	}

	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}	
	
	
}

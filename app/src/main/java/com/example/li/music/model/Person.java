package com.example.li.music.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by li on 2016/5/10.
 */
public class Person extends BmobObject{

    private String name;

    private String sex;

    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

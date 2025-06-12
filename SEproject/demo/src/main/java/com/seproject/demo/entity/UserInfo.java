package com.seproject.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userid;
    private String employeeid;
    private String email;
    private int gender; // 0: 男；1: 女 2：未知
    private int age;
    private String phone;
    private String avatar; // 存储头像URL或base64编码


    public UserInfo(int id, int userid, String employeeid, String email, int gender, int age, String phone) {
        this.id = id;
        this.userid = userid;
        this.employeeid = employeeid;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
    }

    public UserInfo() {

    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", userid=" + userid +
                ", employeeid='" + employeeid + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                '}';
    }


}

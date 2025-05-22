package com.seproject.demo.entity;

import jakarta.persistence.*;
import java.util.Date;


@Entity
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int equipid;
    private String equipnum;    // 设备编号
    private String equipname;     // 设备名称
    private String equipkind;    // 设备种类
    private String equipmodel;   // 设备型号
    private Date equipdate;      // 购置日期
    private String company; // 生产厂家

    @Enumerated(EnumType.STRING)  // 存储枚举的字符串值
    private EquipmentStatus equipstatus; // 设备状态

    public Equipment() {}

    @Override
    public String toString() {
        return "Equipment{" +
                "equipid=" + equipid +
                ", equipnum='" + equipnum + '\'' +
                ", equipname='" + equipname + '\'' +
                ", equipkind='" + equipkind + '\'' +
                ", equipmodel='" + equipmodel + '\'' +
                ", equipdate=" + equipdate +
                ", company='" + company + '\'' +
                ", equipstatus=" + equipstatus +
                '}';
    }

    public int getEquipid() {
        return equipid;
    }

    public void setEquipid(int equipid) {
        this.equipid = equipid;
    }

    public String getEquipnum() {
        return equipnum;
    }

    public void setEquipnum(String equipnum) {
        this.equipnum = equipnum;
    }

    public String getEquipname() {
        return equipname;
    }

    public void setEquipname(String equipname) {
        this.equipname = equipname;
    }

    public String getEquipkind() {
        return equipkind;
    }

    public void setEquipkind(String equipkind) {
        this.equipkind = equipkind;
    }

    public String getEquipmodel() {
        return equipmodel;
    }

    public void setEquipmodel(String equipmodel) {
        this.equipmodel = equipmodel;
    }

    public Date getEquipdate() {
        return equipdate;
    }

    public void setEquipdate(Date equipdate) {
        this.equipdate = equipdate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public EquipmentStatus getEquipstatus() {
        return equipstatus;
    }

    public void setEquipstatus(EquipmentStatus equipstatus) {
        this.equipstatus = equipstatus;
    }

    public Equipment(int equipid, String equipnum, String equipname, String equipkind, String equipmodel, Date equipdate, String company, EquipmentStatus equipstatus) {
        this.equipid = equipid;
        this.equipnum = equipnum;
        this.equipname = equipname;
        this.equipkind = equipkind;
        this.equipmodel = equipmodel;
        this.equipdate = equipdate;
        this.company = company;
        this.equipstatus = equipstatus;
    }

}
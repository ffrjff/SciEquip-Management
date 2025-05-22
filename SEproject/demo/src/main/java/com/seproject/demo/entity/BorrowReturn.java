package com.seproject.demo.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class BorrowReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userid;
    private int equipid;
    private Date borrowdate;
    private Date returndate;

    @Enumerated(EnumType.STRING)  // 存储枚举的字符串值
    private BorrowReturnStatus equipstatus; // 设备状态

    public BorrowReturn() {}
    public BorrowReturn(int id, int userid, int equipid, Date borrowdate, Date returndate, BorrowReturnStatus equipstatus) {
        this.id = id;
        this.userid = userid;
        this.equipid = equipid;
        this.borrowdate = borrowdate;
        this.returndate = returndate;
        this.equipstatus = equipstatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getEquipid() {
        return equipid;
    }

    public void setEquipid(int equipid) {
        this.equipid = equipid;
    }

    public Date getBorrowdate() {
        return borrowdate;
    }

    public void setBorrowdate(Date borrowdate) {
        this.borrowdate = borrowdate;
    }

    public Date getReturndate() {
        return returndate;
    }

    public void setReturndate(Date returndate) {
        this.returndate = returndate;
    }

    public BorrowReturnStatus getEquipstatus() {
        return equipstatus;
    }

    public void setEquipstatus(BorrowReturnStatus equipstatus) {
        this.equipstatus = equipstatus;
    }

    @Override
    public String toString() {
        return "BorrowReturn{" +
                "id=" + id +
                ", userid=" + userid +
                ", equipid=" + equipid +
                ", borrowdate=" + borrowdate +
                ", returndate=" + returndate +
                ", equipstatus=" + equipstatus +
                '}';
    }


}

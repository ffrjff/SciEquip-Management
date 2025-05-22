package com.example.seproject.entity;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {
    private String equip_id;
    private String equip_name;
    private String equip_kind;
    private String equip_model;
    private Date equip_date;
    private String equip_producer;
    private long equip_price;
    private EquipmentStatus equip_status;
}

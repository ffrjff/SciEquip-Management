package com.example.seproject.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

import com.example.seproject.entity.EquipmentStatus;

@Service
public interface EquipmentService {
    void addEquipment(String equip_id, String equip_name, 
            String equip_kind, String equip_model, Date equip_date, 
            String equip_producer, long equip_price);
    void deleteEquipment(String equip_id);
    void updataEquipment(String equip_id, String equip_name, 
            String equip_kind, String equip_model, Date equip_date, 
            String equip_producer, long equip_price, EquipmentStatus equip_status);
    void lendEquipment(String equip_id, String user_id);
    void returnEquipment(String equip_id); 
    void findEquipment(String equip_id);
    void findAllEquipment();
}

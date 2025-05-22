package com.example.seproject.service.Impl;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.seproject.entity.EquipmentStatus;
import com.example.seproject.mapper.EquipmentMapper;
import com.example.seproject.service.EquipmentService;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;
    
    @Override
    public void addEquipment(String equip_id, String equip_name, 
            String equip_kind, String equip_model, Date equip_date, 
            String equip_producer, long equip_price) {
        equipmentMapper.addEquipment(equip_id, equip_name, equip_kind, 
                equip_model, equip_date, equip_producer, equip_price);
    }

    @Override
    public void deleteEquipment(String equip_id) {
        equipmentMapper.deleteEquipment(equip_id);
    }

    @Override
    public void updataEquipment(String equip_id, String equip_name, 
            String equip_kind, String equip_model, Date equip_date, 
            String equip_producer, long equip_price, EquipmentStatus equip_status) {
        equipmentMapper.updateEquipment(equip_id, equip_name, equip_kind, 
                equip_model, equip_date, equip_producer, equip_price, equip_status);
    }

    @Override
    public void lendEquipment(String equip_id, String user_id) {
        equipmentMapper.lendEquipment(equip_id, user_id);
    }

    @Override
    public void returnEquipment(String equip_id) {
        equipmentMapper.returnEquipment(equip_id);
    }
    @Override
    public void findEquipment(String equip_id) {
        equipmentMapper.findEquipment(equip_id);
    }
    @Override
    public void findAllEquipment() {
        equipmentMapper.findAllEquipment();
    }

    
}

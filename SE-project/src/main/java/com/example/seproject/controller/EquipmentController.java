package com.example.seproject.controller;

import com.example.seproject.entity.EquipmentStatus;
import com.example.seproject.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping("/add")
    public void addEquipment(@RequestParam String equip_id,
                             @RequestParam String equip_name,
                             @RequestParam String equip_kind,
                             @RequestParam String equip_model,
                             @RequestParam Date equip_date,
                             @RequestParam String equip_producer,
                             @RequestParam long equip_price) {
        equipmentService.addEquipment(equip_id, equip_name, equip_kind, equip_model, equip_date, equip_producer, equip_price);
    }

    @DeleteMapping("/delete")
    public void deleteEquipment(@RequestParam String equip_id) {
        equipmentService.deleteEquipment(equip_id);
    }

    @PutMapping("/update")
    public void updateEquipment(@RequestParam String equip_id,
                                @RequestParam String equip_name,
                                @RequestParam String equip_kind,
                                @RequestParam String equip_model,
                                @RequestParam Date equip_date,
                                @RequestParam String equip_producer,
                                @RequestParam long equip_price,
                                @RequestParam EquipmentStatus equip_status) {
        equipmentService.updataEquipment(equip_id, equip_name, equip_kind, equip_model, equip_date, equip_producer, equip_price, equip_status);
    }

    @PutMapping("/lend")
    public void lendEquipment(@RequestParam String equip_id,
                              @RequestParam String user_id) {
        equipmentService.lendEquipment(equip_id, user_id);
    }

    @PutMapping("/return")
    public void returnEquipment(@RequestParam String equip_id) {
        equipmentService.returnEquipment(equip_id);
    }

    @GetMapping("/find")
    public void findEquipment(@RequestParam String equip_id) {
        equipmentService.findEquipment(equip_id);
    }

    @GetMapping("/all")
    public void findAllEquipment() {
        equipmentService.findAllEquipment();
    }
}

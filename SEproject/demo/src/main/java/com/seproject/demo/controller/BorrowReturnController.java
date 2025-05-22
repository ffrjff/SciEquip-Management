package com.seproject.demo.controller;

import com.seproject.demo.entity.BorrowReturn;
import com.seproject.demo.entity.BorrowReturnStatus;
import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import com.seproject.demo.service.BorrowReturnService;
import com.seproject.demo.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/borrow-return")
public class BorrowReturnController {

    private final BorrowReturnService borrowReturnService;
    private final EquipmentService equipmentService;

    @Autowired
    public BorrowReturnController(BorrowReturnService borrowReturnService, EquipmentService equipmentService) {
        this.borrowReturnService = borrowReturnService;
        this.equipmentService = equipmentService;
    }

    // 申请借用设备
    @PostMapping("/borrow")
    public ResponseEntity<BorrowReturn> borrowEquipment(@RequestParam int userId, @RequestParam int equipId) {
        Equipment equipment = equipmentService.findById(equipId);
        if (equipment == null) {
            return ResponseEntity.notFound().build();
        }

        if (equipment.getEquipstatus() != EquipmentStatus.AVAILABLE) {
            return ResponseEntity.badRequest().build();
        }

        // 创建借用记录
        BorrowReturn borrowReturn = new BorrowReturn();
        borrowReturn.setUserid(userId);
        borrowReturn.setEquipid(equipId);
        borrowReturn.setBorrowdate(new Date());
        borrowReturn.setEquipstatus(BorrowReturnStatus.PENDING);

        // 更新设备状态
        equipment.setEquipstatus(EquipmentStatus.PENDING);
        equipmentService.save(equipment);

        return ResponseEntity.ok(borrowReturnService.save(borrowReturn));
    }

    // 归还设备
    @PostMapping("/return")
    public ResponseEntity<BorrowReturn> returnEquipment(@RequestParam int recordId) {
        BorrowReturn borrowReturn = borrowReturnService.findById(recordId);
        if (borrowReturn == null) {
            return ResponseEntity.notFound().build();
        }

        // 更新借用记录
        borrowReturn.setReturndate(new Date());
        borrowReturn.setEquipstatus(BorrowReturnStatus.RETURNED);

        // 更新设备状态
        Equipment equipment = equipmentService.findById(borrowReturn.getEquipid());
        if (equipment != null) {
            equipment.setEquipstatus(EquipmentStatus.AVAILABLE);
            equipmentService.save(equipment);
        }

        return ResponseEntity.ok(borrowReturnService.save(borrowReturn));
    }

    // 申请延期
    @PostMapping("/extend")
    public ResponseEntity<BorrowReturn> extendBorrow(@RequestParam int recordId, @RequestParam int userId) {
        // 先处理原记录的归还
        ResponseEntity<BorrowReturn> response = returnEquipment(recordId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        // 然后创建新的借用记录
        BorrowReturn originalRecord = response.getBody();
        return borrowEquipment(userId, originalRecord.getEquipid());
    }

    // 获取用户的待归还设备
    @GetMapping("/to-return")
    public List<BorrowReturn> getToReturnRecords(@RequestParam int userId) {
        return borrowReturnService.findByUseridAndEquipstatus(userId, BorrowReturnStatus.BORROWED);
    }

    // 获取用户的所有借还记录
    @GetMapping("/user-records")
    public List<BorrowReturn> getUserRecords(@RequestParam int userId) {
        return borrowReturnService.findByUserid(userId);
    }

    // 获取设备的所有借还记录
    @GetMapping("/equipment-records")
    public List<BorrowReturn> getEquipmentRecords(@RequestParam int equipId) {
        return borrowReturnService.findByEquipid(equipId);
    }







    // 获取当前用户设备的待审批请求
    @GetMapping("/pending-requests")
    public List<BorrowReturn> getPendingRequests(@RequestParam String company) {
        // 1. 查询company拥有的所有设备
        List<Equipment> companyEquipments = equipmentService.findByCompany(company);
        List<Integer> equipIds = companyEquipments.stream()
                .map(Equipment::getEquipid)
                .collect(Collectors.toList());

        // 2. 查询这些设备的待审批记录
        return borrowReturnService.findByEquipidInAndEquipstatus(equipIds, BorrowReturnStatus.PENDING);
    }

    // 同意借用申请
    @PostMapping("/approve/{recordId}")
    public ResponseEntity<BorrowReturn> approveBorrow(@PathVariable int recordId) {
        BorrowReturn record = borrowReturnService.findById(recordId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        // 更新借用记录状态
        record.setEquipstatus(BorrowReturnStatus.BORROWED);
        borrowReturnService.save(record);

        // 更新设备状态
        Equipment equipment = equipmentService.findById(record.getEquipid());
        if (equipment != null) {
            equipment.setEquipstatus(EquipmentStatus.IN_USE);
            equipmentService.save(equipment);
        }

        return ResponseEntity.ok(record);
    }

    // 拒绝借用申请
    @PostMapping("/reject/{recordId}")
    public ResponseEntity<BorrowReturn> rejectBorrow(@PathVariable int recordId) {
        BorrowReturn record = borrowReturnService.findById(recordId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        // 更新借用记录状态
        record.setEquipstatus(BorrowReturnStatus.REFUSED);
        borrowReturnService.save(record);

        // 更新设备状态
        Equipment equipment = equipmentService.findById(record.getEquipid());
        if (equipment != null) {
            equipment.setEquipstatus(EquipmentStatus.AVAILABLE);
            equipmentService.save(equipment);
        }

        return ResponseEntity.ok(record);
    }
    
}
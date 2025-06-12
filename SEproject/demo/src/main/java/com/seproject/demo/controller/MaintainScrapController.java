package com.seproject.demo.controller;

import com.seproject.demo.entity.MaintainScrap;
import com.seproject.demo.service.MaintainScrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/maintain-scrap")
public class MaintainScrapController {

    @Autowired
    private MaintainScrapService maintainScrapService;

    @GetMapping
    public Page<MaintainScrap> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return maintainScrapService.findAll(page, size);
    }

    @GetMapping("/user-records")
    public Page<MaintainScrap> getUserRecords(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return maintainScrapService.findByUserCompany(userId, page, size);
    }

    @GetMapping("/by-equipment/{equipid}")
    public List<MaintainScrap> getRecordsByEquipment(@PathVariable int equipid) {
        return maintainScrapService.findByEquipid(equipid);
    }

    @PostMapping
    public MaintainScrap createRecord(@RequestBody MaintainScrap record) {
        return maintainScrapService.save(record);
    }
}
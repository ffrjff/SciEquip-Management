package com.seproject.demo.service;

import com.seproject.demo.entity.MaintainScrap;
import com.seproject.demo.repository.MaintainScrapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MaintainScrapService {

    @Autowired
    private MaintainScrapRepository repository;

    public List<MaintainScrap> findByEquipid(int equipid) {
        return repository.findByEquipid(equipid);
    }

    public MaintainScrap save(MaintainScrap record) {
        record.setDate(new Date());
        return repository.save(record);
    }

    public Page<MaintainScrap> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Page<MaintainScrap> findByUserCompany(String userId, int page, int size) {
        return repository.findByCompany(userId, PageRequest.of(page, size));
    }
}
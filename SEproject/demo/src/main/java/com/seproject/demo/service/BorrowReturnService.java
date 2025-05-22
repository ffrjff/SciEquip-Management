package com.seproject.demo.service;

import com.seproject.demo.entity.BorrowReturn;
import com.seproject.demo.entity.BorrowReturnStatus;
import com.seproject.demo.repository.BorrowReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowReturnService {

    private final BorrowReturnRepository borrowReturnRepository;

    @Autowired
    public BorrowReturnService(BorrowReturnRepository borrowReturnRepository) {
        this.borrowReturnRepository = borrowReturnRepository;
    }

    public BorrowReturn save(BorrowReturn borrowReturn) {
        return borrowReturnRepository.save(borrowReturn);
    }

    public BorrowReturn findById(int id) {
        return borrowReturnRepository.findById(id).orElse(null);
    }

    public List<BorrowReturn> findByUserid(int userid) {
        return borrowReturnRepository.findByUserid(userid);
    }

    public List<BorrowReturn> findByUseridAndEquipstatus(int userid, BorrowReturnStatus equipstatus) {
        return borrowReturnRepository.findByUseridAndEquipstatus(userid, equipstatus);
    }

    public List<BorrowReturn> findByEquipid(int equipid) {
        return borrowReturnRepository.findByEquipid(equipid);
    }

    // 根据设备ID列表和状态查询记录
    public List<BorrowReturn> findByEquipidInAndEquipstatus(List<Integer> equipIds, BorrowReturnStatus status) {
        return borrowReturnRepository.findByEquipidInAndEquipstatus(equipIds, status);
    }

}
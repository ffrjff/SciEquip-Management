package com.seproject.demo.repository;

import com.seproject.demo.entity.BorrowReturn;
import com.seproject.demo.entity.BorrowReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowReturnRepository extends JpaRepository<BorrowReturn, Integer> {
    List<BorrowReturn> findByUserid(int userid);
    List<BorrowReturn> findByUseridAndEquipstatus(int userid, BorrowReturnStatus equipstatus);
    List<BorrowReturn> findByEquipid(int equipid);
    // 在接口中添加
    List<BorrowReturn> findByEquipidInAndEquipstatus(List<Integer> equipIds, BorrowReturnStatus status);
}
package com.seproject.demo.repository;

import com.seproject.demo.entity.MaintainScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintainScrapRepository extends JpaRepository<MaintainScrap, Integer> {
    List<MaintainScrap> findByEquipid(int equipid);
    Page<MaintainScrap> findAll(Pageable pageable);
}
package com.seproject.demo.repository;

import com.seproject.demo.entity.MaintainScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MaintainScrapRepository extends JpaRepository<MaintainScrap, Integer> {
    List<MaintainScrap> findByEquipid(int equipid);
    Page<MaintainScrap> findAll(Pageable pageable);

    @Query("SELECT ms FROM MaintainScrap ms JOIN Equipment e ON ms.equipid = e.equipid WHERE e.company = :company")
    Page<MaintainScrap> findByCompany(@Param("company") String company, Pageable pageable);

}
package com.seproject.demo.repository;

import com.seproject.demo.entity.Equipment;
import com.seproject.demo.entity.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer>, JpaSpecificationExecutor<Equipment> {

    // 方式1: 使用JPA方法命名约定
    List<Equipment> findByEquipnameContainingAndEquipkindAndEquipstatus(
            String equipname, String equipkind, EquipmentStatus equipstatus);

    // 方式2: 使用自定义查询
    @Query("SELECT e FROM Equipment e WHERE " +
            "(:equipname IS NULL OR e.equipname LIKE %:equipname%) AND " +
            "(:equipkind IS NULL OR e.equipkind = :equipkind) AND " +
            "(:equipstatus IS NULL OR e.equipstatus = :equipstatus) AND " +
            "(:company IS NULL OR e.company = :company)")
    List<Equipment> findEquipmentByCriteria(
            @Param("equipname") String equipname,
            @Param("equipkind") String equipkind,
            @Param("equipstatus") EquipmentStatus equipstatus,
            @Param("company") String company);

    long countByEquipstatus(EquipmentStatus status);

    List<Equipment> findByCompany(String company);
    List<Equipment> findByEquipnameContaining(String name);
    List<Equipment> findByEquipkind(String type);
    List<Equipment> findByEquipstatus(String status);
}
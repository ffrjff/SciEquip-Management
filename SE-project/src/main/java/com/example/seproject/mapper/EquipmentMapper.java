package com.example.seproject.mapper;

import com.example.seproject.entity.EquipmentStatus;
import org.apache.ibatis.annotations.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface EquipmentMapper {

    @Insert("INSERT INTO equipment (equip_id, equip_name, equip_kind, equip_model, equip_date, equip_producer, equip_price, equip_status) " +
        "VALUES (#{equip_id}, #{equip_name}, #{equip_kind}, #{equip_model}, #{equip_date}, #{equip_producer}, #{equip_price}, 'available')")
    void addEquipment(@Param("equip_id") String equip_id,
                  @Param("equip_name") String equip_name,
                  @Param("equip_kind") String equip_kind,
                  @Param("equip_model") String equip_model,
                  @Param("equip_date") Date equip_date,
                  @Param("equip_producer") String equip_producer,
                  @Param("equip_price") long equip_price);


    @Delete("DELETE FROM equipment WHERE equip_id = #{equip_id}")
    void deleteEquipment(@Param("equip_id") String equip_id);

    @Update("UPDATE equipment SET equip_name = #{equip_name}, equip_kind = #{equip_kind}, equip_model = #{equip_model}, " +
            "equip_date = #{equip_date}, equip_producer = #{equip_producer}, equip_price = #{equip_price}, equip_status = #{equip_status} " +
            "WHERE equip_id = #{equip_id}")
    void updateEquipment(@Param("equip_id") String equip_id,
                         @Param("equip_name") String equip_name,
                         @Param("equip_kind") String equip_kind,
                         @Param("equip_model") String equip_model,
                         @Param("equip_date") Date equip_date,
                         @Param("equip_producer") String equip_producer,
                         @Param("equip_price") long equip_price,
                         @Param("equip_status") EquipmentStatus equip_status);

    @Update("UPDATE equipment SET equip_status = '借出', current_user = #{user_id} WHERE equip_id = #{equip_id}")
    void lendEquipment(@Param("equip_id") String equip_id,
                       @Param("user_id") String user_id);

    @Update("UPDATE equipment SET equip_status = '空闲', current_user = NULL WHERE equip_id = #{equip_id}")
    void returnEquipment(@Param("equip_id") String equip_id);

    @Select("SELECT * FROM equipment WHERE equip_id = #{equip_id}")
    Map<String, Object> findEquipment(@Param("equip_id") String equip_id);

    @Select("SELECT * FROM equipment")
    List<Map<String, Object>> findAllEquipment();
}

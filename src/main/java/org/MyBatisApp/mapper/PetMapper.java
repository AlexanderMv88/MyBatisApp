package org.MyBatisApp.mapper;

import org.MyBatisApp.entity.Pet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PetMapper {
    @Select("select * from pet where name = #{n}")
    Pet findOneByName(@Param("n") String name);

    @Select("select * from pet where id = #{id}")
    Pet findOneById(@Param("id") long id);

    @Select("select count(id) from Pet")
    Integer count();

    @Insert("Insert into Pet (name, owner_id) values (#{name}, #{owner_id})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void create(Pet pet);

    @Select("select * from pet")
    List<Pet> findAll();

    @Update("UPDATE pet SET name=#{name}, owner_id=#{owner_id} WHERE id =#{id}")
    void save(Pet pet);

    @Delete("DELETE FROM pet WHERE id =#{id}")
    void delete(Pet pet);

    @Select({"<script>",
            "SELECT *",
            "FROM Pet",
            "WHERE name IN",
            "<foreach item='item' index='index' collection='array'",
            "open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<Pet> findByNames(String... names);

    @Delete({"<script>",
            "DELETE",
            "FROM Pet",
            "WHERE id IN",
            "<foreach item='item' index='index' collection='list'",
            "open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    void deleteAllWhereIdIn(List<Long> ids);

    @Delete("DELETE FROM pet")
    void deleteAll();
}
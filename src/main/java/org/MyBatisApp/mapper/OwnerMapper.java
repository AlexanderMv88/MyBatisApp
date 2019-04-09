package org.MyBatisApp.mapper;

import org.MyBatisApp.entity.Owner;
import org.MyBatisApp.entity.Pet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OwnerMapper {
    @Select("select * from owner where firstName = #{n}")
    Owner findOneByName(@Param("n") String firstName);


    @Insert("Insert into Owner (firstName) values (#{firstName})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    //@SelectKey(keyProperty = "id", resultType = long.class, before = false, statement = "CALL SCOPE_IDENTITY()")
    void create(Owner owner);


    @Select("select * from owner")
    List<Owner> findAll();


    @Update("UPDATE owner SET firstName=#{firstName} WHERE id =#{id}")
    void save(Owner owner);

    @Delete("DELETE FROM owner WHERE id =#{id}")
    void delete(Owner owner);

    @Select("select count(id) from Owner")
    Integer count();

    @Select({"<script>",
            "SELECT *",
            "FROM Owner",
            "WHERE firstName IN",
            "<foreach item='item' index='index' collection='array'",
            "open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    List<Owner> findByNames(String... firstNames);

    @Delete({"<script>",
            "DELETE",
            "FROM Owner",
            "WHERE id IN",
            "<foreach item='item' index='index' collection='list'",
            "open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    void deleteAllWhereIdIn(List<Long> ids);

    @Delete("DELETE FROM owner")
    void deleteAll();




    @Select("SELECT * FROM Owner")
    @Results(value = {
            @Result(property="id", column = "id"),
            @Result(property="firstName", column = "firstName"),
            @Result(property="pets", column="id", javaType= List.class, many=@Many(select="selectPets"))
    })
    List<Owner> findAllWithPets();

    @Select("SELECT * FROM Owner where firstName = #{p}")
    @Results(value = {
            @Result(property="id", column = "id"),
            @Result(property="firstName", column = "firstName"),
            @Result(property="pets", column="id", javaType= List.class, many=@Many(select="selectPets"))
    })
    List<Owner> findByNameWithPets(@Param("p") String firstName);
    /*public List<Team> getAllTeams();*/

    @Select("SELECT * FROM Pet WHERE owner_id = #{owner_id}")
    @Results(value={
            @Result(property="id", column ="id" ),
            @Result(property="name", column = "name")
    })
    List<Pet> selectPets(Long owner_id);



    @Select("select * from owner where id = #{id}")
    Owner findOneById(@Param("id") long id);
}

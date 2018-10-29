package com.dd.activiti.admin.repo;

import com.dd.activiti.admin.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group,String> {


    @Query(value ="select g from Group g where g.id=:id")
    Group findGroupById(@Param("id") String id);


    @Query(value ="select g from Group g where g.type=:type")
    List<Group> findGroupByType(@Param("type") Integer type);

    @Query(value ="select g from Group g where g.id in (select u.groupId from MemberShip u where u.userId=:userId)")
    List<Group> findGroupByUserId(@Param("userId") String userId);

}

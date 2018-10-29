package com.dd.activiti.admin.repo;

import com.dd.activiti.admin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    @Query(nativeQuery = true, name = "User.findByIdAndPwd")
    User findByIdAndPwd(@Param("id") String id, @Param("pwd") String pwd);

    @Query(value ="select g from User g where g.processId=:processId")
    User findByProcessId(@Param("processId") String processId);

}

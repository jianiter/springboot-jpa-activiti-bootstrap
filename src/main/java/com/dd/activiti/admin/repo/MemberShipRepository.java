package com.dd.activiti.admin.repo;

import com.dd.activiti.admin.model.MemberShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberShipRepository extends JpaRepository<MemberShip,String> {


//    @Query(value ="select m from MemberShip m where m.userId=:userId")
//@Query(nativeQuery = true, name = "MemberShip.findByUserId")
@Query(value ="select g from MemberShip g where g.userId=:userId")
MemberShip findByUserId(@Param("userId") String userId);



}

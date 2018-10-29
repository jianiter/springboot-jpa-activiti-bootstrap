package com.dd.activiti.admin.service.service;

import com.dd.activiti.admin.exception.UserNotFoundException;
import com.dd.activiti.admin.model.Group;
import com.dd.activiti.admin.model.MemberShip;
import com.dd.activiti.admin.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {

    public Page<User> findUserNoCriteria(Pageable pageable);

    public Page<Group> findGroupNoCriteria(Pageable pageable);

    public MemberShip checkLogin(String id, String pwd) throws UserNotFoundException;

    public User save(User user);

    public Group saveGroup(Group group);

    public User updatePwd(String id,String pwd);

    public User updateState(User user);

    public void delete(String id);

    public void deleteGroup(String id);

    public User findUserById(String userId);

    public User findByProcessId(String taskId);

    public Group findGroupByUserId(String userId);

    public List<Group> findGroupByType(Integer type);

}


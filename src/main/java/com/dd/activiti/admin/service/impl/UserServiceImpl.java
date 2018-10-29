package com.dd.activiti.admin.service.impl;

import com.dd.activiti.admin.exception.UserIdMismatchException;
import com.dd.activiti.admin.exception.UserNotFoundException;
import com.dd.activiti.admin.model.Group;
import com.dd.activiti.admin.model.MemberShip;
import com.dd.activiti.admin.model.User;
import com.dd.activiti.admin.repo.GroupRepository;
import com.dd.activiti.admin.repo.MemberShipRepository;
import com.dd.activiti.admin.repo.UserRepository;
import com.dd.activiti.admin.service.service.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service(value = "userService") // activiti注解时引用
@Transactional
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MemberShipRepository memberShipRepository;

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public  Page<User> findUserNoCriteria(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<User> userList= userPage.getContent();
        for(User user:userList){
           List<Group> groupList = groupRepository.findGroupByUserId(user.getId());
           if(groupList!=null&&groupList.size()>0){
               user.setGroups(groupList.stream().map(Group::getName).collect(Collectors.toList()).toString());
           }
        }
        return userPage;
    }


    @Override
    public MemberShip checkLogin(String id, String pwd) throws UserNotFoundException {
        MemberShip memberShip = memberShipRepository.findByUserId(id);
        User user = userRepository.findByIdAndPwd(id,pwd);
        if(user==null){
            throw  new UserNotFoundException();
        }
        Group group =groupRepository.findGroupById(memberShip.getGroupId());
        memberShip.setUser(user);
        memberShip.setGroup(group);
        return memberShip;
    }


    @Override
    public Group findGroupByUserId(String userId) {
        return groupRepository.findGroupById(userId);
    }

    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public List<Group> findGroupByType(Integer type) {
        return groupRepository.findGroupByType(type);
    }

    @Override
    public User save(User user) throws UserIdMismatchException {
        // 新增,验证是否重复
        if(user.getPwd()!=null){
            return userRepository.saveAndFlush(user);
        }else{
            // 更新
           User oldUser = userRepository.findById(user.getId()).get();
           oldUser.setFirst(user.getFirst()).setLast(user.getLast()).setEmail(user.getEmail());
            return userRepository.saveAndFlush(oldUser);
        }
    }

    @Override
    public Group saveGroup(Group group) {
        return groupRepository.saveAndFlush(group);
    }

    @Override
    public User updatePwd(String id, String pwd) {
        User user = userRepository.findById(id).get();
        user.setPwd(pwd);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User updateState(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findByProcessId(String taskId) {
        return userRepository.findByProcessId(taskId);
    }

    @Override
    public Page<Group> findGroupNoCriteria(Pageable pageable) {
        Page<Group> groupPage = groupRepository.findAll(pageable);
        return groupPage;
    }

    @Override
    public void deleteGroup(String id) {
        groupRepository.deleteById(id);
    }
}

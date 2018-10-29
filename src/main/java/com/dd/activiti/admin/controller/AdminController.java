package com.dd.activiti.admin.controller;

import com.dd.activiti.admin.common.Config;
import com.dd.activiti.admin.common.JsonResult;
import com.dd.activiti.admin.common.ResponseCode;
import com.dd.activiti.admin.exception.UserNotFoundException;
import com.dd.activiti.admin.model.Group;
import com.dd.activiti.admin.model.MemberShip;
import com.dd.activiti.admin.model.User;
import com.dd.activiti.admin.service.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value="/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "用户管理")
public class AdminController extends BaseController{

    @Autowired
    private IUserService userService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @ApiOperation(value ="获取用户列表",notes = "分页展示用户基础信息")
    @GetMapping(value ="/list/page/{page}/size/{size}")
    public Page<User> list(@ApiParam(value = "页数",required = true)
            @PathVariable Integer page,@ApiParam(value = "条数",required = true)
                           @PathVariable Integer size){
        Pageable pageable = new PageRequest(page-1, size);
        Page<User> userPage = userService.findUserNoCriteria(pageable);
        return userPage;
    }
    @ApiOperation(value = "用户登录验证",notes = "输入账号、密码验证用户并获取信息")
    @RequestMapping(value ="/login/id/{id}/pwd/{pwd}",method=RequestMethod.GET)
    public MemberShip checkLogin(@ApiParam(value = "编号",required = true)
            @PathVariable String id,@ApiParam(value = "密码",required = true)
                                 @PathVariable String pwd, HttpSession session) {
        MemberShip user =null;
        try {
            user = userService.checkLogin(id, pwd);
            // 验证通过,存入session
            if(user!=null){
                session.setAttribute(Config.User_Session_Name,user);
                session.setAttribute(Config.User_Session_Id,user.getUser().getId());
                session.setAttribute(Config.User_Session_Group,user.getGroup().getId());
            }
        }catch (UserNotFoundException e){
            user = new MemberShip();
            log.error(e.fillInStackTrace());
        }

        return user;
    }

    @ApiOperation(value = "重置用户密码",notes = "输入新的密码替换")
    @GetMapping(value ="/reset/pwd/{pwd}")
    public String resetPwd(@ApiParam(value = "密码",required = true)
            @PathVariable String pwd, HttpSession session){
        String result = "success";
        String id = getCurrentUserId(session);
        User user = userService.updatePwd(id,pwd);
        if(user==null){
            result = "error";
        }
        return result;
    }

    @ApiOperation(value = "保存用户信息",notes = "添加、修改用户基础信息")
    @ApiImplicitParam(name="user",value = "用户对象实体",required = true,dataType = "User")
    @PostMapping(value ="/save")
    public JsonResult create(@RequestBody User user) {
        JsonResult result = new JsonResult();
        try {
            user = userService.save(user);
            result.setCode(ResponseCode.SUCCESS.getValue());
        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "申请添加一个用户",notes = "提交一个添加用户信息的申请，走activiti流程第一步")
    @ApiImplicitParam(name="user",value = "用户对象实体",required = true,dataType = "User")
    @Transactional
    @PostMapping(value ="/add/request")
    public JsonResult addUserRequest(@RequestBody User user, HttpSession session) {
        JsonResult result = new JsonResult();
        try {
            //取得角色用户登入的session对象
            MemberShip currentMemberShip=(MemberShip) session.getAttribute(Config.User_Session_Name);
            //取出用户，角色信息
            User currentUser=currentMemberShip.getUser();
            Group currentGroup=currentMemberShip.getGroup();

            if(currentUser.getState()!=1){
                result.setCode(ResponseCode.NOALLOW.getValue()).setErrMsg("无权限操作");
            }else{
                Map<String,Object> variables=new HashMap<String,Object>();
                variables.put("userId",user.getId());
                // 启动流程
                String processId= runtimeService.startProcessInstanceByKey(Config.ACTIVITI_USER_ROLE_PROCESS,variables).getId();
                log.info("***************启动一个添加用户流程完成***************" + processId);

                String auditMsg = "管理员";
                if(currentGroup.getName().equals("管理员")){
                    auditMsg="无审批";
                    user.setState(1);
                    //修改状态
                    user.setAuditState("无需审核");
                }else{
                    // 判断当前用户级别，让其上级审批，无符合则管理员
                    Integer currentType = currentGroup.getType();
                    List<Group> groupList = userService.findGroupByType(currentType+1);
                    if(groupList!=null&&groupList.size()>0){
                        String name = groupList.get(0).getName();
                        if(name.equals("总监")||name.equals("经理")){
                            auditMsg=name;
                        }
                    }
                    user.setState(0);
                    //修改状态
                    user.setAuditState("审核中");

                }


                // 处理新增用户任务为完成

                log.info("***************提交添加用户申请开始***************");
                List<Task> tasks = taskService.createTaskQuery().taskAssignee(user.getId()).list();
                log.info("待处理任务数:"+tasks.size());
                for (Task task : tasks) {
                        log.info(user.getId() + "的任务taskname:" + task.getName() + ",id:" + task.getId());
                        taskService.setVariable(task.getId(), "msg", auditMsg);//设置审批类型
                        taskService.complete(task.getId());//完成任务
                        log.info("需要做的下一步审批:" + auditMsg);
                }
                log.info("***************提交添加用户申请完成***************");


                user.setProcessId(processId);

                user= userService.save(user);
                if(user!=null){
                    result.setCode(ResponseCode.SUCCESS.getValue());
                }else{
                    result.setCode(ResponseCode.ERROR.getValue()).setErrMsg("添加用户失败");
                }
            }
        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "删除一个用户",notes = "根据编号删除用户信息")
    @DeleteMapping(value ="/delete/id/{id}")
    public JsonResult delete(@ApiParam(value = "编号",required = true)
            @PathVariable String id) {

        JsonResult result = new JsonResult();
        try {
            userService.delete(id);
            result.setCode(ResponseCode.SUCCESS.getValue());
        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }


}

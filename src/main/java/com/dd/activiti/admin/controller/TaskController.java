package com.dd.activiti.admin.controller;

import com.dd.activiti.admin.common.Config;
import com.dd.activiti.admin.common.JsonResult;
import com.dd.activiti.admin.common.ResponseCode;
import com.dd.activiti.admin.model.Group;
import com.dd.activiti.admin.model.MemberShip;
import com.dd.activiti.admin.model.User;
import com.dd.activiti.admin.request.AuditAddUserRequest;
import com.dd.activiti.admin.service.service.IUserService;
import com.dd.activiti.admin.util.JsonUtil;
import com.dd.activiti.admin.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Log4j2
@RestController
@RequestMapping(value = "/api/task", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "流程任务管理")
public class TaskController extends BaseController{

    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private IUserService userService;
    @Autowired
    private HistoryService historyService;

    @ApiOperation(value ="获取流程待办任务列表",notes = "分页展示流程待办任务基础信息")
    @GetMapping(value ="/todo/list/page/{page}/size/{size}")
    public String todoList(@ApiParam(value = "页数",required = true)
            @PathVariable Integer page,@ApiParam(value = "条数",required = true)
                           @PathVariable Integer size, HttpSession session,HttpServletResponse response){
        JsonResult result = new JsonResult();
        // 获取当前登录者id
        //取得角色用户登入的session对象
        MemberShip currentMemberShip=(MemberShip) session.getAttribute(Config.User_Session_Name);
        //取出用户，角色信息
        Group currentGroup=currentMemberShip.getGroup();

        TaskQuery query = taskService.createTaskQuery().taskCandidateGroup(currentGroup.getId());
        Long count =query.count();
        List<Task> list =query.listPage(page-1,size);

        Long totalPage =Math.abs(count/size);
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(list).setSize(count).setTotalPages(totalPage==0?1:totalPage);

        try {
            String resultStr =  JsonUtil.obj2String(result,new String[]{"variableInstances","execution","processInstance",
                    "delegationState","taskIdentityLinkEntities","taskDefinition","queryVariables","candidates","identityLinks",
                    "variableInstanceEntities","variables","variableInstancesLocal","variablesLocal","variableNames","variableNamesLocal"});
            ResponseUtil.write(response, resultStr);
        } catch (Exception e) {
            result.setCode(ResponseCode.ERROR.getValue()).setContent(Collections.emptyList());
            try {
                ResponseUtil.write(response, JsonUtil.obj2String(result));
            } catch (Exception e1) {
                log.error(e.getCause());
            }
            log.error(e.fillInStackTrace());
        }
        return null;
    }

    @ApiOperation(value ="获取流程已办任务列表",notes = "分页展示流程已办任务基础信息")
    @GetMapping(value ="/finish/list/page/{page}/size/{size}")
    public String finishList(@ApiParam(value = "页数",required = true)
            @PathVariable Integer page,@ApiParam(value = "条数",required = true)
                             @PathVariable Integer size, HttpSession session,HttpServletResponse response){
        JsonResult result = new JsonResult();
        // 获取当前登录者id
        //取得角色用户登入的session对象
        MemberShip currentMemberShip=(MemberShip) session.getAttribute(Config.User_Session_Name);
        //取出用户，角色信息
        Group currentGroup=currentMemberShip.getGroup();

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskCandidateGroup(currentGroup.getId());
        Long count =query.count();
        List<HistoricTaskInstance> list =query.listPage(page-1,size);

        Long totalPage =Math.abs(count/size);
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(list).setSize(count).setTotalPages(totalPage==0?1:totalPage);

        try {
            String resultStr =  JsonUtil.obj2String(result);
            ResponseUtil.write(response, resultStr);
        } catch (Exception e) {
            result.setCode(ResponseCode.ERROR.getValue()).setContent(Collections.emptyList());
            try {
                ResponseUtil.write(response, JsonUtil.obj2String(result));
            } catch (Exception e1) {
                log.error(e.getCause());
            }
            log.error(e.fillInStackTrace());
        }
        return null;
    }


    @ApiOperation(value ="获取流程执行过程列表",notes = "根据任务编号查看执行过程历史记录")
    @GetMapping(value ="/action/list/task_id/{taskId}")
    public String actionList(@ApiParam(value = "任务编号",required = true)
            @PathVariable String taskId, HttpServletResponse response){
        JsonResult result = new JsonResult();
        List<HistoricTaskInstance> htiList=historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .list();
        List<HistoricActivityInstance> haiList = new ArrayList<HistoricActivityInstance>();

        for(HistoricTaskInstance historicTaskInstance:htiList){
            String processInstanceId=historicTaskInstance.getProcessInstanceId(); // 获取流程实例id
            List<HistoricActivityInstance> tempList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            if(tempList!=null){
                haiList.addAll(tempList);
            }
        }

        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(haiList).setSize(Long.valueOf(haiList.size()));

        try {
            String resultStr =  JsonUtil.obj2String(result);
            ResponseUtil.write(response, resultStr);
        } catch (Exception e) {
            result.setCode(ResponseCode.ERROR.getValue()).setContent(Collections.emptyList());
            try {
                ResponseUtil.write(response, JsonUtil.obj2String(result));
            } catch (Exception e1) {
                log.error(e.getCause());
            }
            log.error(e.fillInStackTrace());
        }
        return null;
    }


    @ApiOperation(value ="获取流程批注过程列表",notes = "根据任务编号查看批注历史记录")
    @GetMapping(value ="/history/list/task_id/{taskId}")
    public String historyList(@ApiParam(value = "任务编号",required = true)
            @PathVariable String taskId, HttpServletResponse response){
        JsonResult result = new JsonResult();
        HistoricTaskInstance hti=historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();

        List<Comment> list = taskService.getProcessInstanceComments(hti.getProcessInstanceId());
        Long count =Long.valueOf(list.size());
        Collections.reverse(list);
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(list).setSize(count);

        try {
            String resultStr =  JsonUtil.obj2String(result);
            ResponseUtil.write(response, resultStr);
        } catch (Exception e) {
            result.setCode(ResponseCode.ERROR.getValue()).setContent(Collections.emptyList());
            try {
                ResponseUtil.write(response, JsonUtil.obj2String(result));
            } catch (Exception e1) {
                log.error(e.getCause());
            }
            log.error(e.fillInStackTrace());
        }
        return null;
    }

    @ApiOperation(value ="待办任务点击办理操作时，判断跳转的页面",notes = "根据任务编号查看流程处理跳转的页面")
    @GetMapping(value ="/todo/redirect/id/{id}")
    public JsonResult redirectPage(@ApiParam(value = "任务编号",required = true)
            @PathVariable String id,HttpServletResponse response){
        JsonResult result = new JsonResult();
        TaskFormData formData=formService.getTaskFormData(id);
        String url=formData.getFormKey();
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(url);
        return result;
    }

    @ApiOperation(value ="待办任务跳转的用户审核页面获取用户信息",notes = "根据任务编号查看待审核的用户信息")
    @GetMapping(value ="/audit/user/task_id/{taskId}")
    public JsonResult auditUserByTaskId(@PathVariable String taskId){
        JsonResult result = new JsonResult();
        //先根据流程ID查询
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        User user=userService.findByProcessId(task.getProcessInstanceId());
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(user);
        return result;
    }

    @ApiOperation(value ="待办任务跳转的用户审核页面添加批注处理审核",notes = "添加用户申请的审核处理")
    @ApiImplicitParam(name="auditAddUserRequest",value = "用户申请审核请求实体",required = true,dataType = "AuditAddUserRequest")
    @PostMapping(value ="/audit/add_user")
    public JsonResult auditAddUser(@RequestBody AuditAddUserRequest auditAddUserRequest, HttpSession session ) {
        JsonResult result = new JsonResult();
        try {
            //取得角色用户登入的session对象
            MemberShip currentMemberShip=(MemberShip) session.getAttribute(Config.User_Session_Name);
            if(currentMemberShip==null){
                result.setCode(ResponseCode.NOLOGIN.getValue()).setErrMsg("请先登录");
                return result;
            }
            //首先根据ID查询任务
            Task task=taskService.createTaskQuery() // 创建任务查询
                    .taskId(auditAddUserRequest.getTaskId()) // 根据任务id查询
                    .singleResult();
            Map<String,Object> variables=new HashMap<String,Object>();

            //取出用户，角色信息
            User currentUser=currentMemberShip.getUser();
            Group currentGroup=currentMemberShip.getGroup();
            // 取用户信息,更新审核状态
            String userId=(String) taskService.getVariable(auditAddUserRequest.getTaskId(), "userId");
            User user=userService.findUserById(userId);
            if(auditAddUserRequest.getState()==1){
                user.setAuditState("审核通过");
                user.setState(1);
                variables.put("msg", "通过");
            }else{
                user.setAuditState("审核未通过");
                user.setState(-1);
                variables.put("msg", "未通过");
            }
            // 更新审核信息
            userService.updateState(user);

            // 获取流程实例id
            String processInstanceId=task.getProcessInstanceId();
            // 设置用户id
            Authentication.setAuthenticatedUserId(currentUser.getFirst()+currentUser.getLast()+"["+currentGroup.getName()+"]");
            // 添加批注信息
            taskService.addComment(auditAddUserRequest.getTaskId(), processInstanceId, auditAddUserRequest.getComment());
            // 完成任务
            taskService.complete(auditAddUserRequest.getTaskId(), variables);

        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        result.setCode(ResponseCode.SUCCESS.getValue());
        return result;
    }

}

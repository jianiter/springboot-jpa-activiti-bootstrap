package com.dd.activiti.admin.controller;

import com.dd.activiti.admin.common.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

@Log4j2
@Controller
@RequestMapping("/")
@Api(description = "页面跳转管理")
public class HomeController extends BaseController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @ApiOperation(value ="跳转到登录页面",notes = "未登录时，跳转到登录页面")
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @ApiOperation(value ="跳转到后台首页",notes = "登录成功后，默认跳转到首页")
    @GetMapping("/index")
    public String index(Model model,HttpSession session) {
        model.addAttribute("userId", getCurrentUserId(session));
        return "index";
    }

    @ApiOperation(value ="跳转到错误页",notes = "出错时，跳转到错误信息页面")
    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }

    @ApiOperation(value ="跳转到流程部署列表页",notes = "点击菜单时，跳转到流程部署列表页")
    @GetMapping("/deploy/deploy_list")
    public String deployList(Model model) {
        return "deploy/deploy_list";
    }

    @ApiOperation(value ="跳转到流程定义列表页",notes = "点击菜单时，跳转到流程定义列表页")
    @GetMapping("/deploy/processdefinition_list")
    public String processDefinitionList(Model model) {
        return "deploy/processdefinition_list";
    }

    @ApiOperation(value ="跳转到待办任务列表页",notes = "点击菜单时，跳转到待办任务列表页")
    @GetMapping("/task/task_todo_list")
    public String taskToDoList(Model model) {
        return "task/task_todo_list";
    }

    @ApiOperation(value ="跳转到已办任务列表页",notes = "点击菜单时，跳转到已办任务列表页")
    @GetMapping("/task/task_finish_list")
    public String taskFinishList(Model model) {
        return "task/task_finish_list";
    }

    @ApiOperation(value ="跳转到待办审核用户信息页",notes = "点击待办列表中到办理操作时，跳转到用户审核页")
    @GetMapping("/task/audit_add_user/task_id/{id}")
    public String auditAddUser(Model model,@ApiParam(value = "任务编号",required = true)
                               @PathVariable String id) {
        model.addAttribute("taskId",id);
        return "task/audit_add_user";
    }

    @ApiOperation(value ="跳转到添加角色信息页",notes = "点击菜单时，跳转到角色信息页")
    @GetMapping("/group/add_group")
    public String addGroup(Model model) {
        return "group/add_group";
    }

    @ApiOperation(value ="跳转到角色列表页",notes = "点击菜单时，跳转到角色列表页")
    @GetMapping("/group/group_list")
    public String groupList(Model model) {
        return "group/group_list";
    }

    @ApiOperation(value ="跳转到重置用户密码页",notes = "点击菜单时，跳转到重置用户密码页")
    @GetMapping("/user/reset_pwd")
    public String resetPwd(Model model, HttpSession session) {
        model.addAttribute("userId", getCurrentUserId(session));
        return "user/reset_pwd";
    }

    @ApiOperation(value ="跳转到添加用户信息页",notes = "点击菜单时，跳转到用户信息页")
    @GetMapping("/user/add_user")
    public String addUser(Model model) {
        return "user/add_user";
    }

    @ApiOperation(value ="跳转到用户列表页",notes = "点击菜单时，跳转到用户列表页")
    @GetMapping("/user/user_list")
    public String userList(Model model) {
        return "user/user_list";
    }

    @ApiOperation(value ="退出登录",notes = "退出登录账号，默认跳转到登录页面")
    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {
        session.removeAttribute(Config.User_Session_Name);
        return "login";
    }

    @ApiOperation(value ="获取流程定义的流信息",notes = "根据定义编号和名称加载流程图资源io流信息")
    @GetMapping(value ="/api/processdefinition/show/id/{id}/name/{name}")
    public String showView(@ApiParam(value = "编号",required = true)
            @PathVariable String id, @ApiParam(value = "名称",required = true)
                           @PathVariable String name, HttpServletResponse response) {
        try{
            String nameStr = new String(Base64.getDecoder().decode(name));
            InputStream inputStream=repositoryService.getResourceAsStream(id, nameStr);
            OutputStream out=response.getOutputStream();
            for(int b=-1;(b=inputStream.read())!=-1;){
                out.write(b);
            }
            out.close();
            inputStream.close();

        }catch (Exception e){
            log.error(e.fillInStackTrace());
        }
        return null;
    }

    @ApiOperation(value ="查看当前的流程图",notes = "根据任务编号获取部署编号和流程图资源名")
    @GetMapping("process_view/id/{id}")
    public String showCurrentView(Model model, @ApiParam(value = "任务编号",required = true)
                                  @PathVariable String  id){
        //视图

        Task task=taskService.createTaskQuery() // 创建任务查询
                .taskId(id) // 根据任务id查询
                .singleResult();
        // 获取流程定义id
        String processDefinitionId=task.getProcessDefinitionId();
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery() // 创建流程定义查询
                // 根据流程定义id查询
                .processDefinitionId(processDefinitionId)
                .singleResult();
        // 部署id
        model.addAttribute("deploymentId",processDefinition.getDeploymentId());
        model.addAttribute("diagramResourceName", processDefinition.getDiagramResourceName()); // 图片资源文件名称

        return "process_view";
    }

    @ApiOperation(value ="查看历史的流程图",notes = "根据任务编号获取部署编号和流程图资源名")
    @GetMapping("process_history_view/id/{id}")
    public String showHistoryView(Model model,@ApiParam(value = "任务编号",required = true)
                                  @PathVariable String  id){
        //视图

        HistoricTaskInstance hti=historyService.createHistoricTaskInstanceQuery()
                .taskId(id)
                .singleResult();
        // 获取流程定义id
        String processDefinitionId=hti.getProcessDefinitionId();
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery() // 创建流程定义查询
                // 根据流程定义id查询
                .processDefinitionId(processDefinitionId)
                .singleResult();
        // 部署id
        model.addAttribute("deploymentId",processDefinition.getDeploymentId());
        model.addAttribute("diagramResourceName", processDefinition.getDiagramResourceName()); // 图片资源文件名称

        return "process_view";
    }


}

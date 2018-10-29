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
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value="/api/group", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "角色管理")
public class GroupController extends BaseController{

    @Autowired
    private IUserService userService;


    @ApiOperation(value ="获取角色列表",notes = "分页展示角色基础信息")
    @GetMapping(value ="/list/page/{page}/size/{size}")
    public Page<Group> list(@ApiParam(value = "页数",required = true)
            @PathVariable Integer page,@ApiParam(value = "条数",required = true)
                           @PathVariable Integer size){
        Pageable pageable = new PageRequest(page-1, size);
        Page<Group> groupPage = userService.findGroupNoCriteria(pageable);
        return groupPage;
    }

    @ApiOperation(value = "保存角色信息",notes = "添加、修改角色基础信息")
    @ApiImplicitParam(name="group",value = "角色对象实体",required = true,dataType = "Group")
    @PostMapping(value ="/save")
    public JsonResult create(@RequestBody Group group) {
        JsonResult result = new JsonResult();
        try {
            group = userService.saveGroup(group);
            result.setCode(ResponseCode.SUCCESS.getValue());
        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "删除一个角色",notes = "根据编号删除角色信息")
    @DeleteMapping(value ="/delete/id/{id}")
    public JsonResult delete(@ApiParam(value = "编号",required = true)
            @PathVariable String id) {

        JsonResult result = new JsonResult();
        try {
            userService.deleteGroup(id);
            result.setCode(ResponseCode.SUCCESS.getValue());
        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }


}

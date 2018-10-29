package com.dd.activiti.admin.controller;

import com.dd.activiti.admin.common.JsonResult;
import com.dd.activiti.admin.common.ResponseCode;
import com.dd.activiti.admin.util.JsonUtil;
import com.dd.activiti.admin.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value="/api/processdefinition", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "流程定义管理")
public class ProcessDefinitionController extends BaseController{

    @Autowired
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;

    @ApiOperation(value ="获取流程定义列表",notes = "分页展示流程定义基础信息")
    @GetMapping(value ="/list/page/{page}/size/{size}")
    public String list(@ApiParam(value = "页数",required = true)
            @PathVariable Integer page,@ApiParam(value = "条数",required = true)
                       @PathVariable Integer size,HttpServletResponse response){
        JsonResult result = new JsonResult();
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        Long count =query.count();
        List<ProcessDefinition> list = query.listPage(page-1,size);
        Long totalPage =Math.abs(count/size);
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(list).setSize(count).setTotalPages(totalPage==0?1:totalPage);
        try {
            String resultStr =  JsonUtil.obj2String(result,new String[]{"identityLinks","processDefinition"});
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


}

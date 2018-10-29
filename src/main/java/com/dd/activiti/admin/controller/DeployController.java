package com.dd.activiti.admin.controller;

import com.dd.activiti.admin.common.JsonResult;
import com.dd.activiti.admin.common.ResponseCode;
import com.dd.activiti.admin.util.JsonUtil;
import com.dd.activiti.admin.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

@Log4j2
@RestController
@RequestMapping(value="/api/deploy", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(description = "流程部署管理")
public class DeployController extends BaseController{

    @Autowired
    private RepositoryService repositoryService;

    @ApiOperation(value ="获取流程部署列表",notes = "分页展示流程部署基础信息")
    @GetMapping(value ="/list/page/{page}/size/{size}")
    public String list(@ApiParam(value = "页数",required = true)
            @PathVariable Integer page,@ApiParam(value = "条数",required = true)
                       @PathVariable Integer size,HttpServletResponse response){
        JsonResult result = new JsonResult();
        DeploymentQuery query = repositoryService.createDeploymentQuery();
        Long count =query.count();
        List<Deployment> deployList =query.listPage(page-1,size);

        Long totalPage =Math.abs(count/size);
        result.setCode(ResponseCode.SUCCESS.getValue()).setContent(deployList).setSize(count).setTotalPages(totalPage==0?1:totalPage);
        try {
            String resultStr =  JsonUtil.obj2String(result,new String[]{"resources"});
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

    @ApiOperation(value = "保存上传的流程信息",notes = "添加上传流程基础信息")
    @PostMapping(value ="/save")
    public JsonResult save(@ApiParam(value = "上传的文件",required = true)
                               @PathVariable MultipartFile deployFile) {
        JsonResult result = new JsonResult();
        try {
            Deployment deployment = repositoryService.createDeployment() //创建部署
                    .name(deployFile.getOriginalFilename())	//需要部署流程名称
                    .addZipInputStream(new ZipInputStream(deployFile.getInputStream()))//添加ZIP输入流
                    .deploy();//开始部署
            result.setCode(ResponseCode.SUCCESS.getValue());
            result.setContent(deployment);

        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "删除上传的流程信息",notes = "根据编号删除上传流程基础信息")
    @DeleteMapping(value ="/delete/id/{id}")
    public JsonResult delete(@ApiParam(value = "编号",required = true)
            @PathVariable String id) {

        JsonResult result = new JsonResult();
        try {
            repositoryService.deleteDeployment(id, true);
            result.setCode(ResponseCode.SUCCESS.getValue());
        }catch (Exception e){
            log.error(e.fillInStackTrace());
            result.setCode(ResponseCode.ERROR.getValue()).setErrMsg(e.getMessage());
        }
        return result;
    }


}

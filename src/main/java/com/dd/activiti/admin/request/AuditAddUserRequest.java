package com.dd.activiti.admin.request;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Api(description = "用户申请审核请求实体")
public class AuditAddUserRequest {

   @ApiModelProperty(value = "任务编号")
   private String taskId;

   @ApiModelProperty(value = "批注")
   private String comment;

   @ApiModelProperty(value = "状态，1：批准，2：驳回")
   private Integer state;
}

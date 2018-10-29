package com.dd.activiti.admin.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Api(description = "返回给客户端的数据定义")
public class JsonResult implements Serializable {

    @ApiModelProperty(value = "返回码：success，error，nologin，noallow")
    private String code;

    @ApiModelProperty(value = "返回错误信息")
    private String errMsg;

    @ApiModelProperty(value = "返回的数据，任意类型")
    private Object content;

    @ApiModelProperty(value = "返回的数据条数，分页时是总条数")
    private Long size;

    @ApiModelProperty(value = "返回的总页数，分页时使用")
    private Long totalPages;


}

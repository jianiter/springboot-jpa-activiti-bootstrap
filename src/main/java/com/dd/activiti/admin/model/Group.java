package com.dd.activiti.admin.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACT_ID_GROUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Api(description = "角色表")
public class Group {

    @ApiModelProperty(value = "编号")
    @Id
    @Column(name = "ID_",nullable = false,unique = true)
    private String id;

    @ApiModelProperty(hidden = true)
    @Column(name = "REV_",nullable=true)
    private Integer rev;

    @ApiModelProperty(value = "名称")
    @Column(name = "NAME_",nullable=true)
    private String name;

    @ApiModelProperty(value = "类型，所在层级，默认为0，最低级员工")
    @Column(name = "TYPE_",nullable=true)
    private Integer type;

}

package com.dd.activiti.admin.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "ACT_ID_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Api(description = "用户表")
public class User {

    public User(String id,String pwd,String first,String last,String email){
        this.id = id;
        this.pwd=pwd;
        this.first = first;
        this.last = last;
        this.email = email;
    }
    @ApiModelProperty(value = "编号")
    @Id
    @Column(name = "ID_",nullable = false,unique = true)
    private String id;

    @ApiModelProperty(hidden = true)
    @Column(name = "REV_",nullable=true)
    private Integer rev;

    @ApiModelProperty(value = "姓氏")
    @Column(name = "FIRST_",nullable = true)
    private String first;

    @ApiModelProperty(value="名字")
    @Column(name="LAST_",nullable = true)
    private String last;

    @ApiModelProperty(value="邮箱")
    @Column(name="EMAIL_",nullable = true)
    private String email;

    @ApiModelProperty(value="密码")
    @Column(name="PWD_",nullable = true)
    private String pwd;

    @ApiModelProperty(hidden=true)
    @Column(name="PICTURE_ID_",nullable = true)
    private String pictureId;

    @ApiModelProperty(value="状态，1正常，0待审核,-1审核失败")
    @Column(name = "STATE_",nullable = true)
    private Integer state;

    @ApiModelProperty(value="审核状态")
    @Column(name = "AUDIT_STATE_",nullable = true)
    private String auditState;

    @ApiModelProperty(value="进程编号")
    @Column(name = "PROCESS_ID_", nullable = true)
    private String processId;

    @ApiModelProperty(value="所属角色")
    @Transient
    private String groups;

    @ApiModelProperty(value="所属角色编号")
    @Transient
    private String groupIds;
}

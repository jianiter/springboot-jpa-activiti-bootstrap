package com.dd.activiti.admin.model;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "ACT_ID_MEMBERSHIP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Api("用户角色表")
public class MemberShip {
    @ApiModelProperty(value = "编号")
    @Id
    @Column(name = "ID_", nullable = true)
    private int id;

    @ApiModelProperty(value = "用户表")
    @Transient
    private User user; // 用户

    @ApiModelProperty(value = "角色表")
    @Transient
    private Group group; // 角色

    @ApiModelProperty(value = "用户编号")
    @Column(name = "USER_ID_", nullable = true)
    private String userId;

    @ApiModelProperty(value = "角色编号")
    @Column(name = "GROUP_ID_", nullable = true)
    private String groupId;
}

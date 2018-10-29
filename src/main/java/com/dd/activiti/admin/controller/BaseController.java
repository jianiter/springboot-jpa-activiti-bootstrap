package com.dd.activiti.admin.controller;

import com.dd.activiti.admin.common.Config;
import com.dd.activiti.admin.exception.UserNotFoundException;
import com.dd.activiti.admin.model.MemberShip;

import javax.servlet.http.HttpSession;

public class BaseController {

    protected MemberShip getCurrentUser(HttpSession session){
       Object obj = session.getAttribute(Config.User_Session_Name);
       if(obj==null){
           throw new UserNotFoundException();
       }
       return (MemberShip)obj;
    }

    protected String getCurrentUserId(HttpSession session){
        MemberShip user = getCurrentUser(session);
       return user.getUserId();
    }

}

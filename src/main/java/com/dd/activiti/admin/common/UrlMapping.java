package com.dd.activiti.admin.common;

import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
public class UrlMapping {
    public static final String LOGIN="/login";
    public static final String LOGOUT = "/logout";
    public static final String ERROR = "/error";




    public static final List<String> noFilterList = new ArrayList<>();
    static {
        noFilterList.add(LOGIN);
        noFilterList.add(LOGOUT);
        noFilterList.add(ERROR);
    }
}

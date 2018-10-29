package com.dd.activiti.admin.common;

import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.util.Properties;

@Log4j2
public class Config {

    public final static String User_Session_Name ="User_Session_Name";

    public final static String User_Session_Id ="User_Session_Id";

    public final static String User_Session_Group ="User_Session_Group";

    public static String UPLOAD_PATH = "E:/upload/";//values("upload_path");

    public static String ACTIVITI_USER_ROLE_PROCESS = "activitiUserRoleProcess";


    static Properties pp;

    static{
        pp = new Properties();
        try {
            InputStream fps = Config.class.getResourceAsStream("/application.properties");
            pp.load(fps);
            fps.close();
        } catch (Exception e) {
           log.error("application.properties文件异常!"+e.getCause());
        }
    }
    public static String values(String key) {
        String value = pp.getProperty(key);
        if (value != null) {
            return value;
        } else {
            return null;
        }
    }


}

package com.dd.activiti.admin.interceptor;

import com.dd.activiti.admin.common.Config;
import com.dd.activiti.admin.common.UrlMapping;
import lombok.extern.log4j.Log4j2;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Log4j2
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean isSuccess = true;
        String actionName = "";
        String resourcePath = new UrlPathHelper().getPathWithinApplication(request);
//        try {
        if (!postToAuthenticate(request, resourcePath)) {
            isSuccess =  vaidateActivity(request, actionName, response);
        }
//        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
//            log.error("Internal authentication service exception", internalAuthenticationServiceException);
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        } catch (AuthenticationException authenticationException) {
//            JSONObject json = new JSONObject();
//            logger.info("Authentication Exception: {}", authenticationException.getMessage());
//
//            responseObject(authenticationException.getMessage(), HttpServletResponse.SC_UNAUTHORIZED, false, json,
//                    response, request);
//
//        }
        return isSuccess;

    }

    private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
        log.info("**************请求地址:" + resourcePath);
        if (UrlMapping.noFilterList.contains(resourcePath)) {
            return true;
        }
        if(resourcePath.startsWith("/api/users/login/id/")){
            return true;
        }
        if(resourcePath.startsWith("/css/")||resourcePath.startsWith("/js/")||resourcePath.startsWith("/images/")||resourcePath.startsWith("/favicon.ico")){
            return true;
        }
        return false;
    }

    public boolean vaidateActivity(HttpServletRequest request, String actionName, HttpServletResponse response) {
//        String token = request.getHeader(HEADER_STRING);
//        String user = Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
//                .getBody().getSubject();
//
//        SecurityRepository securityRepository = WebApplicationContextUtils
//                .getRequiredWebApplicationContext(request.getServletContext()).getBean(SecurityRepository.class);
//        ActivityRepository activityRepository = WebApplicationContextUtils
//                .getRequiredWebApplicationContext(request.getServletContext()).getBean(ActivityRepository.class);
//        UserRepository userRepository = WebApplicationContextUtils
//                .getRequiredWebApplicationContext(request.getServletContext()).getBean(UserRepository.class);
//        User u = userRepository.findById(securityRepository.findByEmail(user).getUserId());
//        Activity isExist = activityRepository.findByRoleListAndActivityName(u.getRole(), actionName);
//        if (isExist == null) {
//            logger.info("Sorry, you are not authorized*********");
//            throw new BadCredentialsException("Sorry, you are not authorized");
//        } else {
//            return true;
//        }

        //获取session
        HttpSession session = request.getSession(true);
        //过滤不需要登陆的请求

        //判断用户ID是否存在，不存在就跳转到登录界面
        if (session.getAttribute(Config.User_Session_Name) == null) {
            log.info("------:跳转到login页面！");
            try {
                response.sendRedirect(request.getContextPath() + "/login");
            } catch (IOException e) {
                log.error(e.getCause());
            }
            return false;
        } else {
            return true;
        }

    }

    boolean responseObject(String message, Integer status, boolean error, JSONObject json,
                           HttpServletResponse httpResponse, HttpServletRequest httpRequest) {
        try {
            json.put("message", message);
            json.put("status", status);
            json.put("isSuccess", error);
        } catch (JSONException e) {
            log.error(e.getCause());
        }
        httpResponse.setStatus(status);
        return setHttpRequestResponse(httpRequest, httpResponse, json);
    }

    boolean setHttpRequestResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, JSONObject json) {
        httpResponse.addHeader("Content-Type", "application/json");
        try {
            httpResponse.getWriter().write(json.toString());
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (IOException e) {
            log.error(e.getCause());
        }
        return false;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

package com.dd.activiti.admin;


import com.dd.activiti.admin.common.UrlMapping;
import com.dd.activiti.admin.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.Arrays;

@Configuration("CustomWebMvcConfig")
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截规则：除了配置的过滤路径，其他都拦截判断
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(UrlMapping.noFilterList)
                .excludePathPatterns("/api/users/login/id/**")
                .excludePathPatterns(Arrays.asList("/static/**","/css/**","/js/**","/images/", "/templates/**"));
        super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/rest/api/doc/**").addResourceLocations("classpath:/swagger/dist/");
        super.addResourceHandlers(registry);
    }



}

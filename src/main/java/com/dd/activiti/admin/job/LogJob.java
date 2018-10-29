/**
 * Copyright DuZ 2018-2019
 */
package com.dd.activiti.admin.job;


import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author zdd
 * @version V1.0
 * @date 2018/10/10 17:20
 */
@Component
@Log4j2
public class LogJob {

    /**
     * 2秒钟执行1次
     */
    //@Scheduled(fixedRate = 2000)
    public void logging(){
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        log.info("-----INFO-------"+simpleDateFormat.format(now));
        log.debug("-------DEBUG---------");
        log.error("-----ERROR-------"+now.getTime());
    }

}

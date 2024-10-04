package com.cheolhyeon.stockdividends.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 작업을 수행할 스케쥴러를 담고있는 쓰레드 풀 생성
        ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();

        // cpu 코어 갯수를 가지고 옴
        int core = Runtime.getRuntime().availableProcessors();
        System.out.println("core = " + core);

        // 가지고온 코어로 TreadPool 사이즈 설정
        tpts.setPoolSize(core);
        tpts.initialize();

        // 그리고 인자로 들어온 레지스터에 내가 설정해준 Thread를 넘겨줌
        taskRegistrar.setTaskScheduler(tpts);
    }
}

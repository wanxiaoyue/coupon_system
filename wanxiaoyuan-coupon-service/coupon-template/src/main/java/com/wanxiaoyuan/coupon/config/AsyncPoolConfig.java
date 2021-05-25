package com.wanxiaoyuan.coupon.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <h1>自定义异步任务线程池</h1>
 * Created by WanYue
 */

@Slf4j
@EnableAsync  //开启异步，不用写在启动类
@Configuration//配置类
public class AsyncPoolConfig implements AsyncConfigurer {

    /** 线程池执行器 */
    @Bean
    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(20);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("WanXiaoyuanAsync_");

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return executor;
    }
     /** 捕获异常*/
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler{

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            //打印堆栈
            throwable.printStackTrace();
            //打印错误信息
            log.error("asyncError: {}, Method: {}, Param: {}",
                    throwable.getMessage(), method.getName(),
                    JSON.toJSONString(objects));

            //TODO 发送邮件或者短信，做进一步的处理
        }
    }
}

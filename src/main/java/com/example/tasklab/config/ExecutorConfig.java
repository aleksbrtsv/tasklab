package com.example.tasklab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Bean(destroyMethod = "")
    public ExecutorService taskPool(
            @Value("${tasklab.executor.pool-size:3}") int poolSize
    ) {
        return Executors.newFixedThreadPool(poolSize);
    }
}

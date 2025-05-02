package com.team5.backend.global.config.toss;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class TossAsyncConfig {
	@Bean(name = "tossTaskExecutor")
	public Executor tossTaskExecutor() {
		return Executors.newFixedThreadPool(10);
	}
}

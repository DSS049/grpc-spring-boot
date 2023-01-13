package com.telokos.grpc.config;

import com.telokos.grpc.service.IdGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    public GrpcServerLifecycle grpcServerLifecycle(GrpcProperties grpcProperties) {
        return new GrpcServerLifecycle(new IdGeneratorService(), grpcProperties);
    }
}

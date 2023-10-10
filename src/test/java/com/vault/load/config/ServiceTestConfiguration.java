package com.vault.load.config;

import com.vault.load.services.LoadFundService;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("com.vault.load.services")
public class ServiceTestConfiguration
        implements EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public LoadFundService getLoadFundService() {
        return new LoadFundService();
    }
}

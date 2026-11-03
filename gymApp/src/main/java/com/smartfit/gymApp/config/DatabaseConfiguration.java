package com.smartfit.gymApp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseConfiguration {
    @Bean(destroyMethod="close")
    public HikariDataSource dataSource(Environment environment) {
        HikariDataSource shared=new HikariDataSource(ConexionBD.config(environment::getProperty,true));
        ConexionBD.usarDataSource(shared);
        return shared;
    }
}

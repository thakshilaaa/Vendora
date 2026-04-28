package com.vendora.epic1.config;

import com.vendora.epic1.security.SetupInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("epic1WebMvcConfig")
public class WebMvcConfig implements WebMvcConfigurer {

    private final SetupInterceptor setupInterceptor;

    public WebMvcConfig(SetupInterceptor setupInterceptor) {
        this.setupInterceptor = setupInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(setupInterceptor).addPathPatterns("/**");
    }
}

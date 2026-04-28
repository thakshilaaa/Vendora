package com.vendora.epic1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomerViewConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/dashboard/customer-dashboard")
                .setViewName("forward:/dashboard/customer-dashboard");
        registry.addViewController("/auth/login").setViewName("epic1/auth/login");
        registry.addViewController("/registration/signup").setViewName("epic1/registration/customer-signup");
        registry.addViewController("/customer/profile").setViewName("epic1/auth/profile");
    }
}

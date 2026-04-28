package com.vendora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * application-test.properties excludes {@code MailSenderAutoConfiguration}; this stub bean
 * satisfies {@link com.vendora.epic1.service.impl.EmailServiceImpl} wiring. It is not used to
 * deliver real mail in tests.
 */
@Configuration
@Profile("test")
public class TestMailSenderConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("127.0.0.1");
        mailSender.setPort(1025);
        mailSender.setUsername("test");
        mailSender.setPassword("test");
        Properties p = new Properties();
        p.put("mail.smtp.auth", "false");
        p.put("mail.smtp.starttls.enable", "false");
        mailSender.setJavaMailProperties(p);
        return mailSender;
    }
}

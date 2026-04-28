package com.vendora.epic1.service;

import com.vendora.epic1.model.enums.RoleType;
import com.vendora.epic1.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SystemSetupService {

    private static final Logger log = LoggerFactory.getLogger(SystemSetupService.class);

    private final UserRepository userRepository;
    private boolean isLaunched = false;

    public SystemSetupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        checkAndSetLaunchStatus();
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public void launchSite() {
        this.isLaunched = true;
        log.info("Vendora Platform has been officially LAUNCHED. Setup mode is now disabled.");
    }

    private void checkAndSetLaunchStatus() {
        if (userRepository.existsByRole(RoleType.ROLE_ADMIN)) {
            this.isLaunched = true;
            log.info("Vendora Platform is LAUNCHED. Admin user exists.");
        } else {
            this.isLaunched = false;
            log.warn("Vendora Platform is in SETUP MODE. Awaiting first Admin registration.");
        }
    }
}

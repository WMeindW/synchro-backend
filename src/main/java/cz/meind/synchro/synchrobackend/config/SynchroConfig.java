package cz.meind.synchro.synchrobackend.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

@Getter
@Component
public class SynchroConfig {
    @Value("${security.jwt.secure-route:src/main/resources/secure}")
    private String secureRoute;

    @Value("${security.jwt.combined-role:ADMIN/USER}")
    private String combinedRole;

    @Value("${security.jwt.login-page}")
    private String loginPage;

    @Value("${security.jwt.user-dashboard-page}")
    private String userDashboardPage;

    @Value("${security.jwt.admin-role:ADMIN}")
    private String adminRole;

    @Value("${security.jwt.default-role:USER}")
    private String defaultRole;

    @Value("${security.jwt.host-address:http://localhost:8080}")
    private String host;

    @Value("${security.jwt.expiration-time:3600000}")
    private long expirationTime;

    @Value("${security.jwt.admin-username:admin_user}")
    private String adminUsername;

    @Value("${security.jwt.admin-password:Bm7HjSLW}")
    private String adminPassword;

    @Value("${security.jwt.signup-link-expires:36000000}")
    private long signupLinkExpires;

    @Value("${security.jwt.secret-key:cc53da86b2af8ec3e83e2cf79224687741cdc3470ed2290b87ca43cb9cef143b}")
    private String secretKey;

    private static final Logger logger = LoggerFactory.getLogger(SynchroConfig.class);

    @PostConstruct
    private void debug() {
        logger.info("secureRoute: {}", secureRoute);
        logger.info("combinedRole: {}", combinedRole);
        logger.info("loginPage: {}", loginPage);
        logger.info("userDashboardPage: {}", userDashboardPage);
        logger.info("adminRole: {}", adminRole);
        logger.info("defaultRole: {}", defaultRole);
        logger.info("host: {}", host);
        logger.info("expirationTime: {}", expirationTime);
        logger.info("adminUsername: {}", adminUsername);
        logger.info("adminPassword: {}", adminPassword);
        logger.info("signupLinkExpires: {}", signupLinkExpires);
        logger.info("secretKey: {}", secretKey);
        logger.info("Configuration loaded successfully.");
    }

}

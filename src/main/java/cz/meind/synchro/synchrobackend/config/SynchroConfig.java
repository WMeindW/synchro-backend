package cz.meind.synchro.synchrobackend.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import java.util.List;

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

    @Value("${attendance.synchro.type:WORK}")
    private String workPeriod;

    @Value("#{'${events.synchro.types:SHIFT,VACATION,HOMEOFFICE-SHIFT,SICK-LEAVE}'.split(',')}")
    private List<String> eventTypeList;

    @Value("${files.synchro.max-size.bites:100}")
    private int maxUserFileSize;

    @Value("${files.synchro.location:src/main/resources/files}")
    private String userFileLocation;

    private static final Logger logger = LoggerFactory.getLogger(SynchroConfig.class);

    /**
     * Logs the configuration values for debugging purposes.
     * This method is automatically called after the bean is constructed and dependencies are injected.
     * It logs all the configuration properties to facilitate debugging and verification of loaded values.
     * <p>
     * The following properties are logged:
     * - secureRoute
     * - combinedRole
     * - loginPage
     * - userDashboardPage
     * - adminRole
     * - defaultRole
     * - host
     * - expirationTime
     * - adminUsername
     * - adminPassword
     * - signupLinkExpires
     * - secretKey
     * - eventTypeList
     * - workPeriod
     * <p>
     * After logging all properties, it logs a success message indicating that the configuration was loaded successfully.
     */
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
        logger.info("eventTypeList: {}", eventTypeList);
        logger.info("workPeriod: {}", workPeriod);
        logger.info("fileSize: {}", maxUserFileSize);
        logger.info("fileLocation: {}", userFileLocation);
        logger.info("Configuration loaded successfully.");
    }

}

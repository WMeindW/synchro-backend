package cz.meind.synchro.synchrobackend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope("prototype")
public class SynchroConfig {
    @Value("${security.jwt.secure-route}")
    private String secureRoute;

    @Value("${security.jwt.combined-role}")
    private String combinedRole;

    @Value("${security.jwt.login-page}")
    private String loginPage;

    @Value("${security.jwt.user-dashboard-page}")
    private String userDashboardPage;

    @Value("${security.jwt.admin-role}")
    private String adminRole;

    @Value("${security.jwt.default-role}")
    private String defaultRole;

    @Value("${security.jwt.host-address}")
    private String host;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    @Value("${security.jwt.admin-username:admin_user}")
    private String adminUsername;

    @Value("${security.jwt.admin-password}")
    private String adminPassword;

    @Value("${security.jwt.signup-link-expires}")
    private long signupLinkExpires;

    @Value("${security.jwt.secret-key}")
    private String secretKey;


}

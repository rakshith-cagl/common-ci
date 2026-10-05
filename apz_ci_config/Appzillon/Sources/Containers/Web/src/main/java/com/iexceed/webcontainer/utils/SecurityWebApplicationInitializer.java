package com.iexceed.webcontainer.utils;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends
        AbstractSecurityWebApplicationInitializer {
    public SecurityWebApplicationInitializer() {
        super(SpringSecurityConfiguration.class);
    }
}
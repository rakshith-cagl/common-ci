package com.iexceed.webcontainer.utils;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfiguration {
    static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(SpringSecurityConfiguration.class.getName());

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        LOG.debug("CSRF PREVENTION ENABLE OR DISABLE:::"+WebProperties.getCsrfPrevention());

        if ("Y".equalsIgnoreCase(WebProperties.getCsrfPrevention())) {
            LOG.debug("entered in SpringSecurityConfiguration- csrf");
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            //  .and().authorizeHttpRequests().requestMatchers("/**").permitAll();
            LOG.debug("after added in cookie - csrf");
            // http.csrf().disable();
            return http.build();
        }else {
            LOG.debug("entered in SpringSecurityConfiguration- csrf");
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            //  .and().authorizeHttpRequests().requestMatchers("/**").permitAll();
            LOG.debug("csrf is disabled");
            http.csrf().disable();
            return http.build();

        }
    }

    }

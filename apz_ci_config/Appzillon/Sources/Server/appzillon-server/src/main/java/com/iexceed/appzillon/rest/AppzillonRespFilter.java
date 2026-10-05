package com.iexceed.appzillon.rest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;


public class AppzillonRespFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {

        response.getHeaders().add("X-Frame-Options", "DENY");
        response.getHeaders().add("X-XSS-Protection", "1; mode=block");
        response.getHeaders().add("X-Content-Type-Options", "nosniff");
        response.getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        if (request.getUriInfo().getPath().contains(".wadl")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getHeaders().remove("content-type");
            response.setEntity("Forbidden");
            return;
        }
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.setEntity("Method Not Allowed");

        }

    }


}

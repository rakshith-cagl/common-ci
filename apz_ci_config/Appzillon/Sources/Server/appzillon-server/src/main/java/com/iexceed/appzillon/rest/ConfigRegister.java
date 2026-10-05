package com.iexceed.appzillon.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/*")
public class ConfigRegister extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        //Registering main resource class
        resources.add(AppzillonRestWS.class);
        //Registering filter class
        resources.add(AppzillonRespFilter.class);
        return resources;
    }
}

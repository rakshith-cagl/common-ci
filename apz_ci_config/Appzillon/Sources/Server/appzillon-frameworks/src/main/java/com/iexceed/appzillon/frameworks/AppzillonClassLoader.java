package com.iexceed.appzillon.frameworks;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AppzillonClassLoader {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS,
            AppzillonClassLoader.class.getName());


    public IEnrichData loadDataHooks(String className) {
        IEnrichData iEnrichData = null;

        try {

            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();
            iEnrichData = (IEnrichData) constructor.newInstance();

        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return iEnrichData;
    }

}

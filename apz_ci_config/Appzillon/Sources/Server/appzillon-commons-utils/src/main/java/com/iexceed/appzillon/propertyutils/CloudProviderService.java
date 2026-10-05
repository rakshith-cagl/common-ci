package com.iexceed.appzillon.propertyutils;

import java.util.Map;

public class CloudProviderService {

    public Map<String, String> loadCloudProperties(ICloudProvider iCloudProvider) {
        return iCloudProvider.loadCloudProperties();
    }

}

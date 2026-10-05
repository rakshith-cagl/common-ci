/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
 */
package com.iexceed.appzillon.notification.utils.hauwei.util;

import com.iexceed.appzillon.notification.utils.hauwei.messaging.HuaweiApp;
import com.iexceed.appzillon.notification.utils.hauwei.messaging.HuaweiCredential;
import com.iexceed.appzillon.notification.utils.hauwei.messaging.HuaweiOption;

import java.util.ResourceBundle;

public class InitAppUtils {
    private InitAppUtils() {
        //default constructor
    }

    /**
     * @return HuaweiApp
     */
    public static HuaweiApp initializeApp() {
        String appId = ResourceBundle.getBundle("url").getString("appid");
        String appSecret = ResourceBundle.getBundle("url").getString("appsecret");
        // Create HuaweiCredential
        // This appId and appSecret come from Huawei Developer Alliance
        return initializeApp(appId, appSecret);
    }

    private static HuaweiApp initializeApp(String appId, String appSecret) {
        HuaweiCredential credential = HuaweiCredential.builder()
                .setAppId(appId)
                .setAppSecret(appSecret)
                .build();

        // Create HuaweiOption
        HuaweiOption option = HuaweiOption.builder()
                .setCredential(credential)
                .build();

        // Initialize HuaweiApp
        return HuaweiApp.getInstance(option);
    }
}

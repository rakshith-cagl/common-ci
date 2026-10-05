package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsnfDevicesMaster;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;

public class DeviceMasterSpecification {

    public static Specification<TbAsnfDevicesMaster> likeDeviceName(
            final String deviceName) {
        return (root, query, builder) -> builder
                .like(root.get(ServerConstants.NOTIFICATION_DEVICE_NAME), deviceName);
    }

    public static Specification<TbAsnfDevicesMaster> likeOsId(final String osId) {
        return (root, query, builder) -> builder.like(root.<String>get(ServerConstants.NOTIFICATION_OS_ID), osId);
    }

    public static Specification<TbAsnfDevicesMaster> deviceNameisNull() {
        return (root, query, builder) -> builder.isNull(root.<String>get(ServerConstants.NOTIFICATION_DEVICE_NAME));
    }

    public static Specification<TbAsnfDevicesMaster> statusIsActive() {
        return (root, query, builder) -> builder.equal(root.<String>get(ServerConstants.MESSAGE_HEADER_STATUS), ServerConstants.YES);
    }
}

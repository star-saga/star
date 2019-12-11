package org.event.driven.light.omegacommon.config;

public class ServiceConfig {

    private final String serviceName;

    public ServiceConfig(String serviceName) {
        this.serviceName=serviceName;
    }

    public String serviceName() {
        return serviceName;
    }
}

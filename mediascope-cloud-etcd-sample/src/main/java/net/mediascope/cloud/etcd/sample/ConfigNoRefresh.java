package net.mediascope.cloud.etcd.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigNoRefresh {

    private String property;

    public ConfigNoRefresh(@Value("${testproperty}") String property) {
        this.property = property;
    }

    String getProperty() {
        return this.property;
    }
}

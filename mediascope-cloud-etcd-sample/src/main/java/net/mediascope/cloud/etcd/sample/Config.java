package net.mediascope.cloud.etcd.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class Config {

    private String property;

    public Config(@Value("${testproperty}") String property) {
        this.property = property;
    }

    String getProperty() {
        return this.property;
    }
}

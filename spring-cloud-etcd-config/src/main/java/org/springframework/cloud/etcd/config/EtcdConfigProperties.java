/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.etcd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 */
@ConfigurationProperties("spring.cloud.etcd.config")
public class EtcdConfigProperties {
    private boolean enabled = true;
    private String prefix = "config";
    private String defaultContext = "application";
    private String profileSeparator = "-";
    private String name;
    private int timeout = 1;
    private Format format = Format.YAML;

    /*
     * Throw exceptions during config lookup if true, otherwise, log warnings.
     * */
    private boolean failFast = true;

    public EtcdConfigProperties() {
    }

    @PostConstruct
    public void init() {
        if (this.format == Format.FILES) {
            this.profileSeparator = "-";
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFailFast() {
        return this.failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public Format getFormat() {
        return this.format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDefaultContext() {
        return defaultContext;
    }

    public void setDefaultContext(String defaultContext) {
        this.defaultContext = defaultContext;
    }

    public String getProfileSeparator() {
        return profileSeparator;
    }

    public void setProfileSeparator(String profileSeparator) {
        this.profileSeparator = profileSeparator;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EtcdConfigProperties that = (EtcdConfigProperties) o;

        if (enabled != that.enabled) return false;
        if (timeout != that.timeout) return false;
        if (!Objects.equals(prefix, that.prefix)) return false;
        if (!Objects.equals(defaultContext, that.defaultContext))
            return false;
        return Objects.equals(profileSeparator, that.profileSeparator);
    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (defaultContext != null ? defaultContext.hashCode() : 0);
        result = 31 * result + (profileSeparator != null ? profileSeparator.hashCode() : 0);
        result = 31 * result + timeout;
        return result;
    }

    @Override
    public String toString() {
        return String.format("EtcdConfigProperties{enabled=%s, prefix='%s', defaultContext='%s', profileSeparator='%s', timeout=%d}", enabled, prefix, defaultContext, profileSeparator, timeout);
    }

    public enum Format {
        KEY_VALUE,
        PROPERTIES,
        YAML,
        FILES,
    }
}

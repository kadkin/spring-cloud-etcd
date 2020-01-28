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

package net.mediascope.cloud.etcd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

import javax.annotation.PostConstruct;

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
    private Watch watch = new Watch();
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

    public Watch getWatch() {
        return this.watch;
    }

    public void setWatch(Watch watch) {
        this.watch = watch;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("enabled", this.enabled)
                .append("prefix", this.prefix)
                .append("defaultContext", this.defaultContext)
                .append("name", this.name)
                .append("profileSeparator", this.profileSeparator)
                .append("format", this.format)
                .append("timeout", this.timeout)
                .append("watch", this.watch)
                .toString();
    }

    public enum Format {
        KEY_VALUE,
        PROPERTIES,
        YAML,
        FILES,
    }

    /**
     * etcd watch properties.
     */
    public static class Watch {

        private int waitTime = 55;

        /**
         * If the watch is enabled. Defaults to true.
         */
        private boolean enabled = true;

        /**
         * The value of the fixed delay for the watch in millis. Defaults to 1000.
         */
        private int delay = 1000;

        public Watch() {
        }

        public int getWaitTime() {
            return this.waitTime;
        }

        public void setWaitTime(int waitTime) {
            this.waitTime = waitTime;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getDelay() {
            return this.delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        @Override
        public String toString() {
            return new ToStringCreator(this)
                    .append("waitTime", this.waitTime)
                    .append("enabled", this.enabled)
                    .append("delay", this.delay)
                    .toString();
        }

    }
}

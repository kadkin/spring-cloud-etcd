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

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.kv.GetResponse;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 * @author Igor Kadkin
 */

@Order(0)
public class EtcdPropertySourceLocator implements PropertySourceLocator {
    private final Client etcd;
    private final EtcdConfigProperties properties;
    private static final Log log = LogFactory.getLog(EtcdPropertySourceLocator.class);
    private final List<String> contexts = new ArrayList<>();

    public EtcdPropertySourceLocator(Client etcd, EtcdConfigProperties properties) {
        this.etcd = etcd;
        this.properties = properties;
    }

    @Override
    public PropertySource<?> locate(Environment environment) {

        if (environment instanceof ConfigurableEnvironment) {
            final ConfigurableEnvironment env = (ConfigurableEnvironment) environment;

            String appName = this.properties.getName();

            if (appName == null) {
                appName = env.getProperty(EtcdConstants.PROPERTY_SPRING_APPLICATION_NAME);
            }

            List<String> profiles = Arrays.asList(env.getActiveProfiles());

            String prefix = this.properties.getPrefix();

            List<String> suffixes = new ArrayList<>();
            if (this.properties.getFormat() != EtcdConfigProperties.Format.FILES) {
                suffixes.add("");
            } else {
                suffixes.add(".yml");
                suffixes.add(".yaml");
                suffixes.add(".properties");
            }

            String defaultContext = getContext(prefix, this.properties.getDefaultContext());

            for (String suffix : suffixes) {
                contexts.add(defaultContext + suffix);
            }
            for (String suffix : suffixes) {
                addProfiles(contexts, defaultContext, profiles, suffix);
            }

            String baseContext = getContext(prefix, appName);

            for (String suffix : suffixes) {
                contexts.add(baseContext + suffix);
            }
            for (String suffix : suffixes) {
                addProfiles(contexts, baseContext, profiles, suffix);
            }

            CompositePropertySource composite = new CompositePropertySource(EtcdConstants.NAME);
            Collections.reverse(contexts);

            for (String propertySourceContext : contexts) {
                try {

                    EtcdPropertySource propertySource = null;
                    if (this.properties.getFormat() == EtcdConfigProperties.Format.FILES) {

                        GetResponse getResponse = etcd.getKVClient().get(
                                ByteSequence.from(propertySourceContext, UTF_8))
                                .get();
                        if (getResponse.getCount() > 0) {
                            EtcdFilesPropertySource etcdFilesPropertySource = new EtcdFilesPropertySource(propertySourceContext, this.etcd, this.properties);
                            etcdFilesPropertySource.init();
                            propertySource = etcdFilesPropertySource;
                        }
                    } else {
                        propertySource = new EtcdPropertySource(propertySourceContext, etcd, properties);
                        propertySource.init();
                    }
                    if (propertySource != null) {
                        composite.addPropertySource(propertySource);
                    }
                } catch (Exception e) {
                    if (this.properties.isFailFast()) {
                        log.error(
                                "Fail fast is set and there was an error reading configuration from etcd.");
                        ReflectionUtils.rethrowRuntimeException(e);
                    } else {
                        log.warn("Unable to load etcd config from "
                                + propertySourceContext, e);
                    }
                }

            }

            return composite;
        }

        return null;
    }

    private String getContext(String prefix, String context) {
        if (StringUtils.isEmpty(prefix)) {
            return context;
        } else {
            return prefix + "/" + context;
        }
    }

    private void addProfiles(List<String> contexts, String baseContext,
                             List<String> profiles, String suffix) {
        for (String profile : profiles) {
            contexts.add(baseContext + this.properties.getProfileSeparator() + profile
                    + suffix);
        }
    }
}

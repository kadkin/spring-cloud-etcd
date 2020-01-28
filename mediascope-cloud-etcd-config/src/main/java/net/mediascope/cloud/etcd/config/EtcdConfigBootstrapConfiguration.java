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

import io.etcd.jetcd.Client;
import net.mediascope.cloud.etcd.ConditionalOnEtcdEnabled;
import net.mediascope.cloud.etcd.EtcdAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Luca Burgazzoli
 */
@Configuration
@Import(EtcdAutoConfiguration.class)
@EnableConfigurationProperties
@ConditionalOnEtcdEnabled
//@ConditionalOnProperty(name = "spring.cloud.etcd.enabled", matchIfMissing = true)
public class EtcdConfigBootstrapConfiguration {

    @Autowired
    private Client etcd;

    @Bean
    public EtcdConfigProperties etcdConfigProperties() {
        return new EtcdConfigProperties();
    }

    @Bean
    public EtcdPropertySourceLocator etcdPropertySourceLocator() {
        return new EtcdPropertySourceLocator(etcd, etcdConfigProperties());
    }
}

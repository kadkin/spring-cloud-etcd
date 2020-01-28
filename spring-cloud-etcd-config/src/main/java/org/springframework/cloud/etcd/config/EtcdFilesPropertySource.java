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

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;

import static org.springframework.cloud.etcd.config.EtcdConfigProperties.Format.PROPERTIES;
import static org.springframework.cloud.etcd.config.EtcdConfigProperties.Format.YAML;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 * @author Igor Kadkin
 */
public class EtcdFilesPropertySource extends EtcdPropertySource {


    public EtcdFilesPropertySource(String root, Client source, EtcdConfigProperties config) {
        super(root, source, config);
    }

    @Override
    protected void parseValue(KeyValue getValue, EtcdConfigProperties.Format format) {
        if (getContext().endsWith(".yml") || this.getContext().endsWith(".yaml")) {
            parseValue(getValue, YAML);
        } else if (this.getContext().endsWith(".properties")) {
            parseValue(getValue, PROPERTIES);
        } else {
            throw new IllegalStateException(
                    "Unknown files extension for context " + this.getContext());
        }

    }
}

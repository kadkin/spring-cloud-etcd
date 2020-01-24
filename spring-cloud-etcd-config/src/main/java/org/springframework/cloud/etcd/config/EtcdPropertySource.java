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

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.common.exception.ErrorCode;
import io.etcd.jetcd.common.exception.EtcdException;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 */
public class EtcdPropertySource extends EnumerablePropertySource<Client> {

    private static final Log log = LogFactory.getLog(EtcdPropertySource.class);

    private final Map<String, Object> properties;
    private final EtcdConfigProperties config;
    private String context;

    public EtcdPropertySource(String context, Client source, EtcdConfigProperties config) {
        super(context, source);
        this.context = context;
        this.properties = new HashMap<>();
        this.context = context.startsWith(EtcdConstants.PATH_SEPARATOR) ? context
                + EtcdConstants.PATH_SEPARATOR : EtcdConstants.PATH_SEPARATOR + context;
        this.config = config;
    }

    public void init() {
        try {
            GetResponse getResponse = getSource()
                    .getKVClient()
                    .get(ByteSequence.from(context, UTF_8), GetOption.newBuilder().build())
                    .get();
            List<KeyValue> values = getResponse.getKvs();

            EtcdConfigProperties.Format format = config.getFormat();
            switch (format) {
                case KEY_VALUE:
                    parsePropertiesInKeyValueFormat(values);
                    break;
                case PROPERTIES:
                    break;
                case YAML:
                    break;
                case FILES:
                    break;

            }
        } catch (EtcdException e) {
            if (e.getErrorCode() == ErrorCode.NOT_FOUND) {//key not found, no need to print stack trace
                log.warn("Unable to init property source: " + getName() + ", " + e.getMessage());
            } else {
                log.warn("Unable to init property source: " + getName(), e);
            }
        } catch (Exception e) {
            log.warn("Unable to init property source: " + getName(), e);

        }
    }

    @Override
    public String[] getPropertyNames() {
        return properties.keySet().toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    /**
     * Parses the properties in key value style i.e., values are expected to be either a
     * sub key or a constant.
     *
     * @param values values to parse
     */
    protected void parsePropertiesInKeyValueFormat(List<KeyValue> values) {
        if (values == null) {
            return;
        }

        for (KeyValue getValue : values) {
            String key = getValue.getKey().toString(UTF_8);
            if (!StringUtils.endsWithIgnoreCase(key, "/")) {
                key = key.replace(this.context, "").replace('/', '.');
                String value = getValue.getValue().toString(UTF_8);
                this.properties.put("testproperty", value);
            }
        }
    }
}

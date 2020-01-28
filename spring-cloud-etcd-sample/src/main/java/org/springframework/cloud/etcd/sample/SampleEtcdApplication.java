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

package org.springframework.cloud.etcd.sample;

import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@RestController
public class SampleEtcdApplication {

    public static final String CLIENT_NAME = "testEtcdApp";

    @Autowired
    Config config;
    @Autowired
    ConfigNoRefresh configNoRefresh;
    @Autowired
    Environment env;
    @Autowired
    Client client;

    public SampleEtcdApplication() {

    }

    public static void main(String[] args) {
        SpringApplication.run(SampleEtcdApplication.class, args);
    }

    @GetMapping("/myenv")
    public Map<String, Object> env() {
        Map<String, Object> map = new HashMap();
        map.put("config", config.getProperty());
        map.put("configNoRefresh", configNoRefresh.getProperty());
        return map;
    }
}

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

package net.mediascope.cloud.etcd.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
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
    ApplicationEventPublisher publisher;

    public SampleEtcdApplication() {
//        CountDownLatch latch = new CountDownLatch(50);
//        ByteSequence key = ByteSequence.from("1", StandardCharsets.UTF_8);
//        List<String> asd = new ArrayList<>();
//        asd.add("http://192.168.21.37:2379");
//        Collection<URI> endpoints = Util.toURIs(asd);
//
//        try (Client client = Client.builder().endpoints(endpoints).build();
//             Watch watch = client.getWatchClient();
//             Watch.Watcher watcher = watch.watch(key, response -> {
//
//
//                 for (WatchEvent event : response.getEvents()) {
//                     System.out.println("EVENT");
//                     System.out.println(String.format("type={0}, key={1}, value={2}", event.getEventType(),
//                             Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(StandardCharsets.UTF_8)).orElse(""),
//                             Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(StandardCharsets.UTF_8))
//                                     .orElse("")));
//                 }
//
//                 latch.countDown();
//             })) {
//            client.getKVClient().put(key, key);
//            latch.await();
//        } catch (Exception e) {
//            System.exit(1);
//        }


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

    @GetMapping("/refresh")
    public String refresh() {

//                new RefreshEvent(this, data, data.toString()));
        publisher.publishEvent(new RefreshEvent(this, 1, "1"));
        return "refresh";
    }
}

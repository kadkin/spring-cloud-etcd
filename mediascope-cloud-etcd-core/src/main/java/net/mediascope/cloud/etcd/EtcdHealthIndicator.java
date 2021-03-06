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

package net.mediascope.cloud.etcd;

import io.etcd.jetcd.Client;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * @author Spencer Gibb
 */
public class EtcdHealthIndicator extends AbstractHealthIndicator {

    private final Client client;

    public EtcdHealthIndicator(Client client) {
        this.client = client;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            //CompletableFuture<AlarmResponse> alarmResponseCompletableFuture = client.getMaintenanceClient().listAlarms();
            // TODO: support Health
            builder.withDetail("version", "3.0").up();
        } catch (Exception e) {
            builder.down(e);
        }
    }

    public Client getClient() {
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EtcdHealthIndicator that = (EtcdHealthIndicator) o;

        return client.equals(that.client);
    }

    @Override
    public int hashCode() {
        return client.hashCode();
    }

    @Override
    public String toString() {
        return String.format("EtcdHealthIndicator{client=%s}", client);
    }
}

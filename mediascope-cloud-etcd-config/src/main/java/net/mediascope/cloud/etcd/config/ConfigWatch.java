package net.mediascope.cloud.etcd.config;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.style.ToStringCreator;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigWatch implements ApplicationEventPublisherAware, SmartLifecycle {

    private static final Log log = LogFactory.getLog(ConfigWatch.class);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final EtcdConfigProperties properties;
    private final Client etcdClient;
    private ApplicationEventPublisher publisher;


    private LinkedHashMap<ByteSequence, Long> etcdIndexes;
    private Watch watchClient;
    private List<Watch.Watcher> watchers;

    public ConfigWatch(EtcdConfigProperties properties, Client etcdClient, Map<ByteSequence, Long> initialIndexes) {
        this.properties = properties;
        this.etcdClient = etcdClient;

        this.etcdIndexes = new LinkedHashMap<>(initialIndexes);

        this.watchers = new ArrayList<>();
    }

    public ConfigWatch(EtcdConfigProperties properties, Client etcdClient) {
        this.properties = properties;
        this.etcdClient = etcdClient;
        ;
        this.watchers = new ArrayList<>();
    }

    private void etcdWatchListener(WatchResponse response) {
        response.getEvents().forEach(watchEvent -> {
            try {
                KeyValue keyValue = watchEvent.getKeyValue();
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                log.info("update for key=" + key);
                if (watchEvent.getEventType().equals(WatchEvent.EventType.PUT)) {
                    if (etcdIndexes.containsKey(keyValue.getKey())) {
                        RefreshEventData data = new RefreshEventData(key, etcdIndexes.get(keyValue.getKey()), keyValue.getModRevision());
                        this.publisher.publishEvent(
                                new RefreshEvent(this, data, data.toString()));
                        etcdIndexes.put(keyValue.getKey(), keyValue.getModRevision());
                    }
                }
            } catch (Exception e) {
                log.warn("Error querying etcd Key/Values for context", e);
            }
        });
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void start() {
        Watch.Listener listener = Watch.listener(this::etcdWatchListener);
        watchClient = etcdClient.getWatchClient();
        etcdIndexes.forEach((key, aLong) -> {
            log.info("Watching for key=" + key.toString(StandardCharsets.UTF_8));
            Watch.Watcher watcher = watchClient.watch(key, listener);
            watchers.add(watcher);
        });
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void stop() {
        watchers.forEach(Watch.Watcher::close);
        watchClient.close();
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }


    public static class RefreshEventData {


        private final String context;

        private final Long prevIndex;

        private final Long newIndex;

        public RefreshEventData(String context, Long prevIndex, Long newIndex) {
            this.context = context;
            this.prevIndex = prevIndex;
            this.newIndex = newIndex;
        }

        public String getContext() {
            return this.context;
        }

        public Long getPrevIndex() {
            return this.prevIndex;
        }

        public Long getNewIndex() {
            return this.newIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RefreshEventData that = (RefreshEventData) o;
            return Objects.equals(this.context, that.context)
                    && Objects.equals(this.prevIndex, that.prevIndex)
                    && Objects.equals(this.newIndex, that.newIndex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.context, this.prevIndex, this.newIndex);
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("context", this.context)
                    .append("prevIndex", this.prevIndex).append("newIndex", this.newIndex)
                    .toString();
        }

    }

}
package net.mediascope.cloud.etcd.config;

import io.etcd.jetcd.Client;
import net.mediascope.cloud.etcd.ConditionalOnEtcdEnabled;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration(proxyBeanMethods = false)
@ConditionalOnEtcdEnabled
@ConditionalOnProperty(name = "spring.cloud.etcd.config.enabled", matchIfMissing = true)
public class EtcdConfigAutoConfiguration {

    /**
     * Name of the config watch task scheduler bean.
     */
    public static final String CONFIG_WATCH_ETCD_TASK_SCHEDULER_NAME = "configWatchEtcdTaskScheduler";

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RefreshEndpoint.class)
    protected static class ConsulRefreshConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.cloud.etcd.config.watch.enabled",
                matchIfMissing = true)
        public ConfigWatch configWatch(EtcdConfigProperties properties,
                                       EtcdPropertySourceLocator locator,
                                       Client consul,
                                       @Qualifier(CONFIG_WATCH_ETCD_TASK_SCHEDULER_NAME) TaskScheduler taskScheduler) {
            return new ConfigWatch(properties, consul, locator.getContextIndexes());
            //return new ConfigWatch(properties, consul, locator.getContextIndexes(), taskScheduler);
        }

        @Bean(name = CONFIG_WATCH_ETCD_TASK_SCHEDULER_NAME)
        @ConditionalOnProperty(name = "spring.cloud.etcd.config.watch.enabled",
                matchIfMissing = true)
        public TaskScheduler configWatchTaskScheduler() {
            return new ThreadPoolTaskScheduler();
        }

    }
}

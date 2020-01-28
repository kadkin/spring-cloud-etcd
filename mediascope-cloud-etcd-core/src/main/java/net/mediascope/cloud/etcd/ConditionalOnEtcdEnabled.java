package net.mediascope.cloud.etcd;

import io.etcd.jetcd.Client;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When both property and consul classes are on the classpath.
 *
 * @author Spencer Gibb
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(ConditionalOnEtcdEnabled.OnEtcdEnabledCondition.class)
public @interface ConditionalOnEtcdEnabled {

    /**
     * Verifies multiple conditions to see if Consul should be enabled.
     */
    class OnEtcdEnabledCondition extends AllNestedConditions {

        OnEtcdEnabledCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        /**
         * Consul property is enabled.
         */
        @ConditionalOnProperty(value = "spring.cloud.etcd.enabled",
                matchIfMissing = true)
        static class FoundProperty {

        }

        /**
         * Consul client class found.
         */
        @ConditionalOnClass(Client.class)
        static class FoundClass {

        }

    }
}

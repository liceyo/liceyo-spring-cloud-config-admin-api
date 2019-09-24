package com.liceyo.core.storage;

import com.liceyo.core.AdminProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * ApplicationStorageRepositoryFactory
 * @description 应用信息存储factory
 * @author lichengyong
 * @date 2019/9/12 17:24
 * @version 1.0
 */
@AutoConfigureAfter({
        CompositeDiscoveryClientAutoConfiguration.class,
        EnvironmentRepositoryConfiguration.class
})
@Component
@Configuration
@ConditionalOnBean(DiscoveryClient.class)
@EnableConfigurationProperties(AdminProperties.class)
@Import({
        NativeApplicationStorageRepositoryFactory.class,
        GitApplicationStorageRepositoryFactory.class
})
public class ApplicationStorageRepositoryFactory {
}

@Profile("native")
@Configuration
class NativeApplicationStorageRepositoryFactory{
    @Bean("applicationStorageRepository")
    @ConditionalOnMissingBean
    public ApplicationStorageRepository applicationStorageRepository(
            AdminProperties adminProperties,
            DiscoveryClient discoveryClient) {
        return new NativeApplicationStorageRepository(discoveryClient, adminProperties);
    }
}

@Profile("git")
@ConditionalOnBean(MultipleJGitEnvironmentRepository.class)
@Configuration
class GitApplicationStorageRepositoryFactory {
    @Bean("applicationStorageRepository")
    @ConditionalOnMissingBean
    public ApplicationStorageRepository applicationStorageRepository(
            DiscoveryClient discoveryClient,
            MultipleJGitEnvironmentRepository repository) throws IOException {
        return new GitApplicationStorageRepository(discoveryClient, repository);
    }
}
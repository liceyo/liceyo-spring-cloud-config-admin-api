package com.liceyo.core;

import com.liceyo.api.ApplicationStorageApi;
import com.liceyo.api.EnvFileApi;
import com.liceyo.api.InstanceRefreshApi;
import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.file.EnvFileRepository;
import com.liceyo.core.file.EnvFileRepositoryFactory;
import com.liceyo.core.refresh.HttpInstanceRefreshRepository;
import com.liceyo.core.refresh.InstanceRefreshRepository;
import com.liceyo.core.storage.ApplicationStorageRepository;
import com.liceyo.core.storage.ApplicationStorageRepositoryFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * AdminAutoConfiguration
 * @description 自动配置类
 * @author lichengyong
 * @date 2019/9/12 17:17
 * @version 1.0
 */
@AutoConfigureAfter(CompositeDiscoveryClientAutoConfiguration.class)
@EnableConfigurationProperties(AdminProperties.class)
@Configuration
@Import({
        ApplicationStorageRepositoryFactory.class,
        EnvFileRepositoryFactory.class
})
public class AdminAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public InstanceRefreshRepository instanceRefreshRepository(DiscoveryClient discoveryClient, AdminProperties adminProperties) {
        if (AdminProperties.REFRESH_WAY_HTTP.equals(adminProperties.getRefreshWay())) {
            return new HttpInstanceRefreshRepository(adminProperties, discoveryClient, restTemplate());
        }
        throw new ConfigAdminApiException("不支持的刷新类型");
    }

    @ConditionalOnMissingBean(AdminGlobalExceptionHandler.class)
    @Bean
    public AdminGlobalExceptionHandler adminGlobalExceptionHandler() {
        return new AdminGlobalExceptionHandler();
    }

    @Bean
    public ApplicationStorageApi applicationStorageApi(ApplicationStorageRepository repository) {
        return new ApplicationStorageApi(repository);
    }

    @Bean
    public EnvFileApi envFileApi(EnvFileRepository repository) {
        return new EnvFileApi(repository);
    }

    @Bean
    public InstanceRefreshApi instanceRefreshApi(InstanceRefreshRepository repository) {
        return new InstanceRefreshApi(repository);
    }
}

package com.liceyo.core.storage;

import com.liceyo.core.entity.ApplicationStorageConfigs;
import com.liceyo.core.entity.ServiceStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BaseApplicationStorageRepository
 * @description 应用信息存储Service
 * @author lichengyong
 * @date 2019/9/12 14:19
 * @version 1.0
 */
public abstract class BaseApplicationStorageRepository implements ApplicationStorageRepository {
    /** discoveryClient **/
    protected final DiscoveryClient discoveryClient;

    /** 应用名 **/
    @Value("${spring.application.name}")
    protected String configAppName;

    BaseApplicationStorageRepository(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
    }

    /**
     * ApplicationStorageRepository
     * @description 查询所有应用信息
     * @author lichengyong
     * @date 2019/9/12 14:25
     * @return java.util.List<com.liceyo.core.entity.ServiceStatus>
     * @version 1.0
     */
    @Override
    public List<ServiceStatus> readAllService() {
        List<ServiceStatus> serviceStatuses = new ArrayList<>();
        ApplicationStorageConfigs configs = readStorage();
        Set<String> storageApplications = new HashSet<>(configs.getServices());
        Set<String> services = new HashSet<>(discoveryClient.getServices());
        services.stream()
                // 排除配置中心服务本身
                .filter(s -> !configAppName.equals(s))
                .forEach(s -> {
                    if (storageApplications.contains(s)) {
                        serviceStatuses.add(new ServiceStatus(s, true, true));
                    } else {
                        serviceStatuses.add(new ServiceStatus(s, true, false));
                    }
                });
        storageApplications.stream()
                .filter(s -> !services.contains(s) && !configAppName.equals(s))
                .forEach(s -> serviceStatuses.add(new ServiceStatus(s, false, true)));
        return serviceStatuses;
    }
}

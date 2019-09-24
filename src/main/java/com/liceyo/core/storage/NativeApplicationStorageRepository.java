package com.liceyo.core.storage;

import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.AdminProperties;
import com.liceyo.core.entity.ApplicationStorageConfigs;
import com.liceyo.utils.ApplicationFileUtils;
import com.liceyo.utils.CacheUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * NativeApplicationStorageRepository
 * @description 本地环境应用信息存储操作Service
 * @author lichengyong
 * @date 2019/9/12 16:41
 * @version 1.0
 */
public class NativeApplicationStorageRepository extends BaseApplicationStorageRepository {

    /** 应用信息存储路径 **/
    private final String applicationInfoFile;

    public NativeApplicationStorageRepository(DiscoveryClient discoveryClient, AdminProperties properties) {
        super(discoveryClient);
        applicationInfoFile = properties.getApplicationInfoFile();
    }

    /**
     * ApplicationStorageRepository
     * @description 读取所有配置信息
     * @author lichengyong
     * @date 2019/9/12 14:17
     * @return com.liceyo.core.entity.ApplicationStorageConfigs
     * @version 1.0
     */
    @Override
    public ApplicationStorageConfigs readStorage() {
        Object obj = CacheUtils.getInstance().get(CACHE_KEY);
        if (obj != null) {
            return (ApplicationStorageConfigs) obj;
        }
        ApplicationStorageConfigs configs;
        try {
            configs = ApplicationFileUtils.readAppByLine(applicationInfoFile);
        } catch (IOException e) {
            throw new ConfigAdminApiException(e);
        }
        CacheUtils.getInstance().add(CACHE_KEY, configs, 10, TimeUnit.MINUTES);
        return configs;
    }

    /**
     * ApplicationStorageRepository
     * @description 存储
     * @author lichengyong
     * @date 2019/9/12 14:18
     * @param storageEnum 类型
     * @param item 项
     * @version 1.0
     */
    @Override
    public void storage(ApplicationStorageEnum storageEnum, String item) {
        ApplicationStorageConfigs configs = readStorage();
        configs.add(storageEnum, item);
        CacheUtils.getInstance().remove(CACHE_KEY);
        try {
            ApplicationFileUtils.write(applicationInfoFile, configs.toWriteString());
        } catch (IOException e) {
            throw new ConfigAdminApiException(e);
        }
    }

    /**
     * ApplicationStorageRepository
     * @description 删除
     * @author lichengyong
     * @date 2019/9/12 14:18
     * @param storageEnum 类型
     * @param item 项
     * @version 1.0
     */
    @Override
    public void remove(ApplicationStorageEnum storageEnum, String item) {
        ApplicationStorageConfigs configs = readStorage();
        configs.remove(storageEnum, item);
        CacheUtils.getInstance().remove(CACHE_KEY);
        try {
            ApplicationFileUtils.write(applicationInfoFile, configs.toWriteString());
        } catch (IOException e) {
            throw new ConfigAdminApiException(e);
        }
    }
}

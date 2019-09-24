package com.liceyo.core.storage;

import com.liceyo.core.entity.ApplicationStorageConfigs;
import com.liceyo.core.entity.ServiceStatus;

import java.util.List;

/**
 * ApplicationStorageRepository
 * @description 应用信息存储Service
 * @author lichengyong
 * @date 2019/9/12 13:54
 * @version 1.0
 */
public interface ApplicationStorageRepository {
    /** 缓存键 **/
    String CACHE_KEY="APPLICATION_INFO";

    /**
     * ApplicationStorageRepository
     * @description 读取所有配置信息
     * @author lichengyong
     * @date 2019/9/12 14:17
     * @return com.liceyo.core.entity.ApplicationStorageConfigs
     * @throws Exception 操作异常
     * @version 1.0
     */
    ApplicationStorageConfigs readStorage();

    /**
     * ApplicationStorageRepository
     * @description 查询所有应用信息
     * @author lichengyong
     * @date 2019/9/12 14:25
     * @return java.util.List<com.liceyo.core.entity.ServiceStatus>
     * @throws Exception 操作异常
     * @version 1.0
     */
    List<ServiceStatus> readAllService();

    /**
     * ApplicationStorageRepository
     * @description 存储
     * @author lichengyong
     * @date 2019/9/12 14:18
     * @param storageEnum 类型
     * @param item 项
     * @throws Exception 操作异常
     * @version 1.0
     */
    void storage(ApplicationStorageEnum storageEnum, String item);

    /**
     * ApplicationStorageRepository
     * @description 删除
     * @author lichengyong
     * @date 2019/9/12 14:18
     * @param storageEnum 类型
     * @param item 项
     * @throws Exception 操作异常
     * @version 1.0
     */
    void remove(ApplicationStorageEnum storageEnum, String item);
}

package com.liceyo.core.refresh;

import com.liceyo.core.entity.InstanceRefreshResult;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Map;

/**
 * InstanceRefreshRepository
 * @description 实例刷新接口
 * @author lichengyong
 * @date 2019/9/12 10:58
 * @version 1.0
 */
public interface InstanceRefreshRepository {
    /**
     * InstanceRefreshRepository
     * @description 获取所有服务信息（按服务名分）
     * @author lichengyong
     * @date 2019/9/12 11:03
     * @return java.util.Map<java.lang.String,java.util.List<org.springframework.cloud.client.ServiceInstance>>
     * @version 1.0
     */
    Map<String, List<ServiceInstance>> service();

    /**
     * InstanceRefreshRepository
     * @description 刷新指定服务
     * @author lichengyong
     * @date 2019/9/12 11:28
     * @param service 服务
     * @return com.liceyo.core.entity.InstanceRefreshResult
     * @version 1.0
     */
    InstanceRefreshResult refresh(String service);
}

package com.liceyo.core.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * InstanceRefreshDetail
 * @description 实例刷新详情
 * @author lichengyong
 * @date 2019/9/12 11:23
 * @version 1.0
 */
@Data
public class InstanceRefreshDetail {
    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 实例状态
     */
    private String status;
    /**
     * 实例IP地址
     */
    private String ipAddr;
    /**
     * 实例管理端口
     */
    private String managementPort;
    /**
     * 刷新的配置参数
     */
    private JSON refreshData;
    /**
     * 异常信息
     */
    private Throwable throwable;
}

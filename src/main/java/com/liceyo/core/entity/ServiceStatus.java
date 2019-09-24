package com.liceyo.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ServiceStatus
 * @description 应用信息
 * @author lichengyong
 * @date 2019/9/12 11:30
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStatus {
    /**
     * 服务名称
     */
    private String service;
    /**
     * 服务状态
     */
    private Boolean state;
    /**
     * 该服务名是否保存到本地
     */
    private Boolean storage;
}

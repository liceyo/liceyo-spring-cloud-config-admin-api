package com.liceyo.core.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ApplicationStorageEnum
 * @description 应用信息分类
 * @author lichengyong
 * @date 2019/9/12 13:57
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ApplicationStorageEnum {
    /** 服务信息 **/
    SERVICE("[service]"),
    /** 环境信息 **/
    ENV("[env]"),
    /** 标签信息**/
    LABEL("[label]");
    /** 文本标记 **/
    private String sign;
}

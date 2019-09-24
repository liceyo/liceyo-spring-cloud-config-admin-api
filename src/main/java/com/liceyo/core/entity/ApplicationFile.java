package com.liceyo.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApplicationFile
 * @description 配置文件信息
 * @author lichengyong
 * @date 2019/9/12 11:31
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFile {
    /** 默认字符 **/
    private static final String DEFAULT_STRING = "default";
    /**
     * 服务名称
     */
    private String service;
    /**
     * env
     */
    private String env = DEFAULT_STRING;
    /**
     * 标签
     */
    private String label = DEFAULT_STRING;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件名称
     */
    private String filename;

    /**
     * 配置文件内容
     */
    private String text;

    /**
     * ApplicationFile
     * @description 清除默认值
     * @author lichengyong
     * @date 2019/9/19 16:51
     * @version 1.0
     */
    public void clearDefault() {
        if (DEFAULT_STRING.equals(env)) {
            env = null;
        }
        if (DEFAULT_STRING.equals(label)) {
            label = null;
        }
    }
}

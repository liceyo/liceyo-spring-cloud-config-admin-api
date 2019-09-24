package com.liceyo.core.entity;

import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.storage.ApplicationStorageEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationStorageConfigs
 * @description 应用存储信息配置
 * @author lichengyong
 * @date 2019/9/12 11:33
 * @version 1.0
 */
@Data
public class ApplicationStorageConfigs {
    /**
     * 应用
     */
    private List<String> services;
    /**
     * envs
     */
    private List<String> envs;
    /**
     * 标签
     */
    private List<String> labels;

    public ApplicationStorageConfigs() {
        services = new ArrayList<>();
        envs = new ArrayList<>();
        labels = new ArrayList<>();
    }

    /**
     * ApplicationStorageConfigs
     * @description 转换为配置文本
     * @author lichengyong
     * @date 2019/9/12 11:33
     * @return java.lang.String
     * @version 1.0
     */
    public String toWriteString() {
        List<String> write = new ArrayList<>();
        write.add(ApplicationStorageEnum.SERVICE.getSign());
        write.addAll(services);
        write.add(ApplicationStorageEnum.ENV.getSign());
        write.addAll(envs);
        write.add(ApplicationStorageEnum.LABEL.getSign());
        write.addAll(labels);
        return String.join("\n", write);
    }

    /**
     * ApplicationStorageConfigs
     * @description 添加内容
     * @author lichengyong
     * @date 2019/9/12 14:12
     * @param storageEnum 类型
     * @param item 需要添加的内容
     * @version 1.0
     */
    public void add(ApplicationStorageEnum storageEnum, String item) {
        switch (storageEnum) {
            case SERVICE:
                if (services.contains(item)) {
                    throw new ConfigAdminApiException("已有该应用");
                }
                services.add(item);
                break;
            case ENV:
                if (envs.contains(item)) {
                    throw new ConfigAdminApiException("已有该环境");
                }
                envs.add(item);
                break;
            case LABEL:
                if (labels.contains(item)) {
                    throw new ConfigAdminApiException("已有该标签");
                }
                labels.add(item);
                break;
            default:
                throw new ConfigAdminApiException("不支持的存储分类");
        }
    }

    /**
     * ApplicationStorageConfigs
     * @description 删除内容
     * @author lichengyong
     * @date 2019/9/12 14:12
     * @param storageEnum 类型
     * @param item 删除的内容
     * @version 1.0
     */
    public void remove(ApplicationStorageEnum storageEnum, String item) {
        switch (storageEnum) {
            case SERVICE:
                services.remove(item);
                break;
            case ENV:
                envs.remove(item);
                break;
            case LABEL:
                labels.remove(item);
                break;
            default:
                throw new ConfigAdminApiException("不支持的存储分类");
        }
    }
}

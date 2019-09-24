package com.liceyo.core.file;

import com.liceyo.core.entity.ApplicationFile;

import java.util.List;

/**
 * EnvFileRepository
 * @description 环境Service
 * @author lichengyong
 * @date 2019/9/12 10:59
 * @version 1.0
 */
public interface EnvFileRepository {
    /** 默认标签 **/
    String DEFAULT_LABEL = "master";
    /** application和profile的分隔符 **/
    String APP_PRO_SEPARATOR = "-";

    /**
     * EnvFileRepository
     * @description 判断配置文件是否存在
     * @author lichengyong
     * @date 2019/9/12 17:45
     * @param file 配置文件信息
     * @return boolean
     * @version 1.0
     */
    boolean exist(ApplicationFile file);

    /**
     * EnvFileRepository
     * @description 读取配置文件信息
     * @author lichengyong
     * @date 2019/9/12 17:46
     * @param file 配置文件信息
     * @return java.lang.String
     * @version 1.0
     */
    String read(ApplicationFile file);

    /**
     * EnvFileRepository
     * @description 写入配置文件
     * @author lichengyong
     * @date 2019/9/12 17:46
     * @param file 配置文件信息
     * @version 1.0
     */
    void write(ApplicationFile file);

    /**
     * EnvFileRepository
     * @description 删除配置文件
     * @author lichengyong
     * @date 2019/9/12 17:47
     * @param file 配置文件信息
     * @version 1.0
     */
    void delete(ApplicationFile file);

    /**
     * EnvFileRepository
     * @description 列出配置文件
     * @author lichengyong
     * @date 2019/9/12 17:47
     * @param file 配置文件信息
     * @return java.util.List<com.liceyo.core.entity.ApplicationFile>
     * @version 1.0
     */
    List<ApplicationFile> list(ApplicationFile file);

}

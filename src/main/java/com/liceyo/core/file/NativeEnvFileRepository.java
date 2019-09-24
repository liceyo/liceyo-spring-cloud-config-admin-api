package com.liceyo.core.file;

import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.entity.ApplicationFile;
import com.liceyo.utils.ApplicationFileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * NativeEnvFileRepository
 * @description 本地环境操作
 * @author lichengyong
 * @date 2019/9/12 17:43
 * @version 1.0
 */
public class NativeEnvFileRepository implements EnvFileRepository {
    private static Logger logger = LogManager.getLogger(NativeEnvFileRepository.class);

    /** 配置文件搜索路径绝对位置 **/
    protected String searchLocation;

    public NativeEnvFileRepository(NativeEnvironmentRepository nativeEnvironmentRepository) {
        String[] locations = nativeEnvironmentRepository.getSearchLocations();
        if (locations == null || locations.length == 0) {
            throw new ConfigAdminApiException("search-locations必须不为空");
        }
        // 配置文件搜索路径
        try {
            this.searchLocation = ResourceUtils.getFile(locations[0]).getAbsolutePath();
            logger.info("配置文件搜索路径:{}", this.searchLocation);
        } catch (FileNotFoundException e) {
            throw new ConfigAdminApiException("读取search-locations失败", e);
        }
    }

    /**
     * EnvFileRepository
     * @description 判断配置文件是否存在
     * @author lichengyong
     * @date 2019/9/12 17:45
     * @param applicationFile 配置文件信息
     * @return boolean
     * @version 1.0
     */
    @Override
    public boolean exist(ApplicationFile applicationFile) {
        String path = resolvePath(applicationFile);
        File file = new File(path);
        return file.exists();
    }

    /**
     * EnvFileRepository
     * @description 读取配置文件信息
     * @author lichengyong
     * @date 2019/9/12 17:46
     * @param file 配置文件信息
     * @return java.lang.String
     * @version 1.0
     */
    @Override
    public String read(ApplicationFile file) {
        String path = resolvePath(file);
        // 读取配置文件
        if (new File(path).exists()) {
            try {
                return ApplicationFileUtils.read(path);
            } catch (IOException e) {
                throw new ConfigAdminApiException(e);
            }
        }
        return null;
    }

    /**
     * EnvFileRepository
     * @description 写入配置文件
     * @author lichengyong
     * @date 2019/9/12 17:46
     * @param applicationFile 配置文件信息
     * @version 1.0
     */
    @Override
    public void write(ApplicationFile applicationFile) {
        String path = resolvePath(applicationFile);
        File file = new File(path);
        boolean exist = file.exists();
        try {
            if (!exist) {
                exist = file.createNewFile();
            }
            if (exist) {
                ApplicationFileUtils.write(path, applicationFile.getText());
            }
        } catch (IOException e) {
            throw new ConfigAdminApiException(e);
        }
    }

    /**
     * EnvFileRepository
     * @description 删除配置文件
     * @author lichengyong
     * @date 2019/9/12 17:47
     * @param file 配置文件信息
     * @version 1.0
     */
    @Override
    public void delete(ApplicationFile file) {
        String path = resolvePath(file);
        ApplicationFileUtils.delete(path);
    }

    /**
     * EnvFileRepository
     * @description 列出配置文件
     * @author lichengyong
     * @date 2019/9/12 17:47
     * @param applicationFile 配置文件信息
     * @return java.util.List<com.liceyo.core.entity.ApplicationFile>
     * @version 1.0
     */
    @Override
    public List<ApplicationFile> list(ApplicationFile applicationFile) {
        String service = applicationFile.getService();
        String env = applicationFile.getEnv();
        String label = applicationFile.getLabel();
        List<ApplicationFile> filePojos = new ArrayList<>();
        String filepath = searchLocation;
        // 处理标签为空
        if (label != null && !label.isEmpty()) {
            // 判断标签文件夹是否存在，如果不存在就创建
            File file = new File(searchLocation + label);
            if (!file.exists()) {
                file.mkdir();
            }
            filepath = file.getAbsolutePath();
        }
        File dir = new File(filepath);
        if (!dir.exists()) {
            return filePojos;
        }
        String filename;
        //判断profile是否为空
        if (StringUtils.isNotBlank(env)) {
            filename = service + APP_PRO_SEPARATOR + env;
        } else {
            filename = service;
        }
        File[] files = dir.listFiles(f -> f.isFile() && f.getName().startsWith(filename));
        for (File appFile : files != null ? files : new File[0]) {
            ApplicationFile pojo = new ApplicationFile();
            pojo.setService(service);
            pojo.setLabel(label);
            String name = appFile.getName();
            pojo.setFilename(name);
            String s = name.replace(service, "");
            String[] split = s.split("\\.");
            if (StringUtils.isNotBlank(split[0])) {
                pojo.setEnv(split[0].substring(1));
            }
            pojo.setType(split[1]);
            filePojos.add(pojo);
        }
        return filePojos;
    }

    /**
     * NativeEnvFileRepository
     * @description 解析文件路径
     * @author lichengyong
     * @date 2019/9/12 17:52
     * @param applicationFile 配置文件信息
     * @return java.lang.String
     * @version 1.0
     */
    private String resolvePath(ApplicationFile applicationFile) {
        String label = applicationFile.getLabel();
        String service = applicationFile.getService();
        String env = applicationFile.getEnv();
        String type = applicationFile.getType();
        String filepath = searchLocation;
        // 处理标签为空
        if (label != null && !label.isEmpty()) {
            // 判断标签文件夹是否存在，如果不存在就创建文件夹
            File file = new File(searchLocation + label);
            if (!file.exists()) {
                file.mkdir();
            }
            filepath = file.getAbsolutePath();
        }
        String filename;
        //判断profile是否为空
        if (StringUtils.isNotBlank(env)) {
            filename = service + APP_PRO_SEPARATOR + env;
        } else {
            filename = service;
        }
        String path;
        // 判断类型是否存在，如果类型存在就读取相应类型文件，否则顺序读取
        if (type == null || type.isEmpty()) {
            throw new ConfigAdminApiException("文件类型不能为空");
        } else {
            path = filepath + File.separator + filename + "." + type;
        }
        return path;
    }
}

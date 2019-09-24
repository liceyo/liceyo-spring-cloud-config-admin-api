package com.liceyo.utils;

import com.liceyo.core.entity.ApplicationStorageConfigs;
import com.liceyo.core.storage.ApplicationStorageEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationFileUtils
 * @description 应用文件工具
 * @author lichengyong
 * @date 2019/9/12 13:37
 * @version 1.0
 */
public class ApplicationFileUtils {
    private static Logger logger = LogManager.getLogger(ApplicationFileUtils.class);
    /** 默认编码 **/
    private static final String DEFAULT_CHARSET = "utf-8";

    private ApplicationFileUtils() {
    }

    /**
     * ApplicationFileUtils
     *
     * @param path 配置文件路径
     * @param text 文本内容
     * @description 写配置文件
     * @author lichengyong
     * @date 2019/9/12 13:37
     * @version 1.0
     */
    public static void write(String path, String text) throws IOException {
        File file = ResourceUtils.getFile(path);
        try (FileOutputStream os = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            bos.write(text.getBytes(Charset.forName(DEFAULT_CHARSET)));
        }
    }

    /**
     * ApplicationFileUtils
     *
     * @param path 路径
     * @return java.lang.String
     * @description 从配置文件中读取文本
     * @author lichengyong
     * @date 2019/9/12 13:37
     * @version 1.0
     */
    public static String read(String path) throws IOException {
        File file = ResourceUtils.getFile(path);
        StringBuilder text = new StringBuilder();
        try (FileInputStream is = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis, DEFAULT_CHARSET), 5 * 1024 * 1024);
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        }
        return text.toString();
    }

    /**
     * ApplicationFileUtils
     *
     * @param path 路径
     * @return java.lang.String
     * @description 从配置文件中读取行文本到列表
     * @author lichengyong
     * @date 2019/9/12 13:37
     * @version 1.0
     */
    public static List<String> readLine(String path) throws IOException {
        File file = ResourceUtils.getFile(path);
        List<String> result = new ArrayList<>();
        try (FileInputStream is = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis, DEFAULT_CHARSET), 5 * 1024 * 1024);
            String line;
            while ((line = reader.readLine()) != null) {
                String trim = line.trim();
                if (StringUtils.isEmpty(trim)) {
                    continue;
                }
                result.add(trim);
            }
        }
        return result;
    }

    /**
     * ApplicationFileUtils
     *
     * @param path 路径
     * @return java.lang.String
     * @description 从配置文件中读取行文本到列表
     * @author lichengyong
     * @date 2019/9/12 13:37
     * @version 1.0
     */
    public static ApplicationStorageConfigs readAppByLine(String path) throws IOException {
        File file = ResourceUtils.getFile(path);
        ApplicationStorageConfigs configs = new ApplicationStorageConfigs();
        try (FileInputStream is = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis, DEFAULT_CHARSET), 5 * 1024 * 1024);
            String line;
            String config = null;
            while ((line = reader.readLine()) != null) {
                String trim = line.trim();
                if (StringUtils.isEmpty(trim)) {
                    continue;
                }
                if (trim.startsWith("[") && trim.endsWith("]")) {
                    config = trim;
                    continue;
                }
                if (ApplicationStorageEnum.SERVICE.getSign().equals(config)) {
                    configs.getServices().add(trim);
                } else if (ApplicationStorageEnum.ENV.getSign().equals(config)) {
                    configs.getEnvs().add(trim);
                } else if (ApplicationStorageEnum.LABEL.getSign().equals(config)) {
                    configs.getLabels().add(trim);
                }
            }
        }
        return configs;
    }

    /**
     * ApplicationFileUtils
     * @description 删除文件
     * @author lichengyong
     * @date 2019/9/12 13:37
     * @param path 路径
     * @version 1.0
     */
    public static void delete(String path) {
        try {
            File file = ResourceUtils.getFile(path);
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        } catch (IOException e) {
            logger.error(e);
        }

    }
}

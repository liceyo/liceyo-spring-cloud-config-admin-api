package com.liceyo.core.file;

import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.entity.ApplicationFile;
import com.liceyo.utils.ApplicationFileUtils;
import com.liceyo.utils.GitTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GitEnvFileRepository
 * @description Git环境配置文件操作
 * @author lichengyong
 * @date 2019/9/12 18:00
 * @version 1.0
 */
public class GitEnvFileRepository implements EnvFileRepository {
    private static Logger logger = LogManager.getLogger(GitEnvFileRepository.class);
    private GitTemplate gitTemplate;
    private String searchLocation;

    public GitEnvFileRepository(JGitEnvironmentRepository repository) {
        this.searchLocation = GitTemplate.getGitBaseDir(repository);
        String searchPath = repository.getSearchPaths()[0];
        if (StringUtils.isNotBlank(searchPath)) {
            this.searchLocation = this.searchLocation + File.separator + searchPath;
        }
        File localDir = new File(searchLocation).getParentFile();
        this.gitTemplate = GitTemplate.createFromJGitEnvironmentRepository(repository, localDir);
        logger.info("配置文件搜索路径:{}", this.searchLocation);
        // 切换回主分支
        try (Git git = gitTemplate.client()) {
            gitTemplate.checkoutBranch(git, "master");
        } catch (IOException | GitAPIException e) {
            throw new ConfigAdminApiException("切换分支失败", e);
        }
    }

    /**
     * EnvFileRepository
     * @description 判断配置文件是否存在
     * @author lichengyong
     * @date 2019/9/12 17:45
     * @param file 配置文件信息
     * @return boolean
     * @version 1.0
     */
    @Override
    public boolean exist(ApplicationFile file) {
        String path = resolvePath(file);
        return new File(path).exists();
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
            gitTemplate.refresh();
        } catch (GitAPIException | IOException e) {
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
        // 将修改提交到Git仓库
        try {
            gitTemplate.refresh();
        } catch (GitAPIException | IOException e) {
            throw new ConfigAdminApiException(e);
        }
    }

    /**
     * EnvFileRepository
     * @description 列出配置文件
     * @author lichengyong
     * @date 2019/9/12 17:47
     * @param file 配置文件信息
     * @return java.util.List<com.liceyo.core.entity.ApplicationFile>
     * @version 1.0
     */
    @Override
    public List<ApplicationFile> list(ApplicationFile file) {
        String label = file.getLabel();
        String service = file.getService();
        String env = file.getEnv();
        List<ApplicationFile> files = new ArrayList<>();
        String filepath = searchLocation;
        // 切换分支
        if (StringUtils.isBlank(label)) {
            label = DEFAULT_LABEL;
        }
        try (Git git = this.gitTemplate.client()) {
            this.gitTemplate.checkoutBranch(git, label);
        } catch (IOException | GitAPIException e) {
            throw new ConfigAdminApiException(e);
        }
        File dir = new File(filepath);
        if (!dir.exists()) {
            return files;
        }
        String filename;
        //判断profile是否为空
        if (StringUtils.isNotBlank(env)) {
            filename = service + APP_PRO_SEPARATOR + env;
        } else {
            filename = service;
        }
        // 查找目录下属于该应用信息的文件
        File[] listFiles = dir.listFiles(f -> f.isFile() && f.getName().startsWith(filename));
        for (File appFile : listFiles != null ? listFiles : new File[0]) {
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
            files.add(pojo);
        }
        return files;
    }

    /**
     * GitEnvFileRepository
     * @description 解析路径
     * @author lichengyong
     * @date 2019/9/12 18:07
     * @param applicationFile 配置文件信息
     * @return java.lang.String
     * @version 1.0
     */
    private String resolvePath(ApplicationFile applicationFile) {
        String filepath = searchLocation;
        // 切换分支
        String label = applicationFile.getLabel();
        if (StringUtils.isBlank(label)) {
            label = DEFAULT_LABEL;
        }
        try (Git git = this.gitTemplate.client()) {
            this.gitTemplate.checkoutBranch(git, label);
        } catch (IOException | GitAPIException e) {
            throw new ConfigAdminApiException(e);
        }
        String application = applicationFile.getService();
        String profile = applicationFile.getEnv();
        String type = applicationFile.getType();
        String filename;
        //判断profile是否为空
        if (StringUtils.isNotBlank(profile)) {
            filename = application + APP_PRO_SEPARATOR + profile;
        } else {
            filename = application;
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

package com.liceyo.core.storage;

import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.entity.ApplicationStorageConfigs;
import com.liceyo.utils.ApplicationFileUtils;
import com.liceyo.utils.CacheUtils;
import com.liceyo.utils.GitTemplate;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * GitApplicationStorageRepository
 * @description Git环境应用信息存储操作
 * 其实比起本地环境的存储操作，只加了git刷新而已
 * @author lichengyong
 * @date 2019/9/12 17:01
 * @version 1.0
 */
public class GitApplicationStorageRepository extends BaseApplicationStorageRepository {


    private final String applicationPath;

    /** GIT环境操作 **/
    private GitTemplate gitTemplate;

    public GitApplicationStorageRepository(DiscoveryClient discoveryClient,
                                           JGitEnvironmentRepository repository) throws IOException {
        super(discoveryClient);
        String baseDir = GitTemplate.getGitBaseDir(repository);
        this.gitTemplate = GitTemplate.createFromJGitEnvironmentRepository(repository, new File(baseDir));
        this.applicationPath = baseDir + File.separator + "admin-application.txt";
        final File file = new File(this.applicationPath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * ApplicationStorageRepository
     * @description 读取所有配置信息
     * @author lichengyong
     * @date 2019/9/12 14:17
     * @return com.liceyo.core.entity.ApplicationStorageConfigs
     * @version 1.0
     */
    @Override
    public ApplicationStorageConfigs readStorage() {
        Object obj = CacheUtils.getInstance().get(CACHE_KEY);
        if (obj != null) {
            return (ApplicationStorageConfigs) obj;
        }
        //先刷新文件
        ApplicationStorageConfigs configs;
        try {
            gitTemplate.refresh();
            configs = ApplicationFileUtils.readAppByLine(applicationPath);
        } catch (IOException | GitAPIException e) {
            throw new ConfigAdminApiException(e);
        }
        CacheUtils.getInstance().add(CACHE_KEY, configs, 10, TimeUnit.MINUTES);
        return configs;
    }

    /**
     * ApplicationStorageRepository
     * @description 存储
     * @author lichengyong
     * @date 2019/9/12 14:18
     * @param storageEnum 类型
     * @param item 项
     * @version 1.0
     */
    @Override
    public void storage(ApplicationStorageEnum storageEnum, String item) {
        ApplicationStorageConfigs configs = readStorage();
        configs.add(storageEnum, item);
        CacheUtils.getInstance().remove(CACHE_KEY);
        try {
            ApplicationFileUtils.write(applicationPath, configs.toWriteString());
            gitTemplate.refresh();
        } catch (GitAPIException | IOException e) {
            throw new ConfigAdminApiException(e);
        }
    }

    /**
     * ApplicationStorageRepository
     * @description 删除
     * @author lichengyong
     * @date 2019/9/12 14:18
     * @param storageEnum 类型
     * @param item 项
     * @version 1.0
     */
    @Override
    public void remove(ApplicationStorageEnum storageEnum, String item) {
        ApplicationStorageConfigs configs = readStorage();
        configs.remove(storageEnum, item);
        CacheUtils.getInstance().remove(CACHE_KEY);
        try {
            ApplicationFileUtils.write(applicationPath, configs.toWriteString());
            gitTemplate.refresh();
        } catch (GitAPIException | IOException e) {
            throw new ConfigAdminApiException(e);
        }
    }
}

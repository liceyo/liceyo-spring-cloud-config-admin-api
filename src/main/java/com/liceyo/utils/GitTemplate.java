package com.liceyo.utils;

import com.liceyo.commons.ConfigAdminApiException;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.support.GitCredentialsProviderFactory;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GitTemplate
 * @description Git操作
 * @author lichengyong
 * @date 2019/9/12 13:40
 * @version 1.0
 */
@Data
public class GitTemplate {
    private static Logger logger = LogManager.getLogger(GitTemplate.class);
    /*** 默认标签 **/
    private static final String DEFAULT_LABEL = "master";
    /**
     * git仓库地址
     */
    private String repoUri;
    /**
     * GIT本地文件夹
     */
    private File localDir;
    /**
     * 超时时间
     */
    private int timeout;
    /**
     * 身份认证信息
     */
    private CredentialsProvider credentialsProvider;
    /**
     * TransportConfigCallback
     */
    private TransportConfigCallback transportConfigCallback;

    /**
     * GitTemplate
     * @description 从已有环境创建环境
     * @author lichengyong
     * @date 2019/7/2 14:26
     * @param repository 已有环境
     * @param localDir Git本地目录
     * @return pers.cloud.config.admin.utils.GitTemplate
     * @version 1.0
     */
    public static GitTemplate createFromJGitEnvironmentRepository(JGitEnvironmentRepository repository, File localDir) {
        GitTemplate gitTemplate = new GitTemplate(repository.getUri(), localDir);
        gitTemplate.setTimeout(repository.getTimeout());
        gitTemplate.setTransportConfigCallback(repository.getTransportConfigCallback());
        CredentialsProvider credentialsProvider = new GitCredentialsProviderFactory()
                .createFor(repository.getUri(),
                        repository.getUsername(),
                        repository.getPassword(),
                        repository.getPassphrase(),
                        repository.isSkipSslValidation());
        gitTemplate.setCredentialsProvider(credentialsProvider);
        // 初始化加载下
        try (Git git = gitTemplate.client()) {
            final List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
            String collect = refs.stream().map(Ref::getName).collect(Collectors.joining("|"));
            logger.info("远程分支：{}", collect);
        } catch (IOException | GitAPIException e) {
            logger.error("克隆Git仓库失败");
        }
        return gitTemplate;
    }

    /**
     * GitEnvOperationRepository
     * @description 获取GIT配置文件搜索目录
     * @author lichengyong
     * @date 2019/7/1 15:39
     * @param repository GIT环境
     * @return java.lang.String
     * @version 1.0
     */
    public static String getGitBaseDir(JGitEnvironmentRepository repository) {
        String uri = repository.getUri();
        File file;
        if (uri.startsWith("file:")) {
            try {
                file = new UrlResource(org.springframework.util.StringUtils.cleanPath(uri)).getFile();
            } catch (Exception e) {
                throw new ConfigAdminApiException("Cannot convert uri to file: " + uri);
            }
        } else {
            file = repository.getBasedir();
        }
        String filepath = file.getAbsolutePath() + "-admin";
        final File baseDir = new File(filepath);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        return filepath;
    }

    public GitTemplate(String repoUri, File localDir) {
        this.localDir = localDir;
        this.repoUri = repoUri;
    }

    public Git client() throws GitAPIException, IOException {
        return Git.wrap(getRepo());
    }

    /**
     * GitTemplate
     * @description 刷新仓库
     * @author lichengyong
     * @date 2019/7/2 14:36
     * @version 1.0
     */
    public void refresh() throws GitAPIException, IOException {
        // 将修改提交到Git仓库
        try (Git git = client()) {
            if (canPull(git)) {
                push(git);
            }
            if (addAll(git)) {
                commit(git);
                push(git);
            }
        }
    }

    public void pull(Git git) throws GitAPIException, IOException {
        final PullCommand pull = git.pull();
        configureCommand(pull);
        final Repository repository = git.getRepository();
        pull.setRemoteBranchName(repository.getBranch());
        pull.call();
    }

    public void push(Git git) throws GitAPIException {
        final PushCommand push = git.push();
        configureCommand(push);
        push.call();
    }

    public void fetch(Git git) throws GitAPIException {
        final FetchCommand fetch = git.fetch();
        configureCommand(fetch);
        fetch.call();
    }

    public void commit(Git git) throws GitAPIException {
        final CommitCommand commit = git.commit();
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        commit.setMessage("commit." + format.format(System.currentTimeMillis()));
        commit.call();
    }

    public void clean() {
        final File[] files = localDir.listFiles(n -> !".git".equals(n.getName()));
        assert files != null;
        Arrays.stream(files).forEach(File::delete);
    }

    public boolean addAll(Git git) throws GitAPIException {
        final DiffCommand diff = git.diff();
        final List<DiffEntry> entries = diff.call();
        final AddCommand add = git.add();
        if ((entries == null) || entries.isEmpty()) {
            return false;
        }
        for (DiffEntry entry : entries) {
            if (entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
                add.addFilepattern(entry.getOldPath());
            } else {
                add.addFilepattern(entry.getNewPath());
            }
        }
        add.call();
        return true;
    }

    public void add(Git git, String filePattern) throws GitAPIException {
        final AddCommand add = git.add();
        add.addFilepattern(filePattern);
        add.call();
    }

    /**
     * GitTemplate
     * @description 从远程同名分支检出
     * @author lichengyong
     * @date 2019/7/1 17:46
     * @param git git客户端
     * @param branchName 分支名称
     * @version 1.0
     */
    public void checkoutForOrigin(Git git, String branchName) throws GitAPIException {
        git.checkout()
                .setCreateBranch(true)
                .setName(branchName)
                .setStartPoint("origin/" + branchName)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                .call();
    }

    /**
     * GitTemplate
     * @description 创建分支
     * @author lichengyong
     * @date 2019/7/2 10:43
     * @param git git客户端
     * @param branchName 分支名称
     * @version 1.0
     */
    public void createBranch(Git git, String branchName) throws GitAPIException {
        // 如果现有列表中已有该分支就不创建
        final List<Ref> branches = git.branchList().call();
        if (branches.stream().anyMatch(ref -> ref.getName().endsWith(branchName))) {
            return;
        }
        // 从主分支创建分支
        git.branchCreate()
                .setName(branchName)
                .setStartPoint("origin/master")
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                .call();

    }

    /**
     * GitTemplate
     * @description 删除分支
     * @author lichengyong
     * @date 2019/7/2 10:45
     * @param git git客户端
     * @param branchName 分支名称
     * @version 1.0
     */
    public void deleteBranch(Git git, String branchName) throws GitAPIException {
        final List<Ref> branches = git
                .branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call();
        final Set<String> collect = branches.stream()
                .map(Ref::getName)
                .filter(name -> name.endsWith("/" + branchName))
                .collect(Collectors.toSet());
        System.out.println(collect.stream().collect(Collectors.joining(",")));
        if (collect.isEmpty()) {
            return;
        }
        git.branchDelete()
                .setBranchNames(collect.toArray(new String[0]))
                .call();
    }

    /**
     * GitTemplate
     * @description 检出分支
     * @author lichengyong
     * @date 2019/7/1 17:54
     * @version 1.0
     */
    public void checkoutBranch(Git git, String branchName) throws GitAPIException, IOException {
        final Repository repository = git.getRepository();
        final String branch = repository.getBranch();
        if (branch.equals(branchName)) {
            return;
        }
        // 切换分支前先保存已有的
        if (canPull(git)) {
            pull(git);
        }
        if (addAll(git)) {
            commit(git);
        }
        push(git);
        // 已有的分支不能创建
        final List<Ref> branches = git.branchList().call();
        boolean create = true;
        if (branches.stream().anyMatch(ref -> ref.getName().endsWith("/" + branchName))) {
            create = false;
        }
        // 检出分支
        git.checkout()
                .setCreateBranch(create)
                .setName(branchName)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                .call();
    }

    /**
     * GitTemplate
     * @description 是否可以拉取
     * @author lichengyong
     * @date 2019/7/2 11:33
     * @param git 客户端
     * @return boolean
     * @version 1.0
     */
    public boolean canPull(Git git) throws IOException, GitAPIException {
        final String branch = git.getRepository().getBranch();
        final List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        return refs.stream().anyMatch(ref -> ref.getName().endsWith("/" + branch));
    }


    private void configureCommand(TransportCommand command) {
        command.setTimeout(timeout);
        if (credentialsProvider != null) {
            command.setCredentialsProvider(credentialsProvider);
        }
        if (transportConfigCallback != null) {
            command.setTransportConfigCallback(transportConfigCallback);
        }
    }

    private Repository getRepo() throws GitAPIException, IOException {
        File file = new File(localDir, ".git");
        if (file.exists()) {
            return getRepoForExist();
        } else {
            return getRepoForClone();
        }
    }

    private Repository getRepoForExist() throws IOException {
        return new FileRepository(localDir + "/.git");
    }

    private Repository getRepoForClone() throws GitAPIException {
        localDir.deleteOnExit();
        localDir.mkdir();
        try (Git result = Git.cloneRepository()
                .setURI(repoUri)
                .setBranch(DEFAULT_LABEL)
                .setCredentialsProvider(credentialsProvider)
                .setDirectory(localDir)
                .setTransportConfigCallback(transportConfigCallback)
                .call()) {
            return result.getRepository();
        }
    }

}

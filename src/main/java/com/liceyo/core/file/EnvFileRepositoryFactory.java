package com.liceyo.core.file;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * EnvFileRepositoryFactory
 * @description 配置文件操作factory
 * @author lichengyong
 * @date 2019/9/12 18:12
 * @version 1.0
 */
@Configuration
@AutoConfigureAfter({
        EnvironmentRepositoryConfiguration.class
})
@Import({
        NativeEnvFileRepositoryFactory.class,
        GitEnvFileRepositoryFactory.class
})
public class EnvFileRepositoryFactory {
}

@Configuration
@Profile("native")
class NativeEnvFileRepositoryFactory {

    @Bean("envFileRepository")
    @ConditionalOnMissingBean
    public EnvFileRepository envFileRepository(NativeEnvironmentRepository repository) {
        return new NativeEnvFileRepository(repository);
    }
}

@Configuration
@Profile("git")
class GitEnvFileRepositoryFactory {

    @Bean("envFileRepository")
    @ConditionalOnMissingBean
    public EnvFileRepository envFileRepository(MultipleJGitEnvironmentRepository repository) {
        return new GitEnvFileRepository(repository);
    }
}

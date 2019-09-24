package com.liceyo.api;

import com.liceyo.commons.CommonResponse;
import com.liceyo.commons.ConfigAdminApiException;
import com.liceyo.core.entity.ApplicationFile;
import com.liceyo.core.file.EnvFileRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EnvFileApi
 * @description 配置文件操作API
 * @author lichengyong
 * @date 2019/9/16 10:26
 * @version 1.0
 */
@RestController
@RequestMapping("/api/file")
public class EnvFileApi{
    private final EnvFileRepository envFileRepository;

    public EnvFileApi(EnvFileRepository envFileRepository) {
        this.envFileRepository = envFileRepository;
    }

    @GetMapping("/list")
    public CommonResponse list(ApplicationFile file) {
        file.clearDefault();
        List<ApplicationFile> list = envFileRepository.list(file);
        return CommonResponse.ok(list);
    }

    @GetMapping
    public CommonResponse get(ApplicationFile file) {
        file.clearDefault();
        String text = envFileRepository.read(file);
        return CommonResponse.ok(text);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CommonResponse update(@RequestBody ApplicationFile file) {
        file.clearDefault();
        envFileRepository.write(file);
        return CommonResponse.ok();
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CommonResponse create(@RequestBody ApplicationFile file) {
        file.clearDefault();
        if (envFileRepository.exist(file)) {
            throw new ConfigAdminApiException("该配置文件已存在");
        }
        envFileRepository.write(file);
        return CommonResponse.ok();
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CommonResponse delete(@RequestBody ApplicationFile file) {
        file.clearDefault();
        envFileRepository.delete(file);
        return CommonResponse.ok();
    }

}

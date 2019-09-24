package com.liceyo.api;

import com.liceyo.commons.CommonResponse;
import com.liceyo.core.entity.ServiceStatus;
import com.liceyo.core.storage.ApplicationStorageEnum;
import com.liceyo.core.storage.ApplicationStorageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ApplicationStorageApi
 * @description 应用信息存储API
 * @author lichengyong
 * @date 2019/9/16 10:18
 * @version 1.0
 */
@RestController
@RequestMapping("/api/storage")
public class ApplicationStorageApi {
    private final ApplicationStorageRepository applicationStorageRepository;

    public ApplicationStorageApi(ApplicationStorageRepository applicationStorageRepository) {
        this.applicationStorageRepository = applicationStorageRepository;
    }

    @GetMapping("/services")
    public CommonResponse services(){
        List<ServiceStatus> list = applicationStorageRepository.readAllService();
        return CommonResponse.okIfNotNull(list,"没有找到服务");
    }

    @PostMapping("/services/{item}")
    public CommonResponse saveService(@PathVariable String item){
        applicationStorageRepository.storage(ApplicationStorageEnum.SERVICE,item);
        return CommonResponse.ok();
    }

    @DeleteMapping("/services/{item}")
    public CommonResponse deleteService(@PathVariable String item){
        applicationStorageRepository.remove(ApplicationStorageEnum.SERVICE,item);
        return CommonResponse.ok();
    }

    @GetMapping("/envs")
    public CommonResponse envs(){
        List<String> envs = applicationStorageRepository.readStorage().getEnvs();
        return CommonResponse.okIfNotNull(envs,"没有找到环境");
    }

    @PostMapping("/envs/{item}")
    public CommonResponse saveEnv(@PathVariable String item){
        applicationStorageRepository.storage(ApplicationStorageEnum.ENV,item);
        return CommonResponse.ok();
    }

    @DeleteMapping("/envs/{item}")
    public CommonResponse deleteEnv(@PathVariable String item){
        applicationStorageRepository.remove(ApplicationStorageEnum.ENV,item);
        return CommonResponse.ok();
    }

    @GetMapping("/labels")
    public CommonResponse labels(){
        List<String> labels = applicationStorageRepository.readStorage().getLabels();
        return CommonResponse.okIfNotNull(labels,"没有找到标签");
    }

    @PostMapping("/labels/{item}")
    public CommonResponse saveLabel(@PathVariable String item){
        applicationStorageRepository.storage(ApplicationStorageEnum.LABEL,item);
        return CommonResponse.ok();
    }

    @DeleteMapping("/labels/{item}")
    public CommonResponse deleteLabel(@PathVariable String item){
        applicationStorageRepository.remove(ApplicationStorageEnum.LABEL,item);
        return CommonResponse.ok();
    }
}

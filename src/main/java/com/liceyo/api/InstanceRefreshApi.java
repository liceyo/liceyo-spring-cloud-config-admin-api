package com.liceyo.api;

import com.liceyo.commons.CommonResponse;
import com.liceyo.core.entity.InstanceRefreshResult;
import com.liceyo.core.refresh.InstanceRefreshRepository;
import org.springframework.web.bind.annotation.*;

/**
 * InstanceRefreshApi
 * @description 实例配置刷新API
 * @author lichengyong
 * @date 2019/9/16 10:12
 * @version 1.0
 */
@RestController
@RequestMapping("/api/instance")
public class InstanceRefreshApi {
    private final InstanceRefreshRepository instanceRefreshRepository;

    public InstanceRefreshApi(InstanceRefreshRepository instanceRefreshRepository) {
        this.instanceRefreshRepository = instanceRefreshRepository;
    }

    @GetMapping
    public CommonResponse services() {
        return CommonResponse.ok(instanceRefreshRepository.service());
    }

    @PostMapping("/{service}")
    public CommonResponse refresh(@PathVariable String service) {
        InstanceRefreshResult result = instanceRefreshRepository.refresh(service);
        return CommonResponse.okIfNotNull(result, "没有找到相应实例");
    }

}

package com.liceyo.core.refresh;

import com.alibaba.fastjson.JSON;
import com.liceyo.core.AdminProperties;
import com.liceyo.core.entity.InstanceRefreshDetail;
import com.liceyo.core.entity.InstanceRefreshResult;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpInstanceRefreshRepository
 * @description Http方式实例刷新
 * @author lichengyong
 * @date 2019/9/12 11:38
 * @version 1.0
 */
public class HttpInstanceRefreshRepository implements InstanceRefreshRepository {
    /** discoveryClient **/
    private final DiscoveryClient discoveryClient;
    /** http **/
    private final RestTemplate restTemplate;
    /** 刷新接口 **/
    private final String refreshUrl;

    public HttpInstanceRefreshRepository(AdminProperties adminProperties, DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
        this.refreshUrl = adminProperties.getRefreshUrl();
    }
    /**
     * InstanceRefreshRepository
     * @description 获取所有服务信息（按服务名分）
     * @author lichengyong
     * @date 2019/9/12 11:03
     * @return java.util.Map<java.lang.String, java.util.List < org.springframework.cloud.client.ServiceInstance>>
     * @version 1.0
     */
    @Override
    public Map<String, List<ServiceInstance>> service() {
        Map<String, List<ServiceInstance>> msl = new HashMap<>();
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            List<ServiceInstance> sis = discoveryClient.getInstances(service);
            msl.put(service, sis);
        }
        return msl;
    }

    /**
     * InstanceRefreshRepository
     * @description 刷新指定服务
     * @author lichengyong
     * @date 2019/9/12 11:28
     * @param service 服务
     * @return com.liceyo.core.entity.InstanceRefreshResult
     * @version 1.0
     */
    @Override
    public InstanceRefreshResult refresh(String service) {
        List<ServiceInstance> instances = discoveryClient.getInstances(service);
        if (CollectionUtils.isEmpty(instances)){
            return null;
        }
        // 获取该服务的全部实例
        InstanceRefreshResult result = new InstanceRefreshResult();
        for (ServiceInstance instance : instances) {
            InstanceRefreshDetail detail = new InstanceRefreshDetail();
            // 获取实例详细信息
            EurekaDiscoveryClient.EurekaServiceInstance eurekaInstance = (EurekaDiscoveryClient.EurekaServiceInstance) instance;
            InstanceInfo info = eurekaInstance.getInstanceInfo();
            detail.setInstanceId(info.getInstanceId());
            detail.setIpAddr(info.getIPAddr());
            detail.setStatus(info.getStatus().name());
            Map<String, String> metadata = info.getMetadata();
            if (metadata !=null && metadata.containsKey("management.port")){
                detail.setManagementPort(metadata.get("management.port"));
            }else {
                detail.setManagementPort(String.valueOf(instance.getPort()));
            }
            // 如果实例没有在线则不处理
            if (!InstanceInfo.InstanceStatus.UP.equals(info.getStatus())){
                continue;
            }
            // 通过实例接口刷新配置
            try {
                URI uri = instance.getUri();
                String url = uri.getScheme() + "://" + info.getIPAddr() + ":" + detail.getManagementPort() + refreshUrl;
                HttpHeaders headers=new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);
                String body = restTemplate.postForObject(url, requestEntity, String.class);
                detail.setRefreshData(JSON.parseArray(body));
                result.incSuccess();
            }catch (Exception e){
                detail.setThrowable(e);
                result.incFailure();
            } finally {
                result.addDetail(detail);
            }
        }
        return result;
    }
}

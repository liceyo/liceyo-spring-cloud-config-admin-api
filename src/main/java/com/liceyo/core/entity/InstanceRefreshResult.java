package com.liceyo.core.entity;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * InstanceRefreshResult
 * @description 实例刷新结果
 * @author lichengyong
 * @date 2019/9/12 11:23
 * @version 1.0
 */
@Data
public class InstanceRefreshResult {
    private int successInstance;
    private int failureInstance;
    private List<InstanceRefreshDetail> details;

    public InstanceRefreshResult(){
        this.successInstance=0;
        this.failureInstance=0;
        this.details =new LinkedList<>();
    }

    /**
     * RefreshResult
     * @description 成功自增
     * @author lichengyong
     * @date 2019/9/12 11:23
     * @version 1.0
     */
    public void incSuccess(){
        this.successInstance++;
    }

    /**
     * RefreshResult
     * @description 失败自增
     * @author lichengyong
     * @date 2019/9/12 11:23
     * @version 1.0
     */
    public void incFailure(){
        this.failureInstance++;
    }

    /**
     * RefreshResult
     * @description 添加实例刷新详情
     * @author lichengyong
     * @date 2019/9/12 11:23
     * @param detail 刷新详情
     * @version 1.0
     */
    public void addDetail(InstanceRefreshDetail detail){
        details.add(detail);
    }
}

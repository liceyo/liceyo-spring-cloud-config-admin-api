package com.liceyo.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * CacheUtils
 * @description 缓存类
 * 避免引入其他包，自己写个简单缓存
 * @author lichengyong
 * @date 2019/9/12 14:42
 * @version 1.0
 */
public class CacheUtils {
    /** 使用map缓存 **/
    private Map<String, Node> cacheMap;
    /** 超时队列 **/
    private PriorityQueue<Node> expireQueue;
    /** 读锁 **/
    private Lock readLock;
    /** 写锁 **/
    private Lock writeLock;

    private CacheUtils(boolean clear) {
        System.out.println(clear);
        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        cacheMap = new HashMap<>(1024);
        expireQueue = new PriorityQueue<>(1024);
        if (clear) {
            ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("cache-pool-%d").build();
            // Schedule线程池
            ScheduledExecutorService swapExpiredPool = new ScheduledThreadPoolExecutor(4, factory);
            // 每隔1分钟清理一次过期数据
            swapExpiredPool.scheduleWithFixedDelay(this::clearExpire, 1, 1, TimeUnit.SECONDS);
        }
    }

    /** 不清除过期缓存 **/
    private static class InnerNoClear {
        /** 实例 **/
        private static CacheUtils instance = new CacheUtils(false);
    }

    /** 清除过期缓存 **/
    private static class InnerClear {
        /** 实例 **/
        private static CacheUtils instance = new CacheUtils(true);
    }

    /**
     * CacheUtils
     * @description 获取单例实例-不会清除过期缓存
     * @author lichengyong
     * @date 2019/9/12 15:08
     * @return com.liceyo.utils.CacheUtils
     * @version 1.0
     */
    public static CacheUtils getInstance() {
        return InnerNoClear.instance;
    }

    /**
     * CacheUtils
     * @description 获取单例实例-会清除过期缓存
     * @author lichengyong
     * @date 2019/9/12 15:08
     * @return com.liceyo.utils.CacheUtils
     * @version 1.0
     */
    public static CacheUtils getClearInstance() {
        return InnerClear.instance;
    }


    /**
     * CacheUtils
     * @description 简单缓存
     * @author lichengyong
     * @date 2019/9/12 15:15
     * @param key 键
     * @param data 数据
     * @return 老数据
     * @version 1.0
     */
    public Object add(String key, Object data, long ttl, TimeUnit unit) {
        long mTtl = TimeUnit.MILLISECONDS.convert(ttl, unit);
        long expire = System.currentTimeMillis() + mTtl;
        Node node = new Node(key, data, expire);
        writeLock.lock();
        try {
            Node old = cacheMap.put(key, node);
            if (old != null) {
                expireQueue.remove(old);
                return old.value;
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * CacheUtils
     * @description 简单获取
     * @author lichengyong
     * @date 2019/9/12 15:15
     * @param key 键
     * @return java.lang.Object
     * @version 1.0
     */
    public Object get(String key) {
        readLock.lock();
        try {
            Node node = cacheMap.get(key);
            if (node == null) {
                return null;
            }
            // 如果过期，返回null
            if (node.expireTime < System.currentTimeMillis()) {
                return null;
            }
            return node.value;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * CacheUtils
     * @description 简单删除
     * @author lichengyong
     * @date 2019/9/12 15:16
     * @param key 键
     * @return 老数据
     * @version 1.0
     */
    public Object remove(String key) {
        writeLock.lock();
        try {
            Node node = cacheMap.remove(key);
            if (node != null) {
                expireQueue.remove(node);
                return node.value;
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * CacheUtils
     * @description 清理过期缓存
     * @author lichengyong
     * @date 2019/9/12 15:43
     * @version 1.0
     */
    private void clearExpire() {
        long now = System.currentTimeMillis();
        while (true) {
            readLock.lock();
            Node node;
            try {
                node = expireQueue.peek();
                if (node == null || node.expireTime > now) {
                    break;
                }
            } finally {
                readLock.unlock();
            }
            writeLock.lock();
            try {
                cacheMap.remove(node.key);
                expireQueue.poll();
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * CacheUtils
     * @description 节点
     * @author lichengyong
     * @date 2019/9/12 15:30
     * @version 1.0
     */
    @AllArgsConstructor
    private static class Node implements Comparable<Node> {
        private String key;
        private Object value;
        private long expireTime;

        @Override
        public int compareTo(Node o) {
            long r = this.expireTime - o.expireTime;
            if (r > 0) {
                return 1;
            }
            if (r < 0) {
                return -1;
            }
            return 0;
        }

    }

}

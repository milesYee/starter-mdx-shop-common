package com.mdx.common.util;

import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RedissonUtil {
    private static final Logger log = LoggerFactory.getLogger(RedissonUtil.class);

    private static final String KEY_PREFIX = "lock:";

    private static RedissonClient redissonClient = SpringUtils
            .getBean(RedissonClient.class);

    private RedissonUtil() {
    }

    /**
     * 不设置超时时间
     *
     * @param lockKey
     */
    public static void lock(String lockKey) {
        RLock lock = redissonClient.getLock(KEY_PREFIX+lockKey);
        lock.lock();
    }

    /**
     * 获取分布式锁(先加锁)
     *
     * @param lockKey
     * @return RLock
     */
    public static RLock lockAndReturn(String lockKey) {
        RLock lock = redissonClient.getLock(KEY_PREFIX+lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 获取分布式锁对象(还未加锁)
     *
     * @param lockKey
     * @return RLock
     */
    public static RLock getLock(String lockKey) {
        return redissonClient.getLock(KEY_PREFIX+lockKey);
    }

    /**
     * @param lockKey
     * @param time    锁的释放（过期）时间，单位：秒
     */
    public static void lock(String lockKey, long time) {
        RLock lock = redissonClient.getLock(KEY_PREFIX+lockKey);
        lock.lock(time, TimeUnit.SECONDS);
    }

    /**
     * 获取分布式锁
     *
     * @param lockKey
     * @param time    锁的释放（过期）时间，单位：秒
     * @return
     */
    public static RLock lockAndReturn(String lockKey, long time) {
        RLock lock = redissonClient.getLock(KEY_PREFIX+lockKey);
        lock.lock(time, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public static void unLock(String lockKey) {
        RLock lock = redissonClient.getLock(KEY_PREFIX+lockKey);
        lock.unlock();
    }

    /**
     * 释放锁
     *
     * @param lock 锁对象
     */
    public static void unLock(RLock lock) {
        lock.unlock();
    }

    /**
     * 尝试获取锁；在获取该锁时如果被其他线程先拿到锁就会进入等待，等待 waitTime 时间，如果还没用机会获取到锁就放弃，返回 false；
     * 若获得了锁，除非是调用 unlock 释放，那么会一直持有锁，直到超过 leaseTime 指定的时间。
     *
     * @param lockKey
     * @param unit      时间单位
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public static boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(KEY_PREFIX+lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            log.error("tryLock exception:", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 获取原子数
     *
     * @param key
     * @return
     */
    public static RAtomicLong getRAtomicLong(String key) {
        return redissonClient.getAtomicLong(KEY_PREFIX+key);
    }

    /**
     * 获取原子数
     *
     * @param key
     * @param date
     * @return
     */
    public static RAtomicLong getRAtomicLong(String key, Date date) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(KEY_PREFIX+key);
        rAtomicLong.expireAt(date);
        return rAtomicLong;
    }

    /**
     * 获取记数锁
     *
     * @param key
     * @return
     */
    public static RCountDownLatch getRCountDownLatch(String key) {
        return redissonClient.getCountDownLatch(KEY_PREFIX+key);
    }

    public static RSet<Object> getRset(String key) {
        return redissonClient.getSet(KEY_PREFIX+key);
    }

    /**
     * @param key
     * @param date 过期时间
     * @return
     */
    public static RSet<Object> getRset(String key, Date date) {
        RSet<Object> rSet = redissonClient.getSet(KEY_PREFIX+key);
        rSet.expireAt(date);
        return rSet;
    }

}

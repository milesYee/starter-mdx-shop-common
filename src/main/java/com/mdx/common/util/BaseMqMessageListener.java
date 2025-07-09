package com.mdx.common.util;

import com.alibaba.fastjson.JSONObject;
import com.mdx.common.base.BaseMqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public abstract class BaseMqMessageListener<T extends BaseMqMessage> {
    /**
     * 这里的日志记录器是哪个子类的就会被哪个子类的类进行初始化
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 消息者名称
     *
     * @return 消费者名称
     */
    protected abstract String consumerName();

    /**
     * 消息处理
     *
     * @param message 待处理消息
     * @throws Exception 消费异常
     */
    protected abstract void handleMessage(T message);

    /**
     * 由父类来完成基础的日志和调配，下面的只是提供一个思路
     */
    public void dispatchMessage(T message) {
        logger.debug("[{}]消费者收到消息[{}]", consumerName(), JSONObject.toJSONString(message));
        /*RLock lock = RedissonUtil.getLock(message.getKey());
        if(lock.isLocked()){
            logger.debug("[{}]消息消费重复，已舍弃",message.getKey());
            return;
        }*/
        long start = Instant.now().toEpochMilli();
        handleMessage(message);
        long end = Instant.now().toEpochMilli();
        logger.debug("[{}]消息消费成功，耗时[{}ms]",message.getKey(), (end - start));
        //RedissonUtil.lock(message.getKey());
//        try {
//        } catch (Exception e) {
////            logger.error("消息消费异常", e);
//            throw new RuntimeException(e);
//        }

    }
}

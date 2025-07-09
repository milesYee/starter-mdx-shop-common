package com.mdx.common.config;

import com.alibaba.fastjson.JSONObject;
import com.mdx.common.base.BaseMqMessage;
import com.mdx.common.constant.RocketMqSysConstant;
import com.mdx.common.util.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RocketMqTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqTemplate.class);
    private static final AtomicInteger sequenceNumberGenerator = new AtomicInteger(0);

    @Resource(name = "rocketMQTemplate")
    private RocketMQTemplate template;

    /**
     * 获取模板，如果封装的方法不够提供原生的使用方式
     */
    public RocketMQTemplate getTemplate() {
        return template;
    }

    /**
     * 构建目的地
     */
    public String buildDestination(String topic, String tag) {
        if(StringUtils.isEmpty(tag)){
            return topic;
        }
        return topic + RocketMqSysConstant.DELIMITER + tag;
    }

    /**
     * 发送顺序消息
     */
    public <T extends BaseMqMessage> SendResult sendOrderly(String topic, String tag, T message, String hashKey) {
        try {
            Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
            SendResult result = template.syncSendOrderly(buildDestination(topic,tag), sendMessage, hashKey);
            LOGGER.info("[{}]顺序消息发送成功，hashKey：{}，内容：{}", buildDestination(topic,tag), hashKey, JSONObject.toJSONString(result));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送同步消息
     */
    public <T extends BaseMqMessage> SendResult send(String topic, String tag, T message) {

        return send(buildDestination(topic,tag), message);
    }

    public <T extends BaseMqMessage> SendResult send(String destination, T message) {
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        SendResult sendResult = template.syncSend(destination, sendMessage);
        // 此处为了方便查看给日志转了json，根据选择选择日志记录方式，例如ELK采集
        LOGGER.debug("[{}]同步消息[{}]发送结果[{}]", destination, JSONObject.toJSONString(message), JSONObject.toJSON(sendResult));
        return sendResult;
    }

    /***
     * @Description: 发送延迟消息
     * @Param: [topic, tag, message, delayLevel：支持18个级别1-18 分别是1s、5s、10s、30s、1m、2m、3m、4m、5m、6m、7m、8m、9m、10m、20m、30m、1h、2h]
     * @return: org.apache.rocketmq.client.producer.SendResult
     * @Author: tanghs
     * @Date: 2024/12/23 19:47
     */
    public <T extends BaseMqMessage> SendResult send(String topic, String tag, T message, int delayLevel) {
        return send(buildDestination(topic,tag), message, delayLevel);
    }

    public <T extends BaseMqMessage> SendResult send(String destination, T message, int delayLevel) {
        Message<T> sendMessage = MessageBuilder.withPayload(message).setHeader(RocketMQHeaders.KEYS, message.getKey()).build();
        SendResult sendResult = template.syncSend(destination, sendMessage, 3000, delayLevel);
        LOGGER.debug("[{}]延迟等级[{}]消息[{}]发送结果[{}]", destination, delayLevel, JSONObject.toJSONString(message), JSONObject.toJSONString(sendResult));
        return sendResult;
    }

    /**
     * 发送事务消息
     *
     * @param topic     主题
     * @param tag       标签
     * @param message   事务消息内容
     * @param arg       本地事务执行的参数
     * @param listener  事务监听器
     */
    public <T extends BaseMqMessage> TransactionSendResult sendInTransaction(
            String topic, String tag, T message, Object arg, TransactionListener listener) {
        Message<T> sendMessage = MessageBuilder.withPayload(message)
                .setHeader(RocketMQHeaders.KEYS, message.getKey())
                .build();
        String destination = buildDestination(topic, tag);
        TransactionSendResult sendResult = template.sendMessageInTransaction(destination, sendMessage, arg);
        LOGGER.debug("[{}]事务消息[{}]发送结果[{}]", destination, JSONObject.toJSONString(message), JSONObject.toJSON(sendResult));
        return sendResult;
    }
}
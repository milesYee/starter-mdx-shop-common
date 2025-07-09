package com.mdx.common.base;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class BaseMqMessage {
    /**
     * 业务键，用于RocketMQ控制台查看消费情况
     */
    protected String key = UUID.randomUUID().toString();

    /**
     * 发送消息来源，用于排查问题
     */
    protected String source = "";
    /**
     * 发送时间
     */
    protected LocalDateTime sendTime = LocalDateTime.now();


}
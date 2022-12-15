package tech.yiki.producer.demo.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2022/12/15 18:51
 * @Email: moumouguan@gmail.com
 */
@Configuration // 声明该类是一个配置类
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct // 构造方法执行之后就会执行, 项目时添加此配置类 调用该类的无参构造方法初始化. 添加该注解构造方法执行之后就会执行设置两个回调
//    @PreDestroy // 对象销毁之前执行
    public void init() {
        // 确认消息是否到达交换机的回调, 不管消息是否到达交换机都会执行
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息已到达交换机");
            } else {
                System.err.println("消息没有达到交换机: 原因 " + cause);
            }
        });

        // 确认消息是否到达队列的回调, 只有消息没有到达队列才会执行
        // 例如 消息没有达到交换机: 原因 channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'spring_test_exchange2' in vhost '/admin', class-id=60, method-id=40)
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                System.err.println("消息没有到达队列: " + " 交换机 " + exchange + " 路由键 " + routingKey
                        + " 消息内容 " + replyText + " 状态码 " + replyCode + " 消息内容 " + new String(message.getBody()))
        );
    }

}

package tech.yiki.producer.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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


    /**
     * 业务交换机: spring_test_exchange2
     */
    @Bean
    public Exchange exchange() {
        // Topic 交换机: 交换机名称、是否持久化、是否自动删除(没有任何队列与之绑定、没有任何生产者给交换机发送消息, 就会自动销毁. 可能暂时没有 生产者 队列 一般不会设置自动删除)
//        return new TopicExchange("spring_test_exchange2", true, false);

        return ExchangeBuilder.topicExchange("spring_test_exchange2")
                .durable(true) // 是否持久化
//                .autoDelete() // 是否自动删除
                .build();
    }

    /**
     * 延迟队列: spring_ttl_queue
     * @return
     */
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable("spring_ttl_queue")
                .ttl(20000) // 延迟时间单位是 毫秒
                .deadLetterExchange("spring_dead_exchange") // 死信交换机
                .deadLetterRoutingKey("msg.dead") // rk
                .build();
    }

    /**
     * 把延迟队列绑定到业务交换机: msg.ttl
     */
    @Bean
    public Binding ttlBinding(Queue ttlQueue, Exchange exchange) {
        return BindingBuilder.bind(ttlQueue)
                .to(exchange)
                .with("msg.ttl")
                .noargs();
    }

    /**
     * 业务队列: spring_test_queue2
     */
    @Bean
    public Queue queue() {
//        Map<String, Object> arguments = new HashMap<>();
        // 指定死信交换机, 交换机名称(以后死信消息发 送给 什么名称的死信交换机 转发消息)
//        arguments.put("x-dead-letter-exchange", "spring_dead_exchange");
        // 指定 rk, 经过该 rk 就会进入 spring_dead_queue 死信队列
//        arguments.put("x-dead-letter-routing-key", "msg.dead");

        // 队列名称、是否持久化、是否排他(true 不能有多个消费者 只能有一个消费者。如果搭建集群 先启动的会生效 后启动的无法生效)、是否自动删除、其他参数(死信交换机 、 死信 rk、 死信队列)
//        return new Queue("spring_test_queue2", true, false, false, arguments);

        return QueueBuilder.durable("spring_test_queue2")
                .deadLetterExchange("spring_dead_exchange")
                .deadLetterRoutingKey("msg.dead")
                .build();
    }

    /**
     * 把业务队列绑定到业务交换机: rk = msg.test
     */
    @Bean
    public Binding binding(Queue queue, Exchange exchange) { // 声明这两个参数 就会把对应的对象注入进来
        // 声明绑定关系: 目的地(队列名称)、目的地类型(选择队列即可)、交换机名称(把队列绑定到那个交换机)、rk、其他参数
//        return new Binding("spring_test_queue2", Binding.DestinationType.QUEUE,
//                "spring_test_exchange2", "msg.test", null);

        return BindingBuilder.bind(queue).to(exchange).with("msg.test").noargs();
    }

    /**
     * 死信交换机: spring_dead_exchange
     */
    @Bean
    public Exchange deadExchange() {
        return ExchangeBuilder.topicExchange("spring_dead_exchange")
                .durable(true)
                .ignoreDeclarationExceptions() // 忽略声明异常
                .build();
    }

    /**
     * 死信队列: spring_dead_queue
     */
    @Bean
    public Queue deadQueue() {
        return QueueBuilder.durable("spring_dead_queue")
                .build();
    }

    /**
     * 把死信队列绑定到死信交换机: msg.dead
     */
    @Bean
    public Binding deadBinding(Queue deadQueue, Exchange deadExchange) {
        return BindingBuilder.bind(deadQueue)
                .to(deadExchange)
                .with("msg.dead")
                .noargs();
    }
}

package tech.yiki.producer.demo;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProducerDemoApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() throws InterruptedException {
        // convertAndSend 转化并发送消息, 消息内容是一个对象 在消息传输过程中会转化成二进制的形式发送(需要先声明交换机 启动消费者工程)
        rabbitTemplate.convertAndSend(
                // 发送给那个交换机, rk
                "spring_test_exchange2", "a.b",
                // 消息内容
                "Hello World"
        );

        // 在测试方法中进行测试，当测试方法结束，rabbitmq相关的资源也就关闭了，虽然我们的消息发送出去，但异步的ConfirmCallback却由于资源关闭而出现了异常
        // clean channel shutdown; protocol method: #method<channel.close>(reply-code=200, reply-text=OK, class-id=0, method-id=0)
        // 在测试方法等待一段时间即可
        Thread.sleep(2000);
    }

    // 测试消费者集群 与 多线程消费
    @Test
    void contextLoads2() {
        for (int i = 0; i < 100; i++) {
            // convertAndSend 转化并发送消息, 消息内容是一个对象 在消息传输过程中会转化成二进制的形式发送(需要先声明交换机 启动消费者工程)
            rabbitTemplate.convertAndSend(
                    "spring_test_exchange", "a.b",
                    "Hello " + i
            ); // 交换机, rk, 消息
        }
    }

}

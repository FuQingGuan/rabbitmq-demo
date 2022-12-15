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
    void contextLoads() {
        // convertAndSend 转化并发送消息, 消息内容是一个对象 在消息传输过程中会转化成二进制的形式发送(需要先声明交换机 启动消费者工程)
        rabbitTemplate.convertAndSend(
                // 发送给那个交换机, rk
                "spring_test_exchange", "a.b",
                // 消息内容
                "Hello World"
        );
    }

}

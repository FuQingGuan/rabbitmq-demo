package tech.yiki.consumer.demo.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2022/12/14 16:59
 * @Email: moumouguan@gmail.com
 */
@Component // 注入到 Spring 容器中
public class ConsumerListener {

    // 该注解会声明此方法是一个监听器, 可以监听队列获取消息
    @RabbitListener(bindings = @QueueBinding( // 声明绑定关系
            // 绑定的 队列, 将下方声明的交换机绑定给此队列
            value = @Queue("spring_test_queue"),
            // 需要与生产者中的交换机一致, type 交换机类型 TOPIC 通配模型支持通配符
            exchange = @Exchange(value = "spring_test_exchange", type = ExchangeTypes.TOPIC),
            // a.* 以 a 开头 任意单词都可以匹配
            key = {"a.*"} // rk, 可以绑定多个
    ))
    public void test(String msg) { // 参数需要与生产者发送的类型一致

        try {
            Thread.sleep(1000);
            System.out.println("消费者接收到消息: " + msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package tech.yiki.consumer.demo.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @Author: Guan FuQing
 * @Date: 2022/12/14 16:59
 * @Email: moumouguan@gmail.com
 */
@Component // 注入到 Spring 容器中
@Slf4j
public class ConsumerListener {

    // 该注解会声明此方法是一个监听器, 可以监听队列获取消息
    @RabbitListener(bindings = @QueueBinding( // 声明绑定关系
            // 绑定的 队列, 将下方声明的交换机绑定给此队列
            value = @Queue(value = "spring_test_queue", durable = "true", // 使用 durable 指定是否需要持久化 默认是 true
                    // 队列存在的情况下 如果声明一个属性与之前队列不一样 rabbitmq 就会报声明错误, ignoreDeclarationExceptions 可以使用忽略声明异常进行忽略(可以忽略声明异常使用既有的)
                    ignoreDeclarationExceptions = "true"),
            // 需要与生产者中的交换机一致, type 交换机类型 TOPIC 通配模型支持通配符
            exchange = @Exchange(value = "spring_test_exchange", // 使用 durable 指定是否需要持久化 默认是 true
                    ignoreDeclarationExceptions = "ture", // 通常在 交换机中设置忽略声明异常, 可以避免重复声明
                    type = ExchangeTypes.TOPIC),
            // a.* 以 a 开头 任意单词都可以匹配
            key = {"a.*"} // rk, 可以绑定多个
    ))
    // Channel Message 消息确认时需要这两个参数
    public void test(String msg , Channel channel, Message message) throws IOException { // 参数需要与生产者发送的类型一致

//        try {
//            Thread.sleep(1000);
//            System.out.println("消费者接收到消息: " + msg);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            System.out.println("消费者接收到消息: " + msg);
//            int i = 1 / 0;

            // TODO: 其他操作

            // 确认消息: 游标 直接 copy, 是否批量确认消息: 设置为 true 的话 从最近确认消息开始到当前消息之间未被确认的消息都被批量确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();

            // 判断消息是否重试投递过
            if (message.getMessageProperties().getRedelivered()) { // 已重试过直接拒绝
                // TODO: 记录日志, 或者保存到数据库表中 通过定时任务, 或者 人工排查 处理消息. 如果有死信队列会进入死信队列
                log.error(msg + " : 未被正常消费");

                // 拒绝消息: 游标, 重新入队
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else { // 未重试过, 重新入队
                // 不确认消息: 游标, 是否批量确认, 重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }

    // 该注解会声明此方法是一个监听器, 可以监听业务队列2获取消息
    @RabbitListener(queues = {"spring_test_queue2"}) // 在 RabbitConfig 已经配置, 无需再次重复定义
    // Channel Message 消息确认时需要这两个参数
    public void deadTest(String msg , Channel channel, Message message) throws IOException { // 参数需要与生产者发送的类型一致

//        try {
//            Thread.sleep(1000);
//            System.out.println("消费者接收到消息: " + msg);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            System.out.println("消费者接收到消息: " + msg);
            int i = 1 / 0;

            // TODO: 其他操作

            // 确认消息: 游标 直接 copy, 是否批量确认消息: 设置为 true 的话 从最近确认消息开始到当前消息之间未被确认的消息都被批量确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();

            // 判断消息是否重试投递过
            if (message.getMessageProperties().getRedelivered()) { // 已重试过直接拒绝
                // TODO: 记录日志, 或者保存到数据库表中 通过定时任务, 或者 人工排查 处理消息. 如果有死信队列会进入死信队列
                log.error(msg + " : 未被正常消费");

                // 拒绝消息: 游标, 重新入队
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 进入死信队列
            } else { // 未重试过, 重新入队
                // 不确认消息: 游标, 是否批量确认, 重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }

    // 该注解会声明此方法是一个监听器, 可以监听 死信队列 获取消息
    @RabbitListener(queues = {"spring_dead_queue"}) // 在 RabbitConfig 已经配置, 无需再次重复定义
    // Channel Message 消息确认时需要这两个参数
    public void ttlTest(String msg , Channel channel, Message message) throws IOException { // 参数需要与生产者发送的类型一致

//        try {
//            Thread.sleep(1000);
//            System.out.println("消费者接收到消息: " + msg);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            System.out.println("消费者接收到消息: " + msg);
            int i = 1 / 0;

            // TODO: 关单操作

            // 确认消息: 游标 直接 copy, 是否批量确认消息: 设置为 true 的话 从最近确认消息开始到当前消息之间未被确认的消息都被批量确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();

            // 判断消息是否重试投递过
            if (message.getMessageProperties().getRedelivered()) { // 已重试过直接拒绝
                // TODO: 记录日志, 或者保存到数据库表中 通过定时任务, 或者 人工排查 处理消息. 如果有死信队列会进入死信队列
                log.error(msg + " : 未被正常消费");

                // 拒绝消息: 游标, 重新入队
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 没有绑定死信队列 拒绝消息
            } else { // 未重试过, 重新入队
                // 不确认消息: 游标, 是否批量确认, 重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
}

# RabbitMQ 测试

* P: Producer, 消息生产者, 发送消息的应用程序
* C: Consumer: 消息消费者, 接受并消费消息的应用程序
* Q: Queue: 消息队列, 接收、存储、转发消息
* X: Exchange, 交换机, 接收并转发消息, 不能存储消息. 转发消息给那个队列取决于交换机类型
  * Fanout: 转发消息给所有队列
  * Direct: 转发消息给特定队列, 具体给那个队列取决于 RK
  * Topic: 同上, 只是 rk 支持通配符
* RK: RoutingKey, 路由键. 生产者发送消息, 获取队列绑定到交换机

![](https://oss.yiki.tech/oss/202212141644699.png)

## 安装

* 端口
    * 5672 -- client通信端口
    * 15672 -- 管理界面ui端口
    * 25672 -- server间内部通信口

```shell
docker pull rabbitmq:management

docker run -d -p 5672:5672 -p 15672:15672 -p 25672:25672 --name rabbitmq rabbitmq:management
```

## 管理页面

> 账号 密码 guest

![](https://oss.yiki.tech/oss/202212141644883.png)

![](https://oss.yiki.tech/oss/202212141645361.png)

### 新增用户

![](https://oss.yiki.tech/oss/202212141645221.png)

### 新增 Virtual Hosts

![](https://oss.yiki.tech/oss/202212141645929.png)

### 新增权限

![](https://oss.yiki.tech/oss/202212141645452.png)

![](https://oss.yiki.tech/oss/202212141645393.png)

### 切换用户

![](https://oss.yiki.tech/oss/202212141645672.png)

## 基本概念

* MQ: Message Queue, 消息队列
* MOM: Message Oriented Middleware, 消息中间件
* 两种主流实现方式
  * JMS: Java Message Service, Java 消息服务. 只能使用 java 语言实现
    * 只有两种消息模型。点对点、发布订阅
  * AMQP: Advanced Message Queueing Protocol, 高级消息队列协议. 本质上一个协议, 只规范了数据格式

* 三大作用
  * 异步
  * 解耦
  * 削峰填谷

## 搭建生产者与消费者

> 生产者只需要发送消息 添加 amqp 依赖即可, 消费者需要接收消息以及监听消息 所以不仅需要添加 amqp 还需要添加 web 启动器

## 入门程序

* 生产者发送消息

```java
        // convertAndSend 转化并发送消息, 消息内容是一个对象 在消息传输过程中会转化成二进制的形式发送(需要先声明交换机 启动消费者工程)
        rabbitTemplate.convertAndSend(
                // 发送给那个交换机, rk
                "xxx_test_exchange", "r.k",
                // 消息内容
                "Hello World"
        );
```

* 消费者监听器：

```java
@RabbitListener(bindings = @QueueBinding(
  value = @Queue("队列名称"),
  exchange = @Exchange(value = "交换机名称", type = ExchangeTypes.TOPIC/DIRECT/FANOUT),
  key = {}
))
```



<div>
  <!-- mp4格式 -->
  <video id="video" controls="" width="800" height="500" preload="none" poster="封面">
        <source id="mp4" src="https://oss.yiki.tech/oss/202212151611274.mp4" type="video/mp4">
  </videos>
</div>

![image-20221215161401037](https://oss.yiki.tech/oss/202212151615770.png)

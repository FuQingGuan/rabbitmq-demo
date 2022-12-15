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

## 避免消息堆积 与 避免消息丢失

### 避免消息堆积

* 怎么避免消息堆积
  * 搭建消费者集群(使用工作模型) 配合能者多劳
    * 同一套服务在多个服务器上运行, 运行多个实例
  * 使用多线程消费

![](https://oss.yiki.tech/oss/202212151621727.png)

![](https://oss.yiki.tech/oss/202212151624549.png)

![](https://oss.yiki.tech/oss/202212151625575.png)

![](https://oss.yiki.tech/oss/202212151626047.png)

![](https://oss.yiki.tech/oss/202212151633009.png)

![](https://oss.yiki.tech/oss/202212151633268.png)

> 搭建消费者集群后可以发现 rabbitmq 默认是采用轮训的方式分配消息。当有多个消费者接入时，消息的分配模式是一个消费者分配一条，直至消息消费完成.
> 现实场景中我们不同的服务器性能可能不一致, 消费消息能力也有所差异, 如果继续使用默认的分配策略. 一台服务可能已经把分配给自己的消息全部消费完了, 另一台可能还在慢慢消费. 这也是对计算资源的一种浪费.
> (给消费者工程代码添加 睡眠 并重启某一个服务看到差异效果.). 

<div>
  <!-- mp4格式 -->
  <video id="video" controls="" width="800" height="500" preload="none" poster="封面">
        <source id="mp4" src="https://oss.yiki.tech/oss/202212151640202.mp4" type="video/mp4">
  </videos>
</div>

![](https://oss.yiki.tech/oss/202212151644693.png)

![](https://oss.yiki.tech/oss/202212151644469.png)

> 配置能者多劳后, 可以发现能力较弱的服务A只消费了一条消息, 而能力强的服务B则将剩下的全部消息消费完毕.
> (去掉消费者端 睡眠方法, 配置文件中添加 能者多劳配置. 重启两台服务, 在消费者端添加睡眠代码. 任意重启一台服务 发送消息即可看到前后差异)

![](https://oss.yiki.tech/oss/202212151646497.png)

![](https://oss.yiki.tech/oss/202212151647913.png)

### 避免消息丢失

* 怎么避免消息丢失
  * 生产者确认(确保消息到达消息中间件)
  * 消息持久化(交换机持久化 队列持久化 消息持久化)
  * 消费者确认(确保消息被消费者正确无误的消费)

#### 消费者确认

> 消费者确认, yml 配置 acknowledge-mode: manual. 监听器添加相应代码

![](https://oss.yiki.tech/oss/202212151724909.png)

![image-20221215183938761](https://oss.yiki.tech/oss/202212151839488.png)

<div>
  <!-- mp4格式 -->
  <video id="video" controls="" width="800" height="500" preload="none" poster="封面">
        <source id="mp4" src="https://oss.yiki.tech/oss/202212151843815.mp4" type="video/mp4">
  </videos>
</div>

> 交换机、队列、消息都是在消费者中声明的. 消息持久化 也是在 消费者中声明的, 默认就是持久化的

![](https://oss.yiki.tech/oss/202212151847673.png)

![image-20221215184734877](https://oss.yiki.tech/oss/202212151847879.png)

![](https://oss.yiki.tech/oss/202212151849755.png)

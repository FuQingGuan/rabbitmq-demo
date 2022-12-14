
# RabbitMQ 测试

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

## 搭建生产者与消费者

> 生产者只需要发送消息 添加 amqp 依赖即可, 消费者需要接收消息以及监听消息 所以不仅需要添加 amqp 还需要添加 web 启动器

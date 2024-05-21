# netty-monitor-agent
# 1. 介绍

## 1.1 基本概况

该项目是基于Java  Agent的静态加载实现的Netty无侵入监控，创建目的是为了学习和使用Java Agent技术及Netty数据监控

## 1.2 技术栈

Java Agent + bytebuddy + Netty

## 1.3 使用方式

### 项目中

将代码打包为jar包，或者从标签处下载，在jar包运行时添加参数 -javaagent:agen包路径

例如项目包test.jar和agent包netty-monitor-agent-1.0.jar在同一目录，那么启动命令则如下：

```
 java -javaagent:./netty-monitor-agent-1.0.jar.jar  -jar  test.jar
```

### IDEA中

点击 Add VM options，然后在该行中填入 -javaagent:agent包路径，如下：

```
-javaagent:./netty-monitor-agent-1.0.jar.jar
```

如果需要Debug，则把该agent包的依赖导入即可

## 2. 其它

### 2.1 自定义输出

#### 侵入式（不推荐）

在项目包中引入agent依赖，实现接口MonitorOutService，在该接口中进行监控数据输出，然后通过添加启动参数：-DmonitorOutServiceClass=实现类的全类名，如下：

```
public class TestMonitorOutService implements MonitorOutService {
    @Override
    public void outputMonitorData(GlobalMonitorData globalMonitorData, List<EventLoopMonitorData> eventLoopMonitorDataList, List<ChannelMonitorData> channelMonitorDataList) {
        System.out.println("测试输出");
    }
}

启动参数：
-DmonitorOutServiceClass=com.github.netty.monitor.agent.test.TestMonitorOutService
```

#### 非侵入式

自己修改源码，实现接口MonitorOutService，然后添加启动参数：-DmonitorOutServiceClass=实现类的全类名，之后再打jar去做无侵入监控，可以把监控数据持久化记录到Redis、MySQL等数据库中

### 2.2 监控数据

#### 全局数据

| 字段            | 说明                                                         |
| --------------- | ------------------------------------------------------------ |
| ActiveChannel   | 激活的连接数                                                 |
| TotalInput      | 总接受字节量，单位B、KB、MB、GB、TB自动变化                  |
| TotalOutput     | 总输出字节量，单位B、KB、MB、GB、TB自动变化                  |
| InputRate       | 总输入速率，单位B/s、KB/s、MB/s、GB/s、TB/s自动变化          |
| OutputRate      | 总输出速率，单位B/s、KB/s、MB/s、GB/s、TB/s自动变化          |
| PoolUseHeap     | 池化堆内内存使用总量，单位B、KB、MB、GB、TB自动变化          |
| PoolUseDirect   | 池化直接内存使用总量，单位B、KB、MB、GB、TB自动变化          |
| unpoolUseHeap   | 非池化堆内内存使用总量，单位B、KB、MB、GB、TB自动变化        |
| unpoolUseDirect | 非池化直接内存使用总量，单位B、KB、MB、GB、TB自动变化        |
| useDirect       | 直接内存使用总量（池化+非池化），单位B、KB、MB、GB、TB自动变化 |

#### EventLoop数据

| 字段          | 说明                          |
| ------------- | ----------------------------- |
| EventLoopName | 当前EventLoop的名称           |
| ChannelCount  | 当前EventLoop下的活跃连接总数 |
| PendingTask   | 当前EventLoop下的任务总数     |
| State         | 当前EventLoop的状态           |

每个EventLoop都会打印一份

#### Channel数据

| 字段           | 说明                                                |
| -------------- | --------------------------------------------------- |
| LocalAddress   | 本地地址（IP：端口）                                |
| RemoteAddress  | 远端地址（IP：端口）                                |
| TotalInput     | 接受字节量，单位B、KB、MB、GB、TB自动变化           |
| TotalOutput    | 输出字节量，单位B、KB、MB、GB、TB自动变化           |
| InputRate      | 输入速率，单位B/s、KB/s、MB/s、GB/s、TB/s自动变化   |
| OutputRate     | 输出速率，单位B/s、KB/s、MB/s、GB/s、TB/s自动变化   |
| activeDate     | 激活时间，格式：MM-dd HH:mm:ss                      |
| inactiveDate   | 断开时间，格式：MM-dd HH:mm:ss                      |
| LastInputDate  | 最后一次接受数据时间，格式：MM-dd HH:mm:ss          |
| LastOutputDate | 最后一次输出数据的时间，格式：MM-dd HH:mm:ss        |
| LastEventDate  | 最后一次发生事件的时间，格式：MM-dd HH:mm:ss        |
| LastExceptDate | 最后一次Channel发生异常的时间，格式：MM-dd HH:mm:ss |
| LastExceptMsg  | 最后一次Channel发生异常的信息                       |

### 2.3 注意事项

1.该项目主要是学习为主，代码可供参考

2.支持JDK版本为1.8以上（其它版本需要自测）

3.暂不支持Netty版本为4.0.45.Final以下的，4.0.0至4.0.44.Final的版本需要自行通过反射去PlatformDepedent类中获取池化和非池化的数据

4.默认是控制台输出，如果导入了日志框架，则会打印日志，默认是每5秒打印一次，对于离线的Channel，离线的30秒后该Channel数据才会消失

5.最好自己修改源码，实现持久化数据存储
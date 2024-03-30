# WendyToy

wendy为本人英文名

此项目为手写JUC，netty，json工具类，http请求和返回组装，依赖注入，spring相关注解，nacos，服务注册，网关

等有趣小项目



## Lock为锁模块
包含cas锁，和基于cas的条件变量机制

#### CAS
使用java的unsafe库实现cas操作的调用，通过拟定state锁状态和循环抢锁实现锁的竞争和释放

#### 条件变量机制

在条件变量类中设计 等待线程队列，将尝试获取条件变量的线程加入等待队列
通过加入等待队列实现condition的await，通过循环判断是否被移出等待队列实现线程的继续工作。通过移出线程队列的元素实现condition的notify和notifyall
await方法中，通过检查中断状态，抛出中断异常，供调用方法抓取进行对阻塞线程的操作
在CAS锁中加入<String,Condition>的hash表，提供CAS锁以字符串命名的细化控制功能，并支持多线程对一个条件的细化控制

## container为容器模块
包含支持高并发的容器，和不支持高并发的容器

#### hash表
整体采用经典的哈希+链表存储键值对。并实现当数据量超过threshold进行扩容
拟定模糊size，将模糊size与每个索引链表的长度独立，理解了threshold冗余的意义
对每条链表单独加锁，增加效率
扩容判断时使用单独一个对象加锁，同时解决了多次扩容和全局加锁的低效
实现了扩容时直接赋值和重新散列两种扩容方式

#### 队列
以基本的queue为基础，在队列操作上下添加cas锁，实现支持高并发，实现队列基本方法，put，take，contains，clear等
使用上述条件变量机制，以队列锁为基锁，建立两个condition，非空和非满
队列满后调用非满的await，队列添加元素后调用非空的signal；队列空时调用非空的等待，队列取出元素后调用非满的signal
实现基于条件变量的唤醒与等待机制的高效高并发队列
take方法中，抛出条件变量抛出的中断异常，提供调用方线程在take方法阻塞时的进一步操作

## thread为线程模块
包含线程池的简单实现，以上述模块为基础

#### 线程池
初始化核心线程数量的线程，在上述的高并发队列中获取任务
若加入任务时，高并发队列已满且核心线程也满，则创建线程代理任务，直到最大线程，拒绝新任务
通过线程的中断调用，抓取到队列的中断异常，退出线程的无限循环，实现线程中止
拟定isShutdown和isTerminated属性，在收到shutdown指令后拒绝新任务的加入，配合任务队列的非空与否实现完成任务后的线程中止
实现等待队列中task完成和不等待两种线程池关闭方式
通过条件变量机制，主线程在等待线程池进入isTerminated状态后，即释放所有线程和任务后，才释放主线程


高并发容器，锁，条件变量，线程池均实现了测试main函数

## wendyNetty为netty模块
使用java原生N IO实现netty基本要素

通过hash，实现处理线程与selector独立

http协议解析，json解析，组装response返回，组装request请求



### wendySpring
仿spring注解式方法调用，实现GetMapping和PostMapping，RequestBody和RequestParm，配合json解析器实现实体类的json格式信息传输

仿spring依赖注入，单例多例两种方式，通过递归深度检索注解实现在Bean容器中注册实例

仿spring经典控制层+服务层。通过Controller注释注册所有http请求实现方法接口，通过@Resorce注入

仿nacos，启动nacos后可以注册其他应用，并且实现心跳检测，用hash表存储服务对应的服务url列表和心跳时间，实现转发与心跳检测

实现网关转发功能，网关可以根据服务id从nacos服务器得到注册的应用，并且实现负载均衡



<span style="color: red;">***仅做教学目的***</span>

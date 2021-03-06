# 目标
这是我在看了 zk 的文档后尝试写的。主要是对 zk 的一些功能的练手。
目前没有业务的编程目标，所以自己定了一些目标：尝试我尝试依赖 zk 实现一个分布式锁的相关操作。

# 理论依据
## 算法

### 概述

本程序中锁的实现思路借鉴之前读的`Lock`锁，其实按照自己对锁的理解，**就是把多线程（当然，多进程也是一样）的并发访问在这里顺序化（也就是把自己变成临界资源）**，**顺序化的方法，或者说结果就是排队，根据时间先后或者其他优先级进行排队，最后队列中的人顺序使用资源**，这就是锁的全部逻辑。

当然，排好队后：

* 每次放行的人数是可以控制的，然后根据限制的人数就有了**独占锁、共享锁**的区分
* 获得使用资源的人，重复获取理论上也可以，根据是否对锁占有者的**重复获得**进行技术支持，可以抽出一个新的锁：可重入锁；

到此，锁的基本实现就完成了。后续也可以在锁的基础上继续实现什么别的工具，这个花样太多了，不再细说。

### java 锁

jdk 中的`Lock`锁的最核心的实现是`AbstractQueuedSynchronizer`，也就是我们常说的 `AQS`。

`AQS`的核心逻辑就是排序，当大量的请求一同到来时，`AQS`会：

1. 将其塞到一个高效的线程安全队列中，然后让请求线程循环检查/阻塞等待

2. 如果线程（循环/被唤醒）检查到

   1. **自己位于队头**

   2. 且**临界条件允许**

      就出队【这里根据情况唤醒一下后面的人，看看有木有共享出队的】

3. 执行完成，释放锁：

   1. **恢复临界条件**
   2. **唤醒队列中等待的线程**，让他们自己根据情况进行2操作

## 技术

锁实现的最基本依据有两个：

* 高并发的顺序排队系统
* 队列变动的通知系统（不必须，如果没有通知，可能就得用轮询了）

其中，zk是一个高可用的分布式协作平台。经常用来做分布式系统的命名、配置管理，处理同时过来的请求并进行排序肯定可以实现。同时，zk提供了同步的钩子调用和异步的事件通知机制，可以用来做队列变动的通知。

# 具体实现

## 整体流程

使用 zk 的**顺序临时节点**。【后面会带上 zk 给分配的数字后缀，**此后缀保证唯一且递增**，可以抽象的当成队列数组的下标。】

首先根据锁名确定一个节点，**设置此父节点值为临界资源**然后竞争锁时就**在此节点下创建顺序临时节点**，并注册此父节点的数据事件监听和此父节点的子节点事件监听。

用户创建属于自己的临时节点后进行检查，看如果自己的序号是最小的，说明自己在队首，就**设置节点临界资源**，如果竞争成功，就删除自己的节点。如果竞争失败，就使用轮询或者等待变动通知，等待别人用完临界资源再重新竞争。

占有临界资源的人用完了，恢复父节点的标记临界资源使用的数据。

## 问题

1. 使用临时节点是为了突然宕机时能让子节点自动删除，但是如果要依赖父节点记录临界资源的值，宕机时无法恢复，会导致**临界资源泄漏**。
2. 大量用户注册了同样的事件，但是相应的只有能用到临界资源那几个，存在大量的网络，zk事件订阅开销。
3. 创建节点是否一定成功？可能存在网络原因导致没有拿到返回结果，由于操作非幂等，无法重试，容易出现**临界资源泄漏**

## 优化

1. 在临时节点前缀加上 sessionid 进行标记，在反查时如果发现没有自己的临时节点再加。

方案选择：

* 不使用父节点维护临界资源使用情况。父节只维护临界资源常量，子节点根据排序自行判断是否可使用临界资源。需要订阅父节点的子节点事件。

* 不支持多用户使用临界资源，只单用户。子节点只订阅自己前面节点的事件。降低压力。

目前没想到很好的满足所有条件的方法。

考虑到 redis 的 `setNx()`，其实分布式的锁没有想象中要求那么多，选第二个方案吧，第一个方案有点麻烦。




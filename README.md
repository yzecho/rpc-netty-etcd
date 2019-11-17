# rpc-netty-etcd
基于Netty和Etcd实现的初步RPC框架

开始学习Netty一段时间了，正好实现一个简易的rpc框架也会用到序列化，反射等知识。
<br />
git clone导入idea中，首先开启本地的etcd服务，然后启动rpctest包下的server下的ServerApplication，最后再启动SpringBoot，访问[htttp://localhost:8080](http://localhost:8080)即可看到rpc调用服务结果。
<br />
参考资料：
- [http://devo.ps/blog/zookeeper-vs-doozer-vs-etcd](http://devo.ps/blog/zookeeper-vs-doozer-vs-etcd)
- [http://www.alexzfx.com/2018/10/11/%E5%AE%9E%E7%8E%B0%E4%B8%80%E4%B8%AA%E7%AE%80%E5%8D%95%E7%9A%84RPC%E6%A1%86%E6%9E%B6](http://www.alexzfx.com/2018/10/11/%E5%AE%9E%E7%8E%B0%E4%B8%80%E4%B8%AA%E7%AE%80%E5%8D%95%E7%9A%84RPC%E6%A1%86%E6%9E%B6)
- [https://github.com/etcd-io/jetcd](https://github.com/etcd-io/jetcd)
- [https://juejin.im/post/5bac34b4e51d450e5d0b236b](https://juejin.im/post/5bac34b4e51d450e5d0b236b)
- [https://zhuanlan.zhihu.com/p/27335748](https://zhuanlan.zhihu.com/p/27335748)
- [https://zhuanlan.zhihu.com/p/27207160](https://zhuanlan.zhihu.com/p/27207160)

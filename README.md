# nacos_hessian_rce_poc
- 目前网上公开的poc，走的JNDI(jackson)链子，可以打3次
- 主要参考了Y4er和l3yx师傅
- 生成序列化的数据请使用Y4er师傅的yso
### 复现步骤
- `sh /Users/zhchen/Downloads/nacos/bin/startup.sh -m standalone`启动服务，并等待一段时间，等待nacos的初始化
- 设置lib库
- 运行`HackerLdapServer`启动ldap服务，运行`Main`攻击


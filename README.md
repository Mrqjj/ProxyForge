# ProxyForge

#### 介绍
- 手动维护 Nginx + Certbot 工作量大，证书易过期  
- 目标网站内容无法按需改写，品牌合规困难  
- 缺少可视化、插件化、零运维的私有化反向代理方案  

**目标**：打造一款「自动 HTTPS + 内容改写 + 插件扩展 + 后台配置」的 Java 技术栈 反向代理网关(镜像)，单 jar 即可运行，亦可 K8s 弹性伸缩。 也可编译目标平台可执行文件运行.

#### 软件架构
1. springboot + undertow + redis + mysql
2. 插件支持热加载
3. 可视配置化



#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1. 插件编写,请注意pom文件内的引用
2. 如果引入的依赖和pom文件内冲突,请添加 <scope>provided</scope>


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
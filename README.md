 Library System（图书借阅管理系统）

本项目是一个基于 Spring Boot + Thymeleaf + MySQL 实现的图书借阅管理系统，支持用户注册登录、图书查询与借阅、管理员图书管理等功能，适用于课程设计与实验项目展示。


一、项目技术栈

后端框架：Spring Boot 3.x
ORM 框架：Spring Data JPA (Hibernate)
模板引擎：Thymeleaf
数据库：MySQL 9.5
数据库可视化工具： Navicat Premium 16
构建工具：Maven（已集成 Maven Wrapper）
Web 容器：内嵌 Tomcat
版本控制：Git

---

 二、运行环境要求

 JDK 17
 IntelliJ IDEA 2024.3.7 专业版
 MySQL 9.5
 Navicat Premium 16
 Windows 
 浏览器（Chrome / Edge）

---

 三、数据库初始化说明
 
1、Navicat 先与MySQL成功建立连接

2、 创建数据库（必须）

JPA 不会自动创建数据库，请先在 MySQL 中执行：

CREATE DATABASE library_db DEFAULT CHARSET utf8mb4;

数据库名称：library_db
---

3、数据表创建方式

本项目使用：
spring.jpa.hibernate.ddl-auto=update

因此在 项目首次启动时：
 Hibernate 会根据实体类自动创建以下三张表：
 user 
 book
 borrow
无需手动建表。

---

4、初始化数据（data.sql）



> ⚠️ 注意：`data.sql`在数据库连接成功且表存在之后执行。


4、 数据库连接配置

在 `application.yml` 中修改为你自己的数据库账号密码：

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: 用户数据库账号密码  # 换成你自己的
    driver-class-name: com.mysql.cj.jdbc.Driver


 四、项目启动方式
方式一：默认 HTTP 启动（推荐）

在IDEA终端（Windows PowerShell）执行以下一条命令：
".\mvnw.cmd spring-boot:run"


启动成功后访问：
"http://localhost:8081/auth"
---

方式二：启用 HTTPS（可选，ssl profile）

出于安全与规范考虑，SSL 证书文件不提交到仓库，需由运行者自行生成。

1、 生成 SSL 证书（只需一次）

在项目根目录执行：
keytool -genkeypair -alias library-https -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 3650


生成过程中输入你自己的证书密码（请记住）。
执行完后，会在项目路径下产生keystore.p12证书文件。

---

2、启动 HTTPS（ssl profile）

在IDEA终端（Windows PowerShell）执行以下两条命令：

$env:SSL_KEYSTORE_PASSWORD="你的证书密码"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=ssl"

访问地址：

"https://localhost:8443/auth"

 使用的是自签名证书，浏览器首次访问会提示“不安全”，选择“高级 → 继续访问”即可。

---

五、账号说明

管理员账号：由代码初始化（写死在程序中）
普通用户：通过注册功能创建

---

 六、项目结构说明


library-system
├── src
│   ├── main
│   │   ├── java        # 后端代码
│   │   └── resources   # 配置文件、模板、data.sql
│   └── test
├── .gitignore
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md


---

 七、安全与规范说明

* SSL 证书文件（*.p12 / *.jks）属于私钥文件，不提交到 Git 仓库
* 通过 .gitignore 进行统一忽略
* HTTPS 通过 Spring Profile 控制，避免强制依赖证书文件

---
八、项目说明

本系统采用 Spring Boot 框架实现图书借阅管理功能，使用 MySQL 数据库存储数据，支持用户与管理员两种角色。系统默认通过 HTTP 方式运行，并提供 HTTPS 的可选支持。项目结构清晰，配置合理，能够满足课程实验与功能演示需求。

---

 九、版本管理说明

* 项目使用 Git 进行版本控制
* 可托管于 GitHub / Gitee
* 适用于个人或小组协作开发


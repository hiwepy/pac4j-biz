# pac4j-biz

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://github.com/easy-4-java/pac4j-biz) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

基于 pac4j-core 的业务扩展：验证码表单登录、Token 与签名客户端、失败计数、授权器与回调 URL 解析器

> **当前分支**：`feature/2.0.x`
> **版本**：`2.0.x.x.20260630-SNAPSHOT`
> **JDK 基线**：8
> **项目状态**：维护中（1.0.x 线）。尚未发布 Maven Central；制品通过 Aliyun Maven 仓库与 GitHub Releases 分发。

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 能力与状态](#2-features--status)
- [3. 运行要求与兼容性](#3-requirements--compatibility)
- [4. 架构与模块](#4-architecture--modules)
- [5. 引入依赖](#5-installation)
- [6. 快速开始](#6-quick-start)
- [7. 配置](#7-configuration)
- [8. 核心用法](#8-core-usage)
- [9. 测试与构建](#9-testing--build)
- [10. 版本线与分支](#10-versioning--branches)
- [11. 贡献与许可证](#11-contributing--license)

## 1. 项目概述

### 1.1 是什么

**pac4j-biz** 是基于 [pac4j-core](https://github.com/pac4j/pac4j)（本线 4.5.7）的业务化扩展，在 pac4j 之上补充典型业务应用需要的可复用认证构件：

- 用户名 + 密码 + **验证码**表单登录（`UsernamePasswordCaptchaFormClient`）；
- 面向 API 认证的 **Token** 与**签名**客户端 / 凭据 / 认证器；
- 认证失败计数（会话级、请求级或共享）与超限提醒；
- 内外部档案规则授权器；
- 回调 URL 解析器（query / path / 无参数）与 Web 工具。

### 1.2 不是什么

- 不是 pac4j 的分支——它扩展 `pac4j-core` / `pac4j-config` / `pac4j-http`。
- 不是 Spring Boot Starter；本模块为框架无关的 Java。
- 不是完整的身份提供者——只提供业务侧构件。

### 1.3 典型使用场景

| 场景 | 推荐入口 | 结果 |
|---|---|---|
| 带验证码的 Web 表单登录 | `UsernamePasswordCaptchaFormClient` + `SessionCaptchaResolver` | 用户名 / 密码 / 验证码认证 |
| 按 token 参数的 API 认证 | `TokenClient` + `TokenAuthenticator` + `TokenProfileCreator` | 基于 Token 的档案 |
| 按签名参数的 API 认证 | `SignatureClient` + `SignatureAuthenticator` | 验签通过的档案 |
| 登录失败后的锁定 / 重试限制 | `AuthenticatingFailureCounter`（+ Request / Session 变体） | `OverRetryRemindException` |
| 登录后的档案增强 | `UserDetailsAuthorizationGenerator` + `UserDetailsService` | 从用户详情服务补充角色 / 属性 |
| 回调 URL 定制 | `QueryParameterCallbackUrlExtResolver` / `PathParameterCallbackUrlExtResolver` | 无多余参数的回调 URL |

<a id="2-features--status"></a>
## 2. 能力与状态

| 能力 | 状态 | 说明 |
|---|:---:|---|
| 验证码表单登录 | 可用 | `UsernamePasswordCaptchaFormClient`（继承 `IndirectClient`）、`UsernamePasswordCaptchaAuthenticator` |
| 验证码解析器 | 可用 | `CaptchaResolver`、`SessionCaptchaResolver`、`NullCaptchaResolver` |
| Token 认证 | 可用 | `TokenClient`（抽象，`DirectClient`）、`TokenAuthenticator`、`TokenParameterExtractor`、`TokenProfile` / `TokenProfileCreator` / `TokenProfileDefinition` |
| 签名认证 | 可用 | `SignatureClient`、`SignatureAuthenticator`、`SignatureParameterExtractor`、`SignatureCredentials`、`SignatureProfile` / creator / definition |
| 失败计数 | 可用 | `AuthenticatingFailureCounter`、`AuthenticatingFailureRequestCounter`、`AuthenticatingFailureSessionCounter`；`OverRetryRemindException` |
| 授权器 | 可用 | `Pac4jExternalAuthorizer`、`Pac4jInternalAuthorizer`（继承 `ProfileAuthorizer<UserProfile>`） |
| 用户详情生成 | 可用 | `UserDetailsService`、`UserDetails`、`UserDetailsAuthorizationGenerator` |
| 回调 URL 解析器 | 可用 | `QueryParameterCallbackUrlExtResolver`、`PathParameterCallbackUrlExtResolver`、`NoParameterCallbackUrlExtResolver` |
| 异常 | 可用 | `CaptchaIncorrectException`、`CaptchaNotFoundException`、`MethodNotSupportedException`、`UsernameNotFoundException`、`OverRetryRemindException` |
| HTTP 工具 | 可用 | `WebUtils`、`HttpUtils2`、`ContentType`、`HttpHeaders` 常量 |

<a id="3-requirements--compatibility"></a>
## 3. 运行要求与兼容性

| 组件 | 版本 | 说明 |
|---|---:|---|
| JDK | 17+ | 1.0.x 线基线 |
| Maven | 3.0+ | Enforcer 下限 |
| pac4j-core / config / http | 4.5.7 | 固定版本 |
| fastjson | 2.0.x | JSON 处理 |
| commons-lang3 | 3.x | 工具类 |
| SLF4J | 2.0.18 | 日志门面 |

版本线矩阵：

| 版本线 | 分支 | JDK | 版本模式 | 用途 |
|---|---|---:|---|---|
| 1.0.x | `feature/2.0.x`（当前分支） | 8 | `1.0.x.*` | 存量项目、Boot 2.x Starter 线 |
| 2.0.x | `feature/2.0.x` | 17 | `2.0.x.*` | JDK 17 线 |
| 3.0.x | `feature/3.0.x` | 21 | `3.0.x.*` | 新项目 |

<a id="4-architecture--modules"></a>
## 4. 架构与模块

```text
[ Web / API 应用 ]
        |
        | pac4j-biz + pac4j-core/config/http
        v
+------------------------------------------+
| 客户端   UsernamePasswordCaptchaForm      |
|          TokenClient / SignatureClient    |
| 认证     CaptchaResolver、                |
|          Token/Signature 认证器、         |
|          UserDetailsService               |
| 凭据     UsernamePasswordCaptcha、        |
|          Token、Signature                 |
| 档案     TokenProfile / SignatureProfile  |
|          + creators/definitions           |
| 失败     AuthenticatingFailureCounter     |
|          （request/session/shared）       |
| 扩展     授权器、回调 URL 解析器、        |
|          WebUtils                         |
+------------------------------------------+
        |
        v
[ pac4j core 引擎 ] -> [ 应用安全流程 ]
```

单模块库（打包类型 `jar`）。包结构：

| 包 | 职责 |
|---|---|
| `org.pac4j.core.ext` | 常量、`Pac4jExternalAuthorizer`、`Pac4jInternalAuthorizer` |
| `org.pac4j.core.ext.authentication` | 验证码表单客户端 / 认证器、失败计数、`UserDetailsService` |
| `org.pac4j.core.ext.authentication.captcha` | `CaptchaResolver`、`SessionCaptchaResolver`、`NullCaptchaResolver` |
| `org.pac4j.core.ext.client` | `TokenClient`、`SignatureClient` |
| `org.pac4j.core.ext.credentials`（+ `.authenticator` / `.extractor`） | 凭据、认证器、提取器 |
| `org.pac4j.core.ext.profile`（+ `.creator` / `.definition`） | Token / 签名档案、creator、definition |
| `org.pac4j.core.ext.http.callback` | 回调 URL 解析器 |
| `org.pac4j.core.ext.exception` | 业务异常 |
| `org.pac4j.core.ext.utils` / `org.pac4j.core.util` | `WebUtils`、`HttpUtils2`、常量 |

<a id="5-installation"></a>
## 5. 引入依赖

Maven：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>pac4j-biz</artifactId>
    <version>2.0.x.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle：

```groovy
implementation 'io.github.easy4j:pac4j-biz:2.0.x.x.20260630-SNAPSHOT'
```

快照版本需要启用对应快照仓库（`pom.xml` 中 `distributionManagement` 指向 Aliyun Maven 仓库）。

<a id="6-quick-start"></a>
## 6. 快速开始

验证码表单登录：

```java
UsernamePasswordCaptchaFormClient client = new UsernamePasswordCaptchaFormClient(
        "/login", usernamePasswordAuthenticator, captchaAuthenticator);
client.setCallbackUrl("/callback");
```

基于 Token 的 API 认证：

```java
TokenAuthenticator<MyTokenProfile, MyToken> authenticator =
        new MyTokenAuthenticator("token", true);       // 你的协议实现

TokenClient<MyTokenProfile, MyToken> client = new TokenClient<>("token", authenticator) {
    // resolve() / requestToken() 等按你的 token 协议实现
};
client.setName("token-client");
```

**预期结果**：在 pac4j `Config` 中注册 `UsernamePasswordCaptchaFormClient` 后，向登录 URL POST 用户名 / 密码 / 验证码，成功时产出档案；验证码错误抛 `CaptchaIncorrectException`；配合失败计数器时，连续失败后抛 `OverRetryRemindException`。

<a id="7-configuration"></a>
## 7. 配置

本库通过构造器与 setter 配置，无配置属性、无 `application.yml` 条目。

| 入口 | 配置面 |
|---|---|
| `UsernamePasswordCaptchaFormClient` | `loginUrl`、用户名 / 密码参数名、凭据 `Authenticator` + 验证码认证器 |
| `TokenClient` | `parameterName`、`TokenAuthenticator`、GET/POST 支持开关（`isSupportGetRequest` / `isSupportPostRequest`） |
| `SignatureClient` | 签名参数名、`SignatureAuthenticator`、字符集 |
| `AuthenticatingFailureCounter` | 内存或会话 / 请求级计数 + 重试阈值 |
| `SessionCaptchaResolver` | 会话属性名（`KAPTCHA_SESSION_ATTRIBUTE_NAME`、`KAPTCHA_DATE_SESSION_ATTRIBUTE_NAME`） |
| `UserDetailsAuthorizationGenerator` | 包装一个 `UserDetailsService` |

<a id="8-core-usage"></a>
## 8. 核心用法

### 8.1 失败计数与超限提醒

```java
AuthenticatingFailureSessionCounter counter = new AuthenticatingFailureSessionCounter();
// 一次失败尝试之后
if (counter.exceedsThreshold(context, sessionStore)) {
    throw new OverRetryRemindException();
}
```

### 8.2 自定义回调 URL 解析器

```java
// 从 query 参数解析回调 URL，替代默认机制
QueryParameterCallbackUrlExtResolver resolver = new QueryParameterCallbackUrlExtResolver("callback");
String url = resolver.compute(context, "clientName");
```

<a id="9-testing--build"></a>
## 9. 测试与构建

```bash
mvn clean verify
```

- JaCoCo 在 `verify` 阶段执行 `prepare-agent`、`report` 与 `check`，行覆盖率规则为 **90%**（`haltOnFailure=false`）。
- 模块自带 Maven Wrapper（`mvnw`）。
- 发布打包（`mvn -Prelease deploy`）附带 sources 与 javadoc 构件并执行 GPG 签名，对接 Sonatype Central Publishing；普通 `mvn deploy` 按版本后缀路由到 Aliyun Maven 仓库（见 `distributionManagement`）。

<a id="10-versioning--branches"></a>
## 10. 版本线与分支

| 分支 | 版本模式 | JDK | 维护策略 |
|---|---|---|---|
| `feature/1.0.x`（当前分支） | `1.0.x.*` | 8 | 仅接受兼容性修复与 JDK 8 安全的依赖升级 |
| `feature/2.0.x` | `2.0.x.*` | 17 | JDK 17 线 |
| `feature/3.0.x` | `3.0.x.*` | 21 | JDK 21 线 |

<a id="11-contributing--license"></a>
## 11. 贡献与许可证

欢迎贡献。提交 Pull Request 前请执行 `mvn clean verify`，并说明兼容性、测试与迁移影响。本项目采用 [Apache License 2.0](LICENSE) 许可证。

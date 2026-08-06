# pac4j-biz

[English](./README.md) | [简体中文](./README.zh-CN.md)

Business-oriented extensions for pac4j-core: captcha form login, token & signature clients, failure counters, authorizers and callback URL resolvers
[简体中文](./README.zh-CN.md)

> **Current branch**: `feature/1.0.x`
> **Version**: `1.0.x.20260630-SNAPSHOT`
> **JDK baseline**: 8
> **Project status**: maintenance (1.0.x line). Not yet published to Maven Central; artifacts are distributed via the Aliyun Maven repository and GitHub Releases.

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage](#8-core-usage)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

### 1.1 What it is

**pac4j-biz** is a business-oriented extension built on [pac4j-core](https://github.com/pac4j/pac4j) (4.5.7 in this line). It adds reusable authentication building blocks that typical business applications need on top of pac4j:

- Username + password + **captcha** form login (`UsernamePasswordCaptchaFormClient`);
- **Token** and **signature** clients / credentials / authenticators for API-style auth;
- Authentication failure counters (session, request, or shared) with over-retry reminders;
- Authorizers for internal/external profile rules;
- Callback URL resolvers (query / path / no-parameter) and web helpers.

### 1.2 What it is not

- Not a pac4j fork — it extends `pac4j-core` / `pac4j-config` / `pac4j-http`.
- Not a Spring Boot starter; this module is framework-neutral Java.
- Not a complete identity provider — it supplies business-facing pieces only.

### 1.3 Typical scenarios

| Scenario | Recommended entry | Result |
|---|---|---|
| Web form login with captcha | `UsernamePasswordCaptchaFormClient` + `SessionCaptchaResolver` | Username/password/captcha authentication |
| API authentication by token parameter | `TokenClient` + `TokenAuthenticator` + `TokenProfileCreator` | Token-based profiles |
| API authentication by signature parameters | `SignatureClient` + `SignatureAuthenticator` | Signature-verified profiles |
| Lockout / retry limits after failed logins | `AuthenticatingFailureCounter` (+ Request/Session variants) | `OverRetryRemindException` |
| Post-login profile enrichment | `UserDetailsAuthorizationGenerator` + `UserDetailsService` | Roles/attributes from a user-details service |
| Callback URL customization | `QueryParameterCallbackUrlExtResolver` / `PathParameterCallbackUrlExtResolver` | Callback URLs without extra parameters |

<a id="2-features--status"></a>
## 2. Features & Status

| Capability | Status | Notes |
|---|:---:|---|
| Captcha form login | Available | `UsernamePasswordCaptchaFormClient` (extends `IndirectClient`), `UsernamePasswordCaptchaAuthenticator` |
| Captcha resolvers | Available | `CaptchaResolver`, `SessionCaptchaResolver`, `NullCaptchaResolver` |
| Token authentication | Available | `TokenClient` (abstract, `DirectClient`), `TokenAuthenticator`, `TokenParameterExtractor`, `TokenProfile` / `TokenProfileCreator` / `TokenProfileDefinition` |
| Signature authentication | Available | `SignatureClient`, `SignatureAuthenticator`, `SignatureParameterExtractor`, `SignatureCredentials`, `SignatureProfile` / creator / definition |
| Failure counting | Available | `AuthenticatingFailureCounter`, `AuthenticatingFailureRequestCounter`, `AuthenticatingFailureSessionCounter`; `OverRetryRemindException` |
| Authorizers | Available | `Pac4jExternalAuthorizer`, `Pac4jInternalAuthorizer` (extend `ProfileAuthorizer<UserProfile>`) |
| User-details generation | Available | `UserDetailsService`, `UserDetails`, `UserDetailsAuthorizationGenerator` |
| Callback URL resolvers | Available | `QueryParameterCallbackUrlExtResolver`, `PathParameterCallbackUrlExtResolver`, `NoParameterCallbackUrlExtResolver` |
| Exceptions | Available | `CaptchaIncorrectException`, `CaptchaNotFoundException`, `MethodNotSupportedException`, `UsernameNotFoundException`, `OverRetryRemindException` |
| HTTP utilities | Available | `WebUtils`, `HttpUtils2`, `ContentType`, `HttpHeaders` constants |

<a id="3-requirements--compatibility"></a>
## 3. Requirements & Compatibility

| Component | Version | Notes |
|---|---:|---|
| JDK | 8+ | 1.0.x line baseline |
| Maven | 3.0+ | Enforcer minimum |
| pac4j-core / config / http | 4.5.7 | Pinned |
| fastjson | 2.0.x | JSON handling |
| commons-lang3 | 3.x | Utilities |
| SLF4J | 2.0.18 | Logging facade |

Version-line matrix:

| Version line | Branch | JDK | Version pattern | Purpose |
|---|---|---:|---|---|
| 1.0.x | `feature/1.0.x` (this branch) | 8 | `1.0.x.*` | Legacy projects, Boot 2.x starter line |
| 2.0.x | `feature/2.0.x` | 17 | `2.0.x.*` | JDK 17 line |
| 3.0.x | `feature/3.0.x` | 21 | `3.0.x.*` | New projects |

<a id="4-architecture--modules"></a>
## 4. Architecture & Modules

```text
[ Web / API Application ]
        |
        | pac4j-biz + pac4j-core/config/http
        v
+------------------------------------------+
| Clients    UsernamePasswordCaptchaForm    |
|            TokenClient / SignatureClient  |
| Authn      CaptchaResolver,               |
|            Token/Signature authenticators,|
|            UserDetailsService             |
| Creds      UsernamePasswordCaptcha,       |
|            Token, Signature               |
| Profiles   TokenProfile / SignaturePro-   |
|            file + creators/definitions    |
| Failure    AuthenticatingFailureCounter   |
|            (request/session/shared)       |
| Extras     Authorizers, callback URL      |
|            resolvers, WebUtils            |
+------------------------------------------+
        |
        v
[ pac4j core engine ] -> [ app security flows ]
```

Single-module library (packaging `jar`). Package layout:

| Package | Responsibility |
|---|---|
| `org.pac4j.core.ext` | Constants, `Pac4jExternalAuthorizer`, `Pac4jInternalAuthorizer` |
| `org.pac4j.core.ext.authentication` | Captcha form client/authenticator, failure counters, `UserDetailsService` |
| `org.pac4j.core.ext.authentication.captcha` | `CaptchaResolver`, `SessionCaptchaResolver`, `NullCaptchaResolver` |
| `org.pac4j.core.ext.client` | `TokenClient`, `SignatureClient` |
| `org.pac4j.core.ext.credentials` (+ `.authenticator` / `.extractor`) | Credentials, authenticators, extractors |
| `org.pac4j.core.ext.profile` (+ `.creator` / `.definition`) | Token / signature profiles, creators, definitions |
| `org.pac4j.core.ext.http.callback` | Callback URL resolvers |
| `org.pac4j.core.ext.exception` | Business exceptions |
| `org.pac4j.core.ext.utils` / `org.pac4j.core.util` | `WebUtils`, `HttpUtils2`, constants |

<a id="5-installation"></a>
## 5. Installation

Maven:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>pac4j-biz</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.easy4j:pac4j-biz:1.0.x.20260630-SNAPSHOT'
```

Snapshot builds require an enabled snapshot repository (Aliyun Maven snapshot repository per `distributionManagement` in `pom.xml`).

<a id="6-quick-start"></a>
## 6. Quick Start

Captcha form login:

```java
UsernamePasswordCaptchaFormClient client = new UsernamePasswordCaptchaFormClient(
        "/login", usernamePasswordAuthenticator, captchaAuthenticator);
client.setCallbackUrl("/callback");
```

Token-based API authentication:

```java
TokenAuthenticator<MyTokenProfile, MyToken> authenticator =
        new MyTokenAuthenticator("token", true);       // your protocol implementation

TokenClient<MyTokenProfile, MyToken> client = new TokenClient<>("token", authenticator) {
    // resolve() / requestToken() etc. per your token protocol
};
client.setName("token-client");
```

**Expected result**: with a registered `UsernamePasswordCaptchaFormClient` in the pac4j `Config`, a POST to the login URL with username/password/captcha produces a profile on success, `CaptchaIncorrectException` on a wrong captcha, and (with a failure counter) `OverRetryRemindException` after repeated failures.

<a id="7-configuration"></a>
## 7. Configuration

This is a library configured through constructors and setters — there are no configuration properties or `application.yml` entries.

| Entry | Configuration surface |
|---|---|
| `UsernamePasswordCaptchaFormClient` | `loginUrl`, username/password parameters, credentials `Authenticator` + captcha authenticator |
| `TokenClient` | `parameterName`, `TokenAuthenticator`, GET/POST support flags (`isSupportGetRequest` / `isSupportPostRequest`) |
| `SignatureClient` | signature parameter name, `SignatureAuthenticator`, charset |
| `AuthenticatingFailureCounter` | in-memory or session/request-scoped counting + retry threshold |
| `SessionCaptchaResolver` | session attribute names (`KAPTCHA_SESSION_ATTRIBUTE_NAME`, `KAPTCHA_DATE_SESSION_ATTRIBUTE_NAME`) |
| `UserDetailsAuthorizationGenerator` | wraps a `UserDetailsService` |

<a id="8-core-usage"></a>
## 8. Core Usage

### 8.1 Failure counter with retry reminder

```java
AuthenticatingFailureSessionCounter counter = new AuthenticatingFailureSessionCounter();
// after a failed attempt
if (counter.exceedsThreshold(context, sessionStore)) {
    throw new OverRetryRemindException();
}
```

### 8.2 Custom callback URL resolver

```java
// Resolve the callback URL from a query parameter instead of the default mechanism
QueryParameterCallbackUrlExtResolver resolver = new QueryParameterCallbackUrlExtResolver("callback");
String url = resolver.compute(context, "clientName");
```

<a id="9-testing--build"></a>
## 9. Testing & Build

```bash
mvn clean verify
```

- JaCoCo runs `prepare-agent`, `report` and `check` on the `verify` phase with a **90% line-coverage** rule (`haltOnFailure=false`).
- The module ships a Maven wrapper (`mvnw`).
- Release packaging (`mvn -Prelease deploy`) attaches sources and javadoc jars, GPG-signs artifacts and is wired for Sonatype Central Publishing; plain `mvn deploy` routes SNAPSHOT/release artifacts to the Aliyun Maven repository per `distributionManagement`.

<a id="10-versioning--branches"></a>
## 10. Versioning & Branches

| Branch | Version pattern | JDK | Maintenance policy |
|---|---|---|---|
| `feature/1.0.x` (this branch) | `1.0.x.*` | 8 | Compatibility fixes and JDK-8-safe dependency upgrades only |
| `feature/2.0.x` | `2.0.x.*` | 17 | JDK 17 line |
| `feature/3.0.x` | `3.0.x.*` | 21 | JDK 21 line |

<a id="11-contributing--license"></a>
## 11. Contributing & License

Contributions are welcome. Run `mvn clean verify` before opening a pull request and describe compatibility, testing and migration impact. This project is licensed under the [Apache License 2.0](LICENSE).

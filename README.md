# QSign

[![mirai-core/mirai-console 2.15.0+](https://img.shields.io/badge/mirai--core/mirai--console-2.15.0+-green)](https://github.com/mamoe/mirai)
[![unidbg-fetch-qsign 1.1.6](https://img.shields.io/badge/unidbg--fetch--qsign-1.1.6-yellowgreen)](https://github.com/fuqiuluo/unidbg-fetch-qsign)
[![Releases](https://img.shields.io/github/downloads/MrXiaoM/unidbg-fetch-qsign/total?label=%E4%B8%8B%E8%BD%BD%E9%87%8F&logo=github)](https://github.com/MrXiaoM/qsign/releases)
[![Stars](https://img.shields.io/github/stars/MrXiaoM/unidbg-fetch-qsign?label=%E6%A0%87%E6%98%9F&logo=github)](https://github.com/MrXiaoM/qsign/stargazers)

通过 Unidbg 获取 QQSign 参数，基于 fuqiuluo/unidbg-fetch-qsign 修改，仅适用于 mirai。unidbg-fetch-sign 最低从 QQ8.9.33 (不囊括) 开始支持，TIM 不支持。

本插件**自带**协议信息，在加载时将会应用与签名服务版本相同的设备信息文件。

# 切记

 - 请使用与协议对应版本的 libfekit.so 文件
 - QSign 基于 Android 平台，其它平台 Sign 计算的参数不同，不互通（例如：IPad）。
 - 不支持载入 Tim.apk 的 so 文件。

# 待办事项

未完成，敬请期待

- [ ] initialize-register
  - [x] invoke api
  - [ ] check for possible deadlock issue
- [x] encryptTlv-customEnergy
- [ ] qSecurityGetSign
  - [x] sign
  - [x] request token
  - [ ] check for possible deadlock issue
  - [ ] etc...
- [ ] test
  - [x] login
  - [x] send message
  - [ ] etc...

有别的项目要做，本项目进度暂缓，不定期同步 unidbg-fetch-qsign 的变更。

# 部署方法

请在 mirai 版本大于或等于 `2.15.0` **正式版**的环境下操作。

> 文件很大，下载可能较慢，可以尝试使用以下工具站进行加速  
> https://ghproxy.com/  
> https://github.moeyy.cn/  

到 [Releases](https://github.com/MrXiaoM/unidbg-fetch-qsign/releases) 下载 `qsign-x.x.x-all.zip`，将 `plugins` 和 `txlib` 文件夹解压到 mirai 所在目录。解压后文件夹结构大致情况如下
```
|-bots
|-config
|-data
|-plugins
  |-qsign-x.x.x.mirai2.jar
|-txlib
  |-8.9.58
  |-8.9.63
    |-android_phone.json
    |-android_pad.json
    |-config.json
    |-dtconfig.json
    |-libfekit.so
    |-libQSec.so
  |-8.9.68
  |-8.9.70
```

通常来说，**到这步就完成了**，你可以无脑使用 `ANDROID_PHONE` 协议登录了。如果需要详细配置，请往下看。

## 详细配置(非必要)

首次加载插件会新建配置文件 `config/top.mrxiaom.qsign/config.yml`

目前配置文件里面仅有配置项 `base-path`，即使用的签名服务路径，默认值为 `txlib/8.9.63`，按自己所需进行修改。  
推荐使用默认值，目前 `8.9.58` **未测试**，`8.9.68` **没有合适的**设备信息文件。  

配置文件的修改在重启 mirai 后生效。

本仓库提供的包已自带协议，本插件加载时将会把协议信息应用到 mirai。  
启动 mirai 后检查一下日志输出的`协议版本`与`签名版本`是否一致，  
然后使用 `ANDROID_PHONE` 协议登录即可。

# 更改协议信息

插件加载时，会扫描 `base-path` 下以协议名命名的文件并加载协议变更。如 `android_phone.json`。  
本插件的签名服务仅支持加载了协议变更的协议使用。

# 升级插件

如果你要升级 qsign 插件，通常只需要更换 plugins 文件夹内的 `qsign-x.x.x.mirai2.jar` 即可。  
有时 `txlib` 内的签名服务相关文件会变动，可按需决定是否更新。

# 屏蔽/减少日志
> 仅限 mirai-console，如果你在使用 mirai-core 并且没有使用 MiraiLogger 作为日志框架，请根据自己使用的日志框架自行想办法解决。

通常我们会看到日志的输出格式如下
```
年-月-日 时:分:秒 I/名称: 内容
例如
2023-07-16 11:45:14 I/QSign: Hello World!
```
在 **mirai关闭的情况下** 编辑配置文件 `config/Console/Logger.yml`，  
在 `loggers` 下增加一句 `名称: NONE` 即可将该名称的日志等级改为 `无`，即不输出日志。  
举个例子：
```yaml
# 特定日志记录器输出等级
loggers:
  com.github.unidbg.linux.ARM64SyscallHandler: NONE
  com.github.unidbg.linux.AndroidSyscallHandler: NONE
  stderr: NONE
```

# 在 mirai-core 中使用

先想办法引用本插件为依赖，在登录前执行以下代码  
[kotlin](src/test/kotlin/CoreUsage.kt)  
[java](src/test/java/CoreUsage.java)

构建的包稍大，投放到网络仓库上下载会很慢，请使用本地依赖。

# 编译

编译插件
```
./gradlew buildPlugin
```
打包发布 (将会在项目目录生成 `qsign-x.x.x-all.zip`)
```
./gradlew deploy
```

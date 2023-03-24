# [Mirai Skia Plugin](https://github.com/cssxsh/mirai-skia-plugin)

> Mirai Skia 前置插件

[![maven-central](https://img.shields.io/maven-central/v/xyz.cssxsh.mirai/mirai-skia-plugin)](https://search.maven.org/artifact/xyz.cssxsh.mirai/mirai-skia-plugin)
[![test](https://github.com/cssxsh/mirai-skia-plugin/actions/workflows/test.yml/badge.svg)](https://github.com/cssxsh/mirai-skia-plugin/actions/workflows/test.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ad7fe0e93f794914894fe5e6d3f23b2c)](https://www.codacy.com/gh/cssxsh/mirai-skia-plugin/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cssxsh/mirai-skia-plugin&amp;utm_campaign=Badge_Grade)

Be based on <https://github.com/JetBrains/skiko>

## SkiaToMirai

[SkiaToMirai](src/main/kotlin/xyz/cssxsh/mirai/skia/SkiaToMirai.kt)  
[SkiaExternalResource](src/main/kotlin/xyz/cssxsh/mirai/skia/SkiaExternalResource.kt)  

## Example

[Example](src/main/kotlin/xyz/cssxsh/skia/Example.kt)

## Dependency

作为 Mirai Console 前置插件： 
配置文件 `build.gradle.kts`
```kotlin
repositories {
    mavenCentral()
    // skiko 还未发布正式版到 Central，需要加入下面的 repo
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-skia-plugin:${version}")
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}
```
定义 `dependsOn`
```kotlin
object MemeHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.meme-helper",
        name = "meme-helper",
        version = "1.0.2",
    ) {
        author("cssxsh")
        dependsOn("xyz.cssxsh.mirai.plugin.mirai-skia-plugin", ">= 1.1.0", false)
    }
)
```

作为 Mirai Core Jvm 引用:  
配置文件 `build.gradle.kts`
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("xyz.cssxsh.mirai:mirai-skia-plugin:${version}")
}
```
手动调用库加载函数
```kotlin
import xyz.cssxsh.mirai.skia.*

checkPlatform()
loadJNILibrary()
```

## GIF

由于 Skiko 没有携带 GIF 编码器，
这里提供两个实现
*   [kotlin](src/main/kotlin/xyz/cssxsh/skia/gif) - [petpet](src/main/kotlin/xyz/cssxsh/skia/Example.kt)
*   [rust](src/main/kotlin/xyz/cssxsh/gif) (Base on [JNI](https://github.com/cssxsh/gif-jni)) - [dear](src/main/kotlin/xyz/cssxsh/skia/Example.kt)

## 安装

### MCL 指令安装

**请确认 mcl.jar 的版本是 2.1.0+**  
`./mcl --update-package xyz.cssxsh.mirai:mirai-skia-plugin --channel maven-stable --type plugins`

### 手动安装

1.  从 [Releases](https://github.com/cssxsh/mirai-skia-plugin/releases) 或者 [Maven](https://repo1.maven.org/maven2/xyz/cssxsh/mirai/mirai-skia-plugin/) 下载 `mirai2.jar`
2.  将其放入 `plugins` 文件夹中

### 缺少库

如果启动后出现 `XXX: cannot open shared object file: No such file or directory` 或者 `XXX: 无法打开共享对象文件: 没有那个文件或目录`  
说明你的 `Linux` 系统缺少了某些前置库文件 `XXX`, 你需要自行补充安装, 可以通过 <https://pkgs.org/search> 检索相关信息  

例如，出现 `libGL.so.1: cannot open shared object file: No such file or directory`  
参阅 <https://pkgs.org/search/?q=libGL.so.1>, 找到对应的系统及版本然后，进入相关库介绍页面，下拉找到安装指令  

## 兼容性

|        OS/Arch         | Plugin | Skiko  |  Gif  |
|:----------------------:|:------:|:------:|:-----:|
|     Windows-10-X64     | 1.3.0  | 0.7.54 | 2.0.8 |
|     GNU/Linux-X64      | 1.3.0  | 0.7.54 | 2.0.8 |
|    GNU/Linux-ARM64     | 1.3.0  | 0.7.54 | 2.0.8 |
|       MacOS-X64        | 1.3.0  | 0.7.54 | 2.0.8 |
|      MacOS-ARM64       | 1.3.0  | 0.7.54 | 2.0.8 |
| Termux (Android-ARM64) | 1.3.0  | 0.7.54 | 2.0.8 |

暂时不支持 `Alpine Linux` 等 `MUSL/linux` 系统, 你可以关注 [![issue-11](https://shields.io/github/issues/detail/state/cssxsh/mirai-skia-plugin/11)](https://github.com/cssxsh/mirai-skia-plugin/issues/11)

## [爱发电](https://afdian.net/@cssxsh)

![afdian](.github/afdian.jpg)
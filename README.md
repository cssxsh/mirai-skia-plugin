# [Mirai Skia Plugin](https://github.com/cssxsh/mirai-skia-plugin)

> Mirai Skia 前置插件

[![maven-central](https://img.shields.io/maven-central/v/xyz.cssxsh.mirai/mirai-skia-plugin)](https://search.maven.org/artifact/xyz.cssxsh.mirai/mirai-skia-plugin)
[![test](https://github.com/cssxsh/mirai-skia-plugin/actions/workflows/test.yml/badge.svg)](https://github.com/cssxsh/mirai-skia-plugin/actions/workflows/test.yml)

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
* [kotlin](src/main/kotlin/xyz/cssxsh/skia/gif) - [petpet](src/main/kotlin/xyz/cssxsh/skia/Example.kt)
* [rust](src/main/kotlin/xyz/cssxsh/gif) (Base on [JNI](https://github.com/cssxsh/gif-jni)) - [dear](src/main/kotlin/xyz/cssxsh/skia/Example.kt)

## 安装

### MCL 指令安装

**请确认 mcl.jar 的版本是 2.1.0+**  
`./mcl --update-package xyz.cssxsh.mirai:mirai-skia-plugin --channel stable --type plugin`

### 手动安装

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成`plugins`文件夹
2. 从 [Releases](https://github.com/cssxsh/mirai-skia-plugin/releases) 下载`jar`并将其放入`plugins`文件夹中

## 兼容性

|        OS/Arch         |  Skia  |  Gif  |
|:----------------------:|:------:|:-----:|
|     Windows-10-X64     | 0.7.34 | 2.0.8 |
|       Linux-X64        | 0.7.34 | 2.0.8 |
|      Linux-Arm64       | 0.7.34 | 2.0.8 |
|       MacOS-X64        | 0.7.34 | 2.0.8 |
|      MacOS-Arm64       | 0.7.34 | 2.0.8 |
| Termux (Android-Arm64) | 0.7.34 | 2.0.8 |
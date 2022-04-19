# [Mirai Skia Plugin](https://github.com/cssxsh/mirai-skia-plugin)

> Mirai Skia 前置插件

[![maven-central](https://img.shields.io/maven-central/v/xyz.cssxsh.mirai/mirai-skia-plugin)](https://search.maven.org/artifact/xyz.cssxsh.mirai/mirai-skia-plugin)
[![test](https://github.com/cssxsh/mirai-skia-plugin/actions/workflows/test.yml/badge.svg)](https://github.com/cssxsh/mirai-skia-plugin/actions/workflows/test.yml)

Be based on <https://github.com/JetBrains/skiko>

## SkiaToMirai

[SkiaToMirai](src/main/kotlin/xyz/cssxsh/mirai/SkiaToMirai.kt)  
[SkiaExternalResource](src/main/kotlin/xyz/cssxsh/mirai/SkiaExternalResource.kt)  

## Example

[Example](src/main/kotlin/xyz/cssxsh/skia/Example.kt)

## GIF

由于 Skiko 没有携带 GIF 编码器，
这里提供两个实现
* [kotlin](src/main/kotlin/xyz/cssxsh/skia/gif) 
* [rust](src/main/kotlin/xyz/cssxsh/gif) (Base on [JNI](https://github.com/cssxsh/gif-jni))
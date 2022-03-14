plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("net.mamoe.mirai-console") version "2.10.1"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh.mirai"
version = "1.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "mirai-skia-plugin")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
    }
}

dependencies {
    api("org.jetbrains.skiko:skiko-awt:0.7.16") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }
    api("org.jetbrains.skiko:skiko-awt-runtime-linux-arm64:0.7.16")
    api("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.7.16")
    api("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.16")
    api("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.16")
    api("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.16")
    compileOnly("net.mamoe:mirai-core-utils:2.10.1")
    compileOnly("org.jsoup:jsoup:1.14.3")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    //
    testImplementation(kotlin("test", "1.6.0"))
    testImplementation("org.jsoup:jsoup:1.14.3")
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}


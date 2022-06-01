plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("net.mamoe.mirai-console") version "2.11.1"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh.mirai"
version = "1.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "mirai-skia-plugin")
    licenseFromGitHubProject("AGPL-3.0", "master")
    publication {
        artifact(tasks.getByName("buildPlugin"))
        artifact(tasks.getByName("buildPluginLegacy"))
    }
}

dependencies {
    api("org.jetbrains.skiko:skiko-awt:0.7.20") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("org.jsoup:jsoup:1.14.3")
    compileOnly("net.mamoe:mirai-core-utils:2.11.1")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    //
    testImplementation(kotlin("test", "1.6.21"))
    testRuntimeOnly("org.jetbrains.skiko:skiko-awt-runtime-linux-arm64:0.7.20")
    testRuntimeOnly("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.7.20")
    testRuntimeOnly("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.20")
    testRuntimeOnly("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.20")
    testRuntimeOnly("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.20")
    testRuntimeOnly("org.jetbrains.skiko:skiko-android-runtime-arm64:0.7.20")
    testRuntimeOnly("org.jetbrains.skiko:skiko-android-runtime-x64:0.7.20")
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}


plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("net.mamoe.mirai-console") version "2.10.0"
    id("net.mamoe.maven-central-publish") version "0.7.1"
}

group = "xyz.cssxsh.mirai"
version = "1.0.0-dev"

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
    api("org.jetbrains.skiko:skiko-awt:0.7.9") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.0") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    }
    api("org.jetbrains.skiko:skiko-awt-runtime-linux-arm64:0.7.9")
    api("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.7.9")
    api("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.9")
    api("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.7.9")
    api("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.9")
    compileOnly("net.mamoe:mirai-core-utils:2.10.0")
    //
    testImplementation(kotlin("test", "1.6.0"))
}

kotlin {
    explicitApi()
}

mirai {
    configureShadow {
        exclude("module-info.class")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}


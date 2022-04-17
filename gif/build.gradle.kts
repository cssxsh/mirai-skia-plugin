plugins {
    kotlin("jvm")
}

group = "xyz.cssxsh.gif"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    api("org.jetbrains.skiko:skiko-awt:0.7.18")
    implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.18")
    //
    testImplementation(kotlin("test", "1.6.0"))
}

kotlin {
    explicitApi()
}

tasks {
    test {
        useJUnitPlatform()
    }
}
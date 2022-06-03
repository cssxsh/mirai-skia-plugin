package xyz.cssxsh.mirai.skia

import kotlinx.coroutines.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import xyz.cssxsh.skia.*

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.1.0",
    ) {
        author("cssxsh")
    }
) {

    override fun PluginComponentStorage.onLoad() {
        // Termux
        if (hostOs == OS.Linux && "termux" in System.getProperty("user.dir")) {
            logger.info { "change platform: $hostId to Android" }
            try {
                val kt = Class.forName("org.jetbrains.skiko.OsArch_jvmKt")
                val delegate = kt.getDeclaredField("hostId\$delegate").apply { isAccessible = true }
                val lazy = delegate.get(null)
                val value = lazy::class.java.getDeclaredField("_value").apply { isAccessible = true }
                value.set(lazy, "android-arm64")
            } catch (_: Throwable) {
                logger.warning { "修改 hostId 失败" }
            }
        }
    }

    override fun onEnable() {
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        launch {
            loadJNILibrary(folder = resolveDataFile("lib"), version = version)
            loadTypeface(folder = resolveDataFile("fonts"))
            logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }
        }
    }
}
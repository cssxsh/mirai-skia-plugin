package xyz.cssxsh.mirai.skia

import kotlinx.coroutines.*
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import xyz.cssxsh.skia.*

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.1.2",
    ) {
        author("cssxsh")
    }
) {

    public val loadJob: Job = launch {
        checkPlatform()
        loadJNILibrary(folder = resolveDataFile("lib"))
    }

    override fun PluginComponentStorage.onLoad() {
        loadJob.start()
    }

    override fun onEnable() {
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        runBlocking {
            loadJob.join()
        }
        loadTypeface(folder = resolveDataFile("fonts"))
        logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }
    }
}
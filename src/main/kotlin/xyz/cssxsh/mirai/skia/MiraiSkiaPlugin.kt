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
        version = "1.1.1",
    ) {
        author("cssxsh")
    }
) {

    override fun PluginComponentStorage.onLoad() { checkPlatform() }

    public lateinit var loadJob: Job
        private set

    override fun onEnable() {
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        loadJob = launch {
            loadJNILibrary(folder = resolveDataFile("lib"))
            loadTypeface(folder = resolveDataFile("fonts"))
            logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }
        }
    }
}
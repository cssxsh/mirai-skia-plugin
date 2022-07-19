package xyz.cssxsh.mirai.skia

import kotlinx.coroutines.*
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import xyz.cssxsh.skia.*

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.1.6",
    ) {
        author("cssxsh")
    }
) {

    internal val loadJob: Job = launch {
        checkPlatform()
        loadJNILibrary(folder = resolveDataFile("lib"))
        if (resolveDataFile("fonts").listFiles().isNullOrEmpty()) {
            downloadTypeface(folder = resolveDataFile("fonts"), links = FreeFontLinks)
        }
    }

    override fun PluginComponentStorage.onLoad() {
        loadJob.start()
    }

    override fun onEnable() {
        // XXX: mirai console version check
        check(SemVersion.parseRangeRequirement(">= 2.12.0-RC").test(MiraiConsole.version)) {
            "$name $version 需要 Mirai-Console 版本 >= 2.12.0，目前版本是 ${MiraiConsole.version}"
        }
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        runBlocking {
            loadJob.join()
        }
        loadTypeface(folder = resolveDataFile("fonts"))
        logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }
    }
}
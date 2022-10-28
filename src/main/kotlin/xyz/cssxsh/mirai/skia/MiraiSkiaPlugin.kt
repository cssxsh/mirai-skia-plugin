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
import java.io.Closeable

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.2.0",
    ) {
        author("cssxsh")
    }
) {

    @OptIn(ConsoleExperimentalApi::class)
    internal val process: Closeable? by lazy {
        try {
            val impl = MiraiConsole.newProcessProgress()
            listener = listener@{ message ->
                launch {
                    impl.updateText(message)
                }

                return@listener { total, contentLength ->
                    if (contentLength != 0L) {
                        impl.update(total, contentLength)
                        impl.rerender()
                    }
                }
            }
            impl
        } catch (_: Throwable) {
            null
        }
    }

    internal val loadJob: Job = launch {
        checkPlatform()
        process
        loadJNILibrary(folder = resolveDataFile("lib"))
        if (resolveDataFile("fonts").list().isNullOrEmpty()) {
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
        loadJob.invokeOnCompletion { cause ->
            val message = cause?.message
            if (cause is UnsatisfiedLinkError && message != null) {
                if (message.endsWith(": cannot open shared object file: No such file or directory")) {
                    val lib = message.substringBeforeLast(": cannot open shared object file: No such file or directory")
                        .substringAfterLast(": ")
                    logger.warning { "可能缺少相应库文件，请参阅: https://pkgs.org/search/?q=${lib}" }
                }
                if ("GLIBC_" in message) {
                    val version = message.substringAfterLast("version `")
                        .substringBeforeLast("' not found")
                    logger.warning { "可能缺少 ${version}, 请安装此版本 glibc" }
                }
            }
        }
        try {
            runBlocking {
                loadJob.join()
            }
        } finally {
            listener = { null }
            process?.close()
        }
        loadTypeface(folder = resolveDataFile("fonts"))
        logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }
    }
}
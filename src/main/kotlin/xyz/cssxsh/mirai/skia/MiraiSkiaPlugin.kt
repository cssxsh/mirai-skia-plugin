package xyz.cssxsh.mirai.skia

import io.ktor.client.content.*
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

/**
 * mirai-skia-plugin 插件主类
 */
public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.2.3",
    ) {
        author("cssxsh")
    }
) {

    @OptIn(ConsoleExperimentalApi::class)
    internal val process: Closeable? by lazy {
        try {
            check(SemVersion.parseRangeRequirement("> 2.13.0").test(MiraiConsole.version))
            val impl = MiraiConsole.newProcessProgress()
            listener = { file ->
                val progress: ProgressListener = { total, contentLength ->
                    if (total >= contentLength) {
                        impl.updateText("<${file}> 下载完成")
                    } else {
                        impl.updateText("<${file}> 下载中")
                        impl.update(total, contentLength)
                    }
                    impl.rerender()
                }

                progress
            }
            impl
        } catch (_: Throwable) {
            null
        }
    }

    @PublishedApi
    internal val loadJob: Job = launch(start = CoroutineStart.LAZY) {
        checkPlatform()
        process
        try {
            loadJNILibrary(folder = resolveDataFile("lib"))
            val fonts = resolveDataFile("fonts")
            if (fonts.list().isNullOrEmpty()) {
                downloadTypeface(folder = fonts, links = FreeFontLinks)
            }
        } finally {
            listener = { null }
            runInterruptible(Dispatchers.IO) {
                process?.close()
            }
        }
    }

    override fun PluginComponentStorage.onLoad() {
        loadJob.invokeOnCompletion { cause ->
            val message = cause?.message
            if (cause is UnsatisfiedLinkError && message != null) {
                if (message.endsWith(": cannot open shared object file: No such file or directory")) {
                    val lib = message.substringBeforeLast(": cannot open shared object file: No such file or directory")
                        .substringAfterLast(": ")
                    logger.warning { "可能缺少相应库文件，请参阅: https://pkgs.org/search/?q=${lib}" }
                }
                if (message.endsWith(": 无法打开共享对象文件: 没有那个文件或目录")) {
                    val lib = message.substringBeforeLast(": 无法打开共享对象文件: 没有那个文件或目录")
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
package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import org.jetbrains.skia.Image as SkiaImage
import xyz.cssxsh.mirai.*
import xyz.cssxsh.skia.*

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.0.3",
    ) {
        author("cssxsh")
    }
) {

    override fun onEnable() {
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        launch {
            loadTypeface(folder = dataFolder.resolve("fonts"))
            logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }
        }

        val test = System.getProperty("xyz.cssxsh.skia.test", "false").toBoolean()
        if (test) {
            launch {
                loadFace(folder = dataFolder.resolve("face"))
            }
            globalEventChannel().subscribeMessages {
                """^#ph\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { "ph ${result.value}" }
                    val (porn, hub) = result.destructured

                    subject.uploadImage(resource = pornhub(porn, hub).makeSnapshotResource())
                }
                """^#pet\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { "pet ${result.value}" }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val file = dataFolder.resolve("${id}.jpg")
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    if (file.exists().not()) download(urlString = url, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())

                    subject.uploadImage(resource = SkiaExternalResource(origin = petpet(face), formatName = "gif"))
                }
                """^#choyen\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { "choyen ${result.value}" }
                    val (top, bottom) = result.destructured

                    subject.uploadImage(resource = choyen(top, bottom).makeSnapshotResource())
                }
            }
        }
    }
}
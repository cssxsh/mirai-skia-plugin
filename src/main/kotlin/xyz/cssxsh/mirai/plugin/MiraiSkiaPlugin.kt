package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.jetbrains.skiko.*
import org.jetbrains.skia.Image as SkiaImage
import xyz.cssxsh.mirai.*
import xyz.cssxsh.skia.*

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.0.4",
    ) {
        author("cssxsh")
    }
) {

    override fun onEnable() {
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        launch {
            loadTypeface(folder = dataFolder.resolve("fonts"))
            logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }

            try {
                xyz.cssxsh.gif.Library.staticLoad()
            } catch (cause: Throwable) {
                logger.warning { cause.message }
            }
        }

        val test = System.getProperty("xyz.cssxsh.skia.test", "false").toBoolean()
        if (test) {
            launch {
                loadFace(folder = dataFolder.resolve("face"))
            }
            globalEventChannel().subscribeMessages {
                """^#face\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    val file = dataFolder.resolve("${id}.jpg")
                    file.delete()
                    download(urlString = url, folder = dataFolder).renameTo(file)

                    subject.uploadImage(file)
                }
                """^#ph\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val (porn, hub) = result.destructured

                    subject.uploadImage(resource = pornhub(porn, hub).makeSnapshotResource())
                }
                """^#pet\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val file = dataFolder.resolve("${id}.jpg")
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    if (file.exists().not()) download(urlString = url, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())

                    subject.uploadImage(resource = SkiaExternalResource(origin = petpet(face), formatName = "gif"))
                }
                """^#dear\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val file = dataFolder.resolve("${id}.jpg")
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    if (file.exists().not()) download(urlString = url, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())
                    val dear = dear(face)

                    dear.toExternalResource().use { resource ->
                        subject.uploadImage(resource = resource)
                    }
                }
                """^#choyen\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val (top, bottom) = result.destructured

                    subject.uploadImage(resource = choyen(top, bottom).makeSnapshotResource())
                }
            }
        }
    }
}
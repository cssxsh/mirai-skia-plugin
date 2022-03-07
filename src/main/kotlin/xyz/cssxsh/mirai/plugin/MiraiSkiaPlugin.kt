package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import org.jetbrains.skia.Image as SkiaImage
import xyz.cssxsh.mirai.*
import xyz.cssxsh.skia.*

public object MiraiSkiaPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.mirai-skia-plugin",
        name = "mirai-skia-plugin",
        version = "1.0.0",
    ) {
        author("cssxsh")
    }
) {

    override fun onEnable() {
        logger.info { "platform: ${hostId}, skia: ${Version.skia}, skiko: ${Version.skiko}" }
        loadTypeface(folder = dataFolder.resolve("fonts"))
        logger.info { "fonts: ${FontUtils.provider.makeFamilies().keys}" }

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
                """^#pet( \d+(?:\.\d+)?)?""".toRegex() findingReply { result ->
                    logger.info { "pet ${result.value}" }
                    val delay = result.groups[1]?.value?.toDoubleOrNull() ?: 0.02
                    val user = message.findIsInstance<At>()?.target?.let { (subject as? Group)?.get(it) } ?: sender
                    val file = dataFolder.resolve("${user.id}.jpg")
                    if (file.exists().not()) download(urlString = user.avatarUrl, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())

                    subject.uploadImage(resource = SkiaExternalResource(origin = petpet(face, delay), formatName = "gif"))
                }
                """^#shout(.+)""".toRegex() findingReply { result ->
                    logger.info { "shout ${result.value}" }
                    val lines = message.firstIsInstance<PlainText>().content
                        .removePrefix("#shout")
                        .split(' ').filterNot { it.isBlank() }
                        .toTypedArray()
                    subject.uploadImage(resource = shout(lines = lines).makeSnapshotResource())
                }
                """^#choyen\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { "choyen ${result.value}" }
                    val (top, bottom) = result.destructured

                    subject.uploadImage(resource = choyen(top, bottom).makeSnapshotResource())
                }
                """^#lick""".toRegex() findingReply { result ->
                    logger.info { "lick ${result.value}" }
                    val user = message.findIsInstance<At>()?.target?.let { (subject as? Group)?.get(it) } ?: sender
                    val file = dataFolder.resolve("${user.id}.jpg")
                    if (file.exists().not()) download(urlString = user.avatarUrl, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())

                    subject.uploadImage(resource = SkiaExternalResource(origin = lick(face), formatName = "gif"))
                }
            }
        }
    }
}
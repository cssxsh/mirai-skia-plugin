package xyz.cssxsh.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.contact.*
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
        }

        val test = System.getProperty("xyz.cssxsh.skia.test").toBoolean()
        if (test) {
            launch {
                loadFace(folder = dataFolder.resolve("face"))
            }
            globalEventChannel().subscribeMessages {
                """^#ph\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val (porn, hub) = result.destructured

                    pornhub(porn, hub).makeSnapshotResource()
                        .use { resource -> subject.uploadImage(resource = resource) }
                }
                """^#pet\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val face = SkiaImage.makeFromEncoded(avatar(id = id, size = 140, folder = dataFolder).readBytes())

                    SkiaExternalResource(origin = petpet(face), formatName = "gif")
                        .use { resource -> subject.uploadImage(resource = resource) }
                }
                """^#dear\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val face = SkiaImage.makeFromEncoded(avatar(id = id, size = 140, folder = dataFolder).readBytes())
                    val dear = dear(face)

                    dear.toExternalResource()
                        .use { resource -> subject.uploadImage(resource = resource) }
                }
                """^#choyen\s+(\S+)\s+(\S+)""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val (top, bottom) = result.destructured

                    choyen(top, bottom).makeSnapshotResource()
                        .use { resource -> subject.uploadImage(resource = resource) }
                }
                """^#(zzkia|pinyin)\s+((?s).+)""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val (_, text) = result.destructured

                    zzkia(text).makeSnapshotResource()
                        .use { resource -> subject.uploadImage(resource = resource) }
                }
                """^#spell\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    val file = dataFolder.resolve("${id}.jpg")
                    if (file.exists().not()) download(urlString = url, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())
                    val lines = message.contentToString().lines()
                    val member = (subject as? Group)?.get(id)
                    val name = lines.getOrNull(1)?.takeIf { it.isNotBlank() }
                        ?: member?.remarkOrNameCardOrNick
                        ?: senderName
                    val description = lines.filterIndexed { index, _ -> index > 1 }.joinToString("\n")
                        .ifBlank { (member ?: sender).queryProfile().sign }
                    val spell = YgoCard.Spell(name = name, description = description, face = face)
                        .render(mold = dataFolder.resolve("ygo"))

                    subject.uploadImage(resource = spell.makeSnapshotResource())
                }
                """^#trap\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    val file = dataFolder.resolve("${id}.jpg")
                    if (file.exists().not()) download(urlString = url, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())
                    val lines = message.contentToString().lines()
                    val member = (subject as? Group)?.get(id)
                    val name = lines.getOrNull(1)?.takeIf { it.isNotBlank() }
                        ?: member?.remarkOrNameCardOrNick
                        ?: senderName
                    val description = lines.filterIndexed { index, _ -> index > 1 }.joinToString("\n")
                        .ifBlank { (member ?: sender).queryProfile().sign }
                    val trap = YgoCard.Trap(name = name, description = description, face = face)
                        .render(mold = dataFolder.resolve("ygo"))

                    subject.uploadImage(resource = trap.makeSnapshotResource())
                }
                """^#monster\s*(\d+)?""".toRegex() findingReply { result ->
                    logger.info { result.value }
                    val id = result.groups[1]?.value?.toLongOrNull()
                        ?: message.findIsInstance<At>()?.target
                        ?: sender.id
                    val url = "https://q.qlogo.cn/g?b=qq&nk=${id}&s=640"
                    val file = dataFolder.resolve("${id}.jpg")
                    if (file.exists().not()) download(urlString = url, folder = dataFolder).renameTo(file)
                    val face = SkiaImage.makeFromEncoded(file.readBytes())
                    val lines = message.contentToString().lines()
                    val member = (subject as? Group)?.get(id)
                    val name = lines.getOrNull(1)?.takeIf { it.isNotBlank() }
                        ?: member?.remarkOrNameCardOrNick
                        ?: senderName
                    val profile = (member ?: sender).queryProfile()
                    val race = lines.getOrNull(2)?.takeIf { it.isNotBlank() }?.split(',', ' ', '/')
                        ?: listOfNotNull(member?.specialTitle?.ifBlank { member.permission.name }, profile.sex.name)
                    val description = lines.filterIndexed { index, _ -> index > 2 }.joinToString("\n")
                        .ifBlank { profile.sign }
                    val level = (profile.qLevel / 16) + 1
                    val attribute = YgoCard.Attribute.monster().random()
                    val trap = YgoCard
                        .Monster(
                            name = name,
                            description = description,
                            face = face,
                            level = level,
                            race = race,
                            attribute = attribute
                        )
                        .render(mold = dataFolder.resolve("ygo"))

                    subject.uploadImage(resource = trap.makeSnapshotResource())
                }
            }
        }
    }
}
package xyz.cssxsh.mirai.skia

import net.mamoe.mirai.utils.*
import org.jetbrains.skia.*
import java.io.*

/**
 * [ExternalResource] 关于 skia 的实现
 * @see ExternalResource
 */
public class SkiaExternalResource(override val origin: Data, override val formatName: String) :
    ExternalResource, AbstractExternalResource({ origin.close() }) {
    public constructor(image: Image, format: EncodedImageFormat) : this(
        origin = requireNotNull(image.encodeToData(format)) { "encode $format result null." },
        formatName = format.name.replace("JPEG", "JPG")
    )

    override val md5: ByteArray by lazy { origin.bytes.md5() }
    override val sha1: ByteArray by lazy { origin.bytes.sha1() }
    override val size: Long get() = origin.size.toLong()
    override fun inputStream0(): InputStream = origin.bytes.inputStream()
}

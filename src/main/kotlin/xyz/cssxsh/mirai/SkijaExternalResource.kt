package xyz.cssxsh.mirai

import kotlinx.coroutines.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skia.*
import java.io.*

public class SkijaExternalResource(override val origin: Data, override val formatName: String) : ExternalResource {
    public constructor(image: Image, format: EncodedImageFormat) : this(
        origin = requireNotNull(image.encodeToData(format)) { "encode $format result null." },
        formatName = format.name.replace("JPEG", "JPG")
    )

    override val closed: CompletableDeferred<Unit> = CompletableDeferred()
    override val md5: ByteArray by lazy { origin.bytes.md5() }
    override val sha1: ByteArray by lazy { origin.bytes.sha1() }
    override val size: Long get() = origin.size.toLong()

    override fun close() {
        closed.completeWith(origin.runCatching { close() })
    }

    override fun inputStream(): InputStream = origin.bytes.inputStream()
}

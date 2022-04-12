package xyz.cssxsh.mirai

import net.mamoe.mirai.utils.*
import org.jetbrains.skia.*
import org.jetbrains.skia.svg.*
import xyz.cssxsh.skia.*
import java.io.*

/**
 * 从 [Surface] 获取图片快照资源
 * @see Surface.makeImageSnapshot
 * @see ExternalResource
 */
@JvmOverloads
public fun Surface.makeSnapshotResource(format: EncodedImageFormat = EncodedImageFormat.PNG): SkiaExternalResource {
    return SkiaExternalResource(image = makeImageSnapshot(), format = format)
}

/**
 * 从 [File] 获取图片快照资源, 用于转换图片格式，例如 WEBP to PNG
 *
 * 注意: SVG 格式的矢量图 因为不一定包含 高度 和 宽度，所以不适用此方法, 可见 [SVGDOM.makeImageSnapshot]
 * @see Image.makeFromEncoded
 * @see ExternalResource
 */
@JvmOverloads
public fun File.makeImageResource(format: EncodedImageFormat = EncodedImageFormat.PNG): SkiaExternalResource {
    return SkiaExternalResource(image = Image.makeFromEncoded(readBytes()), format = format)
}
package xyz.cssxsh.skia.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*
import java.nio.*

/**
 * GIF 构建器
 * @param width 宽
 * @param height 高
 */
public class GIFBuilder(public val width: Int, public val height: Int) {
    public companion object {
        internal const val GIF_HEADER = "GIF89a"
        internal const val GIF_TRAILER = ";"
    }

    private fun header(buffer: ByteBuffer) = buffer.put(GIF_HEADER.toByteArray(Charsets.US_ASCII))

    private fun trailer(buffer: ByteBuffer) = buffer.put(GIF_TRAILER.toByteArray(Charsets.US_ASCII))

    /**
     * [ByteBuffer.capacity]
     */
    @GIFDsl
    public var capacity: Int = 1 shl 23

    /**
     * [ByteBuffer.capacity]
     */
    @GIFDsl
    public fun capacity(total: Int): GIFBuilder = apply { capacity = total }

    /**
     * Netscape Looping Application Extension, 0 is infinite times
     * @see [ApplicationExtension.loop]
     */
    @GIFDsl
    public var loop: Int = 0

    /**
     * Netscape Looping Application Extension, 0 is infinite times
     * @see [ApplicationExtension.loop]
     */
    @GIFDsl
    public fun loop(count: Int): GIFBuilder = apply { loop = count }

    /**
     * Netscape Buffering Application Extension
     * @see [ApplicationExtension.buffering]
     */
    @GIFDsl
    public var buffering: Int = 0

    /**
     * Netscape Buffering Application Extension
     * @see [ApplicationExtension.buffering]
     */
    @GIFDsl
    public fun buffering(open: Boolean): GIFBuilder = apply { buffering = if (open) 0x0001_0000 else 0x0000_0000 }

    /**
     * Pixel Aspect Ratio
     * @see [LogicalScreenDescriptor.write]
     */
    @GIFDsl
    public var ratio: Int = 0

    /**
     * Pixel Aspect Ratio
     * @see [LogicalScreenDescriptor.write]
     */
    @GIFDsl
    public fun ratio(size: Int): GIFBuilder = apply {
        ratio = size
    }

    /**
     * GlobalColorTable
     * @see [OctTreeQuantizer.quantize]
     */
    @GIFDsl
    public var global: ColorTable = ColorTable.Empty

    /**
     * GlobalColorTable
     * @see [OctTreeQuantizer.quantize]
     */
    @GIFDsl
    public fun table(bitmap: Bitmap): GIFBuilder = apply {
        global = if (bitmap.computeIsOpaque()) {
            ColorTable(OctTreeQuantizer().quantize(bitmap, 256), true, null)
        } else {
            ColorTable(OctTreeQuantizer().quantize(bitmap, 255), true)
        }
    }

    /**
     * GlobalColorTable
     */
    @GIFDsl
    public fun table(value: ColorTable): GIFBuilder = apply {
        global = value
    }

    /**
     * GlobalFrameOptions
     */
    @GIFDsl
    public var options: AnimationFrameInfo = AnimationFrameInfo(
        requiredFrame = -1,
        duration = 1000,
        // no use
        isFullyReceived = false,
        alphaType = ColorAlphaType.OPAQUE,
        isHasAlphaWithinBounds = false,
        disposalMethod = AnimationDisposalMode.UNUSED,
        blendMode = BlendMode.CLEAR,
        frameRect = IRect.makeXYWH(0, 0, 0, 0)
    )

    /**
     * GlobalFrameOptions
     */
    @GIFDsl
    public fun options(block: AnimationFrameInfo.() -> Unit): GIFBuilder = apply {
        options.apply(block)
    }

    /**
     * 帧集合
     */
    @GIFDsl
    public var frames: MutableList<Triple<Bitmap, ColorTable, AnimationFrameInfo>> = ArrayList()

    /**
     * 写入帧
     * @param bitmap 源位图
     * @param colors 调色板
     * @param block 帧信息DSL
     */
    @GIFDsl
    public fun frame(
        bitmap: Bitmap,
        colors: ColorTable = ColorTable.Empty,
        block: AnimationFrameInfo.() -> Unit = {}
    ): GIFBuilder = apply {
        val rect = IRect.makeXYWH(0, 0, bitmap.width, bitmap.height)
        frames.add(Triple(bitmap, colors, options.withFrameRect(rect).withAlphaType(bitmap.alphaType).apply(block)))
    }

    /**
     * 写入帧
     * @param bitmap 源位图
     * @param colors 调色板
     * @param info 帧信息
     */
    @GIFDsl
    public fun frame(
        bitmap: Bitmap,
        colors: ColorTable = ColorTable.Empty,
        info: AnimationFrameInfo
    ): GIFBuilder = apply {
        frames.add(Triple(bitmap, colors, info))
    }

    /**
     * 构建到 buffer
     * @param buffer 写入的目标
     */
    @PublishedApi
    internal fun build(buffer: ByteBuffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        header(buffer)
        LogicalScreenDescriptor.write(buffer, width, height, global, ratio)
        if (loop >= 0) ApplicationExtension.loop(buffer, loop)
        if (buffering > 0) ApplicationExtension.buffering(buffer, buffering)
        for ((bitmap, colors, info) in frames) {
            val table = when {
                colors.exists() -> colors
                global.exists() -> global
                else -> {
                    if (info.alphaType == ColorAlphaType.OPAQUE || bitmap.computeIsOpaque()) {
                        ColorTable(OctTreeQuantizer().quantize(bitmap, 256), true, null)
                    } else {
                        ColorTable(OctTreeQuantizer().quantize(bitmap, 255), true)
                    }
                }
            }

            GraphicControlExtension.write(buffer, info.disposalMethod, false, table.transparency, info.duration)

            val result = AtkinsonDitherer.dither(bitmap, table.colors)

            @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
            ImageDescriptor.write(buffer, info.frameRect, table, table !== global, result)
        }
        trailer(buffer)
    }

    /**
     * 构建为数据
     */
    public fun data(): Data {
        val data = Data.makeUninitialized(capacity)
        val buffer = BufferUtil.getByteBufferFromPointer(data.writableData(), capacity)
        build(buffer = buffer)

        return data.makeSubset(0, buffer.position())
    }

    /**
     * 构建为 bytes
     */
    public fun build(): ByteArray {
        val buffer = ByteBuffer.allocate(capacity)
        build(buffer = buffer)

        return buffer.array().sliceArray(0 until buffer.position())
    }
}
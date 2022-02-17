package xyz.cssxsh.skia.gif


import org.jetbrains.skia.*
import org.jetbrains.skia.impl.BufferUtil
import java.nio.*

public class GIFBuilder(public val width: Int, public val height: Int) {
    public companion object {
        internal const val GIF_HEADER = "GIF89a"
        internal const val GIF_TRAILER = ";"
    }

    private fun header(buffer: ByteBuffer) = buffer.put(GIF_HEADER.toByteArray(Charsets.US_ASCII))

    private fun trailer(buffer: ByteBuffer) = buffer.put(GIF_TRAILER.toByteArray(Charsets.US_ASCII))

    public var capacity: Int = 1 shl 23

    /**
     * [ByteBuffer.capacity]
     */
    public fun capacity(total: Int): GIFBuilder = apply { capacity = total }

    public var loop: Int = 0

    /**
     * Netscape Looping Application Extension, 0 is infinite times
     * @see [ApplicationExtension.loop]
     */
    public fun loop(count: Int): GIFBuilder = apply { loop = count }

    public var buffering: Int = 0

    /**
     * Netscape Buffering Application Extension
     * @see [ApplicationExtension.buffering]
     */
    public fun buffering(open: Boolean): GIFBuilder = apply { buffering = if (open) 0x0001_0000 else 0x0000_0000 }

    public var ratio: Int = 0

    /**
     * Pixel Aspect Ratio
     * @see [LogicalScreenDescriptor.write]
     */
    public fun ratio(size: Int): GIFBuilder = apply { ratio = size }

    public var global: ColorTable = ColorTable.Empty

    /**
     * GlobalColorTable
     * @see [OctTreeQuantizer.quantize]
     */
    public fun table(bitmap: Bitmap): GIFBuilder = apply { global = ColorTable(OctTreeQuantizer.quantize(bitmap, 256)) }

    /**
     * GlobalColorTable
     */
    public fun table(value: ColorTable): GIFBuilder = apply { global = value }

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
    public fun options(block: AnimationFrameInfo.() -> Unit): GIFBuilder = apply { options.apply(block) }

    public var frames: MutableList<Triple<Bitmap, ColorTable, AnimationFrameInfo>> = ArrayList()

    public fun frame(
        bitmap: Bitmap,
        colors: ColorTable = ColorTable.Empty,
        block: AnimationFrameInfo.() -> Unit = {}
    ): GIFBuilder = apply {
        val rect = IRect.makeXYWH(0, 0, bitmap.width, bitmap.height)
        frames.add(Triple(bitmap, colors, options.withFrameRect(rect).apply(block)))
    }

    public fun frame(bitmap: Bitmap, colors: ColorTable = ColorTable.Empty, info: AnimationFrameInfo): GIFBuilder = apply {
        frames.add(Triple(bitmap, colors, info))
    }

    public fun build(buffer: ByteBuffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        header(buffer)
        LogicalScreenDescriptor.write(buffer, width, height, global, ratio)
        if (loop >= 0) ApplicationExtension.loop(buffer, loop)
        if (buffering > 0) ApplicationExtension.buffering(buffer, buffering)
        for ((bitmap, colors, info) in frames) {
            val table = when {
                colors.exists() -> colors
                global.exists() -> global
                else -> ColorTable(OctTreeQuantizer.quantize(bitmap, 256))
            }
            val transparency = if (options.alphaType == ColorAlphaType.OPAQUE) null else table.background

            GraphicControlExtension.write(buffer, info.disposalMethod, false, transparency, info.duration)

            val result = AtkinsonDitherer.dither(bitmap, table.colors)

            @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
            ImageDescriptor.write(buffer, info.frameRect, table, table !== global, result)
        }
        trailer(buffer)
    }

    public fun data(): Data {
        val data = Data.makeUninitialized(capacity)
        val buffer = BufferUtil.getByteBufferFromPointer(data.writableData(), capacity)
        build(buffer = buffer)

        return data.makeSubset(0, buffer.position())
    }

    public fun build(): ByteArray {
        val buffer = ByteBuffer.allocate(capacity)
        build(buffer = buffer)

        return ByteArray(buffer.position()).also { buffer.get(it) }
    }
}
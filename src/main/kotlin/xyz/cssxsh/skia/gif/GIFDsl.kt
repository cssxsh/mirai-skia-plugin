package xyz.cssxsh.skia.gif

import org.jetbrains.skia.*

internal fun Int.asUnsignedShort(): Short {
    check(this in 0..0xFFFF) { toString(16) }
    return toShort()
}

internal fun Int.asUnsignedByte(): Byte {
    check(this in 0..0xFF) { toString(16) }
    return toByte()
}

internal fun Int.asRGBBytes(): ByteArray {
    return byteArrayOf(
        (this and 0xFF0000 shr 16).toByte(),
        (this and 0x00FF00 shr 8).toByte(),
        (this and 0x0000FF).toByte()
    )
}

/**
 * 标记一些函数用于 GIF DSL
 */
@DslMarker
public annotation class GIFDsl

/**
 * GIF DSL
 */
@GIFDsl
public fun gif(width: Int, height: Int, block: GIFBuilder.() -> Unit): Data {
    return GIFBuilder(width, height)
        .apply(block)
        .data()
}
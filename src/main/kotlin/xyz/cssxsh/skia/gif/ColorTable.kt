package xyz.cssxsh.skia.gif

import java.nio.*

/**
 * 调色板
 * @param colors 颜色
 * @param sort 是否已排序
 * @param transparency 透明颜色序号
 * @param background 背景颜色序号
 */
public class ColorTable(
    public val colors: IntArray,
    public val sort: Boolean,
    public val transparency: Int? = (colors.capacity() - 1).coerceAtLeast(0),
    public val background: Int = 0
) {
    public companion object {
        private val SizeList = listOf(0, 2, 4, 8, 16, 32, 64, 128, 256)
        private fun IntArray.capacity() = SizeList.firstOrNull { it >= size } ?: -1
        public val Empty: ColorTable = ColorTable(IntArray(0), false)
    }

    init {
        check(colors.capacity() != -1) { "Color Table Too Large" }
    }

    /**
     * 写入
     * @param buffer 写入的目标
     */
    public fun write(buffer: ByteBuffer) {
        for (color in colors) {
            buffer.put(color.asRGBBytes())
        }
        buffer.put(ByteArray((colors.capacity() - colors.size) * 3))
    }

    /**
     * 是否存在(颜色不为空)
     */
    public fun exists(): Boolean = colors.isNotEmpty()

    /**
     * GIF 颜色数量位对应值
     */
    public fun size(): Int = when (colors.size) {
        in 129..256 -> 0x07
        in 65..128 -> 0x06
        in 33..64 -> 0x05
        in 17..32 -> 0x04
        in 9..16 -> 0x03
        in 5..8 -> 0x02
        in 3..4 -> 0x01
        in 0..2 -> 0x00
        else -> throw IllegalArgumentException("Color Table Size: ${colors.size}")
    }
}
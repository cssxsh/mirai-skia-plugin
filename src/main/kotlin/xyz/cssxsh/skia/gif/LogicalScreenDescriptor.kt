package xyz.cssxsh.skia.gif

import java.nio.*

/**
 * GIF Logical ScreenDescriptor 写入器
 */
public object LogicalScreenDescriptor {

    private fun block(
        buffer: ByteBuffer,
        width: Short,
        height: Short,
        flags: Byte,
        backgroundColorIndex: Byte,
        pixelAspectRatio: Byte
    ) {
        buffer.putShort(width)
        buffer.putShort(height)
        buffer.put(flags)
        buffer.put(backgroundColorIndex)
        buffer.put(pixelAspectRatio)
    }

    /**
     * 帧数据写入
     * @param buffer 写入的目标
     * @param width 宽
     * @param height 高
     * @param table 调色板
     * @param ratio 像素宽高比
     */
    public fun write(
        buffer: ByteBuffer,
        width: Int,
        height: Int,
        table: ColorTable,
        ratio: Int
    ) {
        // Color Resolution Use 7
        var flags = 0x70

        if (table.exists()) {
            flags = flags or 0x80 or table.size()
        }

        if (table.sort) {
            flags = flags or 0x08
        }

        block(
            buffer = buffer,
            width = width.asUnsignedShort(),
            height = height.asUnsignedShort(),
            flags = flags.asUnsignedByte(),
            backgroundColorIndex = table.background.asUnsignedByte(),
            pixelAspectRatio = ratio.asUnsignedByte()
        )

        table.write(buffer)
    }
}
package xyz.cssxsh.skia.gif

import org.jetbrains.skia.*
import java.nio.*

public object ImageDescriptor {
    private const val SEPARATOR = 0x002C
    private const val TERMINATOR = 0x0000

    private fun block(
        buffer: ByteBuffer,
        left: Short,
        top: Short,
        width: Short,
        height: Short,
        flags: Byte,
    ) {
        buffer.put(SEPARATOR.asUnsignedByte())
        buffer.putShort(left)
        buffer.putShort(top)
        buffer.putShort(width)
        buffer.putShort(height)
        buffer.put(flags)
    }

    private fun data(
        buffer: ByteBuffer,
        min: Int,
        data: ByteArray
    ) {
        buffer.put(min.asUnsignedByte())
        for (index in data.indices step 255) {
            val size = minOf(data.size - index, 255)
            buffer.put(size.asUnsignedByte())
            buffer.put(data, index, size)
        }
    }

    internal fun write(buffer: ByteBuffer, rect: IRect, table: ColorTable, local: Boolean, image: IntArray) {
        // Not Interlaced Images
        var flags = 0x00

        if (local) {
            flags = 0x80 or table.size()
            if (table.sort) {
                flags = flags or 0x10
            }
        }

        block(
            buffer = buffer,
            left = rect.left.asUnsignedShort(),
            top = rect.top.asUnsignedShort(),
            width = rect.width.asUnsignedShort(),
            height = rect.height.asUnsignedShort(),
            flags = flags.asUnsignedByte()
        )

        if (local) table.write(buffer)

        val (min, lzw) = LZWEncoder(table, image).encode()

        data(buffer, min, lzw)

        buffer.put(TERMINATOR.asUnsignedByte())
    }
}
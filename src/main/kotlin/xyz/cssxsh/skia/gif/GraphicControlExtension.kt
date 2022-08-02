package xyz.cssxsh.skia.gif

import org.jetbrains.skia.*
import java.nio.*

public object GraphicControlExtension {
    private const val INTRODUCER = 0x21
    private const val LABEL = 0xF9
    private const val BLOCK_SIZE = 0x04
    private const val TERMINATOR = 0x00

    private fun block(
        buffer: ByteBuffer,
        flags: Byte,
        delay: Short,
        transparencyIndex: Byte,
    ) {
        buffer.put(INTRODUCER.asUnsignedByte())
        buffer.put(LABEL.asUnsignedByte())
        buffer.put(BLOCK_SIZE.asUnsignedByte())
        buffer.put(flags)
        buffer.putShort(delay)
        buffer.put(transparencyIndex)
        buffer.put(TERMINATOR.asUnsignedByte())
    }

    public fun write(
        buffer: ByteBuffer,
        disposalMethod: AnimationDisposalMode,
        userInput: Boolean,
        transparencyIndex: Int?,
        millisecond: Int
    ) {
        // Not Interlaced Images
        var flags = 0x0000

        flags = flags or when (disposalMethod) {
            AnimationDisposalMode.UNUSED -> 0x00
            AnimationDisposalMode.KEEP -> 0x04
            AnimationDisposalMode.RESTORE_BG_COLOR -> 0x08
            AnimationDisposalMode.RESTORE_PREVIOUS -> 0x0C
        }
        if (userInput) flags = flags or 0x02
        if (transparencyIndex in 0..0xFF) flags = flags or 0x01

        block(
            buffer = buffer,
            flags = flags.asUnsignedByte(),
            delay = (millisecond / 10).asUnsignedShort(),
            transparencyIndex = (transparencyIndex ?: 0).asUnsignedByte()
        )
    }
}
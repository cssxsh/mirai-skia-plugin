package xyz.cssxsh.skia.gif

import java.nio.*

public object ApplicationExtension {
    private const val INTRODUCER = 0x21
    private const val LABEL = 0xFF
    private const val BLOCK_SIZE = 0x0B
    private const val TERMINATOR = 0x00

    private fun block(
        buffer: ByteBuffer,
        identifier: ByteArray,
        authentication: ByteArray,
        data: ByteArray,
    ) {
        buffer.put(INTRODUCER.asUnsignedByte())
        buffer.put(LABEL.asUnsignedByte())
        buffer.put(BLOCK_SIZE.asUnsignedByte())
        buffer.put(identifier) // 8 byte
        buffer.put(authentication) // 3 byte
        buffer.put(data.size.asUnsignedByte())
        buffer.put(data)
        buffer.put(TERMINATOR.asUnsignedByte())
    }

    private fun write(buffer: ByteBuffer, identifier: String, authentication: String, data: ByteArray) {
        block(
            buffer = buffer,
            identifier = identifier.toByteArray(Charsets.US_ASCII),
            authentication = authentication.toByteArray(Charsets.US_ASCII),
            data = data
        )
    }

    public fun loop(buffer: ByteBuffer, count: Int) {
        write(
            buffer = buffer,
            identifier = "NETSCAPE",
            authentication = "2.0",
            data = byteArrayOf(
                0x01,
                count.ushr(8).toByte(),
                count.ushr(0).toByte()
            )
        )
    }

    public fun buffering(buffer: ByteBuffer, capacity: Int) {
        write(
            buffer = buffer,
            identifier = "NETSCAPE",
            authentication = "2.0",
            data = byteArrayOf(
                0x01,
                capacity.ushr(24).toByte(),
                capacity.ushr(16).toByte(),
                capacity.ushr(8).toByte(),
                capacity.ushr(0).toByte()
            )
        )
    }

    public fun profile(buffer: ByteBuffer, data: ByteArray) {
        write(
            buffer = buffer,
            identifier = "ICCRGBG1",
            authentication = "012",
            data = data
        )
    }
}
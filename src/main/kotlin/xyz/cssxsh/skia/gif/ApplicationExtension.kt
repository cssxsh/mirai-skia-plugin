package xyz.cssxsh.skia.gif

import java.nio.*

/**
 * GIF Application Extension Info 写入器
 */
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

    /**
     * 循环
     * @param buffer 写入的目标
     * @param count 循环次数
     */
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

    /**
     * 启用缓存
     * @param buffer 写入的目标
     * @param capacity 缓存块容量
     */
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

    /**
     * 简介信息
     * @param buffer 写入的目标
     * @param data 简介数据
     */
    public fun profile(buffer: ByteBuffer, data: ByteArray) {
        write(
            buffer = buffer,
            identifier = "ICCRGBG1",
            authentication = "012",
            data = data
        )
    }
}
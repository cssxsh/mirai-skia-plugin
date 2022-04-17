package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*
import java.io.*

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
public class Encoder internal constructor(ptr: NativePointer) : Native(ptr), AutoCloseable {
    public constructor(path: String, width: Int, height: Int, palette: ByteArray = ByteArray(0)) :
        this(ptr = file(path, width, height, palette))

    public constructor(file: File, width: Int, height: Int, palette: ByteArray = ByteArray(0)) :
        this(ptr = file(file.path, width, height, palette))

    public var repeat: Int = 0
        set(value) {
            setRepeat(_ptr, value)
            field = value
        }

    public fun writeFrame(frame: Frame) {
        writeFrame(_ptr, frame._ptr)
    }

    public fun writeImage(image: Image, mills: Int, disposal: AnimationDisposalMode) {
        writeImage(_ptr, image._ptr, mills / 10, disposal.ordinal)
    }

    override fun close() {
        close(_ptr)
    }

    private companion object {

        @JvmStatic
        external fun file(path: String, width: Int, height: Int, palette: ByteArray): NativePointer

        @JvmStatic
        external fun setRepeat(self: NativePointer, value: Int)

        @JvmStatic
        external fun writeFrame(self: NativePointer, frame: NativePointer)

        @JvmStatic
        external fun writeImage(self: NativePointer, image: NativePointer, centi: Int, dispose: Int)

        @JvmStatic
        external fun close(self: NativePointer): NativePointer
    }
}
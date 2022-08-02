package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*
import java.io.*

@Suppress("INVISIBLE_MEMBER")
public class Frame internal constructor(ptr: NativePointer) : Native(ptr), Closeable {
    public constructor() : this(ptr = default())

    public var delay: Int
        get() = getDelay(self = _ptr) * 10
        set(value) = setDelay(self = _ptr, value = value / 10)

    public var dispose: AnimationDisposalMode
        get() = AnimationDisposalMode.values()[getDispose(self = _ptr)]
        set(value) = setDispose(self = _ptr, value = value.ordinal)

    public var rect: IRect
        get() = getRect(self = _ptr).let { (top, left, width, height) -> IRect.makeXYWH(top, left, width, height) }
        set(value) = setRect(self = _ptr, value.top, value.left, value.width, value.height)

    public val palette: Data
        get() = Data(ptr = getPalette(self = _ptr))

    override fun close() {
        close(ptr = _ptr)
    }

    public companion object {
        init {
            Library.staticLoad()
        }

        public fun fromIndexedPixels(width: Int, height: Int, pixels: Data, transparent: Int?): Frame {
            return Frame(ptr = fromIndexedPixels(width, height, pixels._ptr, transparent ?: -1))
        }

        public fun fromPalettePixels(width: Int, height: Int, pixels: Data, palette: Data, transparent: Int?): Frame {
            return Frame(ptr = fromPalettePixels(width, height, pixels._ptr, palette._ptr, transparent ?: -1))
        }

        public fun fromRGBSpeed(pixels: Data, width: Int, height: Int, speed: Int = 0): Frame {
            return Frame(ptr = fromRGBSpeed(width, height, pixels._ptr, speed))
        }

        public fun fromRGBASpeed(pixels: Data, width: Int, height: Int, speed: Int = 0): Frame {
            return Frame(ptr = fromRGBASpeed(width, height, pixels._ptr, speed))
        }

        public fun fromImage(image: Image, speed: Int = 1): Frame {
            return Frame(ptr = fromImage(image._ptr, speed))
        }

        public fun fromBitmap(bitmap: Bitmap, speed: Int = 1): Frame {
            return Frame(ptr = fromBitmap(bitmap._ptr, speed))
        }

        public fun fromPixmap(pixmap: Pixmap, speed: Int = 1): Frame {
            return Frame(ptr = fromPixmap(pixmap._ptr, speed))
        }

        @JvmStatic
        internal external fun default(): NativePointer

        @JvmStatic
        internal external fun fromIndexedPixels(
            width: Int,
            height: Int,
            pixels: NativePointer,
            transparent: Int
        ): NativePointer

        @JvmStatic
        internal external fun fromPalettePixels(
            width: Int,
            height: Int,
            pixels: NativePointer,
            palette: NativePointer,
            transparent: Int
        ): NativePointer

        @JvmStatic
        internal external fun fromRGBSpeed(width: Int, height: Int, bytes: NativePointer, speed: Int): NativePointer

        @JvmStatic
        internal external fun fromRGBASpeed(width: Int, height: Int, bytes: NativePointer, speed: Int): NativePointer

        @JvmStatic
        internal external fun fromImage(image: NativePointer, speed: Int): NativePointer

        @JvmStatic
        internal external fun fromBitmap(bitmap: NativePointer, speed: Int): NativePointer

        @JvmStatic
        internal external fun fromPixmap(pixmap: NativePointer, speed: Int): NativePointer

        @JvmStatic
        internal external fun close(ptr: NativePointer): NativePointer

        @JvmStatic
        internal external fun getDelay(self: NativePointer): Int

        @JvmStatic
        internal external fun setDelay(self: NativePointer, value: Int)

        @JvmStatic
        internal external fun getDispose(self: NativePointer): Int

        @JvmStatic
        internal external fun setDispose(self: NativePointer, value: Int)

        @JvmStatic
        internal external fun getRect(self: NativePointer): IntArray

        @JvmStatic
        internal external fun setRect(self: NativePointer, top: Int, left: Int, width: Int, height: Int)

        @JvmStatic
        internal external fun getPalette(self: NativePointer): NativePointer
    }
}

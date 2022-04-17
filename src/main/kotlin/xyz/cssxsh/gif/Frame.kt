package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
public class Frame internal constructor(ptr: NativePointer) : Native(ptr), AutoCloseable {
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

    public val palette: ByteArray
        get() = getPalette(self = _ptr)

    override fun close() {
        close(ptr = _ptr)
    }

    public companion object {

        public fun fromIndexedPixels(width: Int, height: Int, pixels: ByteArray, transparent: Int?): Frame {
            return Frame(ptr = fromIndexedPixels(width, height, pixels, transparent ?: -1))
        }

        public fun fromPalettePixels(
            width: Int,
            height: Int,
            pixels: ByteArray,
            palette: ByteArray,
            transparent: Int?
        ): Frame {
            return Frame(ptr = fromPalettePixels(width, height, pixels, palette, transparent ?: -1))
        }

        public fun fromRGBSpeed(pixels: ByteArray, width: Int, height: Int, speed: Int = 0): Frame {
            return Frame(ptr = fromRGBSpeed(width, height, pixels, speed))
        }

        public fun fromRGBASpeed(pixels: ByteArray, width: Int, height: Int, speed: Int = 0): Frame {
            return Frame(ptr = fromRGBASpeed(width, height, pixels, speed))
        }

        public fun fromImage(image: Image): Frame {
            return Frame(ptr = fromImage(image._ptr))
        }

        public fun fromBitmap(bitmap: Bitmap): Frame {
            return Frame(ptr = fromBitmap(bitmap._ptr))
        }

        public fun fromPixmap(pixmap: Pixmap): Frame {
            return Frame(ptr = fromPixmap(pixmap._ptr))
        }

        @JvmStatic
        internal external fun default(): NativePointer

        @JvmStatic
        internal external fun fromIndexedPixels(
            width: Int,
            height: Int,
            pixels: ByteArray,
            transparent: Int
        ): NativePointer

        @JvmStatic
        internal external fun fromPalettePixels(
            width: Int,
            height: Int,
            pixels: ByteArray,
            palette: ByteArray,
            transparent: Int
        ): NativePointer

        @JvmStatic
        internal external fun fromRGBSpeed(width: Int, height: Int, bytes: ByteArray, speed: Int): NativePointer

        @JvmStatic
        internal external fun fromRGBASpeed(width: Int, height: Int, bytes: ByteArray, speed: Int): NativePointer

        @JvmStatic
        internal external fun fromImage(image: NativePointer): NativePointer

        @JvmStatic
        internal external fun fromBitmap(bitmap: NativePointer): NativePointer

        @JvmStatic
        internal external fun fromPixmap(pixmap: NativePointer): NativePointer

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
        internal external fun getPalette(self: NativePointer): ByteArray
    }
}

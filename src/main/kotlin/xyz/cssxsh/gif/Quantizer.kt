package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
public interface Quantizer {

    public fun handle(bitmap: Bitmap, maxColorCount: Int = 256, sort: Boolean = false): Data

    public object OctTree : Quantizer {
        private external fun native(bitmap: NativePointer, maxColorCount: Int, sort: Boolean): NativePointer

        override fun handle(bitmap: Bitmap, maxColorCount: Int, sort: Boolean): Data {
            val p = native(bitmap._ptr, maxColorCount, sort)
            return Data(ptr = p)
        }
    }

    public object MedianCut : Quantizer {
        private external fun native(bitmap: NativePointer, maxColorCount: Int, sort: Boolean): NativePointer

        override fun handle(bitmap: Bitmap, maxColorCount: Int, sort: Boolean): Data {
            return Data(ptr = native(bitmap._ptr, maxColorCount, sort))
        }
    }

    public object KMeans : Quantizer {
        private external fun native(bitmap: NativePointer, maxColorCount: Int, sort: Boolean): NativePointer

        override fun handle(bitmap: Bitmap, maxColorCount: Int, sort: Boolean): Data {
            return Data(ptr = native(bitmap._ptr, maxColorCount, sort))
        }
    }

    public companion object {
        init {
            Library.staticLoad()
        }
    }
}
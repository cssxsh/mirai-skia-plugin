package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

/**
 * 量化器
 * @see OctTree
 * @see MedianCut
 * @see KMeans
 */
@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
public interface Quantizer {

    /**
     * 量化处理
     * @param bitmap 原图
     * @param maxColorCount 最大颜色数
     * @param sort 排序
     * @return GIF位图数据
     */
    public fun handle(bitmap: Bitmap, maxColorCount: Int = 256, sort: Boolean = false): Data

    /**
     * 八叉树量化器
     */
    public object OctTree : Quantizer {
        private external fun native(bitmap: NativePointer, maxColorCount: Int, sort: Boolean): NativePointer

        override fun handle(bitmap: Bitmap, maxColorCount: Int, sort: Boolean): Data {
            val p = native(bitmap._ptr, maxColorCount, sort)
            return Data(ptr = p)
        }
    }

    /**
     * 中值量化器
     */
    public object MedianCut : Quantizer {
        private external fun native(bitmap: NativePointer, maxColorCount: Int, sort: Boolean): NativePointer

        override fun handle(bitmap: Bitmap, maxColorCount: Int, sort: Boolean): Data {
            return Data(ptr = native(bitmap._ptr, maxColorCount, sort))
        }
    }


    /**
     * KMeans 量化器
     */
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
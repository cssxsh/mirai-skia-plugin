package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

/**
 * 抖动器
 */
@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
public interface Ditherer {

    /**
     * 抖动处理
     * @param bitmap 原图
     * @param palette 色板
     * @return 处理完成的数据
     */
    public fun handle(bitmap: Bitmap, palette: Data): Data

    /**
     * Atkinson 算法
     */
    public object Atkinson : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

    /**
     * JJN 算法
     */
    public object JJN : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

    /**
     * SierraLite 算法
     */
    public object SierraLite : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

    /**
     * Stucki 算法
     */
    public object Stucki : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

    public companion object {
        init {
            Library.staticLoad()
        }
    }
}
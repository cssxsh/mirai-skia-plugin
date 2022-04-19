package xyz.cssxsh.gif

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
public interface Ditherer {

    public fun handle(bitmap: Bitmap, palette: Data): Data


    public object Atkinson : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

    public object JJN : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

    public object SierraLite : Ditherer {
        private external fun native(bitmap: NativePointer, palette: NativePointer): NativePointer

        override fun handle(bitmap: Bitmap, palette: Data): Data {
            return Data(ptr = native(bitmap._ptr, palette._ptr))
        }
    }

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
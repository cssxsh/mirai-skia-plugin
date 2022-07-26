package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

@Suppress("INVISIBLE_MEMBER")
public object StyleUtils {

    public fun generateLowPoly(
        bitmap: Bitmap,
        variance: Double,
        cellSize: Int,
        depth: Int,
        dither: Int,
        seed: Long
    ): Surface {
        require(variance in 0.0..1.0) { "variance need in 0.0 .. 1.0" }
        require(cellSize > 0) { "cell size need in 0.0 .. 1.0" }
        require(depth > 0) { "depth need > 0" }
        require(dither > 0) { "dither need > 0" }
        return Surface(ptr = renderLowPoly(variance, cellSize, depth, dither, seed, bitmap._ptr))
    }

    @JvmStatic
    internal external fun renderLowPoly(
        variance: Double,
        cellSize: Int,
        depth: Int,
        dither: Int,
        seed: Long,
        bitmap: NativePointer
    ): NativePointer
}
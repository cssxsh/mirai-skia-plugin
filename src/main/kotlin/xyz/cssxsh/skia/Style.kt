@file:JvmName("StyleUtils")

package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.impl.*

public fun Bitmap.generateLowPoly(
    variance: Double,
    cellSize: Int,
    depth: Int,
    dither: Int,
    seed: Int
): Bitmap {
    require(variance in 0.0..1.0) { "variance need in 0.0 .. 1.0" }
    require(cellSize > 0) { "cell size need in 0.0 .. 1.0" }
    require(depth > 0) { "depth need > 0" }
    require(dither > 0) { "dither need > 0" }
    @Suppress("INVISIBLE_MEMBER")
    renderLowPoly(variance, cellSize, depth, dither, seed, _ptr)
    return this
}

internal external fun renderLowPoly(
    variance: Double,
    cellSize: Int,
    depth: Int,
    dither: Int,
    seed: Int,
    bitmap: NativePointer
): NativePointer
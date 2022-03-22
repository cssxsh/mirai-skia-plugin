package xyz.cssxsh.skia

import org.jetbrains.skia.*
import kotlin.math.pow
import kotlin.reflect.full.*

public sealed class MosaicOption {
    internal abstract fun convert(bitmap: Bitmap)

    public data class Rectangle(var side: Int = 10) : MosaicOption() {
        override fun convert(bitmap: Bitmap) {
            val offset = side
            for (x in 0 until bitmap.width step offset) {
                for (y in 0 until bitmap.height step offset) {
                    val colors = listOf(
                        bitmap.getColor(x, y),
                        bitmap.getColor(x + side - 1, y),
                        bitmap.getColor(x, y + side - 1),
                        bitmap.getColor(x + side - 1, y + side - 1)
                    )
                    val color = Color.makeARGB(
                        a = colors.sumOf { Color.getA(it) } / 4,
                        r = colors.sumOf { Color.getR(it) } / 4,
                        g = colors.sumOf { Color.getG(it) } / 4,
                        b = colors.sumOf { Color.getB(it) } / 4
                    )
                    bitmap.erase(color, IRect.makeXYWH(x, y, side, side))
                }
            }
        }
    }

    public data class Hexagon(var side: Int = 10) : MosaicOption() {
        override fun convert(bitmap: Bitmap) {
            val width = (side * 1.5).toInt()
            val height = (side * 0.5 * 3.0.pow(0.5)).toInt()
            var case = 0

            for (x in 0 until bitmap.width step width) {
                for (y in 0 until bitmap.height step height) {
                    when (case) {
                        0, 2 -> {
                            val left = bitmap.getColor(x, y)
                            val right = bitmap.getColor(
                                (x + width).coerceAtMost(bitmap.width - 1),
                                (y + height).coerceAtMost(bitmap.height - 1)
                            )
                            for (offset in 0 until width) {
                                val h = ((side - offset) * 3.0.pow(0.5)).toInt().coerceAtMost(height).coerceAtLeast(0)
                                if (h > 0) {
                                    bitmap.erase(left, IRect.makeXYWH(x + offset, y, 1, h))
                                }
                                if (h < height) {
                                    bitmap.erase(right, IRect.makeXYWH(x + offset, y + h, 1, height - h))
                                }
                            }
                        }
                        1, 3 -> {
                            val left = bitmap.getColor(x, (y + height).coerceAtMost(bitmap.height - 1))
                            val right = bitmap.getColor((x + width).coerceAtMost(bitmap.width - 1), y)
                            for (offset in 0 until width) {
                                val h = ((side - offset) * 3.0.pow(0.5)).toInt().coerceAtMost(height).coerceAtLeast(0)
                                if (h > 0) {
                                    bitmap.erase(left, IRect.makeXYWH(x + offset, y + height - h, 1, h))
                                }
                                if (h < height) {
                                    bitmap.erase(right, IRect.makeXYWH(x + offset, y, 1, height - h))
                                }
                            }
                        }
                    }

                    case = case xor 0x1
                }
                case = case xor 0x2
            }
        }
    }
}

/**
 * 绘制马赛克
 */
public fun Surface.drawMosaic(area: IRect, options: MosaicOption) {
    val bitmap = Bitmap()

    bitmap.allocPixels(imageInfo.withWidthHeight(area.width, area.height))
    readPixels(bitmap, area.left, area.top)
    // 心里有码，则万物有码
    options.convert(bitmap)
    writePixels(bitmap, area.left, area.top)
}

/**
 * 绘制马赛克
 */
public inline fun <reified O : MosaicOption> Surface.drawMosaic(area: IRect, block: O.() -> Unit = {}) {
    drawMosaic(area = area, options = O::class.createInstance().apply(block))
}
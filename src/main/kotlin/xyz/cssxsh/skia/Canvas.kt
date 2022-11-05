package xyz.cssxsh.skia

import org.jetbrains.skia.*

/**
 * Canvas DSL, draw by [block]
 * @see Canvas.save
 * @see Canvas.restoreToCount
 */
public inline fun Canvas.draw(crossinline block: Canvas.() -> Unit): Canvas {
    val index = save()
    try {
        block.invoke(this)
    } finally {
        restoreToCount(saveCount = index)
    }
    return this
}

/**
 * 调用 [Surface.canvas] 绘图
 * @see draw
 */
public inline fun Surface.canvas(crossinline block: Canvas.() -> Unit): Canvas {
    return canvas.draw(block)
}
package xyz.cssxsh.skia

import org.jetbrains.skia.*

public inline fun Canvas.draw(crossinline block: Canvas.() -> Unit): Canvas {
    val index = save()
    block.invoke(this)
    return restoreToCount(saveCount = index)
}

public inline fun Surface.canvas(crossinline block: Canvas.() -> Unit): Canvas {
    return canvas.draw(block)
}
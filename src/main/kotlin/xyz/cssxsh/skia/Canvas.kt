package xyz.cssxsh.skia

import org.jetbrains.skia.*

public fun Canvas.draw(block: Canvas.() -> Unit): Canvas {
    val index = save()
    block.invoke(this)
    return restoreToCount(saveCount = index)
}

public fun Surface.canvas(block: Canvas.() -> Unit): Canvas {
    return canvas.draw(block)
}
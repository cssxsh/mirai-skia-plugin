package xyz.cssxsh.skia

import org.jetbrains.skia.*
import xyz.cssxsh.skia.gif.*
import java.io.*

/**
 * 构造 PornPub Logo
 */
public fun pornhub(porn: String = "Porn", hub: String = "Hub"): Surface {
    val font = Font(FontUtils.matchArial(FontStyle.BOLD), 90F)
    val prefix = TextLine.make(porn, font)
    val suffix = TextLine.make(hub, font)
    val black = Paint().setARGB(0xFF, 0x00, 0x00, 0x00)
    val white = Paint().setARGB(0xFF, 0xFF, 0xFF, 0xFF)
    val yellow = Paint().setARGB(0xFF, 0xFF, 0x90, 0x00)

    val surface = Surface.makeRasterN32Premul((prefix.width + suffix.width + 50).toInt(), (suffix.height + 40).toInt())
    surface.canvas.clear(black.color)
    surface.canvas.drawTextLine(prefix, 10F, 20 - font.metrics.ascent, white)
    surface.canvas.drawRRect(RRect.makeXYWH(prefix.width + 15, 15F, suffix.width + 20, suffix.height + 10, 10F), yellow)
    surface.canvas.drawTextLine(suffix, prefix.width + 25, 20 - font.metrics.ascent, black)

    return surface
}

internal const val PET_PET_SPRITE = "xyz.cssxsh.skia.petpet"

/**
 * 构造 PetPet Face
 *
 * [PetPet Sprite Image Download](https://benisland.neocities.org/petpet/img/sprite.png)
 * @see PET_PET_SPRITE
 */
public fun petpet(face: Image, second: Double = 0.02): Data {
    val sprite = try {
        Image.makeFromEncoded(File(System.getProperty(PET_PET_SPRITE, "sprite.png")).readBytes())
    } catch (cause: Throwable) {
        throw IllegalStateException(
            "please download https://benisland.neocities.org/petpet/img/sprite.png , file path set property $PET_PET_SPRITE",
            cause
        )
    }
    val surface = Surface.makeRasterN32Premul(112 * 5, 112)
    val rects = listOf(
        // 0, 0, 0, 0
        Rect.makeXYWH(21F, 21F, 91F, 91F),
        // -4, 12, 4, -12
        Rect.makeXYWH(112F + 21F - 4F, 21F + 12F, 91F + 4F, 91F - 12F),
        // -12, 18, 12, -18
        Rect.makeXYWH(224F + 21F - 12F, 21F + 18F, 91F + 12F, 91F - 18F),
        // -8, 12, 4, -12
        Rect.makeXYWH(336F + 21F - 8F, 21F + 12F, 91F + 4F, 91F - 12F),
        // -4, 0, 0, 0
        Rect.makeXYWH(448F + 21F - 4F, 21F, 91F, 91F)
    )

    for (rect in rects) surface.canvas.drawImageRect(face, rect)

    surface.writePixels(Bitmap.makeFromImage(sprite), 0, 0)

    val images = (0 until 5).map { index ->
        val rect = IRect.makeXYWH(112 * index, 0, 112, 112)
        requireNotNull(surface.makeImageSnapshot(rect)) { "Make image snapshot fail" }
    }

    return gif(width = 112, height = 112) {
        table(bitmap = Bitmap.makeFromImage(surface.makeImageSnapshot()))
        loop(count = 0)
        options {
            duration = (second * 1000).toInt()
            disposalMethod = AnimationDisposalMode.RESTORE_BG_COLOR
            alphaType = ColorAlphaType.UNPREMUL
        }
        for (image in images) {
            frame(bitmap = Bitmap.makeFromImage(image))
        }
    }
}

/**
 * [5000choyen](https://github.com/yurafuca/5000choyen)
 */
public fun choyen(top: String, bottom: String): Surface {
    val sans = Font(FontUtils.matchFamilyStyle("Noto Sans SC", FontStyle.BOLD), 100F)
    val serif = Font(FontUtils.matchFamilyStyle("Noto Serif SC", FontStyle.BOLD), 100F)
    val red = TextLine.make(top, sans)
    val silver = TextLine.make(bottom, serif)
    val width = maxOf(red.textBlob!!.blockBounds.right + 70, silver.textBlob!!.blockBounds.right + 250).toInt()
    val surface = Surface.makeRasterN32Premul(width, 290)
    surface.canvas.skew(-0.45F, 0F)
    top(canvas = surface.canvas, text = red)
    bottom(canvas = surface.canvas, text = silver)
    return surface
}

private fun top(canvas: Canvas, text: TextLine) {
    val x = 70F
    val y = 100F
    val paint = Paint().setStroke(true)
    paint.strokeCap = PaintStrokeCap.ROUND
    paint.strokeJoin = PaintStrokeJoin.ROUND
    // 黒 22
    canvas.drawTextLine(text, x + 4, y + 4, paint.apply {
        shader = null
        color = Color.makeRGB(0, 0, 0)
        strokeWidth = 22F
    })
    // 銀 20
    canvas.drawTextLine(text, x + 4, y + 4, paint.apply {
        shader = Shader.makeLinearGradient(
            0F, 24F, 0F, 122F, intArrayOf(
                Color.makeRGB(0, 15, 36),
                Color.makeRGB(255, 255, 255),
                Color.makeRGB(55, 58, 59),
                Color.makeRGB(55, 58, 59),
                Color.makeRGB(200, 200, 200),
                Color.makeRGB(55, 58, 59),
                Color.makeRGB(25, 20, 31),
                Color.makeRGB(240, 240, 240),
                Color.makeRGB(166, 175, 194),
                Color.makeRGB(50, 50, 50)
            ), floatArrayOf(0.0F, 0.10F, 0.18F, 0.25F, 0.5F, 0.75F, 0.85F, 0.91F, 0.95F, 1F)
        )
        strokeWidth = 20F
    })
    // 黒 16
    canvas.drawTextLine(text, x, y, paint.apply {
        shader = null
        color = Color.makeRGB(0, 0, 0)
        strokeWidth = 16F
    })
    // 金 10
    canvas.drawTextLine(text, x, y, paint.apply {
        shader = Shader.makeLinearGradient(
            0F, 20F, 0F, 100F, intArrayOf(
                Color.makeRGB(253, 241, 0),
                Color.makeRGB(245, 253, 187),
                Color.makeRGB(255, 255, 255),
                Color.makeRGB(253, 219, 9),
                Color.makeRGB(127, 53, 0),
                Color.makeRGB(243, 196, 11),
            ), floatArrayOf(0.0F, 0.25F, 0.4F, 0.75F, 0.9F, 1F)
        )
        strokeWidth = 10F
    })
    // 黒 6
    canvas.drawTextLine(text, x + 2, y - 3, paint.apply {
        shader = null
        color = Color.makeRGB(0, 0, 0)
        strokeWidth = 6F
    })
    // 白 6
    canvas.drawTextLine(text, x, y - 3, paint.apply {
        shader = null
        color = Color.makeRGB(255, 255, 255)
        strokeWidth = 6F
    })
    // 赤 4
    canvas.drawTextLine(text, x, y - 3, paint.apply {
        shader = Shader.makeLinearGradient(
            0F, 20F, 0F, 100F, intArrayOf(
                Color.makeRGB(255, 100, 0),
                Color.makeRGB(123, 0, 0),
                Color.makeRGB(240, 0, 0),
                Color.makeRGB(5, 0, 0),
            ), floatArrayOf(0.0F, 0.5F, 0.51F, 1F)
        )
        strokeWidth = 4F
    })
    // 赤
    canvas.drawTextLine(text, x, y - 3, paint.setStroke(false).apply {
        shader = Shader.makeLinearGradient(
            0F, 20F, 0F, 100F, intArrayOf(
                Color.makeRGB(230, 0, 0),
                Color.makeRGB(123, 0, 0),
                Color.makeRGB(240, 0, 0),
                Color.makeRGB(5, 0, 0),
            ), floatArrayOf(0.0F, 0.5F, 0.51F, 1F)
        )
    })
}

private fun bottom(canvas: Canvas, text: TextLine) {
    val x = 250F
    val y = 230F
    val paint = Paint().setStroke(true)
    paint.strokeCap = PaintStrokeCap.ROUND
    paint.strokeJoin = PaintStrokeJoin.ROUND
    // 黒
    canvas.drawTextLine(text, x + 5, y + 2, paint.apply {
        shader = null
        color = Color.makeRGB(0, 0, 0)
        strokeWidth = 22F
    })
    // 銀
    canvas.drawTextLine(text, x + 5, y + 2, paint.apply {
        shader = Shader.makeLinearGradient(
            0F, y - 80, 0F, y + 18, intArrayOf(
                Color.makeRGB(0, 15, 36),
                Color.makeRGB(250, 250, 250),
                Color.makeRGB(150, 150, 150),
                Color.makeRGB(55, 58, 59),
                Color.makeRGB(25, 20, 31),
                Color.makeRGB(240, 240, 240),
                Color.makeRGB(166, 175, 194),
                Color.makeRGB(50, 50, 50)
            ), floatArrayOf(0.0F, 0.25F, 0.5F, 0.75F, 0.85F, 0.91F, 0.95F, 1F)
        )
        strokeWidth = 19F
    })
    // 黒
    canvas.drawTextLine(text, x, y, paint.apply {
        shader = null
        color = Color.makeRGB(16, 25, 58)
        strokeWidth = 17F
    })
    // 白
    canvas.drawTextLine(text, x, y, paint.apply {
        shader = null
        color = Color.makeRGB(221, 221, 221)
        strokeWidth = 8F
    })
    // 紺
    canvas.drawTextLine(text, x, y, paint.apply {
        shader = Shader.makeLinearGradient(
            0F, y - 80, 0F, y, intArrayOf(
                Color.makeRGB(16, 25, 58),
                Color.makeRGB(255, 255, 255),
                Color.makeRGB(16, 25, 58),
                Color.makeRGB(16, 25, 58),
                Color.makeRGB(16, 25, 58),
            ), floatArrayOf(0.0F, 0.03F, 0.08F, 0.2F, 1F)
        )
        strokeWidth = 7F
    })
    // 銀
    canvas.drawTextLine(text, x, y - 3, paint.setStroke(false).apply {
        shader = Shader.makeLinearGradient(
            0F, y - 80, 0F, y, intArrayOf(
                Color.makeRGB(245, 246, 248),
                Color.makeRGB(255, 255, 255),
                Color.makeRGB(195, 213, 220),
                Color.makeRGB(160, 190, 201),
                Color.makeRGB(160, 190, 201),
                Color.makeRGB(196, 215, 222),
                Color.makeRGB(255, 255, 255)
            ), floatArrayOf(0.0F, 0.15F, 0.35F, 0.5F, 0.51F, 0.52F, 1F)
        )
        strokeWidth = 19F
    })
}
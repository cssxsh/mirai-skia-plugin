package xyz.cssxsh.skia

import org.jetbrains.skia.*
import xyz.cssxsh.skia.gif.*
import java.io.*


/**
 * 构造 PornPub Logo
 */
public fun pornhub(porn: String = "Porn", hub: String = "Hub"): Surface {
    val font = Font(FontStyles.matchArial(FontStyle.BOLD), 90F)
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

internal const val PET_PET_SPRITE = "xyz.cssxsh.skija.petpet"

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

    surface.canvas.drawImage(sprite, 0F, 0F)

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

internal const val LICK_BASE_GIF = "xyz.cssxsh.skija.lick"

/**
 * 构造 Lick Face
 * @see LICK_BASE_GIF
 */
public fun lick(face: Image): Data {
    val lick: Codec = try {
        Codec.makeFromData(Data.makeFromFileName(System.getProperty(LICK_BASE_GIF, "lick.gif")))
    } catch (cause: Throwable) {
        throw IllegalStateException(
            "please download https://mirai.mamoe.net/assets/uploads/files/1645014451174-lick.gif , file path set property $LICK_BASE_GIF",
            cause
        )
    }
    val surface = Surface.makeRaster(lick.imageInfo)

    val offsets = listOf(
        0 to 0,
        1 to 1,
        2 to 3,
        3 to 1,
        1 to 0,
        2 to 2,
        3 to 1,
        0 to 1,
    )

    return gif(width = lick.width, height = lick.height) {
        lick.framesInfo.forEachIndexed { index, info ->
            val bitmap = Bitmap()
            bitmap.allocPixels(lick.imageInfo)
            lick.readPixels(bitmap, index)
            surface.canvas.clear(Color.makeARGB(0, 0, 0, 0))
            surface.writePixels(bitmap, 0, 0)

            val (l, t) = offsets[index % 8]
            surface.canvas.drawImageRect(face, Rect.makeXYWH(110F + l, 240F + t, 150F, 150F))

            surface.readPixels(bitmap, 0, 0)

            frame(bitmap = bitmap, info = info)
        }
    }

}

internal const val SHOUT_BACKGROUND = "xyz.cssxsh.skija.shout"

/**
 * Shout Face
 * @see SHOUT_BACKGROUND
 */
public fun shout(vararg lines: String): Surface {
    val background = try {
        Image.makeFromEncoded(File(System.getProperty(SHOUT_BACKGROUND, "shit.png")).readBytes())
    } catch (cause: Throwable) {
        throw IllegalStateException(
            "please download https://mirai.mamoe.net/assets/uploads/files/1644858542844-background.png , file path set property $SHOUT_BACKGROUND",
            cause
        )
    }
    val surface = Surface.makeRasterN32Premul(535, 500)
    val black = Paint().setARGB(0xFF, 0x00, 0x00, 0x00)

    surface.canvas.drawImage(background, 0F, 0F)

    when (lines.size) {
        1 -> {
            val size = 50F
            val font = Font(FontStyles.matchSimHei(FontStyle.BOLD), size)
            val (line) = lines
            for ((index, word) in line.withIndex()) {
                surface.canvas.drawString(word.toString(), 430F, 80F + index * size, font, black)
            }
        }
        2 -> {
            val size = 50F
            val font = Font(FontStyles.matchSimHei(FontStyle.BOLD), size)
            val (first, second) = lines
            for ((index, word) in first.withIndex()) {
                surface.canvas.drawString(word.toString(), 380F, 80F + index * size, font, black)
            }
            for ((index, word) in second.withIndex()) {
                surface.canvas.drawString(word.toString(), 450F, 80F + index * size, font, black)
            }
        }
    }

    return surface
}

/**
 * [5000choyen](https://github.com/yurafuca/5000choyen)
 */
public fun choyen(top: String, bottom: String): Surface {
    val sans = Font(FontStyles.matchFamilyStyle("Noto Sans SC", FontStyle.BOLD), 100F)
    val serif = Font(FontStyles.matchFamilyStyle("Noto Serif SC", FontStyle.BOLD), 100F)
    val red = TextLine.make(top, sans)
    val silver = TextLine.make(bottom, serif)
    val width = maxOf(red.textBlob!!.blockBounds.right + 70, silver.textBlob!!.blockBounds.right + 250).toInt()
    val surface = Surface.makeRasterN32Premul(width, 290)
    // setTransform(m11, m12, m21, m22, dx, dy)
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
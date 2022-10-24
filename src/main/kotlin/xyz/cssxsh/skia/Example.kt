package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.paragraph.*
import xyz.cssxsh.gif.*
import xyz.cssxsh.skia.gif.*
import java.io.*
import java.nio.file.*

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
    surface.canvas {
        clear(black.color)
        drawTextLine(prefix, 10F, 20 - font.metrics.ascent, white)
        drawRRect(RRect.makeXYWH(prefix.width + 15, 15F, suffix.width + 20, suffix.height + 10, 10F), yellow)
        drawTextLine(suffix, prefix.width + 25, 20 - font.metrics.ascent, black)
    }

    return surface
}

public const val PET_PET_SPRITE: String = "xyz.cssxsh.skia.petpet"

/**
 * 构造 PetPet Face
 *
 * [PetPet Sprite Image Download](https://benisland.neocities.org/petpet/img/sprite.png)
 * @see PET_PET_SPRITE
 */
public fun petpet(face: Image, second: Double = 0.02): Data {
    val sprite = try {
        Image.makeFromEncoded(File(System.getProperty(PET_PET_SPRITE, "sprite.png")).readBytes())
    } catch (cause: Exception) {
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
    val mode = FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST)
    val source = Rect.makeWH(face.width.toFloat(), face.height.toFloat())
    val paint = Paint()
    paint.color = Color.WHITE

    surface.canvas {
        for (rect in rects) {
            paint.blendMode = BlendMode.SRC
            drawOval(
                r = rect,
                paint = paint
            )

            paint.blendMode = BlendMode.SRC_IN
            drawImageRect(
                image = face,
                src = source,
                dst = rect,
                samplingMode = mode,
                paint = paint,
                strict = true
            )
        }

        drawImage(sprite, 0F, 0F)
    }

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
    // top
    surface.canvas {
        val x = 70F
        val y = 100F
        val paint = Paint().setStroke(true)

        paint.strokeCap = PaintStrokeCap.ROUND
        paint.strokeJoin = PaintStrokeJoin.ROUND
        // 黒 22
        drawTextLine(red, x + 4, y + 4, paint.apply {
            shader = null
            color = Color.makeRGB(0, 0, 0)
            strokeWidth = 22F
        })
        // 銀 20
        drawTextLine(red, x + 4, y + 4, paint.apply {
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
        drawTextLine(red, x, y, paint.apply {
            shader = null
            color = Color.makeRGB(0, 0, 0)
            strokeWidth = 16F
        })
        // 金 10
        drawTextLine(red, x, y, paint.apply {
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
        drawTextLine(red, x + 2, y - 3, paint.apply {
            shader = null
            color = Color.makeRGB(0, 0, 0)
            strokeWidth = 6F
        })
        // 白 6
        drawTextLine(red, x, y - 3, paint.apply {
            shader = null
            color = Color.makeRGB(255, 255, 255)
            strokeWidth = 6F
        })
        // 赤 4
        drawTextLine(red, x, y - 3, paint.apply {
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
        drawTextLine(red, x, y - 3, paint.setStroke(false).apply {
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
    // bottom
    surface.canvas {
        val x = 250F
        val y = 230F
        val paint = Paint().setStroke(true)

        paint.strokeCap = PaintStrokeCap.ROUND
        paint.strokeJoin = PaintStrokeJoin.ROUND
        // 黒
        drawTextLine(silver, x + 5, y + 2, paint.apply {
            shader = null
            color = Color.makeRGB(0, 0, 0)
            strokeWidth = 22F
        })
        // 銀
        drawTextLine(silver, x + 5, y + 2, paint.apply {
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
        drawTextLine(silver, x, y, paint.apply {
            shader = null
            color = Color.makeRGB(16, 25, 58)
            strokeWidth = 17F
        })
        // 白
        drawTextLine(silver, x, y, paint.apply {
            shader = null
            color = Color.makeRGB(221, 221, 221)
            strokeWidth = 8F
        })
        // 紺
        drawTextLine(silver, x, y, paint.apply {
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
        drawTextLine(silver, x, y - 3, paint.setStroke(false).apply {
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
    return surface
}

public const val DEAR_ORIGIN: String = "xyz.cssxsh.skia.dear"

/**
 * 构造 亲亲 表情
 * @see DEAR_ORIGIN
 * @return 临时文件
 */
public fun dear(face: Image): File {
    val codec = try {
        Codec.makeFromData(Data.makeFromBytes(File(System.getProperty(DEAR_ORIGIN, "dear.gif")).readBytes()))
    } catch (cause: Exception) {
        throw IllegalStateException(
            "please download https://tva3.sinaimg.cn/large/003MWcpMly8gv4s019bzsg606o06o40902.gif , file path set property $DEAR_ORIGIN",
            cause
        )
    }
    val temp = Files.createTempFile("dear", "gif").toFile()
    val surface = Surface.makeRaster(codec.imageInfo)
    val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
    val mode = FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST)
    val src = Rect.makeWH(face.width.toFloat(), face.height.toFloat())
    val rects = listOf(
        Rect.makeXYWH(48F, 118F, 60F, 60F),
        Rect.makeXYWH(70F, 110F, 55F, 60F),
        Rect.makeXYWH(78F, 108F, 55F, 65F),
        Rect.makeXYWH(54F, 125F, 60F, 60F),
        Rect.makeXYWH(65F, 121F, 60F, 70F),
        Rect.makeXYWH(69F, 121F, 56F, 66F),
        Rect.makeXYWH(24F, 148F, 56F, 56F),
        Rect.makeXYWH(30F, 130F, 70F, 60F),
        Rect.makeXYWH(75F, 110F, 50F, 70F),
        Rect.makeXYWH(56F, 120F, 50F, 60F),
        Rect.makeXYWH(75F, 118F, 60F, 65F),
        Rect.makeXYWH(46F, 136F, 55F, 70F),
        Rect.makeXYWH(23F, 151F, 68F, 58F),
    )
    val paint = Paint()
    paint.color = Color.WHITE

    Encoder(temp, surface.width, surface.height).use { encoder ->
        encoder.repeat = -1
        for (index in 0 until codec.frameCount) {
            codec.readPixels(bitmap, index)
            surface.writePixels(bitmap, 0, 0)
            surface.canvas {
                paint.blendMode = BlendMode.SRC_OUT
                drawOval(
                    r = rects[index],
                    paint = paint
                )

                paint.blendMode = BlendMode.DST_OVER
                drawImageRect(
                    image = face,
                    src = src,
                    dst = rects[index],
                    samplingMode = mode,
                    paint = paint,
                    strict = false
                )
            }

            val info = codec.getFrameInfo(index)
            val image = surface.makeImageSnapshot()
            encoder.writeImage(image, info.duration, info.disposalMethod)
        }
    }

    return temp
}

public const val ZZKIA_ORIGIN: String = "xyz.cssxsh.skia.zzkia"

/**
 * [zzkia](https://github.com/dcalsky/zzkia)
 * @see ZZKIA_ORIGIN
 */
public fun zzkia(text: String): Surface {
    val origin = try {
        Image.makeFromEncoded(File(System.getProperty(ZZKIA_ORIGIN, "zzkia.jpg")).readBytes())
    } catch (cause: Exception) {
        throw IllegalStateException(
            "please download https://cdn.jsdelivr.net/gh/dcalsky/bbq/zzkia/images/4.jpg , file path set property $ZZKIA_ORIGIN",
            cause
        )
    }
    val surface = Surface.makeRaster(origin.imageInfo)
    surface.writePixels(Bitmap.makeFromImage(origin), 0, 0)

    surface.canvas.rotate(9.8F)

    val fonts = FontCollection()
        .setDynamicFontManager(FontUtils.provider)
        .setDefaultFontManager(FontMgr.default)
    val families = arrayOf("FZXS14")

    // context
    val context = ParagraphStyle().apply {
        textStyle = TextStyle()
            .setFontSize(70F)
            .setColor(Color.BLACK)
            .setFontFamilies(families)

        maxLinesCount = 7
    }
    ParagraphBuilder(context, fonts)
        .addText(text)
        .build()
        .layout(700F)
        .paint(surface.canvas, 330F, 270F)

    // count
    val count = ParagraphStyle().apply {
        alignment = Alignment.RIGHT
        textStyle = TextStyle()
            .setFontSize(70F)
            .setColor(Color.makeARGB(255, 129, 212, 250))
            .setFontFamilies(families)
    }
    ParagraphBuilder(count, fonts)
        .addText("${text.length}/900")
        .build()
        .layout(680F)
        .paint(surface.canvas, 360F, 170F)

    return surface
}

/**
 * [幻影坦克](https://samarium150.github.io/mirage-tank-images/)
 */
public fun tank(top: Image, bottom: Image): Surface {
    val surface = Surface.makeRasterN32Premul(top.width, top.height)
    val canvas = surface.canvas
    val paint = Paint()
    paint.reset()

//    val gray = ColorFilter.makeMatrix(
//        ColorMatrix(
//            0.299F, 0.587F, 0.114F, 0F, 0F,  // red
//            0.299F, 0.587F, 0.114F, 0F, 0F,  // green
//            0.299F, 0.587F, 0.114F, 0F, 0F,  // blue
//            0F, 0F, 0F, 0F, 1F
//        )
//    )

    canvas.clear(Color.TRANSPARENT)
    paint.reset()
    paint.colorFilter = ColorFilter.makeMatrix(
        ColorMatrix(
            0.5F, 0F, 0F, 0F, 0.5F,  // red
            0F, 0.5F, 0F, 0F, 0.5F,  // green
            0F, 0F, 0.5F, 0F, 0.5F,  // blue
            0F, 0F, 0F, 0F, 1F
        )
    )
    canvas.drawImage(top, 0F, 0F, paint)
    val a = surface.makeImageSnapshot()

    canvas.clear(Color.TRANSPARENT)
    paint.reset()
    paint.colorFilter = ColorFilter.makeMatrix(
        ColorMatrix(
            0.5F, 0F, 0F, 0F, 0F,  // red
            0F, 0.5F, 0F, 0F, 0F,  // green
            0F, 0F, 0.5F, 0F, 0F,  // blue
            0F, 0F, 0F, 0F, 1F
        )
    )
    val rect = if (top.width * bottom.height > bottom.width * top.height) {
        val width = bottom.width.toFloat().times(top.height).div(bottom.height)
        Rect.makeXYWH(
            top.width.minus(width).div(2),
            0F,
            width,
            top.height.toFloat()
        )
    } else {
        val height = bottom.height.toFloat().times(top.width).div(bottom.width)
        Rect.makeXYWH(
            0F,
            top.height.minus(height).div(2),
            top.width.toFloat(),
            height
        )
    }
    canvas.drawImageRect(bottom, rect, paint)
    val b = surface.makeImageSnapshot()

    canvas.clear(Color.TRANSPARENT)
    paint.reset()
    paint.colorFilter = ColorFilter.makeMatrix(
        ColorMatrix(
            -1F, 0F, 0F, 0F, 1F,  // red
            0F, -1F, 0F, 0F, 1F,  // green
            0F, 0F, -1F, 0F, 1F,  // blue
            0F, 0F, 0F, 0F, 1F
        )
    )
    canvas.drawImage(a, 0F, 0F, paint)
    paint.reset()
    paint.blendMode = BlendMode.PLUS
    canvas.drawImage(b, 0F, 0F, paint)
    val o = surface.makeImageSnapshot()

    canvas.clear(Color.TRANSPARENT)
    paint.reset()
    val dsc = Bitmap.makeFromImage(o)
    val src = Bitmap.makeFromImage(b)
    repeat(o.width) { x ->
        repeat(o.height) { y ->
            val s = Color4f(dsc.getColor(x, y))
            val t = Color4f(src.getColor(x, y))

            val g1 = (s.r + s.g + s.b) / 3
            val g2 = (t.r + t.g + t.b) / 3
            val g3 = g2 / g1

            val r = Color4f(g3, g3, g3, g1)
            paint.color4f = r

            canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
        }
    }
//    paint.reset()
//    canvas.drawImage(o, 0F, 0F, paint)
//    paint.blendMode = BlendMode.DIFFERENCE
//    canvas.drawImage(b, 0F, 0F, paint)

    return surface
}
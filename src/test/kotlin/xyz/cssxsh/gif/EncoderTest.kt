package xyz.cssxsh.gif

import kotlinx.coroutines.*
import org.jetbrains.skia.*
import org.junit.jupiter.api.Test
import xyz.cssxsh.mirai.skia.*
import xyz.cssxsh.skia.*
import java.io.File

internal class EncoderTest {
    init {
        runBlocking {
            System.setProperty("xyz.cssxsh.mirai.gif.release", "https://github.com/cssxsh/gif-jni/releases/download")
            loadJNILibrary(folder = File("./run/lib"))
        }
    }

    @Test
    fun build() {
        val face = Image.makeFromEncoded(File("./example/face.png").readBytes())
        val sprite = Image.makeFromEncoded(File("./example/sprite.png").readBytes())
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

        val gif = File("./run/jni.gif")
        gif.parentFile.mkdirs()

        Encoder(gif, 112, 112).use { encoder ->
            encoder.repeat = -1

            repeat(5) { index ->
                val rect = IRect.makeXYWH(112 * index, 0, 112, 112)
                val image = requireNotNull(surface.makeImageSnapshot(rect)) { "Make image snapshot fail" }

                encoder.writeImage(image, 20, AnimationDisposalMode.RESTORE_BG_COLOR)
            }

        }
    }

    @Test
    fun dear() {
        val face = Image.makeFromEncoded(File("./example/face.png").readBytes())
        System.setProperty(DEAR_ORIGIN, "./example/dear.gif")
        val dst = File("./run/dear.gif")
        dst.delete()
        dear(face).renameTo(dst)
    }
}
package xyz.cssxsh.skia

import kotlinx.coroutines.*
import org.jetbrains.skia.*
import org.junit.jupiter.api.*
import xyz.cssxsh.mirai.skia.*
import java.io.File

internal class StyleUtilsTest {

    init {
        runBlocking {
            System.setProperty("xyz.cssxsh.mirai.gif.release", "https://github.com/cssxsh/gif-jni")
            loadJNILibrary(folder = File("./run/lib"))
        }
    }

    @Test
    fun `low_poly`() {
        val file = File("./example/style.test.jpg")
        val image = Image.makeFromEncoded(file.readBytes())
        val bitmap = Bitmap.makeFromImage(image)
        val surface = StyleUtils.generateLowPoly(bitmap, 0.30, 150, 20, 10, 0)
        println(surface.imageInfo)

        File("./run/lowpoly.png").writeBytes(surface.makeImageSnapshot().encodeToData()!!.bytes)
    }
}
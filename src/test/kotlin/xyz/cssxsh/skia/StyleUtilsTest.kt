package xyz.cssxsh.skia

import kotlinx.coroutines.*
import org.jetbrains.skia.*
import org.junit.jupiter.api.*
import xyz.cssxsh.mirai.skia.*
import java.io.File
import kotlin.random.Random

internal class StyleUtilsTest {

    init {
        runBlocking {
            System.setProperty("xyz.cssxsh.mirai.gif.release", "https://github.com/cssxsh/gif-jni")
            loadJNILibrary(folder = File("./run/lib"))
        }
    }

    @Test
    fun generateLowPoly() {
        val file = File("./example/style.test.jpg")
        val image = Image.makeFromEncoded(file.readBytes())
        val bitmap = Bitmap.makeFromImage(image)
        val r = bitmap.generateLowPoly(0.30, 150, 20, 10, Random.nextInt())
        println(r.imageInfo)
        File("./run/render.png").writeBytes(Image.makeFromBitmap(r).encodeToData()!!.bytes)
    }
}
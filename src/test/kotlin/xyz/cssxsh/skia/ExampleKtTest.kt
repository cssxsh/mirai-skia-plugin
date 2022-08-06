package xyz.cssxsh.skia

import kotlinx.coroutines.*
import org.jetbrains.skia.*
import org.jetbrains.skia.svg.*
import org.jetbrains.skiko.*
import org.junit.jupiter.api.*
import xyz.cssxsh.mirai.skia.*
import java.io.*

internal class ExampleKtTest {
    init {
        val fonts = File("./run/fonts")
        fonts.mkdirs()
        runBlocking {
            System.setProperty("xyz.cssxsh.mirai.gif.release", "https://github.com/cssxsh/gif-jni")
            loadJNILibrary(folder = File("./run/lib"))
            val links = FreeFontLinks.map { it.replace("raw.fastgit.org","raw.githubusercontent.com") }.toTypedArray()
            downloadTypeface(folder = fonts, links = links)
        }
    }

    @Test
    fun font() {
        println(FontUtils.families())

        when (hostOs) {
            OS.Windows -> {
                Assertions.assertNotNull(FontUtils.matchSimSun(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchNSimSun(FontStyle.NORMAL))
//                Assertions.assertNotNull(FontUtils.matchSimHei(FontStyle.NORMAL))
//                Assertions.assertNotNull(FontUtils.matchFangSong(FontStyle.NORMAL))
//                Assertions.assertNotNull(FontUtils.matchKaiTi(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchArial(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchCalibri(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchConsolas(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchTimesNewRoman(FontStyle.NORMAL))
            }
            OS.Linux -> {
                Assertions.assertNotNull(FontUtils.matchLiberationSans(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchLiberationSerif(FontStyle.NORMAL))
            }
            OS.MacOS -> {
                Assertions.assertNotNull(FontUtils.matchArial(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchTimesNewRoman(FontStyle.NORMAL))
                Assertions.assertNotNull(FontUtils.matchHelvetica(FontStyle.NORMAL))
            }
            else -> Unit
        }
    }

    @Test
    fun svg() {
        val surface = Surface.makeRasterN32Premul(350, 350)
        val size = Point(350F, 350F)
        val background = SVGDOM.makeFromFile(xml = File("./example/osu-logo-triangles.svg"))
        background.setContainerSize(size)
        background.render(surface.canvas)
        val text = SVGDOM.makeFromFile(xml = File("./example/osu-logo-white.svg"))
        text.setContainerSize(size)
        text.render(surface.canvas)

        val image = surface.makeImageSnapshot()
        val data = image.encodeToData() ?: throw IllegalStateException("encode null.")

        val file = File("./run/Osu.png")
        file.writeBytes(data.bytes)
    }

    @Test
    fun stats() {
        val stats = SVGDOM.makeFromFile(xml = File("./example/cssxsh.svg"))
        val surface = Surface.makeRasterN32Premul(stats.root!!.width.value.toInt(), stats.root!!.height.value.toInt())

        stats.setContainerSize(stats.root!!.width.value, stats.root!!.height.value)
        stats.render(surface.canvas)

        val image = surface.makeImageSnapshot()
        val data = image.encodeToData() ?: throw IllegalStateException("encode null.")

        val file = File("./run/cssxsh.png")
        file.writeBytes(data.bytes)
    }

    @Test
    fun pornhub() {
        val surface = pornhub("Git", "Hub")

        val image = surface.makeImageSnapshot()
        val data = image.encodeToData() ?: throw IllegalStateException("encode null.")

        val file = File("./run/pornhub.png")
        file.writeBytes(data.bytes)
    }

    @Test
    fun petpet() {
        System.setProperty(PET_PET_SPRITE, "./example/sprite.png")
        val data = petpet(face = Image.makeFromEncoded(File("./example/face.png").readBytes()))

        val file = File("./run/petpet.gif")
        file.writeBytes(data.bytes)
    }

    @Test
    fun choyen() {
        val surface = choyen(top = "好了", bottom = "够意思")

        val image = surface.makeImageSnapshot()
        val data = image.encodeToData() ?: throw IllegalStateException("encode null.")

        val file = File("./run/choyen.png")
        file.writeBytes(data.bytes)
    }

    @Test
    fun zzkia() {
        System.setProperty(ZZKIA_ORIGIN, "./example/zzkia.jpg")
        val surface = zzkia("有内鬼，停止交易")

        val image = surface.makeImageSnapshot()
        val data = image.encodeToData() ?: throw IllegalStateException("encode null.")

        val file = File("./run/zzkia.png")
        file.writeBytes(data.bytes)
    }
}
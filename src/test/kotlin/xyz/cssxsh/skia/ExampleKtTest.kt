package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.svg.*
import org.jetbrains.skiko.*
import org.junit.jupiter.api.*
import java.io.*
import java.nio.*

internal class ExampleKtTest {
    init {
        val run = File("./run")
        run.mkdirs()
        for (file in run.resolve("fonts").listFiles().orEmpty()) {
            when (file.extension) {
                "ttf", "otf", "eot", "fon", "font", "woff", "woff2" -> {
                    try {
                        FontUtils.loadTypeface(file.path)
                    } catch (_: Throwable) {

                    }
                }
                "ttc" -> {
                    try {
                        val count = file.inputStream().use { input ->
                            input.readNBytes(8)
                            ByteBuffer.wrap(input.readNBytes(4)).int
                        }
                        for (index in 0 until count) {
                            FontUtils.loadTypeface(file.path, index)
                        }
                    } catch (_: Throwable) {

                    }
                }
            }
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
        val svg = SVGDOM.makeFromString(xml = File("./example/Osu.svg").readText())
        val surface = Surface.makeRasterN32Premul(512, 512)
        svg.setContainerSize(512F, 512F)
        svg.render(surface.canvas)

        val image = surface.makeImageSnapshot()
        val data = image.encodeToData() ?: throw IllegalStateException("encode null.")

        val file = File("./run/Osu.png")
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
}
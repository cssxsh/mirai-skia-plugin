package xyz.cssxsh.mirai.skia

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.jar.*
import java.util.zip.*

internal val logger by lazy {
    try {
        MiraiSkiaPlugin.logger
    } catch (_: Throwable) {
        MiraiLogger.Factory.create(Library::class)
    }
}

private val http = HttpClient(OkHttp) {
    CurlUserAgent()
    ContentEncoding()
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
}

internal val sevenZ: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skia.sevenZ", "7zz")
}

internal suspend fun download(urlString: String, folder: File): File = supervisorScope {
    with(http.get(urlString)) {
        val relative = headers[HttpHeaders.ContentDisposition]
            ?.let { ContentDisposition.parse(it).parameter(ContentDisposition.Parameters.FileName) }
            ?: request.url.encodedPath.substringAfterLast('/').decodeURLPart()

        val file = folder.resolve(relative)

        if (file.isFile && contentLength() == file.length()) {
            logger.info { "文件 ${file.name} 已存在，跳过下载" }
            call.cancel("文件 ${file.name} 已存在，跳过下载")
        } else {
            file.delete()
            logger.info { "文件 ${file.name} 开始下载" }
            file.outputStream().use { output ->
                val channel: ByteReadChannel = bodyAsChannel()

                while (!channel.isClosedForRead) channel.copyTo(output)
            }
        }

        file
    }
}

/**
 * 加载字体
 * @param folder 字体文件文件夹
 */
@JvmSynthetic
public suspend fun loadTypeface(folder: File, vararg links: String) {
    val downloaded: MutableList<File> = ArrayList()
    val download = folder.resolve("download")

    download.mkdirs()

    for (link in links) {
        try {
            downloaded.add(download(urlString = link, folder = download))
        } catch (cause: Throwable) {
            logger.warning({ "字体下载失败, $link" }, cause)
        }
    }

    for (pack in downloaded) {
        when (pack.extension) {
            "7z" -> runInterruptible(Dispatchers.IO) {
                ProcessBuilder(sevenZ, "x", pack.absolutePath, "-y")
                    .directory(folder)
                    .start()
                    // 防止卡顿
                    .apply { inputStream.transferTo(OutputStream.nullOutputStream()) }
                    .waitFor()
            }
            "zip" -> runInterruptible(Dispatchers.IO) {
                ZipFile(pack).use { zip ->
                    for (entry in zip.entries()) {
                        with(folder.resolve(entry.name)) {
                            if (exists().not()) {
                                outputStream().use { output ->
                                    zip.getInputStream(entry).use { input ->
                                        input.transferTo(output)
                                    }
                                }
                            }
                            setLastModified(entry.lastModifiedTime.toMillis())
                        }
                    }
                }
            }
            else -> {}
        }
    }

    loadTypeface(folder = folder)
}

/**
 * 从指定目录加载字体
 * @param folder 字体文件文件夹
 * @see FontUtils.loadTypeface
 */
public fun loadTypeface(folder: File) {
    for (file in folder.listFiles() ?: return) {
        try {
            when (file.extension) {
                "ttf", "otf", "eot", "fon", "font", "woff", "woff2" -> FontUtils.loadTypeface(path = file.path)
                "ttc" -> {
                    val count = file.inputStream().use { input ->
                        input.skip(8)
                        input.readNBytes(4).toInt()
                    }
                    for (index in 0 until count) {
                        FontUtils.loadTypeface(path = file.path, index = index)
                    }
                }
                else -> loadTypeface(folder = file)
            }
        } catch (cause: Throwable) {
            logger.warning({ "加载字体文件失败 ${file.path}" }, cause)
        }
    }
}

private val SKIKO_MAVEN: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skia.maven", "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

private val SKIKO_PACKAGE: String by lazy {
    val name = when {
        "android" in hostId -> "skiko-android-runtime-${hostArch.id}"
        else -> "skiko-awt-runtime-${hostId}"
    }
    System.getProperty("xyz.cssxsh.mirai.skia.package", name)
}

private val SKIKO_VERSION: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skia.version", Version.skiko)
}

private val GIF_RELEASE: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.gif.release", "https://download.fastgit.org/cssxsh/gif-jni")
}

private val GIF_VERSION: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.gif.version", xyz.cssxsh.gif.Version.gif)
}

private const val ICU = "icudtl.dat"

public fun checkPlatform() {
    when (hostOs) {
        OS.Linux -> {
            // Termux
            if ("termux" in System.getProperty("user.dir")) {
                logger.info { "change platform: $hostId to android-arm64" }
                try {
                    val kt = Class.forName("org.jetbrains.skiko.OsArch_jvmKt")
                    val delegate = kt.getDeclaredField("hostId\$delegate").apply { isAccessible = true }
                    val lazy = delegate.get(null)
                    val value = lazy::class.java.getDeclaredField("_value").apply { isAccessible = true }
                    value.set(lazy, "android-arm64")
                } catch (_: Throwable) {
                    logger.warning { "修改 hostId 失败" }
                }
            }
            // OpenGL
            try {
                System.loadLibrary("GL")
            } catch (cause: UnsatisfiedLinkError) {
                if ("java.library.path" in cause.message.orEmpty()) {
                    logger.warning { "无法链接 OpenGL, 你可能需要安装 OpenGL, 请尝试安装包 mesa freeglut" }
                }
            }
        }
        OS.Windows -> {
            // OpenGL
            try {
                System.loadLibrary("OPENGL32")
            } catch (cause: UnsatisfiedLinkError) {
                if ("java.library.path" in cause.message.orEmpty()) {
                    logger.warning({ "无法链接 OpenGL, 你可能需要安装 OpenGL" }, cause)
                }
            }
        }
        else -> {}
    }
}

public suspend fun loadJNILibrary(folder: File) {
    val skiko = System.mapLibraryName("skiko-$hostId")
    val gif = System.mapLibraryName("gif-$hostId")

    folder.mkdirs()

    with(folder.resolve(skiko)) {
        val version = folder.resolve("skia.version.txt")
        val maven = "$SKIKO_MAVEN/org/jetbrains/skiko/$SKIKO_PACKAGE/$SKIKO_VERSION/$SKIKO_PACKAGE-$SKIKO_VERSION.jar"
        logger.debug { maven }
        if (version.exists().not() || version.readText() != SKIKO_VERSION) delete()

        if (exists().not()) {
            val file = download(urlString = maven, folder = folder)
            val jar = JarFile(file)

            outputStream().use { output ->
                jar.getInputStream(jar.getJarEntry(skiko)).transferTo(output)
            }

            if (hostOs == OS.Windows) {
                folder.resolve(ICU).outputStream().use { output ->
                    jar.getInputStream(jar.getJarEntry(ICU)).transferTo(output)
                }
            }

            jar.close()
            file.deleteOnExit()
        }
        version.writeText(SKIKO_VERSION)
    }

    with(folder.resolve(gif)) {
        val version = folder.resolve("gif.version.txt")
        val release = "$GIF_RELEASE/releases/download/v$GIF_VERSION/$gif"
        logger.debug { release }
        if (version.exists().not() || version.readText() != GIF_VERSION) delete()

        if (exists().not()) {
            download(urlString = release, folder = folder)
        }
        version.writeText(GIF_VERSION)
    }
    System.setProperty(xyz.cssxsh.gif.Library.GIF_LIBRARY_PATH_PROPERTY, folder.path)
    xyz.cssxsh.gif.Library.load()
    synchronized (System.getProperties()) {
        @Suppress("INVISIBLE_MEMBER")
        System.setProperty(Library.SKIKO_LIBRARY_PATH_PROPERTY, folder.path)
        Library.load()
    }
}
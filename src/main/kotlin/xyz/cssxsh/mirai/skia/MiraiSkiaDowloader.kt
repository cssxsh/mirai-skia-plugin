package xyz.cssxsh.mirai.skia

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.console.util.SemVersion
import net.mamoe.mirai.utils.*
import org.jetbrains.skiko.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.jar.*
import java.util.zip.*

internal val logger by lazy {
    val open = System.getProperty("xyz.cssxsh.mirai.skia.logger", "true").toBoolean()
    if (open) MiraiSkiaPlugin.logger else SilentLogger
}

private val http = HttpClient(OkHttp) {
    CurlUserAgent()
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
}

internal val sevenZ: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skia.sevenZ", "7zz")
}

internal suspend fun download(urlString: String, folder: File): File = supervisorScope {
    http.get<HttpStatement>(urlString).execute { response ->
        val relative = response.headers[HttpHeaders.ContentDisposition]
            ?.let { ContentDisposition.parse(it).parameter(ContentDisposition.Parameters.FileName) }
            ?: response.request.url.encodedPath.substringAfterLast('/').decodeURLPart()

        val file = folder.resolve(relative)

        if (file.exists()) {
            logger.info { "文件 ${file.name} 已存在，跳过下载" }
            response.call.cancel("文件 ${file.name} 已存在，跳过下载")
        } else {
            logger.info { "文件 ${file.name} 开始下载" }
            file.outputStream().use { output ->
                val channel: ByteReadChannel = response.receive()

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
public suspend fun loadTypeface(folder: File, vararg links: String): Unit = withContext(Dispatchers.IO) {
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
            "7z" -> {
                ProcessBuilder(sevenZ, "x", pack.absolutePath, "-y")
                    .directory(folder)
                    .start()
                    // 防止卡顿
                    .apply { inputStream.transferTo(OutputStream.nullOutputStream()) }
                    .waitFor()
            }
            "zip" -> {
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
            else -> Unit
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

private const val ICU = "icudtl.dat"

public suspend fun loadJNILibrary(folder: File): Unit = withContext(Dispatchers.IO) {
    @Suppress("INVISIBLE_MEMBER")
    System.setProperty(Library.SKIKO_LIBRARY_PATH_PROPERTY, folder.path)
    System.setProperty(xyz.cssxsh.gif.Library.GIF_LIBRARY_PATH_PROPERTY, folder.path)
    val skiko = System.mapLibraryName("skiko-$hostId")
    val gif = System.mapLibraryName("gif-$hostId")

    folder.mkdirs()

    val history = folder.resolve("version.txt")
    if (history.exists().not() || SemVersion.invoke(history.readText()) != SemVersion.invoke(SKIKO_VERSION)) {
        history.writeText(SKIKO_VERSION)
        folder.resolve(skiko).delete()
        folder.resolve(gif).delete()
    }

    val maven = "$SKIKO_MAVEN/org/jetbrains/skiko/$SKIKO_PACKAGE/$SKIKO_VERSION/$SKIKO_PACKAGE-$SKIKO_VERSION.jar"

    folder.resolve(skiko).apply {
        if (exists().not()) {
            val file = download(urlString = maven, folder = folder)
            val jar = JarFile(file)

            outputStream().use { output ->
                jar.getInputStream(jar.getJarEntry(skiko)).transferTo(output)
            }

            if (hostOs == OS.Windows) {
                folder.resolve(ICU).apply {
                    outputStream().use { output ->
                        jar.getInputStream(jar.getJarEntry(ICU)).transferTo(output)
                    }
                }
            }

            jar.close()
            file.deleteOnExit()
        }
    }
    Library.load()

    folder.resolve(gif).apply {
        if (exists().not()) {
            parentFile.mkdirs()

            val latest = http.get<String>("https://api.github.com/repos/cssxsh/gif-jni/releases/latest")
            val release = Json.decodeFromString(JsonObject.serializer(), latest)
            val asset = release.getValue("assets").jsonArray
                .find { gif in it.jsonObject.getValue("name").jsonPrimitive.content }
                ?: throw NoSuchElementException("gif lib $gif")
            val lib = asset.jsonObject.getValue("browser_download_url").jsonPrimitive.content

            download(urlString = lib, folder = folder)
        }
    }
    xyz.cssxsh.gif.Library.load()
}
package xyz.cssxsh.mirai.skia

import io.ktor.client.*
import io.ktor.client.content.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.*
import net.mamoe.mirai.utils.*
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.dnsoverhttps.DnsOverHttps
import org.apache.commons.compress.archivers.sevenz.*
import org.apache.commons.compress.archivers.tar.*
import org.apache.commons.compress.compressors.gzip.*
import org.jetbrains.skiko.*
import xyz.cssxsh.skia.*
import java.io.*
import java.nio.file.*
import java.util.jar.*
import java.util.zip.*

internal val logger by lazy {
    try {
        MiraiSkiaPlugin.logger
    } catch (_: ExceptionInInitializerError) {
        MiraiLogger.Factory.create(Library::class)
    }
}

private val http = HttpClient(OkHttp) {
    CurlUserAgent()
    ContentEncoding()
    expectSuccess = true
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
    engine {
        config {
            dns(object : Dns {
                private val url = System.getProperty("xyz.cssxsh.mirai.doh", "https://public.dns.iij.jp/dns-query")
                private val doh = DnsOverHttps.Builder()
                    .client(okhttp3.OkHttpClient())
                    .url(url.toHttpUrl())
                    .includeIPv6(true)
                    .build()
                private val cn = listOf("repo.huaweicloud.com")

                @Throws(java.net.UnknownHostException::class)
                override fun lookup(hostname: String): List<java.net.InetAddress> {
                    if (hostname in cn) return Dns.SYSTEM.lookup(hostname)
                    return try {
                        doh.lookup(hostname)
                    } catch (_: java.net.UnknownHostException) {
                        Dns.SYSTEM.lookup(hostname)
                    }
                }
            })
        }
    }
}

internal var listener: (urlString: String) -> ProgressListener? = { null }

internal suspend fun download(urlString: String, folder: File): File {
    var name = urlString.substringAfterLast('/').decodeURLPart()
    val listener = listener(name)

    val response = http.get(urlString) {
        onDownload(listener)
    }
    name = response.headers[HttpHeaders.ContentDisposition]
        ?.let { ContentDisposition.parse(it).parameter(ContentDisposition.Parameters.FileName) }
        ?: response.request.url.encodedPath.substringAfterLast('/').decodeURLPart()

    val file = folder.resolve(name)
    val expect = response.contentLength()

    if (file.isFile && expect == file.length()) {
        logger.info { "文件 ${file.name} 已存在，跳过下载" }
    } else {
        file.delete()
        logger.info { "文件 ${file.name} 开始下载" }
        file.outputStream().use { output ->
            val channel = response.bodyAsChannel()

            while (!channel.isClosedForRead) channel.copyTo(output)
        }
        val actual = file.length()
        if (expect != null && actual != expect) {
            logger.warning { "${file.name} 下载异常 expect: $expect actual: $actual" }
        }
    }
    return file
}

/**
 * 下载字体到指定福利
 * @param folder 字体文件夹
 * @see loadTypeface
 */
@JvmSynthetic
public suspend fun downloadTypeface(folder: File, vararg links: String) {
    val downloaded: MutableList<File> = ArrayList(links.size)
    val temp = runInterruptible(Dispatchers.IO) {
        Files.createTempDirectory("skia")
            .toFile()
    }

    folder.mkdirs()

    for (link in links) {
        try {
            downloaded.add(download(urlString = link, folder = temp))
        } catch (cause: Exception) {
            logger.warning({ "字体下载失败, $link" }, cause)
        }
    }

    for (pack in downloaded) runInterruptible(Dispatchers.IO) {
        unpack(pack, folder)
    }
}

/**
 * 解压压缩包到指定文件夹
 * @param pack 压缩包
 * @param folder 文件夹
 */
public fun unpack(pack: File, folder: File) {
    when (pack.extension) {
        "7z" -> SevenZFile(pack).use { sevenZ ->
            for (entry in sevenZ.entries) {
                if (entry.isDirectory) continue
                if (entry.hasStream().not()) continue
                val target = folder.resolve(entry.name)
                if (target.extension !in FontExtensions) continue
                target.parentFile.mkdirs()
                target.outputStream().use { output ->
                    sevenZ.getInputStream(entry).use { input ->
                        input.copyTo(output)
                    }
                }
                target.setLastModified(entry.lastModifiedDate.time)
            }
        }
        "zip" -> ZipFile(pack).use { zip ->
            for (entry in zip.entries()) {
                if (entry.isDirectory) continue
                if (entry.name.startsWith("__MACOSX")) continue
                val target = folder.resolve(entry.name)
                if (target.extension !in FontExtensions) continue
                target.parentFile.mkdirs()
                target.outputStream().use { output ->
                    zip.getInputStream(entry).use { input ->
                        input.copyTo(output)
                    }
                }
                target.setLastModified(entry.lastModifiedTime.toMillis())
            }
        }
        "gz" -> pack.inputStream()
            .buffered()
            .let(::GzipCompressorInputStream)
            .let(::TarArchiveInputStream)
            .use { input ->
                while (true) {
                    val entry = input.nextTarEntry ?: break
                    if (entry.isFile.not()) continue
                    if (input.canReadEntryData(entry).not()) continue
                    val target = folder.resolve(entry.name)
                    if (target.extension !in FontExtensions) continue
                    target.parentFile.mkdirs()
                    target.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    target.setLastModified(entry.modTime.time)
                }
            }
        else -> Files.move(pack.toPath(), folder.resolve(pack.name).toPath())
    }
}

/**
 * 从指定目录加载字体
 * @param folder 字体文件夹
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
        } catch (cause: Exception) {
            logger.warning({ "加载字体文件失败 ${file.path}" }, cause)
        }
    }
}

/**
 * 字体后缀名
 */
public val FontExtensions: Array<String> = arrayOf("ttf", "otf", "eot", "fon", "font", "woff", "woff2", "ttc")

/**
 * 一些免费字体链接
 */
public val FreeFontLinks: Array<String> = arrayOf(
    // "https://raw.githubusercontent.com/googlefonts/noto-emoji/main/fonts/NotoColorEmoji_WindowsCompatible.ttf",
    "https://www.nicolesharp.net/fonts/google/NotoColorEmoji_WindowsCompatible.ttf",
    "https://mirai.mamoe.net/assets/uploads/files/1666870589379-方正书宋简体.ttf",
    "https://mirai.mamoe.net/assets/uploads/files/1666870589357-方正仿宋简体.ttf",
    "https://mirai.mamoe.net/assets/uploads/files/1666870589334-方正楷体简体.ttf",
    "https://mirai.mamoe.net/assets/uploads/files/1666870589312-方正黑体简体.ttf"
)

internal val SKIKO_MAVEN: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skiko.maven")
        ?: System.getProperty("xyz.cssxsh.mirai.skia.maven")
        ?: "https://maven.pkg.jetbrains.space/public/p/compose/dev"
}

internal val SKIKO_PACKAGE: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skiko.package")
        ?: System.getProperty("xyz.cssxsh.mirai.skia.package")
        ?: when {
            "android" in hostId -> "skiko-android-runtime-${hostArch.id}"
            else -> "skiko-awt-runtime-${hostId}"
        }
}

internal val SKIKO_VERSION: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.skiko.version")
        ?: System.getProperty("xyz.cssxsh.mirai.skia.version")
        ?: Version.skiko
}

internal val GIF_RELEASE: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.gif.release", "https://github.com/cssxsh/gif-jni")
}

internal val GIF_VERSION: String by lazy {
    System.getProperty("xyz.cssxsh.mirai.gif.version", xyz.cssxsh.gif.Version.gif)
}

private const val ICU = "icudtl.dat"

/**
 * 检查平台问题并修正
 */
public fun checkPlatform() {
    // Termux
    if (hostOs == OS.Linux && "termux" in System.getProperty("user.dir")) {
        logger.info { "change platform: $hostId to android-arm64" }
        try {
            val kt = Class.forName("org.jetbrains.skiko.OsArch_jvmKt")
            val delegate = kt.getDeclaredField("hostId\$delegate").apply { isAccessible = true }
            val lazy = delegate.get(null)
            val value = lazy::class.java.getDeclaredField("_value").apply { isAccessible = true }
            value.set(lazy, "android-arm64")
        } catch (_: Exception) {
            logger.warning { "修改 hostId 失败" }
        }
    }
}

/**
 * 在 [folder] 中加载所需JNI库
 */
public suspend fun loadJNILibrary(folder: File) {
    val skiko = System.mapLibraryName("skiko-$hostId")
    val gif = System.mapLibraryName("gif-$hostId")

    folder.mkdirs()

    with(folder.resolve(skiko)) {
        val version = folder.resolve("skia.version.txt")
        val maven = "$SKIKO_MAVEN/org/jetbrains/skiko/$SKIKO_PACKAGE/$SKIKO_VERSION/$SKIKO_PACKAGE-$SKIKO_VERSION.jar"
        val huawei = "https://repo.huaweicloud.com/repository/maven/org/jetbrains/skiko/$SKIKO_PACKAGE/$SKIKO_VERSION/$SKIKO_PACKAGE-$SKIKO_VERSION.jar"
        if (version.exists().not() || version.readText() != SKIKO_VERSION) delete()

        if (exists().not()) {
            val file = try {
                download(urlString = huawei, folder = folder)
            } catch (cause: IOException) {
                logger.warning({ huawei }, cause)
                try {
                    download(urlString = maven, folder = folder)
                } catch (io: IOException) {
                    throw io
                }
            }
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
    synchronized(System.getProperties()) {
        @Suppress("INVISIBLE_MEMBER")
        System.setProperty(Library.SKIKO_LIBRARY_PATH_PROPERTY, folder.path)
        Library.load()
    }

    with(folder.resolve(gif)) {
        val version = folder.resolve("gif.version.txt")
        val release = "$GIF_RELEASE/releases/download/v$GIF_VERSION/$gif"
        val proxy = "https://ghproxy.com/https://github.com/cssxsh/gif-jni/releases/download/v$GIF_VERSION/$gif"
        if (version.exists() && version.readText() != GIF_VERSION) delete()

        if (exists().not()) {
            try {
                download(urlString = proxy, folder = folder)
            } catch (cause: IOException) {
                logger.warning({ proxy }, cause)
                try {
                    download(urlString = release, folder = folder)
                } catch (io: IOException) {
                    throw io
                }
            }
        }
        version.writeText(GIF_VERSION)
    }
    System.setProperty(xyz.cssxsh.gif.Library.GIF_LIBRARY_PATH_PROPERTY, folder.path)
    xyz.cssxsh.gif.Library.load()
}
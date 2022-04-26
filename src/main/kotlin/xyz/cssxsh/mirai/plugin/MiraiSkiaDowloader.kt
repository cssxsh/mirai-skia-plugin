package xyz.cssxsh.mirai.plugin

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
import net.mamoe.mirai.utils.*
import xyz.cssxsh.skia.*
import java.io.*
import java.util.zip.*

internal val logger get() = MiraiSkiaPlugin.logger

private val http = HttpClient(OkHttp) {
    CurlUserAgent()
    install(HttpTimeout) {
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
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

internal suspend fun avatar(id: Long, size: Int, folder: File): File = supervisorScope {
    http.get<HttpStatement>("https://q.qlogo.cn/g?b=qq&nk=${id}&s=${size}").execute { response ->
        val target = folder.resolve("${id}.${response.contentType()?.contentSubtype}")

        if (target.exists().not() || target.lastModified() < (response.lastModified()?.time ?: 0)) {
            target.outputStream().use { output ->
                val channel: ByteReadChannel = response.receive()

                while (!channel.isClosedForRead) channel.copyTo(output)
            }
        } else {
            response.call.cancel("文件 ${target.name} 已存在，跳过下载")
        }

        target
    }
}

internal fun sevenZA(folder: File): File {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch")
    val relative = when {
        os.contains(other = "linux") && arch.contains(other = "aarch64") -> "7zz-linux-arm64"
        os.contains(other = "linux") && arch.contains(other = "aarch") -> "7zz-linux-arm"
        os.contains(other = "linux") && arch.contains(other = "64") -> "7zz-linux-x64"
        os.contains(other = "linux") && arch.contains(other = "86") -> "7zz-linux-x86"
        os.contains(other = "windows") && arch.contains(other = "64") -> "7za-x64.exe"
        os.contains(other = "windows") && arch.contains(other = "86") -> "7za-x86.exe"
        os.contains(other = "mac") -> "7zz-mac"
        os.contains(other = "darwin") -> "7zz-mac"
        else -> throw RuntimeException("Unsupported platform: $os $arch")
    }
    val binary = folder.resolve(relative).apply {
        if (exists().not()) {
            outputStream().use { output ->
                MiraiSkiaPlugin.getResourceAsStream("xyz/cssxsh/mirai/plugin/$relative")!!
                    .use { input -> input.transferTo(output) }
            }
        }
        setExecutable(true)
    }

    return binary
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
                ProcessBuilder(sevenZA(folder = download).absolutePath, "x", pack.absolutePath, "-y")
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

/**
 * 下载表情模板
 */
@JvmSynthetic
public suspend fun loadFace(folder: File): Unit = withContext(Dispatchers.IO) {
    folder.mkdirs()
    val sprite = download(urlString = "https://benisland.neocities.org/petpet/img/sprite.png", folder)
    System.setProperty(PET_PET_SPRITE, sprite.absolutePath)
    val dear = download(urlString = "https://tva3.sinaimg.cn/large/003MWcpMly8gv4s019bzsg606o06o40902.gif", folder)
    System.setProperty(DEAR_ORIGIN, dear.absolutePath)
    val zzkia = download(urlString = "https://cdn.jsdelivr.net/gh/dcalsky/bbq/zzkia/images/4.jpg", folder)
    System.setProperty(ZZKIA_ORIGIN, zzkia.absolutePath)
}
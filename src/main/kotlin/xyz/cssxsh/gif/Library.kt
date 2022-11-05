package xyz.cssxsh.gif

import org.jetbrains.skiko.*
import java.io.*
import java.nio.file.*
import java.util.concurrent.atomic.*

/**
 * GIF JNI 加载器
 * @see org.jetbrains.skiko.Library
 */
public object Library {
    internal const val GIF_LIBRARY_PATH_PROPERTY = "gif.library.path"
    internal val cacheRoot = "${System.getProperty("user.home")}/.gif/"
    private val libraryPath = System.getProperty(GIF_LIBRARY_PATH_PROPERTY)
    private var copyDir: File? = null
    internal var loaded = AtomicBoolean(false)

    public fun staticLoad() {
        if (loaded.compareAndSet(false, true)) {
            load()
        }
    }

    private fun loadLibraryOrCopy(library: File) {
        try {
            System.load(library.absolutePath)
        } catch (e: UnsatisfiedLinkError) {
            if (e.message?.contains("already loaded in another classloader") == true) {
                copyDir = Files.createTempDirectory("skiko").toFile()
                val tempFile = copyDir!!.resolve(library.name)
                Files.copy(library.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                tempFile.deleteOnExit()
                System.load(tempFile.absolutePath)
            } else {
                throw e
            }
        }
    }

    private fun unpackIfNeeded(dest: File, resourceName: String): File {
        val file = File(dest, resourceName)
        if (!file.exists()) {
            val tempFile = File.createTempFile("gif", "", dest)
            Library::class.java.getResourceAsStream("/$resourceName")!!.use { input ->
                Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE)
        }
        return file
    }

    @Synchronized
    internal fun load() {
        val name = "gif-$hostId"
        val platformName = System.mapLibraryName(name)

        if (hostOs == OS.Android) {
            System.loadLibrary("gif-$hostId")
            return
        }

        // First try: system property is set.
        if (libraryPath != null) {
            val library = File(libraryPath, platformName)
            loadLibraryOrCopy(library)
            return
        }

        val jvmFiles = File(System.getProperty("java.home"), if (hostOs.isWindows) "bin" else "lib")
        val pathInJvm = jvmFiles.resolve(platformName)
        if (pathInJvm.exists()) {
            loadLibraryOrCopy(pathInJvm)
            return
        }

        val hashResourceStream = Library::class.java.getResourceAsStream(
            "/$platformName.sha256"
        ) ?: throw LibraryLoadException(
            "Cannot find $platformName.sha256, proper native dependency missing."
        )
        val hash = hashResourceStream.use { it.bufferedReader().readLine() }

        val cacheDir = File(cacheRoot, hash)
        cacheDir.mkdirs()
        val library = unpackIfNeeded(cacheDir, platformName)
        loadLibraryOrCopy(library)
    }
}
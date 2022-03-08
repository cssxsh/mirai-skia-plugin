package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.paragraph.*
import java.util.*
import kotlin.collections.*
import kotlin.jvm.*

/**
 * 获取字体工具
 * @see Typeface
 * @see FontMgr
 * @see TypefaceFontProvider
 */
public object FontUtils {

    internal val instances: Sequence<FontMgr> = sequence {
        yield(provider)
        yield(FontMgr.default)
        yieldAll(ServiceLoader.load(FontMgr::class.java, this::class.java.classLoader))
        yieldAll(ServiceLoader.load(TypefaceFontProvider::class.java, this::class.java.classLoader))
    }

    public val provider: TypefaceFontProvider = TypefaceFontProvider()

    /**
     * 字体列表
     */
    public fun families(): Set<String> {
        val names: MutableSet<String> = HashSet()
        for (manager in instances) {
            repeat(manager.familiesCount) { index -> names.add(manager.getFamilyName(index)) }
        }

        return names
    }

    /**
     * 加载字体
     * @see provider
     */
    public fun loadTypeface(path: String, index: Int = 0) {
        provider.registerTypeface(Typeface.makeFromFile(path, index))
    }

    /**
     * 加载字体
     * @see provider
     */
    public fun loadTypeface(data: Data, index: Int = 0) {
        provider.registerTypeface(Typeface.makeFromData(data, index))
    }

    /**
     * 加载字体
     * @see provider
     */
    public fun loadTypeface(bytes: ByteArray, index: Int = 0) {
        Data.makeFromBytes(bytes).use { data -> loadTypeface(data, index) }
    }

    /**
     * 获取指定的 [Typeface]
     */
    public fun matchFamilyStyle(familyName: String, style: FontStyle): Typeface? {
        return instances.firstNotNullOfOrNull { provider -> provider.matchFamilyStyle(familyName, style) }
    }

    /**
     * 获取指定的 [Typeface]
     */
    public fun matchFamiliesStyle(families: Array<String?>, style: FontStyle): Typeface? {
        return instances.firstNotNullOfOrNull { provider -> provider.matchFamiliesStyle(families, style) }
    }

    /**
     * 宋体
     */
    public fun matchSimSun(style: FontStyle): Typeface? = matchFamilyStyle("SimSun", style)

    /**
     * 新宋体
     */
    public fun matchNSimSun(style: FontStyle): Typeface? = matchFamilyStyle("NSimSun", style)

    /**
     * 黑体
     */
    public fun matchSimHei(style: FontStyle): Typeface? = matchFamilyStyle("SimHei", style)

    /**
     * 仿宋
     */
    public fun matchFangSong(style: FontStyle): Typeface? = matchFamilyStyle("FangSong", style)

    /**
     * 楷体
     */
    public fun matchKaiTi(style: FontStyle): Typeface? = matchFamilyStyle("KaiTi", style)

    /**
     * 隶书
     */
    public fun matchLiSu(style: FontStyle): Typeface? = matchFamilyStyle("LiSu", style)

    /**
     * 幼圆
     */
    public fun matchYouYuan(style: FontStyle): Typeface? = matchFamilyStyle("YouYuan", style)

    /**
     * Arial
     */
    public fun matchArial(style: FontStyle): Typeface? = matchFamilyStyle("Arial", style)

    /**
     * Helvetica
     */
    public fun matchHelvetica(style: FontStyle): Typeface? = matchFamilyStyle("Helvetica", style)
}
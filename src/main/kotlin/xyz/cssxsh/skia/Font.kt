package xyz.cssxsh.skia

import org.jetbrains.skia.*

public fun FontMgr.makeFamilies(): Map<String, FontStyleSet> {
    val count = familiesCount
    if (count == 0) return emptyMap()
    val families: MutableMap<String, FontStyleSet> = HashMap()

    for (index in 0 until count) {
        val name = getFamilyName(index)
        val styles = makeStyleSet(index) ?: throw NoSuchElementException("${this}: ${index}.${name}")
        families[name] = styles
    }

    return families
}
package xyz.cssxsh.skia

import org.jetbrains.skia.*

public fun FontMgr.makeFamilies(): Map<String, FontStyleSet> {
    val count = familiesCount
    if (count == 0) return emptyMap()

    return (0 until count).associate { index ->
        getFamilyName(index) to makeStyleSet(index)!!
    }
}
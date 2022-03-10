package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.svg.*
import org.jsoup.parser.*

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

/**
 * 将 Style 写入具体的 Element 中，修正SVG绘图结果
 * @see org.jsoup.parser.Parser
 */
public fun SVGDOM.Companion.makeFromString(xml: String): SVGDOM {
    val document = Parser.xmlParser().parseInput(xml, "")
    for (style in document.select("style")) {
        val text = style?.text() ?: break
        var pos = 0

        while (true) {
            val before = text.indexOf('{', pos)
            if (before == -1) break
            val after = text.indexOf('}', before)
            if (after == -1) break

            val query = text.substring(pos, before).trim()
            val attributes = text.substring(before + 1, after).split(';')

            for (attribute in attributes) {
                val key = attribute.substringBefore(':').trim()
                val value = attribute.substringAfter(':').trim()

                try {
                    for (element in document.select(query)) {
                        element.attr(key, value)
                    }
                } catch (_: org.jsoup.select.Selector.SelectorParseException) {
                    // 
                }
            }

            pos = after + 1
        }
    }

    return SVGDOM(data = Data.makeFromBytes(document.toString().toByteArray()))
}
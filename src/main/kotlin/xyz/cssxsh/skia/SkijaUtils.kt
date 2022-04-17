package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.svg.*
import org.jsoup.nodes.*
import org.jsoup.parser.*
import org.jsoup.select.*
import java.io.*

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
 * 从 xml 读取 SVGDOM
 * @see org.jsoup.parser.Parser
 * @see SVGDOM.Companion.makeFromXml
 */
public fun SVGDOM.Companion.makeFromString(xml: String, baseUri: String = ""): SVGDOM {
    return makeFromXml(document = Parser.xmlParser().parseInput(xml, baseUri))
}

/**
 * 从 file 读取 SVGDOM
 * @see org.jsoup.parser.Parser
 * @see SVGDOM.Companion.makeFromXml
 */
public fun SVGDOM.Companion.makeFromFile(xml: File, baseUri: String = ""): SVGDOM {
    return makeFromXml(document = xml.bufferedReader().use { Parser.xmlParser().parseInput(it, baseUri) })
}

/**
 * 将 Style 写入具体的 Element 中，修正SVG绘图结果
 * @see SVGDOM.Companion.makeFromString
 */
public fun SVGDOM.Companion.makeFromXml(document: Document): SVGDOM {
    for (style in document.select("style")) {
        val text = style?.text() ?: continue
        var pos = 0

        while (true) {
            val before = text.indexOf('{', pos)
            if (before == -1) break
            val after = text.indexOf('}', before)
            if (after == -1) break

            val query = text.substring(pos, before)
            val attributes = text.substring(before + 1, after)
                .splitToSequence(';')
                .filter { it.isNotBlank() }
                .map { it.split(':') }
            val elements = try {
                document.select(query)
            } catch (_: Selector.SelectorParseException) {
                pos = after + 1
                continue
            }

            for ((key, value) in attributes) {
                for (element in elements) {
                    element.attr(key, value)
                }
            }

            pos = after + 1
        }

        style.remove()
    }

    return SVGDOM(data = Data.makeFromBytes(bytes = document.toString().toByteArray()))
}

/**
 * @see SVGDOM.setContainerSize
 */
public fun SVGDOM.makeImageSnapshot(width: Int, height: Int): Image {
    setContainerSize(width.toFloat(), height.toFloat())

    return Surface.makeRasterN32Premul(350, 350).use { it.makeImageSnapshot() }
}
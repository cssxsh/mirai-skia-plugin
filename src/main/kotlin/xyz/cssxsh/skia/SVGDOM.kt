package xyz.cssxsh.skia

import org.jetbrains.skia.*
import org.jetbrains.skia.svg.*
import org.jsoup.nodes.*
import org.jsoup.parser.*
import org.jsoup.select.*
import java.io.*

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
        document.apply(style = text)
        style.remove()
    }

    return SVGDOM(data = Data.makeFromBytes(bytes = document.toString().toByteArray()))
}

/**
 * 将 [style] 生效
 */
private fun Document.apply(style: String) {
    var pos = 0

    while (true) {
        val before = style.indexOf('{', pos)
        if (before == -1) break
        val after = style.indexOf('}', before)
        if (after == -1) break

        val query = style.substring(pos, before)
        val attributes = style.substring(before + 1, after)
            .splitToSequence(';')
            .filter { it.isNotBlank() }
            .map { it.split(':') }
        val elements = try {
            select(query)
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
}

/**
 * @see SVGDOM.setContainerSize
 */
public fun SVGDOM.makeImageSnapshot(width: Int, height: Int): Image {
    setContainerSize(width.toFloat(), height.toFloat())

    return Surface.makeRasterN32Premul(width, height).use { surface ->
        render(surface.canvas)
        surface.makeImageSnapshot()
    }
}
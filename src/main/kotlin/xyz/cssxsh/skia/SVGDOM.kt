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
    for (style in document.select("style").remove()) {
        document.apply(css = style.text())
    }

    return SVGDOM(data = Data.makeFromBytes(bytes = document.toString().toByteArray()))
}

private const val CSS_FONT_REGEX =
    """font:\s*(normal|italic|oblique|inherit|)\s*(normal|small-caps|inherit|)\s*(normal|bold|bolder|inherit|\d+|)\s+([^/ ]+)/?(\S*)\s+(.+);"""

/**
 * 将 [css] 生效
 */
private fun Document.apply(css: String) {
    var pos = 0

    while (true) {
        val before = css.indexOf('{', pos)
        if (before == -1) break
        val after = css.indexOf('}', before)
        if (after == -1) break

        val query = css.substring(pos, before)
        val value = css.substring(before + 1, after)
            .replace(CSS_FONT_REGEX.toRegex()) { match ->
                // fixme: font: ...
                val (style, variant, weight, size, height, family) = match.destructured
                buildString {
                    if (style.isNotEmpty()) append("font-style: ").append(style).append("; ")
                    if (variant.isNotEmpty()) append("font-variant: ").append(variant).append("; ")
                    if (weight.isNotEmpty()) append("font-weight: ").append(weight).append("; ")
                    append("font-size: ").append(size).append("; ")
                    if (height.isNotEmpty()) append("line-height: ").append(height).append("; ")
                    append("font-family: ").append(family).append("; ")
                }
            }
        val elements = try {
            select(query)
        } catch (_: Selector.SelectorParseException) {
            emptyList()
        }

        for (element in elements) {
            element.attr("style", value + element.attr("style"))
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
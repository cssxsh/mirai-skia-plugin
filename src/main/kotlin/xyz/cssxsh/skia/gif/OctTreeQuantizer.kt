package xyz.cssxsh.skia.gif

import org.jetbrains.skia.*

/**
 * Implements qct-tree quantization.
 *
 *
 * The principle of algorithm: [http://www.microsoft.com/msj/archive/S3F1.aspx]
 *
 */
public class OctTreeQuantizer {
    private val mask = intArrayOf(0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01)

    private var leafCount = 0
    private var inIndex = 0
    private val nodeList = arrayOfNulls<Node>(8)

    /**
     * 量化处理
     * @param bitmap 位图数据
     * @param maxColorCount 最大颜色数量
     * @return RGB调色板
     */
    public fun quantize(bitmap: Bitmap, maxColorCount: Int = 256): IntArray {
        val node = createNode(0)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val color = bitmap.getColor(x, y)
                addColor(node, color, 0)
                while (leafCount > maxColorCount) {
                    reduceTree()
                }
            }
        }
        val colors: MutableSet<Int> = HashSet()
        getColorPalette(node, colors)
        leafCount = 0
        inIndex = 0
        for (i in 0..7) {
            nodeList[i] = null
        }
        return colors.toIntArray()
    }

    private fun addColor(node_: Node?, color: Int, inLevel: Int): Boolean {
        val node = node_ ?: createNode(inLevel)
        val nIndex: Int
        val shift: Int
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        if (node.isLeaf) {
            node.pixelCount++
            node.redSum += red
            node.greenSum += green
            node.blueSum += blue
        } else {
            shift = 7 - inLevel
            nIndex = (red and mask[inLevel] shr shift shl 2
                or (green and mask[inLevel] shr shift shl 1)
                or (blue and mask[inLevel] shr shift))
            var tmpNode = node.child[nIndex]
            if (tmpNode == null) {
                tmpNode = createNode(inLevel + 1)
            }
            node.child[nIndex] = tmpNode
            return addColor(node.child[nIndex], color, inLevel + 1)
        }
        return true
    }

    private fun createNode(level: Int): Node {
        val node = Node()
        node.level = level
        node.isLeaf = level == 8
        if (node.isLeaf) {
            leafCount++
        } else {
            node.next = nodeList[level]
            nodeList[level] = node
        }
        return node
    }

    private fun reduceTree() {
        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var count = 0
        var i = 7
        while (i > 0) {
            if (nodeList[i] != null) break
            i--
        }
        val tmpNode = nodeList[i]
        nodeList[i] = tmpNode!!.next
        i = 0
        while (i < 8) {
            if (tmpNode.child[i] != null) {
                redSum += tmpNode.child[i]!!.redSum
                greenSum += tmpNode.child[i]!!.greenSum
                blueSum += tmpNode.child[i]!!.blueSum
                count += tmpNode.child[i]!!.pixelCount
                tmpNode.child[i] = null
                leafCount--
            }
            i++
        }
        tmpNode.isLeaf = true
        tmpNode.redSum = redSum
        tmpNode.greenSum = greenSum
        tmpNode.blueSum = blueSum
        tmpNode.pixelCount = count
        leafCount++
    }

    private fun getColorPalette(node: Node?, colors: MutableSet<Int>) {
        if (node!!.isLeaf) {
            node.colorIndex = inIndex
            node.redSum = node.redSum / node.pixelCount
            node.greenSum = node.greenSum / node.pixelCount
            node.blueSum = node.blueSum / node.pixelCount
            node.pixelCount = 1
            inIndex++
            val red = node.redSum and 0xFF
            val green = node.greenSum and 0xFF
            val blue = node.blueSum and 0xFF
            colors.add((red shl 16) or (green shl 8) or (blue shr 0))
        } else {
            for (i in 0..7) {
                if (node.child[i] != null) {
                    getColorPalette(node.child[i], colors)
                }
            }
        }
    }

    private class Node {
        var isLeaf = false
        var level = 0
        var colorIndex = 0
        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var pixelCount = 0
        var child = arrayOfNulls<Node>(8)
        var next: Node? = null
    }
}
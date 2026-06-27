package de.maibornwolff.treesitter.excavationsite.shared.infrastructure.walker

import org.treesitter.TSNode

object NodeTypeMatcher {
    fun isNodeTypeAllowed(
        node: TSNode,
        nodeType: String,
        allowedTypes: TreeNodeTypes,
        sourceCode: String = "",
        sourceBytes: ByteArray = byteArrayOf()
    ): Boolean {
        if (allowedTypes.simpleNodeTypes.contains(nodeType)) {
            return true
        } else if (allowedTypes.nestedNodeTypes != null) {
            return isNestedTypeAllowed(
                node,
                nodeType,
                allowedTypes.nestedNodeTypes,
                sourceCode,
                sourceBytes
            )
        }
        return false
    }

    fun isNestedTypeAllowed(
        node: TSNode,
        nodeType: String,
        nestedTypes: Set<NestedNodeType>,
        sourceCode: String = "",
        sourceBytes: ByteArray = byteArrayOf()
    ): Boolean {
        for (nestedType in nestedTypes) {
            if (nestedType.baseNodeType != nodeType) continue

            if (nestedType.childNodePosition != null && nestedType.childNodeCount == node.childCount) {
                val childNode = node.getChild(nestedType.childNodePosition)
                if (!childNode.isNull && childMatches(childNode, nestedType.childNodeTypes, sourceCode, sourceBytes)) {
                    return true
                }
            } else if (nestedType.childNodeFieldName != null) {
                val childNode = node.getChildByFieldName(nestedType.childNodeFieldName)
                if (!childNode.isNull && childMatches(childNode, nestedType.childNodeTypes, sourceCode, sourceBytes)) {
                    return true
                }
            }
        }
        return false
    }

    private fun childMatches(childNode: TSNode, allowedValues: Set<String>, sourceCode: String, sourceBytes: ByteArray): Boolean =
        allowedValues.contains(childNode.type) ||
            (
                sourceBytes.isNotEmpty() &&
                    allowedValues.contains(getNodeText(childNode, sourceBytes))
            ) ||
            (
                sourceCode.isNotEmpty() &&
                    allowedValues.contains(TreeTraversal.getNodeText(childNode, sourceCode))
            )

    private fun getNodeText(node: TSNode, sourceBytes: ByteArray): String {
        val start = node.startByte
        val end = node.endByte
        return String(sourceBytes, start, end - start, Charsets.UTF_8)
    }
}

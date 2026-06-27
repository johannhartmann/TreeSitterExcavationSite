package de.maibornwolff.treesitter.excavationsite.shared.infrastructure.walker

import de.maibornwolff.treesitter.excavationsite.languages.LanguageRegistry
import de.maibornwolff.treesitter.excavationsite.shared.domain.Language
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.treesitter.TSNode

class NodeTypeMatcherTest {
    @Test
    fun `should match nested child node type`() {
        // Arrange
        val code = "object Main:\n  val enabled = a && b"
        val node = findInfixExpression(code)
        val allowedTypes = operatorMatcher(setOf("operator_identifier"))

        // Act
        val result = NodeTypeMatcher.isNodeTypeAllowed(node, node.type, allowedTypes)

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun `should match nested child text`() {
        // Arrange
        val code = "object Main:\n  val enabled = a && b"
        val node = findInfixExpression(code)
        val allowedTypes = operatorMatcher(setOf("&&"))

        // Act
        val result = NodeTypeMatcher.isNodeTypeAllowed(
            node = node,
            nodeType = node.type,
            allowedTypes = allowedTypes,
            sourceBytes = code.toByteArray(Charsets.UTF_8)
        )

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun `should match nested child text with UTF-8 byte offsets`() {
        // Arrange
        val code = "object Main:\n  val größe = 1\n  val enabled = a && b"
        val node = findInfixExpression(code)
        val allowedTypes = operatorMatcher(setOf("&&"))

        // Act
        val result = NodeTypeMatcher.isNodeTypeAllowed(
            node = node,
            nodeType = node.type,
            allowedTypes = allowedTypes,
            sourceBytes = code.toByteArray(Charsets.UTF_8)
        )

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun `should not match nested child text without source text`() {
        // Arrange
        val code = "object Main:\n  val enabled = a && b"
        val node = findInfixExpression(code)
        val allowedTypes = operatorMatcher(setOf("&&"))

        // Act
        val result = NodeTypeMatcher.isNodeTypeAllowed(node, node.type, allowedTypes)

        // Assert
        assertThat(result).isFalse()
    }

    private fun operatorMatcher(allowedValues: Set<String>) = TreeNodeTypes(
        simpleNodeTypes = emptySet(),
        nestedNodeTypes = setOf(
            NestedNodeType(
                baseNodeType = "infix_expression",
                childNodeFieldName = "operator",
                childNodeTypes = allowedValues
            )
        )
    )

    private fun findInfixExpression(code: String): TSNode {
        val rootNode = TreeSitterParser.parse(code, LanguageRegistry.getTreeSitterLanguage(Language.SCALA))
        return TreeTraversal.findAllDescendantsOfType(rootNode, "infix_expression").first()
    }
}

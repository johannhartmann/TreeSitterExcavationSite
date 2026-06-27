package de.maibornwolff.treesitter.excavationsite.shared.domain

/**
 * Declarative rule for identifying a function body through a named child field
 * on the surrounding function node.
 */
data class FunctionBodyRule(val parentNodeType: String, val fieldName: String)

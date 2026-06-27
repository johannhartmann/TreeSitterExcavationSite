package de.maibornwolff.treesitter.excavationsite.languages.scala

import de.maibornwolff.treesitter.excavationsite.shared.domain.CommentFormats
import de.maibornwolff.treesitter.excavationsite.shared.domain.Extract
import de.maibornwolff.treesitter.excavationsite.shared.domain.ExtractionMapping
import de.maibornwolff.treesitter.excavationsite.shared.domain.ExtractionStrategy
import de.maibornwolff.treesitter.excavationsite.shared.domain.StringFormats

/**
 * Scala extraction definitions.
 */
object ScalaExtractionMapping : ExtractionMapping {
    private const val IDENTIFIER = "identifier"
    private const val OPERATOR_IDENTIFIER = "operator_identifier"
    private const val PACKAGE_IDENTIFIER = "package_identifier"
    private const val STRING = "string"
    private const val INTERPOLATED_STRING_EXPRESSION = "interpolated_string_expression"
    private val NAME = ExtractionStrategy.FirstChildByTypes(IDENTIFIER, OPERATOR_IDENTIFIER)

    override val nodeExtractions: Map<String, Extract> = buildMap {
        // Identifiers
        put("class_definition", Extract.Identifier(single = NAME))
        put("class_parameter", Extract.Identifier(single = NAME))
        put("enum_definition", Extract.Identifier(single = NAME))
        put("extension_definition", Extract.Identifier(single = NAME))
        put("function_definition", Extract.Identifier(single = NAME))
        put("function_declaration", Extract.Identifier(single = NAME))
        put("given_definition", Extract.Identifier(single = NAME))
        put("object_definition", Extract.Identifier(single = NAME))
        put("package_clause", Extract.Identifier(single = ExtractionStrategy.FirstChildByType(PACKAGE_IDENTIFIER)))
        put("package_object", Extract.Identifier(single = NAME))
        put("parameter", Extract.Identifier(single = NAME))
        put("trait_definition", Extract.Identifier(single = NAME))
        put("type_definition", Extract.Identifier(single = NAME))
        put("val_declaration", Extract.Identifier(single = NAME))
        put("val_definition", Extract.Identifier(single = NAME))
        put("var_declaration", Extract.Identifier(single = NAME))
        put("var_definition", Extract.Identifier(single = NAME))

        // Comments
        put("comment", Extract.Comment(CommentFormats.Line("//")))
        put("block_comment", Extract.Comment(CommentFormats.Block))

        // Strings
        put(STRING, Extract.StringLiteral(format = StringFormats.Scala))
        put(INTERPOLATED_STRING_EXPRESSION, Extract.StringLiteral(format = StringFormats.Scala))
    }
}

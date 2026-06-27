package de.maibornwolff.treesitter.excavationsite.languages.scala

import de.maibornwolff.treesitter.excavationsite.shared.domain.Metric
import de.maibornwolff.treesitter.excavationsite.shared.domain.MetricCondition
import de.maibornwolff.treesitter.excavationsite.shared.domain.MetricMapping

/**
 * Scala metric definitions.
 */
object ScalaMetricMapping : MetricMapping {
    override val nodeMetrics: Map<String, Set<Metric>> = buildMap {
        // Logic complexity
        put("if_expression", setOf(Metric.LogicComplexity))
        put("for_expression", setOf(Metric.LogicComplexity))
        put("while_expression", setOf(Metric.LogicComplexity))
        put("do_while_expression", setOf(Metric.LogicComplexity))
        put("match_expression", setOf(Metric.LogicComplexity))
        put("case_clause", setOf(Metric.LogicComplexity))
        put("guard", setOf(Metric.LogicComplexity))
        put("try_expression", setOf(Metric.LogicComplexity))
        put("catch_clause", setOf(Metric.LogicComplexity))

        // Logic complexity - conditional infix expressions with && or ||
        put(
            "infix_expression",
            setOf(
                Metric.LogicComplexityConditional(
                    MetricCondition.ChildFieldMatches(
                        fieldName = "operator",
                        allowedValues = setOf("&&", "||")
                    )
                )
            )
        )

        // Function complexity and number of functions
        put("function_definition", setOf(Metric.FunctionComplexity, Metric.Function))
        put("function_declaration", setOf(Metric.FunctionComplexity, Metric.Function))
        put("lambda_expression", setOf(Metric.FunctionComplexity))

        // Function body
        put("block", setOf(Metric.FunctionBody))
        put("indented_block", setOf(Metric.FunctionBody))
        put("indented_cases", setOf(Metric.FunctionBody))

        // Function parameters
        put("parameter", setOf(Metric.Parameter))

        // Message chains
        put("call_expression", setOf(Metric.MessageChain, Metric.MessageChainCall))
        put("field_expression", setOf(Metric.MessageChain, Metric.MessageChainCall))

        // Comment lines
        put("comment", setOf(Metric.CommentLine))
        put("block_comment", setOf(Metric.CommentLine))
    }
}

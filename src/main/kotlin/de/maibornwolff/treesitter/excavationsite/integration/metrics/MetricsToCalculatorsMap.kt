package de.maibornwolff.treesitter.excavationsite.integration.metrics

import de.maibornwolff.treesitter.excavationsite.integration.metrics.calculators.CommentLinesCalc
import de.maibornwolff.treesitter.excavationsite.integration.metrics.calculators.ComplexityCalc
import de.maibornwolff.treesitter.excavationsite.integration.metrics.calculators.MessageChainsCalc
import de.maibornwolff.treesitter.excavationsite.integration.metrics.calculators.NumberOfFunctionsCalc
import de.maibornwolff.treesitter.excavationsite.integration.metrics.calculators.ParametersPerFunctionCalc
import de.maibornwolff.treesitter.excavationsite.integration.metrics.calculators.RealLinesOfCodeCalc
import de.maibornwolff.treesitter.excavationsite.integration.metrics.domain.AvailableFileMetrics
import de.maibornwolff.treesitter.excavationsite.integration.metrics.domain.CalculationContext
import de.maibornwolff.treesitter.excavationsite.integration.metrics.ports.MetricNodeTypes
import de.maibornwolff.treesitter.excavationsite.shared.domain.CalculationExtensions
import org.treesitter.TSNode

class MetricsToCalculatorsMap(nodeTypeProvider: MetricNodeTypes, val calcExtensions: CalculationExtensions) {
    var sourceCode: String = ""
    var sourceBytes: ByteArray = byteArrayOf()

    val complexityCalc = ComplexityCalc(nodeTypeProvider)
    val commentCalc = CommentLinesCalc(nodeTypeProvider)
    val numberOfFunctionsCalc = NumberOfFunctionsCalc(nodeTypeProvider)
    val realLinesOfCodeCalc = RealLinesOfCodeCalc(nodeTypeProvider)
    val messageChainsCalc = MessageChainsCalc(nodeTypeProvider)

    val parametersPerFunctionCalc = ParametersPerFunctionCalc(nodeTypeProvider)

    fun getPerFileMetricInfo(): Map<AvailableFileMetrics, (TSNode, String, Int, Int) -> Int> = mapOf(
        AvailableFileMetrics.COMPLEXITY to { node: TSNode, nodeType: String, startRow: Int, endRow: Int ->
            complexityCalc.calculateFunctionComplexityForNode(
                CalculationContext(
                    node = node,
                    nodeType = nodeType,
                    startRow = startRow,
                    endRow = endRow,
                    shouldIgnoreNode = calcExtensions.ignoreNodeForComplexity,
                    sourceCode = sourceCode,
                    sourceBytes = sourceBytes,
                    isFunctionBodyNode = calcExtensions.isFunctionBodyNode
                )
            )
        },
        AvailableFileMetrics.LOGIC_COMPLEXITY to { node: TSNode, nodeType: String, _: Int, _: Int ->
            complexityCalc.calculateMetricForNode(
                CalculationContext(
                    node = node,
                    nodeType = nodeType,
                    shouldIgnoreNode = calcExtensions.ignoreNodeForComplexity,
                    sourceCode = sourceCode,
                    sourceBytes = sourceBytes,
                    isFunctionBodyNode = calcExtensions.isFunctionBodyNode
                )
            )
        },
        AvailableFileMetrics.COMMENT_LINES to { node: TSNode, nodeType: String, startRow: Int, endRow: Int ->
            commentCalc.calculateMetricForNode(
                CalculationContext(
                    node,
                    nodeType,
                    startRow,
                    endRow,
                    calcExtensions.ignoreNodeForCommentLines,
                    sourceCode = sourceCode,
                    sourceBytes = sourceBytes
                )
            )
        },
        AvailableFileMetrics.NUMBER_OF_FUNCTIONS to { node: TSNode, nodeType: String, _: Int, _: Int ->
            numberOfFunctionsCalc.calculateMetricForNode(
                CalculationContext(
                    node,
                    nodeType,
                    shouldIgnoreNode = calcExtensions.ignoreNodeForNumberOfFunctions,
                    sourceCode = sourceCode,
                    sourceBytes = sourceBytes
                )
            )
        },
        AvailableFileMetrics.MESSAGE_CHAINS to { node: TSNode, nodeType: String, _: Int, _: Int ->
            messageChainsCalc.calculateMetricForNode(
                CalculationContext(
                    node,
                    nodeType,
                    shouldIgnoreNode = calcExtensions.ignoreNodeForMessageChainCall,
                    sourceCode = sourceCode,
                    sourceBytes = sourceBytes
                )
            )
        },
        AvailableFileMetrics.REAL_LINES_OF_CODE to { node: TSNode, nodeType: String, startRow: Int, endRow: Int ->
            realLinesOfCodeCalc.calculateMetricForNode(
                CalculationContext(
                    node = node,
                    nodeType = nodeType,
                    startRow = startRow,
                    endRow = endRow,
                    shouldIgnoreNode = calcExtensions.ignoreNodeForRealLinesOfCode,
                    countNodeAsLeafNode = calcExtensions.countNodeAsLeafNode,
                    functionBodyUsesBrackets = calcExtensions.hasFunctionBodyStartOrEndNode,
                    sourceCode = sourceCode,
                    sourceBytes = sourceBytes,
                    isFunctionBodyNode = calcExtensions.isFunctionBodyNode
                )
            )
        }
    )

    fun processPerFunctionMetricsForNode(node: TSNode, nodeType: String, startRow: Int, endRow: Int) {
        parametersPerFunctionCalc.processMetricForNode(
            CalculationContext(
                node = node,
                nodeType = nodeType,
                startRow = startRow,
                endRow = endRow,
                shouldIgnoreNode = calcExtensions.ignoreNodeForParameterOfFunctions,
                sourceCode = sourceCode,
                sourceBytes = sourceBytes,
                isFunctionBodyNode = calcExtensions.isFunctionBodyNode
            )
        )
    }

    fun getMeasuresOfPerFunctionMetrics(): Map<String, Double> {
        val metricNameToValue = mutableMapOf<String, Double>()
        metricNameToValue.putAll(parametersPerFunctionCalc.getMeasureMetricsForMetricType())
        metricNameToValue.putAll(complexityCalc.getMeasureMetricsForMetricType())
        metricNameToValue.putAll(realLinesOfCodeCalc.getMeasureMetricsForMetricType())

        return metricNameToValue
    }
}

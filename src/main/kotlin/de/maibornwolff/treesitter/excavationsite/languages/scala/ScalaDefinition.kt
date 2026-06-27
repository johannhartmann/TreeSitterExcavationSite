package de.maibornwolff.treesitter.excavationsite.languages.scala

import de.maibornwolff.treesitter.excavationsite.shared.domain.Extract
import de.maibornwolff.treesitter.excavationsite.shared.domain.LanguageDefinition
import de.maibornwolff.treesitter.excavationsite.shared.domain.Metric

/**
 * Unified Scala language definition combining metrics and extraction.
 *
 * Composes ScalaMetricMapping and ScalaExtractionMapping.
 */
object ScalaDefinition : LanguageDefinition {
    override val nodeMetrics: Map<String, Set<Metric>> = ScalaMetricMapping.nodeMetrics
    override val nodeExtractions: Map<String, Extract> = ScalaExtractionMapping.nodeExtractions
}

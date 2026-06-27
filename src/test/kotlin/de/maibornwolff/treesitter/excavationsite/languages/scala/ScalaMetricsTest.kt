package de.maibornwolff.treesitter.excavationsite.languages.scala

import de.maibornwolff.treesitter.excavationsite.api.Language
import de.maibornwolff.treesitter.excavationsite.api.TreeSitterMetrics
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScalaMetricsTest {
    private fun parse(code: String) = TreeSitterMetrics.parse(code.trimIndent(), Language.SCALA)

    @Test
    fun `should calculate metrics for Scala 2 braces syntax`() {
        // Arrange
        val code = """
            object Main {
              def foo(a: Int): Int = {
                if (a > 0) a else 0
              }
            }
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.logicComplexity).isEqualTo(1.0)
        assertThat(result.complexity).isEqualTo(2.0)
    }

    @Test
    fun `should calculate metrics for Scala 3 braceless syntax`() {
        // Arrange
        val code = """
            object Main:
              def foo(a: Int): Int =
                if a > 0 then a else 0
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.logicComplexity).isEqualTo(1.0)
        assertThat(result.complexity).isEqualTo(2.0)
    }

    @Test
    fun `should count pattern matching cases and guards for complexity`() {
        // Arrange
        val code = """
            object Main:
              def classify(x: Int): String =
                x match
                  case 0 => "zero"
                  case n if n > 0 => "positive"
                  case _ => "negative"
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.logicComplexity).isEqualTo(5.0)
        assertThat(result.complexity).isEqualTo(6.0)
    }

    @Test
    fun `should count curried and using parameters`() {
        // Arrange
        val code = """
            object Main:
              def f(a: Int)(b: String)(using c: Ordering[Int]): String =
                b
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.perFunctionMetrics["max_parameters_per_function"]).isEqualTo(3.0)
    }

    @Test
    fun `should parse extension methods`() {
        // Arrange
        val code = """
            object Extensions:
              extension (s: String)
                def normalized: String = s.trim.toLowerCase
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.complexity).isEqualTo(1.0)
    }

    @Test
    fun `should parse given definitions`() {
        // Arrange
        val code = """
            object Instances:
              given Ordering[Int] with
                def compare(a: Int, b: Int): Int = a - b
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.perFunctionMetrics["max_parameters_per_function"]).isEqualTo(2.0)
    }

    @Test
    fun `should parse enum definitions`() {
        // Arrange
        val code = """
            enum Color:
              case Red, Green, Blue

              def label: String =
                this.toString.toLowerCase
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.complexity).isEqualTo(1.0)
        assertThat(result.realLinesOfCode).isEqualTo(4.0)
    }

    @Test
    fun `should count for comprehensions for complexity`() {
        // Arrange
        val code = """
            object Main:
              def pairs(xs: List[Int], ys: List[Int]): List[Int] =
                for
                  x <- xs
                  y <- ys
                  if x > 0
                yield x + y
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.numberOfFunctions).isEqualTo(1.0)
        assertThat(result.logicComplexity).isEqualTo(2.0)
        assertThat(result.complexity).isEqualTo(3.0)
    }

    @Test
    fun `should count comments without treating comment markers inside strings as comments`() {
        // Arrange
        val code = """
            object Main:
              // line comment
              /*
               * block comment
               */
              val url = "https://example.test/path//not-a-comment"
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.commentLines).isEqualTo(4.0)
    }

    @Test
    fun `should count message chains`() {
        // Arrange
        val code = """
            object Main:
              def render(user: User): String =
                user.profile.address.street.trim.toLowerCase
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.messageChains).isEqualTo(1.0)
    }

    @Test
    fun `should count logical operators for complexity`() {
        // Arrange
        val code = """
            object Main:
              def enabled(a: Boolean, b: Boolean, c: Boolean): Boolean =
                if a && (b || c) then true else false
        """

        // Act
        val result = parse(code)

        // Assert
        assertThat(result.logicComplexity).isEqualTo(3.0)
        assertThat(result.complexity).isEqualTo(4.0)
    }
}

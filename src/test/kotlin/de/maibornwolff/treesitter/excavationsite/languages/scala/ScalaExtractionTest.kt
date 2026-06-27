package de.maibornwolff.treesitter.excavationsite.languages.scala

import de.maibornwolff.treesitter.excavationsite.api.Language
import de.maibornwolff.treesitter.excavationsite.api.TreeSitterExtraction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScalaExtractionTest {
    private fun extract(code: String) = TreeSitterExtraction.extract(code.trimIndent(), Language.SCALA)

    @Test
    fun `should extract identifiers from Scala definitions`() {
        // Arrange
        val code = """
            package example

            trait Renderable

            object ScalaSample:
              def sum(a: Int, b: Int): Int = a + b
              val rendered = "value"
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.identifiers).containsExactly("example", "Renderable", "ScalaSample", "sum", "a", "b", "rendered")
    }

    @Test
    fun `should extract identifiers from Scala enum definitions`() {
        // Arrange
        val code = """
            enum Color:
              case Red, Green, Blue
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.identifiers).containsExactly("Color")
    }

    @Test
    fun `should extract line comments block comments and strings`() {
        // Arrange
        val code = """
            object Comments:
              // line comment
              /*
               * block comment
               */
              val text = "hello // not a comment"
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.comments).containsExactly("line comment", "block comment")
        assertThat(result.strings).containsExactly("hello // not a comment")
    }

    @Test
    fun `should extract interpolated and multiline strings without Scala prefixes`() {
        // Arrange
        val code = """
            object Strings:
              val name = "Ada"
              val greeting = s"hello ${'$'}name"
              val query = sql"select * from users where name = ${'$'}name"
              val multiline = raw${"\"\"\""}first
            second${"\"\"\""}
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.strings).containsExactly(
            "Ada",
            "hello ${'$'}name",
            "select * from users where name = ${'$'}name",
            "first\nsecond"
        )
    }
}

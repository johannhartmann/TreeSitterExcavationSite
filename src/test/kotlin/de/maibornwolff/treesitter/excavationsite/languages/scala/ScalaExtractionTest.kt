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
    fun `should extract operator identifiers and declarations`() {
        // Arrange
        val code = """
            trait Foo:
              def ++(other: Foo): Foo
              val size: Int
              var count: Int

            class :+:(head: Int)
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.identifiers).containsExactly("Foo", "++", "other", "size", "count", ":+:", "head")
    }

    @Test
    fun `should extract class parameters`() {
        // Arrange
        val code = """
            case class User(name: String, age: Int)
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.identifiers).containsExactly("User", "name", "age")
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

    @Test
    fun `should extract strings from sbt build syntax`() {
        // Arrange
        val code = """
            ThisBuild / scalaVersion := "3.3.3"
            libraryDependencies += "org.typelevel" %% "cats-core" % "2.12.0"
        """

        // Act
        val result = extract(code)

        // Assert
        assertThat(result.strings).containsExactly("3.3.3", "org.typelevel", "cats-core", "2.12.0")
    }
}

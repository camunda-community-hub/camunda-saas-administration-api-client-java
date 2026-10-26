package io.camunda.saas.administration.api.client.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SyncDocsTest {

  @TempDir Path dir;

  private Path source(String relativePath, String content) throws IOException {
    Path file = dir.resolve("src").resolve(relativePath);
    Files.createDirectories(file.getParent());
    return Files.writeString(file, content);
  }

  private Path markdown(String content) throws IOException {
    return Files.writeString(dir.resolve("README.md"), content);
  }

  private void sync() throws IOException {
    SyncDocs.sync(dir.resolve("src"), dir.resolve("README.md"));
  }

  private String readme() throws IOException {
    return Files.readString(dir.resolve("README.md"));
  }

  @Test
  void shouldReplaceBlockContentWithFencedSnippet() throws IOException {
    source("A.java", "class A {\n  // docs:start hello\n  int x = 1;\n  // docs:end hello\n}\n");
    markdown("before\n<!-- docs:snippet hello java -->\nstale\n<!-- docs:end -->\nafter\n");

    sync();

    assertThat(readme())
        .isEqualTo(
            "before\n<!-- docs:snippet hello java -->\n```java\nint x = 1;\n```\n<!-- docs:end -->\nafter\n");
  }

  @Test
  void shouldFillEmptyBlock() throws IOException {
    source("A.java", "// docs:start s\nint x;\n// docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme()).contains("```java\nint x;\n```");
  }

  @Test
  void shouldRemoveCommonIndentationAndKeepRelativeIndentation() throws IOException {
    source("A.java", "    // docs:start s\n    if (a) {\n      b();\n    }\n    // docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme()).contains("```java\nif (a) {\n  b();\n}\n```");
  }

  @Test
  void shouldPreserveBlankLinesInsideSnippet() throws IOException {
    source("A.java", "// docs:start s\na();\n\nb();\n// docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme()).contains("```java\na();\n\nb();\n```");
  }

  @Test
  void shouldKeepSpecialReplacementCharactersLiteral() throws IOException {
    source("A.java", "// docs:start s\nString s = \"${x}\\\\n $1\";\n// docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme()).contains("String s = \"${x}\\\\n $1\";");
  }

  @Test
  void shouldSyncMultipleBlocksFromMultipleFilesAndLanguages() throws IOException {
    source("pkg/A.java", "// docs:start a\nint a;\n// docs:end a\n");
    source("resources/app.yaml", "# docs:start b\nkey: value\n# docs:end b\n");
    markdown(
        "<!-- docs:snippet a java -->\n<!-- docs:end -->\n\ntext\n\n<!-- docs:snippet b yaml -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme())
        .isEqualTo(
            "<!-- docs:snippet a java -->\n```java\nint a;\n```\n<!-- docs:end -->\n\ntext\n\n<!-- docs:snippet b yaml -->\n```yaml\nkey: value\n```\n<!-- docs:end -->\n");
  }

  @Test
  void shouldAllowSameSnippetToBeReferencedTwice() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    markdown(
        "<!-- docs:snippet s java -->\n<!-- docs:end -->\n<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme().split("```java", -1)).hasSize(3);
  }

  @Test
  void shouldBeIdempotent() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();
    String first = readme();
    sync();

    assertThat(readme()).isEqualTo(first);
  }

  @Test
  void shouldNotTouchFileWithoutChanges() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    Path readme =
        markdown("<!-- docs:snippet s java -->\n```java\nint a;\n```\n<!-- docs:end -->\n");
    readme.toFile().setReadOnly();

    sync();

    assertThat(readme()).contains("int a;");
  }

  @Test
  void shouldLeaveMarkdownWithoutBlocksUntouched() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    markdown("# Title\n\n```java\nplain();\n```\n");

    sync();

    assertThat(readme()).isEqualTo("# Title\n\n```java\nplain();\n```\n");
  }

  @Test
  void shouldSyncMarkdownWithCrlfLineEndingsAndKeepThem() throws IOException {
    source("A.java", "// docs:start s\nint a;\n\nint b;\n// docs:end s\n");
    markdown("before\r\n<!-- docs:snippet s java -->\r\nstale\r\n<!-- docs:end -->\r\nafter\r\n");

    sync();

    assertThat(readme())
        .isEqualTo(
            "before\r\n<!-- docs:snippet s java -->\r\n```java\r\nint a;\r\n\r\nint b;\r\n```\r\n<!-- docs:end -->\r\nafter\r\n");
  }

  @Test
  void shouldSyncSourcesWithCrlfLineEndings() throws IOException {
    source("A.java", "// docs:start s\r\n  int a;\r\n// docs:end s\r\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    sync();

    assertThat(readme())
        .isEqualTo("<!-- docs:snippet s java -->\n```java\nint a;\n```\n<!-- docs:end -->\n");
  }

  @Test
  void shouldFailForUnknownSnippet() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    markdown("<!-- docs:snippet missing java -->\n<!-- docs:end -->\n");

    assertThatThrownBy(this::sync)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("README.md")
        .hasMessageContaining("unknown snippet 'missing'");
  }

  @Test
  void shouldFailForDuplicateSnippet() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    source("B.java", "// docs:start s\nint b;\n// docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    assertThatThrownBy(this::sync)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Duplicate snippet 's'");
  }

  @Test
  void shouldFailForUnclosedSnippet() throws IOException {
    source("A.java", "// docs:start s\nint a;\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    assertThatThrownBy(this::sync)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("docs:start s never closed");
  }

  @Test
  void shouldFailForEndWithoutMatchingStart() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end other\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    assertThatThrownBy(this::sync)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("docs:end other without matching docs:start");
  }

  @Test
  void shouldRejectWrongNumberOfArguments() {
    assertThatThrownBy(() -> SyncDocs.main(new String[] {"only-one"}))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Usage");
  }

  @Test
  void shouldSyncViaMain() throws IOException {
    source("A.java", "// docs:start s\nint a;\n// docs:end s\n");
    markdown("<!-- docs:snippet s java -->\n<!-- docs:end -->\n");

    SyncDocs.main(
        new String[] {dir.resolve("src").toString(), dir.resolve("README.md").toString()});

    assertThat(readme()).contains("```java\nint a;\n```");
  }
}

package io.camunda.saas.administration.api.client.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Copies code snippets from source files into the documentation.
 *
 * <p>Snippets are delimited in source files by lines containing {@code docs:start <name>} and
 * {@code docs:end <name>}. The documentation references them with a block of the form
 *
 * <pre>
 * &lt;!-- docs:snippet name lang --&gt;
 * ...
 * &lt;!-- docs:end --&gt;
 * </pre>
 *
 * whose content is replaced with a fenced code block holding the snippet. Syncing fails if a
 * referenced snippet does not exist. LF and CRLF line endings are both accepted and the line ending
 * of each block's opening marker is kept.
 *
 * <p>Usage: {@code SyncDocs <source-root> <markdown-file>}
 */
public final class SyncDocs {

  private static final Pattern START = Pattern.compile("docs:start\\s+(\\S+)");
  private static final Pattern END = Pattern.compile("docs:end\\s+(\\S+)");
  private static final Pattern BLOCK =
      Pattern.compile(
          "^<!-- docs:snippet (\\S+) (\\S+) -->(\\r?\\n)(?:(?!<!-- docs:snippet).)*?<!-- docs:end -->",
          Pattern.DOTALL | Pattern.MULTILINE);
  private static final Pattern OPENING_MARKER =
      Pattern.compile("^<!-- docs:snippet", Pattern.MULTILINE);
  private static final Pattern CLOSING_MARKER =
      Pattern.compile("^<!-- docs:end -->", Pattern.MULTILINE);

  private SyncDocs() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: SyncDocs <source-root> <markdown-file>");
    }
    sync(Path.of(args[0]), Path.of(args[1]));
  }

  /**
   * Rewrites the snippet blocks of {@code markdown} with the snippets found below {@code
   * sourceRoot}. The file is only written if its content changes.
   *
   * @throws IllegalStateException if a block references an unknown snippet or the snippet markers
   *     in the sources are malformed
   */
  public static void sync(Path sourceRoot, Path markdown) throws IOException {
    String original = Files.readString(markdown);
    String synced = inject(original, collectSnippets(sourceRoot), markdown.getFileName());
    if (!synced.equals(original)) {
      Files.writeString(markdown, synced);
    }
  }

  static String inject(String markdown, Map<String, String> snippets, Object documentName) {
    Matcher matcher = BLOCK.matcher(markdown);
    StringBuilder result = new StringBuilder();
    int blocks = 0;
    while (matcher.find()) {
      blocks++;
      String name = matcher.group(1);
      String snippet = snippets.get(name);
      if (snippet == null) {
        throw new IllegalStateException(
            "%s references unknown snippet '%s'".formatted(documentName, name));
      }
      String eol = matcher.group(3);
      String block =
          ("<!-- docs:snippet %1$s %2$s -->\n```%2$s\n%3$s\n```\n<!-- docs:end -->")
              .formatted(name, matcher.group(2), snippet)
              .replace("\n", eol);
      matcher.appendReplacement(result, Matcher.quoteReplacement(block));
    }
    matcher.appendTail(result);
    requireWellFormed(markdown, blocks, documentName);
    return result.toString();
  }

  /**
   * Fails if the markdown contains opening or closing markers that do not belong to a complete
   * block, so a missing marker cannot make a block swallow the text up to the next one.
   */
  private static void requireWellFormed(String markdown, int blocks, Object documentName) {
    long opening = OPENING_MARKER.matcher(markdown).results().count();
    long closing = CLOSING_MARKER.matcher(markdown).results().count();
    if (opening != blocks || closing != blocks) {
      throw new IllegalStateException(
          "%s has %d snippet block(s) but %d opening and %d closing marker(s); every '<!-- docs:snippet <name> <lang> -->' needs a matching '<!-- docs:end -->'"
              .formatted(documentName, blocks, opening, closing));
    }
  }

  static Map<String, String> collectSnippets(Path root) throws IOException {
    Map<String, String> snippets = new HashMap<>();
    try (Stream<Path> files = Files.walk(root)) {
      for (Path file : files.filter(Files::isRegularFile).sorted().toList()) {
        collect(file, snippets);
      }
    }
    return snippets;
  }

  private static void collect(Path file, Map<String, String> snippets) throws IOException {
    String openName = null;
    List<String> lines = new ArrayList<>();
    for (String line : Files.readAllLines(file)) {
      Matcher start = START.matcher(line);
      Matcher end = END.matcher(line);
      if (start.find()) {
        if (openName != null) {
          throw new IllegalStateException(
              "%s: docs:start %s inside open snippet '%s'"
                  .formatted(file, start.group(1), openName));
        }
        openName = start.group(1);
        lines = new ArrayList<>();
      } else if (end.find()) {
        if (!end.group(1).equals(openName)) {
          throw new IllegalStateException(
              "%s: docs:end %s without matching docs:start".formatted(file, end.group(1)));
        }
        if (snippets.put(openName, dedent(lines)) != null) {
          throw new IllegalStateException("Duplicate snippet '%s' in %s".formatted(openName, file));
        }
        openName = null;
      } else if (openName != null) {
        lines.add(line);
      }
    }
    if (openName != null) {
      throw new IllegalStateException("%s: docs:start %s never closed".formatted(file, openName));
    }
  }

  private static String dedent(List<String> lines) {
    int indent =
        lines.stream()
            .filter(line -> !line.isBlank())
            .mapToInt(line -> line.length() - line.stripLeading().length())
            .min()
            .orElse(0);
    return String.join(
        "\n", lines.stream().map(line -> line.isBlank() ? "" : line.substring(indent)).toList());
  }
}

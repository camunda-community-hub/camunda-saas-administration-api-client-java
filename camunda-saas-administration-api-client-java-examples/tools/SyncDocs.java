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
 * Copies code snippets from the examples module into the documentation.
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
 * whose content is replaced with a fenced code block holding the snippet. The build fails if a
 * referenced snippet does not exist.
 *
 * <p>Usage: {@code java SyncDocs.java <source-root> <markdown-file>}
 */
public class SyncDocs {

  private static final Pattern START = Pattern.compile("docs:start\\s+(\\S+)");
  private static final Pattern END = Pattern.compile("docs:end\\s+(\\S+)");
  private static final Pattern BLOCK =
      Pattern.compile(
          "<!-- docs:snippet (\\S+) (\\S+) -->\\n.*?<!-- docs:end -->", Pattern.DOTALL);

  public static void main(String[] args) throws IOException {
    Map<String, String> snippets = collectSnippets(Path.of(args[0]));
    Path markdown = Path.of(args[1]);
    String original = Files.readString(markdown);
    Matcher matcher = BLOCK.matcher(original);
    StringBuilder result = new StringBuilder();
    while (matcher.find()) {
      String name = matcher.group(1);
      String snippet = snippets.get(name);
      if (snippet == null) {
        throw new IllegalStateException(
            "%s references unknown snippet '%s'".formatted(markdown.getFileName(), name));
      }
      String block =
          "<!-- docs:snippet %1$s %2$s -->\n```%2$s\n%3$s\n```\n<!-- docs:end -->"
              .formatted(name, matcher.group(2), snippet);
      matcher.appendReplacement(result, Matcher.quoteReplacement(block));
    }
    matcher.appendTail(result);
    if (!result.toString().equals(original)) {
      Files.writeString(markdown, result.toString());
    }
  }

  private static Map<String, String> collectSnippets(Path root) throws IOException {
    Map<String, String> snippets = new HashMap<>();
    try (Stream<Path> files = Files.walk(root)) {
      for (Path file : files.filter(Files::isRegularFile).toList()) {
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

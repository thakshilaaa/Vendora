import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FixPackages {
    public static void main(String[] args) throws Exception {
        Path baseDir = Paths.get("src/main/java");
        Pattern packagePattern = Pattern.compile("^package\\s+[\\w\\.]+;", Pattern.MULTILINE);

        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> {
                     try {
                         Path relPath = baseDir.relativize(p.getParent());
                         String correctPkg = relPath.toString().replace(File.separatorChar, '.').replace("/", ".");
                         
                         String content = new String(Files.readAllBytes(p));
                         Matcher m = packagePattern.matcher(content);
                         if (m.find()) {
                             String currentPkgLine = m.group();
                             String expectedPkgLine = "package " + correctPkg + ";";
                             if (!currentPkgLine.equals(expectedPkgLine)) {
                                 String newContent = m.replaceFirst(expectedPkgLine);
                                 Files.write(p, newContent.getBytes());
                                 System.out.println("Fixed package in " + p + ": " + expectedPkgLine);
                             }
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 });
        }
    }
}

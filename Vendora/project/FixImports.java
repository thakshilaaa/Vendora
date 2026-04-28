import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FixImports {
    public static void main(String[] args) throws Exception {
        Path baseDir = Paths.get("src/main/java");
        
        // Map of class name -> list of fully qualified names
        Map<String, List<String>> classMap = new HashMap<>();
        
        // Pass 1: Build class map
        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> {
                     String fileName = p.getFileName().toString();
                     String className = fileName.substring(0, fileName.length() - 5);
                     
                     Path relPath = baseDir.relativize(p.getParent());
                     String correctPkg = relPath.toString().replace(File.separatorChar, '.').replace("/", ".");
                     String fqn = correctPkg + "." + className;
                     
                     classMap.computeIfAbsent(className, k -> new ArrayList<>()).add(fqn);
                 });
        }
        
        // Print duplicates to see what we're dealing with
        for (Map.Entry<String, List<String>> entry : classMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println("Duplicate class: " + entry.getKey() + " -> " + entry.getValue());
            }
        }
        
        // Pass 2: Fix imports
        Pattern importPattern = Pattern.compile("^import\\s+([\\w\\.]+);", Pattern.MULTILINE);
        
        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> {
                     try {
                         String content = new String(Files.readAllBytes(p));
                         Matcher m = importPattern.matcher(content);
                         StringBuffer sb = new StringBuffer();
                         boolean modified = false;
                         
                         while (m.find()) {
                             String currentImport = m.group(1);
                             String[] parts = currentImport.split("\\.");
                             String className = parts[parts.length - 1];
                             
                             // If it's a project class
                             if (currentImport.startsWith("com.vendora") || currentImport.startsWith("com.cosmetic")) {
                                 List<String> fqns = classMap.get(className);
                                 if (fqns != null && fqns.size() == 1) {
                                     String correctFqn = fqns.get(0);
                                     if (!currentImport.equals(correctFqn)) {
                                         m.appendReplacement(sb, "import " + correctFqn + ";");
                                         modified = true;
                                         continue;
                                     }
                                 }
                             }
                             m.appendReplacement(sb, m.group(0)); // keep original
                         }
                         m.appendTail(sb);
                         
                         if (modified) {
                             Files.write(p, sb.toString().getBytes());
                             System.out.println("Fixed imports in " + p);
                         }
                         
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 });
        }
    }
}

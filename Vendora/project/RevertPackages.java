import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RevertPackages {
    public static void main(String[] args) throws Exception {
        Path baseDir = Paths.get("src/main/java");
        Pattern packagePattern = Pattern.compile("^package\\s+com\\.vendora\\.epic\\d+([^;]*);", Pattern.MULTILINE);

        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> {
                     try {
                         String content = new String(Files.readAllBytes(p));
                         Matcher m = packagePattern.matcher(content);
                         if (m.find()) {
                             String suffix = m.group(1);
                             // For epic2, it was com.cosmetic.app
                             if (p.toString().contains("epic2")) {
                                 String expectedPkgLine = "package com.cosmetic.app" + suffix + ";";
                                 String newContent = m.replaceFirst(expectedPkgLine);
                                 Files.write(p, newContent.getBytes());
                                 System.out.println("Reverted package in " + p + ": " + expectedPkgLine);
                             } else {
                                 // For epic6 supplier, it was com.vendor.supplier.service in one file?
                                 // Let's just do com.vendora + suffix. Except dto was com.vendora.dto.
                                 // Let's just remove .epicX
                                 String expectedPkgLine = "package com.vendora" + suffix + ";";
                                 String newContent = m.replaceFirst(expectedPkgLine);
                                 Files.write(p, newContent.getBytes());
                                 System.out.println("Reverted package in " + p + ": " + expectedPkgLine);
                             }
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 });
        }
    }
}
